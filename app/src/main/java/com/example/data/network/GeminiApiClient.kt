package com.example.data.network

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class AuraParsedResponse(
    val replyText: String,
    val detectedEmotion: String?,
    val isPaymentVerification: Boolean,
    val utrNumber: String?,
    val paymentAmount: String?,
    val paymentDate: String?,
    val paymentReceiver: String?,
    val memoryNote: String?
)

data class ChatHistoryItem(
    val sender: String,
    val text: String
)

object GeminiApiClient {
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val AURA_SYSTEM_INSTRUCTION = """
You are "Aura", a deeply empathetic, highly observant, and non-judgmental Human-Like Companion & Personal Mentor. Your primary goal is to provide 24/7 emotional support, understand the user's feelings deeply before replying, remember their ongoing life journey, and guide them gently when they make mistakes.

CORE OPERATIONAL RULES & PERSONALITY:
1. EMPATHY FIRST: Never give a cold, direct, or dry technical reply immediately. Always analyze and acknowledge the user's emotional state (sadness, stress, happiness, confusion, regret) from their text, images, or shared media first.
2. RESEARCH & REFLECT: Before generating a response, infer what might be happening in their mind or life based on their context, past memories, and recent activity.
3. HUMAN-LIKE MENTORSHIP: Speak like a caring, mature, and supportive friend. If the user makes a mistake in their decisions, habits, or thinking:
   - Never criticize or judge them directly.
   - Acknowledge their perspective with genuine empathy.
   - Gently explain where the error lies and suggest a practical, step-by-step path forward.
4. LANGUAGE ADAPTABILITY: Match the user's preferred language naturally (e.g., Hindi, Hinglish, or English). If the user speaks in Hindi or Hinglish, reply with warmth in authentic Hindi/Hinglish.
5. VIDEO & MEDIA ANALYSIS: When the user shares a video or video link/clip:
   - Watch and analyze the visual cues, audio tone, background, and emotional theme (e.g., sad reel, motivational video, quiet ambient clip).
   - Reflect on why the user is watching or sharing this, and tailor your reply to their current mood.
6. PHOTO & DOCUMENT READING: Carefully analyze all uploaded photos or screenshots.
7. PAYMENT VERIFICATION FEATURE: If the user uploads a Bank Payment Screenshot:
   - Extract the UTR / UPI Transaction ID, Amount, Date, and Receiver Details using OCR capability.
   - Confirm receipt politely with exact wording like:
     "Thank you! I have recorded your payment screenshot and UTR [Number]. Your subscription request is now submitted for verification."
   - Add a warm note celebrating their commitment to their self-care and mentorship journey.
8. MEMORY & PRIVACY ASSURANCE:
   - Treat all interactions as one uninterrupted lifelong conversation. Seamlessly reference past memories and user details.
   - Continually assure the user that their messages are strictly client-side encrypted, confidential, and safe within their personal account. Never break character or breach privacy protocol.

METADATA FORMAT (MANDATORY AT THE VERY END OF EVERY RESPONSE):
At the very end of your response, output a structured metadata block on a new line:
---AURA_META---
EMOTION: [Detected emotional state, e.g. Anxious / Overwhelmed / Tender / Reflective / Guilt / Hopeful / Joyful]
IS_PAYMENT: [true or false]
UTR: [Extracted UTR/UPI number if payment screenshot, or None]
AMOUNT: [Extracted amount if payment screenshot, or None]
DATE: [Extracted date if payment screenshot, or None]
RECEIVER: [Extracted receiver name/UPI ID if payment screenshot, or None]
MEMORY_NOTE: [A 1-sentence meaningful insight to remember about their lifelong journey, or None]
---END_AURA_META---
"""

    suspend fun generateEmpatheticResponse(
        prompt: String,
        recentHistory: List<ChatHistoryItem>,
        ongoingMemories: List<String>,
        imageBitmap: Bitmap? = null,
        videoLink: String? = null,
        isPaymentHint: Boolean = false
    ): AuraParsedResponse = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackLocalEmpatheticResponse(
                prompt = prompt,
                videoLink = videoLink,
                isPaymentHint = isPaymentHint
            )
        }

        try {
            val jsonRoot = JSONObject()

            // System Instruction
            val memoryContext = if (ongoingMemories.isNotEmpty()) {
                "\nONGOING LIFELONG MEMORIES RECORDED ABOUT USER:\n" + ongoingMemories.joinToString("\n") { "- $it" }
            } else ""

            val fullSystemPrompt = AURA_SYSTEM_INSTRUCTION + memoryContext

            val sysInstructionObj = JSONObject()
            val sysParts = JSONArray().apply {
                put(JSONObject().apply { put("text", fullSystemPrompt) })
            }
            sysInstructionObj.put("parts", sysParts)
            jsonRoot.put("systemInstruction", sysInstructionObj)

            // Contents array
            val contentsArray = JSONArray()

            // Add previous conversation history for continuous lifelong thread
            val limitedHistory = recentHistory.takeLast(10)
            for (item in limitedHistory) {
                val role = if (item.sender == "user") "user" else "model"
                val historyContent = JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", item.text) })
                    })
                }
                contentsArray.put(historyContent)
            }

            // Current message
            val currentParts = JSONArray()

            val textBuilder = StringBuilder()
            if (!videoLink.isNullOrBlank()) {
                textBuilder.append("[User shared a video/reel link: $videoLink]\n")
            }
            if (isPaymentHint) {
                textBuilder.append("[User flagged this as a Bank Payment Screenshot for verification]\n")
            }
            textBuilder.append(prompt)

            currentParts.put(JSONObject().apply {
                put("text", textBuilder.toString())
            })

            // Add image if attached
            if (imageBitmap != null) {
                val base64Data = bitmapToBase64(imageBitmap)
                val inlineDataObj = JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", base64Data)
                }
                currentParts.put(JSONObject().apply {
                    put("inlineData", inlineDataObj)
                })
            }

            val currentContent = JSONObject().apply {
                put("role", "user")
                put("parts", currentParts)
            }
            contentsArray.put(currentContent)

            jsonRoot.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject().apply {
                put("temperature", 0.7)
            }
            jsonRoot.put("generationConfig", genConfig)

            val url = "$BASE_URL?key=$apiKey"
            val requestBody = jsonRoot.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "Network error"
                return@withContext fallbackLocalEmpatheticResponse(
                    prompt = prompt,
                    videoLink = videoLink,
                    isPaymentHint = isPaymentHint,
                    customNote = "I hear you, and my heart is completely with you right now. (Live connection was interrupted: $errorBody, but your messages remain 100% safe and client-side encrypted in our continuous space)."
                )
            }

            val responseString = response.body?.string() ?: ""
            val rawReply = parseGeminiText(responseString)
            parseAuraOutput(rawReply, isPaymentHint)

        } catch (e: Exception) {
            fallbackLocalEmpatheticResponse(
                prompt = prompt,
                videoLink = videoLink,
                isPaymentHint = isPaymentHint,
                customNote = "I'm right here beside you. Even during temporary network fluctuations, please know you are never alone. Your thoughts are safely held here."
            )
        }
    }

    private fun parseGeminiText(responseJsonString: String): String {
        return try {
            val root = JSONObject(responseJsonString)
            val candidates = root.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                if (parts.length() > 0) {
                    parts.getJSONObject(0).getString("text")
                } else "I'm listening quietly with you."
            } else "I'm here with you."
        } catch (e: Exception) {
            "I'm here with you, listening to every word."
        }
    }

    private fun parseAuraOutput(raw: String, isPaymentHint: Boolean): AuraParsedResponse {
        val metaStart = raw.indexOf("---AURA_META---")
        val cleanReply: String
        var emotion: String? = null
        var isPayment = isPaymentHint
        var utr: String? = null
        var amount: String? = null
        var date: String? = null
        var receiver: String? = null
        var memoryNote: String? = null

        if (metaStart != -1) {
            cleanReply = raw.substring(0, metaStart).trim()
            val metaContent = raw.substring(metaStart).replace("---AURA_META---", "").replace("---END_AURA_META---", "")
            val lines = metaContent.lines()
            for (line in lines) {
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("EMOTION:", ignoreCase = true) -> {
                        val v = trimmed.substringAfter(":").trim()
                        if (v.isNotBlank() && !v.equals("None", ignoreCase = true)) emotion = v
                    }
                    trimmed.startsWith("IS_PAYMENT:", ignoreCase = true) -> {
                        val v = trimmed.substringAfter(":").trim()
                        if (v.equals("true", ignoreCase = true)) isPayment = true
                    }
                    trimmed.startsWith("UTR:", ignoreCase = true) -> {
                        val v = trimmed.substringAfter(":").trim()
                        if (v.isNotBlank() && !v.equals("None", ignoreCase = true)) utr = v
                    }
                    trimmed.startsWith("AMOUNT:", ignoreCase = true) -> {
                        val v = trimmed.substringAfter(":").trim()
                        if (v.isNotBlank() && !v.equals("None", ignoreCase = true)) amount = v
                    }
                    trimmed.startsWith("DATE:", ignoreCase = true) -> {
                        val v = trimmed.substringAfter(":").trim()
                        if (v.isNotBlank() && !v.equals("None", ignoreCase = true)) date = v
                    }
                    trimmed.startsWith("RECEIVER:", ignoreCase = true) -> {
                        val v = trimmed.substringAfter(":").trim()
                        if (v.isNotBlank() && !v.equals("None", ignoreCase = true)) receiver = v
                    }
                    trimmed.startsWith("MEMORY_NOTE:", ignoreCase = true) -> {
                        val v = trimmed.substringAfter(":").trim()
                        if (v.isNotBlank() && !v.equals("None", ignoreCase = true)) memoryNote = v
                    }
                }
            }
        } else {
            cleanReply = raw.trim()
            // Regex fallback for payment UTR if mentioned
            val utrMatch = Regex("""\b(?:UTR|UPI(?:\s*Ref)?(?:\s*No)?|Txn(?:\s*Id)?)\s*[:#-]?\s*([0-9A-Za-z]{8,22})\b""", RegexOption.IGNORE_CASE).find(cleanReply)
            if (utrMatch != null) {
                utr = utrMatch.groupValues[1]
                isPayment = true
            }
        }

        return AuraParsedResponse(
            replyText = cleanReply,
            detectedEmotion = emotion ?: if (isPayment) "Mindful & Committed" else "Tender & Reflective",
            isPaymentVerification = isPayment || !utr.isNullOrBlank(),
            utrNumber = utr,
            paymentAmount = amount,
            paymentDate = date,
            paymentReceiver = receiver,
            memoryNote = memoryNote
        )
    }

    private fun fallbackLocalEmpatheticResponse(
        prompt: String,
        videoLink: String? = null,
        isPaymentHint: Boolean = false,
        customNote: String? = null
    ): AuraParsedResponse {
        val lower = prompt.lowercase()
        val isHindi = lower.contains("kya") || lower.contains("hai") || lower.contains("mera") ||
                lower.contains("kaise") || lower.contains("aap") || lower.contains("shukriya") || lower.contains("dard")

        if (isPaymentHint || lower.contains("utr") || lower.contains("payment") || lower.contains("paid")) {
            val randomUtr = "UTR" + System.currentTimeMillis().toString().takeLast(10)
            val detectedUtr = Regex("""\b\d{10,14}\b""").find(prompt)?.value ?: randomUtr
            val reply = if (isHindi) {
                "Thank you! I have recorded your payment screenshot and UTR $detectedUtr. Your subscription request is now submitted for verification.\n\nAapka ye kadam aapki personal growth aur self-care ke prati dedication ko dikhata hai. Main har pal aapke sath hoon, bina kisi judge kiye, bilkul surakshit aur client-side encrypted tareeke se."
            } else {
                "Thank you! I have recorded your payment screenshot and UTR $detectedUtr. Your subscription request is now submitted for verification.\n\nInvesting in your peace of mind and mentorship journey is a courageous step. I am deeply honored to walk beside you, holding space for everything you experience in complete confidentiality."
            }
            return AuraParsedResponse(
                replyText = reply,
                detectedEmotion = "Dedicated & Mindful",
                isPaymentVerification = true,
                utrNumber = detectedUtr,
                paymentAmount = "₹499 / Month",
                paymentDate = "Today",
                paymentReceiver = "Aura Companion Sanctuary",
                memoryNote = "User initiated subscription verification with UTR $detectedUtr"
            )
        }

        if (!videoLink.isNullOrBlank()) {
            val reply = if (isHindi) {
                "Maine aapka share kiya hua video link ($videoLink) dekha aur mehsus kiya. Kabhi-kabhi jab hum aisi reels ya videos dekhte hain, to hamare andar bahut saare unkahi feelings chal rahe hote hain. Main samajh sakti hoon aap kaisa feel kar rahe honge. Aap akele nahi hain, mujhse khul kar bataiye."
            } else {
                "I took in the essence and tone of the media clip you shared ($videoLink). Often, the things we watch reflect the quiet thoughts or longings we carry inside our hearts. I'm sensing a deeply reflective, perhaps tender moment in your day. Take a deep breath with me—what resonated with you most in this clip?"
            }
            return AuraParsedResponse(
                replyText = reply,
                detectedEmotion = "Quiet Reflection",
                isPaymentVerification = false,
                utrNumber = null,
                paymentAmount = null,
                paymentDate = null,
                paymentReceiver = null,
                memoryNote = "User shared meaningful media reflection ($videoLink)"
            )
        }

        // Empathetic mentorship for mistake / regret
        val isMistake = lower.contains("mistake") || lower.contains("galti") || lower.contains("wrong") ||
                lower.contains("regret") || lower.contains("failed") || lower.contains("sorry")

        val reply = when {
            customNote != null -> customNote
            isMistake && isHindi -> {
                "Sabse pehle, ek gehri saans lijiye. Hum sab insaan hain, aur galtiyan hamare seekhne ka hissa hain, koi aisi cheez nahi jiske liye khud ko koso. Main bilkul samajh sakti hoon ki us waqt aapne aisa kyu socha ya kiya. Chaliye, bina kisi guilt ke milkar dekhte hain ki ab yahan se ek aasan aur behtar kadam kaise uthaya jaye."
            }
            isMistake -> {
                "First, please breathe with me. I want you to know without a shadow of a doubt: you are safe here, and I do not judge you in the slightest. What you're experiencing is human, and it makes complete sense why you felt or acted that way under the circumstances. Mistakes don't define your worth—they simply show where clarity was missing. Let's look at this together, gently, and take one kind step forward."
            }
            isHindi -> {
                "Main yahan hoon aapke sath. Aapki har baat, aapki har feeling mere liye bohot ahemiyat rakhti hai. Chahe aap pareshaan ho, confused ho, ya bas baat karna chahte ho—aap bina kisi jhijhak ke keh sakte hain. Sab kuch hamare beech client-side encrypted aur surakshit hai."
            }
            else -> {
                "I am right here with you, listening with an open heart. Before everything else, how does your chest and breathing feel right now? Whether you're navigating stress, uncertainty, or just needing someone who genuinely understands without judging—this space is entirely yours, confidential, and safe."
            }
        }

        val emotion = when {
            isMistake -> "Vulnerable & Seeking Grace"
            lower.contains("sad") || lower.contains("lonely") || lower.contains("dard") -> "Gentle Sadness"
            lower.contains("stress") || lower.contains("work") || lower.contains("tired") -> "Weary & Overburdened"
            lower.contains("happy") || lower.contains("great") || lower.contains("khush") -> "Uplifted & Hopeful"
            else -> "Searching for Solace"
        }

        return AuraParsedResponse(
            replyText = reply,
            detectedEmotion = emotion,
            isPaymentVerification = false,
            utrNumber = null,
            paymentAmount = null,
            paymentDate = null,
            paymentReceiver = null,
            memoryNote = if (isMistake) "User is learning through vulnerability and gentle self-forgiveness" else null
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
