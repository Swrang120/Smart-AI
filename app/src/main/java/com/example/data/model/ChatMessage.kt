package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "user", "aura", "system"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mediaUri: String? = null,
    val mediaType: String? = null, // "image", "video", "payment"
    val videoLink: String? = null,
    val detectedEmotion: String? = null,
    val empathyReflection: String? = null,
    val isPaymentVerification: Boolean = false,
    val utrNumber: String? = null,
    val paymentAmount: String? = null,
    val paymentDate: String? = null,
    val paymentReceiver: String? = null,
    val isClientEncrypted: Boolean = true
)
