package com.example.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ChatMessage
import com.example.ui.theme.AuraAccentAmber
import com.example.ui.theme.AuraCompanionBubble
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraPrivacyGreen
import com.example.ui.theme.AuraSecondary
import com.example.ui.theme.AuraSubtext
import com.example.ui.theme.AuraUserBubble
import java.util.Date

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == "user"
    val timeFormatted = DateFormat.format("hh:mm a", Date(message.timestamp)).toString()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (isUser) {
            // User message bubble
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                // Media preview if image or payment attached
                if (!message.mediaUri.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, AuraPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = message.mediaUri,
                            contentDescription = "Uploaded Media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .widthIn(max = 240.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        if (message.isPaymentVerification || message.mediaType == "payment") {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AuraPrivacyGreen)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Receipt,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Payment Screenshot",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }
                    }
                }

                // Video link chip if shared
                if (!message.videoLink.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, AuraSecondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PlayCircleOutline,
                                contentDescription = "Video Clip",
                                tint = AuraSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Video / Reel Shared",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuraSecondary
                            )
                        }
                    }
                }

                // Bubble container
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 4.dp,
                                bottomStart = 18.dp,
                                bottomEnd = 18.dp
                            )
                        )
                        .background(AuraUserBubble)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("user_chat_bubble")
                ) {
                    Text(
                        text = message.text,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Timestamp & encryption lock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 3.dp, end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = AuraSubtext.copy(alpha = 0.7f),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 10.sp,
                        color = AuraSubtext
                    )
                }
            }
        } else {
            // Aura companion message
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 340.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Small Aura avatar
                Image(
                    painter = painterResource(id = R.drawable.aura_avatar),
                    contentDescription = "Aura",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(1.dp, AuraPrimary, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Emotional state attunement badge
                    if (!message.detectedEmotion.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AuraPrimary.copy(alpha = 0.15f))
                                .border(0.8.dp, AuraPrimary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AuraPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Attuned to: ${message.detectedEmotion}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AuraPrimary
                                )
                            }
                        }
                    }

                    // Main response card
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 18.dp,
                                    bottomStart = 18.dp,
                                    bottomEnd = 18.dp
                                )
                            )
                            .background(AuraCompanionBubble)
                            .border(1.dp, AuraPrimary.copy(alpha = 0.18f), RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                            .padding(horizontal = 15.dp, vertical = 12.dp)
                            .testTag("aura_chat_bubble")
                    ) {
                        Column {
                            Text(
                                text = message.text,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Payment Verification Card if extracted
                            if (message.isPaymentVerification || !message.utrNumber.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                PaymentVerificationCard(
                                    utrNumber = message.utrNumber,
                                    amount = message.paymentAmount,
                                    date = message.paymentDate,
                                    receiver = message.paymentReceiver
                                )
                            }
                        }
                    }

                    // Timestamp and confidential badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 3.dp, start = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confidential",
                                tint = AuraPrivacyGreen.copy(alpha = 0.8f),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Confidential • Lifelong Space",
                                fontSize = 10.sp,
                                color = AuraPrivacyGreen.copy(alpha = 0.8f)
                            )
                        }

                        Text(
                            text = timeFormatted,
                            fontSize = 10.sp,
                            color = AuraSubtext
                        )
                    }
                }
            }
        }
    }
}
