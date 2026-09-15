package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraAccentAmber
import com.example.ui.theme.AuraPrivacyGreen
import com.example.ui.theme.AuraSubtext

@Composable
fun PaymentVerificationCard(
    utrNumber: String?,
    amount: String?,
    date: String?,
    receiver: String?,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val effectiveUtr = utrNumber ?: "Processing verification..."

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("payment_verification_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    AuraPrivacyGreen.copy(alpha = 0.6f),
                    AuraAccentAmber.copy(alpha = 0.4f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AuraPrivacyGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = "Payment Receipt",
                            tint = AuraPrivacyGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Payment Verification OCR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Bank Screenshot Recorded",
                            fontSize = 11.sp,
                            color = AuraSubtext
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AuraPrivacyGreen.copy(alpha = 0.18f))
                        .border(0.8.dp, AuraPrivacyGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AuraPrivacyGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Submitted",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AuraPrivacyGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(10.dp))

            // UTR / UPI Transaction ID Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "UTR / UPI TRANSACTION ID",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = AuraSubtext
                    )
                    Text(
                        text = effectiveUtr,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AuraPrivacyGreen
                    )
                }

                if (!utrNumber.isNullOrBlank()) {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(utrNumber))
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy UTR",
                            tint = AuraSubtext,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Amount, Date, Receiver Grid
            if (!amount.isNullOrBlank() || !date.isNullOrBlank() || !receiver.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!amount.isNullOrBlank()) {
                        Column {
                            Text(
                                text = "AMOUNT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuraSubtext
                            )
                            Text(
                                text = amount,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (!date.isNullOrBlank()) {
                        Column {
                            Text(
                                text = "DATE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuraSubtext
                            )
                            Text(
                                text = date,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (!receiver.isNullOrBlank()) {
                        Column {
                            Text(
                                text = "RECEIVER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuraSubtext
                            )
                            Text(
                                text = receiver,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
