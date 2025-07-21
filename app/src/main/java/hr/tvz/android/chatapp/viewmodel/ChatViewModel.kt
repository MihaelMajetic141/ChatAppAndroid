package hr.tvz.android.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.data.model.ChatMessage
import hr.tvz.android.chatapp.data.model.Conversation
import hr.tvz.android.chatapp.network.Resource
import hr.tvz.android.chatapp.network.WebSocketClient
import hr.tvz.android.chatapp.network.repositories.ConversationRepository
import hr.tvz.android.chatapp.network.repositories.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {
    val connectionState: StateFlow<ConnectionState> = webSocketClient.connectionState
    private val _loadConversationState = MutableStateFlow<LoadConversationState>(
        value = LoadConversationState.Loading)
    val loadConversationState = _loadConversationState.asStateFlow()
    private val _loadMessagesState = MutableStateFlow<LoadMessagesState>(
        value = LoadMessagesState.Loading)
    val loadMessagesState = _loadMessagesState.asStateFlow()

    fun connectToChat(conversationId: String) {
        loadMessages(conversationId)
        viewModelScope.launch {
            val session = webSocketClient.initSession()
            when(session) {
                is Resource.Success<*> -> {
                    webSocketClient.observeTextMessages()
                        .onEach { newMsg ->
                            _loadMessagesState.update { oldState ->
                                val current = (oldState as? LoadMessagesState.Success)?.messageList
                                    ?: emptyList()
                                LoadMessagesState.Success(messageList = listOf(newMsg) + current)
                            }
                        }.launchIn(viewModelScope)
                }
                is Resource.Error<*> -> {

                }
            }
        }
    }

    fun sendMessage(message: ChatMessage) {
        viewModelScope.launch {
            webSocketClient.sendTextMessage(message)
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            try {
                val fetchedMessages = messageRepository.getMessagesByConversationId(conversationId)
                _loadMessagesState.update {
                    LoadMessagesState.Success(messageList = fetchedMessages)
                }
            } catch (e: Exception) {
                _loadMessagesState.update {
                    LoadMessagesState.Error(msg = "Error loading conversation: "+ e.message)
                }
            }
        }
    }

    fun loadConversation(conversationId: String, currentUserId: String) {
        viewModelScope.launch {
            try {
                val fetchedConversation = conversationRepository.getConversation(
                    conversationId = conversationId,
                    currentUserId = currentUserId
                )
                _loadConversationState.update {
                    LoadConversationState.Success(fetchedConversation)
                }
            } catch (e: Exception) {
                _loadConversationState.update {
                    LoadConversationState.Error(msg = "Error loading conversation: "+ e.message)
                }
            }
        }
    }

    fun disconnect() {
        webSocketClient.closeSession()
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

sealed interface LoadConversationState {
    data object Loading: LoadConversationState
    data class Success(val conversation: Conversation): LoadConversationState
    data class Error(val msg: String): LoadConversationState
}

sealed interface LoadMessagesState {
    data object Loading: LoadMessagesState
    data class Success(val messageList: List<ChatMessage>): LoadMessagesState
    data class Error(val msg: String): LoadMessagesState
}