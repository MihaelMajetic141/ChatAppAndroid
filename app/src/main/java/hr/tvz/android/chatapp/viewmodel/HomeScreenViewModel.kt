package hr.tvz.android.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.data.dto.ConversationDTO
import hr.tvz.android.chatapp.network.repositories.ConversationRepository
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
                    ChatListViewState.Empty("No conversations.")
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

}

sealed interface ChatListViewState {
    data object Loading : ChatListViewState
    data class Success(val conversationDTOList: List<ConversationDTO>) : ChatListViewState
    data class Empty(val message: String) : ChatListViewState
    data class Error(val message: String) : ChatListViewState
}
