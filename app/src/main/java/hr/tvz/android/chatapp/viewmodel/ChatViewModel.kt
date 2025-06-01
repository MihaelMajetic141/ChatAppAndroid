package hr.tvz.android.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.network.WebSocketClient
import hr.tvz.android.chatapp.network.repositories.ConversationRepository
import hr.tvz.android.chatapp.network.repositories.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<hr.tvz.android.chatapp.data.model.ChatMessage>>(emptyList())
    val messages: StateFlow<List<hr.tvz.android.chatapp.data.model.ChatMessage>> = _messages

    private val _conversation = MutableStateFlow<hr.tvz.android.chatapp.data.model.Conversation?>(null)
    val conversation: StateFlow<hr.tvz.android.chatapp.data.model.Conversation?> = _conversation

    private val _isDirectMessage = MutableStateFlow(false)
    val isDirectMessage: StateFlow<Boolean> = _isDirectMessage

    val connectionState: StateFlow<ConnectionState> = webSocketClient.connectionState

    init {
        viewModelScope.launch {
            webSocketClient.messages.collect { message ->
                _messages.value += message
            }
        }
    }

    fun startWebSocket() {
        webSocketClient.start()
    }

    fun sendMessage(message: hr.tvz.android.chatapp.data.model.ChatMessage) {
        viewModelScope.launch {
            _messages.value += message
            webSocketClient.send(message)
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            val fetchedMessages = messageRepository.getMessagesByConversationId(conversationId)
            _messages.value = fetchedMessages.sortedBy { it.timestamp }
        }
    }

    fun loadConversation(conversationId: String, currentUserId: String) {
        viewModelScope.launch {
            try {
                val fetchedConversation = conversationRepository
                    .getConversation(conversationId, currentUserId)
                _conversation.value = fetchedConversation
                _isDirectMessage.value = fetchedConversation.isDirectMessage
            } catch (e: Exception) {
                _conversation.value = null
                _isDirectMessage.value = false
                println("ERROR: loadConversation() -> ${e.message}")
            }
        }
    }

    override fun onCleared() {
        webSocketClient.stop()
        super.onCleared()
    }
}

sealed class ConnectionState {
    data object CONNECTING : ConnectionState()
    data object CONNECTED : ConnectionState()
    data object DISCONNECTING : ConnectionState()
    data object DISCONNECTED : ConnectionState()
    data class ERROR(val exception: Throwable) : ConnectionState()
}