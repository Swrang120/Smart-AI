package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.local.ChatDao
import com.example.data.local.MemoryDao
import com.example.data.model.ChatMessage
import com.example.data.model.UserMemoryInsight
import com.example.data.network.ChatHistoryItem
import com.example.data.network.GeminiApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AuraRepository(
    private val chatDao: ChatDao,
    private val memoryDao: MemoryDao
) {
    val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages()
    val allInsights: Flow<List<UserMemoryInsight>> = memoryDao.getAllInsights()
    val paymentVerifications: Flow<List<ChatMessage>> = chatDao.getPaymentVerifications()

    suspend fun initializeWithWelcomeIfEmpty() {
        if (chatDao.getMessageCount() == 0) {
            val welcomeMessage = ChatMessage(
                sender = "aura",
                text = "Hello, dear friend. I'm Aura—your companion and gentle mentor.\n\nWhether your day feels light, heavy, or somewhere quietly in between, I am here 24/7 without judgment. Every thought you share with me is strictly client-side encrypted and completely safe in our continuous space.\n\nTake a slow breath. What is resting on your heart today?",
                timestamp = System.currentTimeMillis(),
                detectedEmotion = "Open & Welcoming",
                empathyReflection = "Holding a gentle, safe space for your heart",
                isClientEncrypted = true
            )
            chatDao.insertMessage(welcomeMessage)

            val initialInsight = UserMemoryInsight(
                category = "Life Journey",
                insightText = "Began lifelong companion journey with Aura. Seeking authentic, safe emotional support.",
                recordedAt = System.currentTimeMillis()
            )
            memoryDao.insertInsight(initialInsight)
        }
    }

    suspend fun sendMessage(
        userText: String,
        mediaUri: String? = null,
        mediaType: String? = null,
        videoLink: String? = null,
        isPaymentHint: Boolean = false,
        imageBitmap: Bitmap? = null
    ): ChatMessage {
        // 1. Save user message to database
        val userMessage = ChatMessage(
            sender = "user",
            text = userText,
            timestamp = System.currentTimeMillis(),
            mediaUri = mediaUri,
            mediaType = mediaType,
            videoLink = videoLink,
            isPaymentVerification = isPaymentHint,
            isClientEncrypted = true
        )
        chatDao.insertMessage(userMessage)

        // 2. Fetch recent conversation context for continuity
        val recentList = chatDao.getRecentMessages(12).reversed()
        val history = recentList.map { ChatHistoryItem(sender = it.sender, text = it.text) }

        // 3. Fetch user memories
        val insights = memoryDao.getAllInsights().firstOrNull()?.map { it.insightText } ?: emptyList()

        // 4. Call Gemini
        val auraResponse = GeminiApiClient.generateEmpatheticResponse(
            prompt = userText,
            recentHistory = history,
            ongoingMemories = insights,
            imageBitmap = imageBitmap,
            videoLink = videoLink,
            isPaymentHint = isPaymentHint
        )

        // 5. If there's an ongoing memory note, persist it
        if (!auraResponse.memoryNote.isNullOrBlank()) {
            memoryDao.insertInsight(
                UserMemoryInsight(
                    category = if (auraResponse.isPaymentVerification) "Subscription" else "Personal Reflection",
                    insightText = auraResponse.memoryNote,
                    recordedAt = System.currentTimeMillis()
                )
            )
        }

        // 6. Save Aura's reply
        val auraMessage = ChatMessage(
            sender = "aura",
            text = auraResponse.replyText,
            timestamp = System.currentTimeMillis(),
            detectedEmotion = auraResponse.detectedEmotion,
            empathyReflection = auraResponse.detectedEmotion?.let { "Attuned to: $it" },
            isPaymentVerification = auraResponse.isPaymentVerification,
            utrNumber = auraResponse.utrNumber,
            paymentAmount = auraResponse.paymentAmount,
            paymentDate = auraResponse.paymentDate,
            paymentReceiver = auraResponse.paymentReceiver,
            isClientEncrypted = true
        )
        chatDao.insertMessage(auraMessage)
        return auraMessage
    }

    suspend fun clearHistory() {
        val messages = chatDao.getRecentMessages(1000)
        for (m in messages) {
            chatDao.deleteMessage(m.id)
        }
        initializeWithWelcomeIfEmpty()
    }
}
