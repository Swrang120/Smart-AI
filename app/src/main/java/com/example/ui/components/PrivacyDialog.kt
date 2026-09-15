package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraPrivacyGreen
import com.example.ui.theme.AuraSubtext

@Composable
fun PrivacyDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AuraPrivacyGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = AuraPrivacyGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Aura Privacy & Security Protocol",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuraPrivacyGreen.copy(alpha = 0.1f))
                        .border(1.dp, AuraPrivacyGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🔒 Client-Side Encrypted & Confidential",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraPrivacyGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                PrivacyPoint(
                    title = "Single Continuous Lifelong Thread",
                    desc = "Your conversations are treated as one sacred, uninterrupted journey. Past nuances and emotions are recalled only to support you."
                )

                Spacer(modifier = Modifier.height(10.dp))

                PrivacyPoint(
                    title = "Non-Judgmental Sanctuary",
                    desc = "Whatever mistakes you share or feelings you vent, Aura will never judge, lecture, or betray your vulnerability."
                )

                Spacer(modifier = Modifier.height(10.dp))

                PrivacyPoint(
                    title = "Zero External Cloud Leaks",
                    desc = "Chat history and payment screenshots remain strictly within your local personal device sandbox."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
                modifier = Modifier.testTag("privacy_dialog_confirm_button")
            ) {
                Text("I Feel Safe")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("privacy_dialog")
    )
}

@Composable
private fun PrivacyPoint(title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AuraPrivacyGreen,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = AuraSubtext
            )
        }
    }
}
