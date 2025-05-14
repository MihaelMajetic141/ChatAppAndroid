package hr.tvz.android.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.model.dto.ConversationDTO
import hr.tvz.android.chatapp.network.repository.ConversationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<ChatListViewState>(ChatListViewState.Loading)
    val viewState: StateFlow<ChatListViewState> = _viewState

    fun fetchConversationsByUserId(userId: String) = viewModelScope.launch {
        try {
            val conversationDTOs = conversationRepository.getAllConversationsByUserId(userId)
            if (conversationDTOs.isEmpty()) {
                _viewState.update {
                    ChatListViewState.Empty("No chat groups available.")
                }
            } else {
                _viewState.update { ChatListViewState.Success(conversationDTOs) }
            }
        } catch (e: Exception) {
            _viewState.update {
                ChatListViewState.Error("Failed to fetch chat groups: ${e.message}")
            }
        }
    }

//    suspend fun createNewConversation(
//        conversation: Conversation,
//        // accessToken: String,
//    ) = viewModelScope.launch {
//        try {
//            val newConversation = conversationRepository.createNewConversation(conversation)
//            if (newConversation != null) {
//                _viewState.update {
//                    val current = (it as? ChatListViewState.Success)
//                        ?.conversationDtoList ?: emptyList()
//                    val newList = conversationRepository.fetchAllConversationsByUserId(userId);
//                    ChatListViewState.Success(conversationDtoList = current + newConversation)
//                }
//            } else {
//                _viewState.update {
//                    ChatListViewState.Error("Failed to create new conversation.")
//                }
//            }
//        } catch (e: Exception) {
//            _viewState.update {
//                ChatListViewState.Error("Failed to create new conversation: ${e.message}")
//            }
//        }
//    }


}

sealed interface ChatListViewState {
    data object Loading : ChatListViewState
    data class Success(val conversationDTOList: List<ConversationDTO>) : ChatListViewState
    data class Empty(val message: String) : ChatListViewState
    data class Error(val message: String) : ChatListViewState
}
