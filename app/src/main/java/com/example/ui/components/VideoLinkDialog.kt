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
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraSecondary
import com.example.ui.theme.AuraSubtext

@Composable
fun VideoLinkDialog(
    onDismiss: () -> Unit,
    onSubmitLink: (String) -> Unit
) {
    var urlText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AuraSecondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = AuraSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Share Video or Reel",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Paste a video link, reel, or short clip URL. Aura will analyze the visual cues, audio tone, background, and emotional theme to understand what you are experiencing.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = AuraSubtext
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    placeholder = { Text("https://youtube.com/... or reel link", fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AuraPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("video_url_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitLink(urlText) },
                enabled = urlText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
                modifier = Modifier.testTag("submit_video_button")
            ) {
                Text("Analyze With Aura")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AuraSubtext)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("video_dialog")
    )
}
