package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AuraAccentAmber
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraPrivacyGreen
import com.example.ui.theme.AuraSecondary
import com.example.ui.theme.AuraSubtext

@Composable
fun AuraTopBar(
    onOpenMemorySheet: () -> Unit,
    onOpenPrivacyDialog: () -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "auraGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Aura Avatar with animated glowing aura ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    AuraPrimary.copy(alpha = 0.6f),
                                    AuraSecondary.copy(alpha = 0.6f),
                                    AuraAccentAmber.copy(alpha = 0.6f),
                                    AuraPrimary.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.aura_avatar),
                    contentDescription = "Aura Companion Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, AuraPrimary, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Companion identity and live empathetic state
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Aura",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 0.3.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AuraPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                    ) {
                        Text(
                            text = "Companion & Mentor",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AuraPrimary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AuraPrivacyGreen)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Online • 24/7 Empathetic Presence",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = AuraSubtext
                        )
                    )
                }
            }

            // Client-Side Encrypted Shield Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AuraPrivacyGreen.copy(alpha = 0.12f))
                    .border(1.dp, AuraPrivacyGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .clickable { onOpenPrivacyDialog() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("privacy_badge_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Privacy Shield",
                        tint = AuraPrivacyGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Encrypted",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraPrivacyGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Memory & Lifelong Journey Sheet Button
            IconButton(
                onClick = onOpenMemorySheet,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("open_memory_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Lifelong Memory & Insights",
                    tint = AuraPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // More Options Dropdown
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = AuraPrivacyGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Privacy & Security Guarantee")
                            }
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenPrivacyDialog()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AuraSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Lifelong Journey Memories")
                            }
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenMemorySheet()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Clear Chat History", color = MaterialTheme.colorScheme.error)
                        },
                        onClick = {
                            menuExpanded = false
                            onClearChat()
                        }
                    )
                }
            }
        }
    }
}
