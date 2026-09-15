package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AuraDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.UserMemoryInsight
import com.example.data.repository.AuraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AuraDatabase.getDatabase(application)
    private val repository = AuraRepository(database.chatDao(), database.memoryDao())

    val messages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val insights: StateFlow<List<UserMemoryInsight>> = repository.allInsights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentVerifications: StateFlow<List<ChatMessage>> = repository.paymentVerifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _selectedVideoLink = MutableStateFlow<String?>(null)
    val selectedVideoLink: StateFlow<String?> = _selectedVideoLink.asStateFlow()

    private val _isPaymentMode = MutableStateFlow(false)
    val isPaymentMode: StateFlow<Boolean> = _isPaymentMode.asStateFlow()

    private val _showMemorySheet = MutableStateFlow(false)
    val showMemorySheet: StateFlow<Boolean> = _showMemorySheet.asStateFlow()

    private val _showVideoDialog = MutableStateFlow(false)
    val showVideoDialog: StateFlow<Boolean> = _showVideoDialog.asStateFlow()

    private val _showPrivacyDialog = MutableStateFlow(false)
    val showPrivacyDialog: StateFlow<Boolean> = _showPrivacyDialog.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeWithWelcomeIfEmpty()
        }
    }

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun togglePaymentMode() {
        _isPaymentMode.value = !_isPaymentMode.value
    }

    fun toggleMemorySheet(show: Boolean) {
        _showMemorySheet.value = show
    }

    fun toggleVideoDialog(show: Boolean) {
        _showVideoDialog.value = show
    }

    fun togglePrivacyDialog(show: Boolean) {
        _showPrivacyDialog.value = show
    }

    fun setVideoLink(link: String) {
        _selectedVideoLink.value = link.ifBlank { null }
        _showVideoDialog.value = false
    }

    fun clearSelectedMedia() {
        _selectedImageBitmap.value = null
        _selectedImageUri.value = null
        _selectedVideoLink.value = null
        _isPaymentMode.value = false
    }

    fun handleImageUriSelected(uri: Uri?, isPayment: Boolean = false) {
        if (uri == null) return
        val context = getApplication<Application>()
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            // Scale bitmap down if very large to prevent memory overhead
            val scaledBitmap = scaleBitmapIfNeeded(bitmap, 1024)
            _selectedImageBitmap.value = scaledBitmap
            _selectedImageUri.value = uri.toString()
            if (isPayment) {
                _isPaymentMode.value = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDim: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDim
            newHeight = (maxDim / ratio).toInt()
        } else {
            newHeight = maxDim
            newWidth = (maxDim * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun sendCurrentMessage() {
        val text = _inputText.value.trim()
        val bitmap = _selectedImageBitmap.value
        val imageUri = _selectedImageUri.value
        val video = _selectedVideoLink.value
        val isPayment = _isPaymentMode.value

        if (text.isBlank() && bitmap == null && video.isNullOrBlank()) {
            return
        }

        val promptText = if (text.isNotBlank()) text else {
            when {
                isPayment -> "Here is my bank payment screenshot for subscription verification."
                video != null -> "Here is a video clip I was watching. What do you reflect on this?"
                else -> "Here is a photo I wanted to share with you."
            }
        }

        val mediaType = when {
            isPayment -> "payment"
            bitmap != null -> "image"
            video != null -> "video"
            else -> null
        }

        _inputText.value = ""
        clearSelectedMedia()
        _isAnalyzing.value = true

        viewModelScope.launch {
            try {
                repository.sendMessage(
                    userText = promptText,
                    mediaUri = imageUri,
                    mediaType = mediaType,
                    videoLink = video,
                    isPaymentHint = isPayment,
                    imageBitmap = bitmap
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun sendQuickPrompt(prompt: String) {
        _inputText.value = prompt
        sendCurrentMessage()
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
