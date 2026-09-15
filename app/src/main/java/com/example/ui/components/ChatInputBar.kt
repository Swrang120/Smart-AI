package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraAccentAmber
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraPrivacyGreen
import com.example.ui.theme.AuraSecondary
import com.example.ui.theme.AuraSubtext

@Composable
fun ChatInputBar(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onQuickPrompt: (String) -> Unit,
    isAnalyzing: Boolean,
    selectedBitmap: Bitmap?,
    selectedVideoLink: String?,
    isPaymentMode: Boolean,
    onClearMedia: () -> Unit,
    onImageSelected: (Uri?, Boolean) -> Unit,
    onOpenVideoDialog: () -> Unit,
    onTogglePaymentMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Android Photo Picker for standard images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImageSelected(uri, false)
    }

    // Android Photo Picker for Bank Payment Screenshot
    val paymentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImageSelected(uri, true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Quick mood & empathy chips
        val quickChips = listOf(
            "Feeling anxious & overwhelmed" to "I'm feeling really overwhelmed right now, could you just be here with me?",
            "Made a mistake today" to "I made a terrible mistake today and I can't stop blaming myself...",
            "Hinglish me baat karo" to "Aura, mujhse thoda Hinglish me baat karo na, dil bohot bhari lag raha hai.",
            "Upload Bank Payment" to "I have uploaded my bank payment screenshot for subscription verification.",
            "Analyze video reel" to "I've been watching this reel repeatedly. Why does it make me feel this way?",
            "Need gentle guidance" to "I'm at a crossroads and feel lost. Can you guide me step-by-step?"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for ((label, prompt) in quickChips) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, AuraPrimary.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .clickable { onQuickPrompt(prompt) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Attached media preview banner
        AnimatedVisibility(
            visible = selectedBitmap != null || !selectedVideoLink.isNullOrBlank() || isPaymentMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (selectedBitmap != null) {
                        Image(
                            bitmap = selectedBitmap.asImageBitmap(),
                            contentDescription = "Selected Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPaymentMode) "Bank Payment Screenshot" else "Photo Attached",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPaymentMode) AuraPrivacyGreen else AuraPrimary
                            )
                            Text(
                                text = if (isPaymentMode) "Aura will OCR extract UTR, Amount & Receiver" else "Aura will analyze visual tone & mood",
                                fontSize = 10.5.sp,
                                color = AuraSubtext
                            )
                        }
                    } else if (!selectedVideoLink.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AuraSecondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayCircleOutline,
                                contentDescription = null,
                                tint = AuraSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Video Link Attached",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraSecondary
                            )
                            Text(
                                text = selectedVideoLink,
                                fontSize = 10.5.sp,
                                maxLines = 1,
                                color = AuraSubtext
                            )
                        }
                    } else if (isPaymentMode) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AuraPrivacyGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = AuraPrivacyGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Payment Verification Mode Active",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuraPrivacyGreen
                            )
                            Text(
                                text = "Attach payment screenshot or paste UTR number",
                                fontSize = 10.5.sp,
                                color = AuraSubtext
                            )
                        }
                    }

                    IconButton(
                        onClick = onClearMedia,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove attachment",
                            tint = AuraSubtext,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Input Field & Action Buttons Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Photo Picker Action
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .size(38.dp)
                    .testTag("attach_photo_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Attach Photo",
                    tint = AuraPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Bank Payment Screenshot OCR Action
            IconButton(
                onClick = {
                    paymentPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .size(38.dp)
                    .testTag("attach_payment_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = "Bank Payment Screenshot",
                    tint = if (isPaymentMode) AuraPrivacyGreen else AuraSubtext,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Video Link Action
            IconButton(
                onClick = onOpenVideoDialog,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("attach_video_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Share Video Link",
                    tint = AuraSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Text Input
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                placeholder = {
                    Text(
                        text = if (isPaymentMode) "Enter or paste UTR / UPI details..." else "Talk to Aura freely...",
                        fontSize = 14.sp,
                        color = AuraSubtext
                    )
                },
                maxLines = 4,
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuraPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text_field")
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Send Button or Loading Indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isAnalyzing) AuraPrimary.copy(alpha = 0.4f) else AuraPrimary)
                    .clickable(enabled = !isAnalyzing) { onSendMessage() }
                    .testTag("send_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
