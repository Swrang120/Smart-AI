package com.example.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.AuraTopBar
import com.example.ui.components.ChatBubble
import com.example.ui.components.ChatInputBar
import com.example.ui.components.MemorySheet
import com.example.ui.components.PaymentVerificationCard
import com.example.ui.components.PrivacyDialog
import com.example.ui.components.VideoLinkDialog
import com.example.ui.theme.AuraAccentAmber
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraPrivacyGreen
import com.example.ui.theme.AuraSecondary
import com.example.ui.theme.AuraSubtext
import com.example.ui.viewmodel.AuraViewModel

@Composable
fun AuraScreen(
    viewModel: AuraViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val paymentVerifications by viewModel.paymentVerifications.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val selectedBitmap by viewModel.selectedImageBitmap.collectAsStateWithLifecycle()
    val selectedVideoLink by viewModel.selectedVideoLink.collectAsStateWithLifecycle()
    val isPaymentMode by viewModel.isPaymentMode.collectAsStateWithLifecycle()
    val showMemorySheet by viewModel.showMemorySheet.collectAsStateWithLifecycle()
    val showVideoDialog by viewModel.showVideoDialog.collectAsStateWithLifecycle()
    val showPrivacyDialog by viewModel.showPrivacyDialog.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    // Auto-scroll when messages change or while analyzing
    LaunchedEffect(messages.size, isAnalyzing) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("aura_screen"),
        topBar = {
            AuraTopBar(
                onOpenMemorySheet = { viewModel.toggleMemorySheet(true) },
                onOpenPrivacyDialog = { viewModel.togglePrivacyDialog(true) },
                onClearChat = { viewModel.clearAllHistory() }
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                onInputTextChanged = { viewModel.onInputTextChanged(it) },
                onSendMessage = { viewModel.sendCurrentMessage() },
                onQuickPrompt = { viewModel.sendQuickPrompt(it) },
                isAnalyzing = isAnalyzing,
                selectedBitmap = selectedBitmap,
                selectedVideoLink = selectedVideoLink,
                isPaymentMode = isPaymentMode,
                onClearMedia = { viewModel.clearSelectedMedia() },
                onImageSelected = { uri, isPayment -> viewModel.handleImageUriSelected(uri, isPayment) },
                onOpenVideoDialog = { viewModel.toggleVideoDialog(true) },
                onTogglePaymentMode = { viewModel.togglePaymentMode() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (messages.isEmpty()) {
                // Welcoming Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aura_avatar),
                        contentDescription = "Aura",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, AuraPrimary, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "I am Aura",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your Empathetic Companion & Non-Judgmental Mentor",
                        fontSize = 13.5.sp,
                        color = AuraPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "24/7 Emotional Support • Continuous Lifelong Thread\nClient-Side Encrypted • Safe Personal Sanctuary",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = AuraSubtext,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)
                        .testTag("chat_messages_list")
                ) {
                    // Continuous lifelong thread notice
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = AuraPrivacyGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Continuous Single Conversation Thread • Client-Side Encrypted",
                                    fontSize = 10.5.sp,
                                    color = AuraSubtext
                                )
                            }
                        }
                    }

                    items(messages, key = { it.id }) { message ->
                        ChatBubble(message = message)
                    }

                    // Analyzing Indicator Bubble
                    if (isAnalyzing) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.aura_avatar),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, AuraPrimary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 1.8.dp,
                                            color = AuraPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Aura is reflecting deeply on your feelings...",
                                            fontSize = 12.sp,
                                            color = AuraPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Lifelong Memory & Insights Bottom Sheet
    if (showMemorySheet) {
        MemorySheet(
            insights = insights,
            payments = paymentVerifications,
            onDismiss = { viewModel.toggleMemorySheet(false) }
        )
    }

    // Video Link Sharing Dialog
    if (showVideoDialog) {
        VideoLinkDialog(
            onDismiss = { viewModel.toggleVideoDialog(false) },
            onSubmitLink = { link ->
                viewModel.setVideoLink(link)
            }
        )
    }

    // Privacy Protocol Dialog
    if (showPrivacyDialog) {
        PrivacyDialog(
            onDismiss = { viewModel.togglePrivacyDialog(false) }
        )
    }
}
