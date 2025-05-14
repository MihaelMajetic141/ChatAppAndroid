package hr.tvz.android.chatapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.model.Conversation
import hr.tvz.android.chatapp.model.dto.ContactDTO
import hr.tvz.android.chatapp.network.repository.ContactRepository
import hr.tvz.android.chatapp.network.repository.ConversationRepository
import hr.tvz.android.chatapp.network.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val contactRepository: ContactRepository,
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _loadContactsViewState = MutableStateFlow<LoadContactsViewState>(LoadContactsViewState.Loading)
    val loadContactsViewState = _loadContactsViewState.asStateFlow()

    private val _createContactViewState = MutableStateFlow<CreateContactViewState>(CreateContactViewState.Loading)
    val createContactViewState = _createContactViewState.asStateFlow()

    private val _createGroupViewState = MutableStateFlow<CreateGroupViewState>(CreateGroupViewState.Loading)
    val createGroupViewState = _createGroupViewState.asStateFlow()

    private val _groupMembers = MutableStateFlow<List<ContactDTO>>(emptyList())
    val groupMembers = _groupMembers.asStateFlow()

    private val _groupName = MutableStateFlow<String>("")
    val groupName = _groupName.asStateFlow()

    private val _groupImageUri = MutableStateFlow<Uri>(Uri.EMPTY)
    val groupImageUri = _groupImageUri.asStateFlow()

    fun getContacts() {
        viewModelScope.launch {
            try {
                _loadContactsViewState.update { LoadContactsViewState.Loading }
                //ToDO: get userId from DataStore
                val contacts = contactRepository.getContacts("userId")
                _loadContactsViewState.update {
                    LoadContactsViewState.Success(contacts)
                }
            } catch (e: Exception) {
                _loadContactsViewState.update {
                    LoadContactsViewState.Error(e.message?: "Error")
                }
            }
        }
    }

    fun getAllUsers() {
        viewModelScope.launch {
            try {
                _loadContactsViewState.update { LoadContactsViewState.Loading }
                val contacts = contactRepository.getAllUsers()
                _loadContactsViewState.update {
                    LoadContactsViewState.Success(contacts)
                }
            } catch (e: Exception) {
                _loadContactsViewState.update {
                    LoadContactsViewState.Error(e.message?: "Error")
                }
            }
        }
    }

    fun newContact(contactId: String, currentUserId: String) {
        viewModelScope.launch {
            try {
                contactRepository.newContact(contactId, currentUserId)
                _createContactViewState.update {
                    CreateContactViewState.Success
                }
            } catch (e: Exception) {
                _loadContactsViewState.update {
                    LoadContactsViewState.Error(e.message?: "Error")
                }
            }
        }
    }

    fun addContacts(contactIdList: List<String>, currentUserEmail: String) {
        viewModelScope.launch {
            try {
                _createContactViewState.update { CreateContactViewState.Loading }
                val currentUserId = contactRepository.getUserByEmail(currentUserEmail).userInfoId
                contactIdList.forEach { contactRepository.newContact(it, currentUserId) }
                _createContactViewState.update {
                    CreateContactViewState.Success
                }
            } catch (e: Exception) {
                _loadContactsViewState.update {
                    LoadContactsViewState.Error(e.message?: "Error")
                }
            }
        }
    }

    fun newGroup(
        groupName: String,
        groupImage: Uri,
        groupMemberIds: List<String>
    ) {
        viewModelScope.launch {
            try {
                _createGroupViewState.update { CreateGroupViewState.Loading }
                val imageFileId = mediaRepository.uploadMedia(groupImage)

                //ToDO: current user is admin
                val conversation = Conversation(
                    name = groupName,
                    imageFileId = imageFileId,
                    memberIds = groupMemberIds,
                    isDirectMessage = false
                )
                conversationRepository.createNewGroupChat(conversation)
                _createGroupViewState.update { CreateGroupViewState.Success }
            } catch (e: Exception) {
                _createGroupViewState.update {
                    CreateGroupViewState.Error(e.message ?: "Error creating group")
                }
            }
        }
    }

    fun createDM(currentUser: String, user2: String) {
        viewModelScope.launch {
            try {
                val newConversation = conversationRepository.createNewDM(currentUser, user2)
            } catch (e: Exception) {
                _loadContactsViewState.update {
                    LoadContactsViewState.Error(e.message?: "Error")
                }
            }
        }
    }

    fun setGroupName(name: String) {
        _groupName.update { name }
    }

    fun setGroupImage(uri: Uri) {
        _groupImageUri.update { uri }
    }

    fun validateGroupName(): Boolean {
        return _groupName.value.isNotEmpty()
    }

    fun validateGroupMembers(): Boolean {
        return _groupMembers.value.size > 1
    }

}

sealed interface LoadContactsViewState {
    data object Loading : LoadContactsViewState
    data class Success(
        val contactList: List<ContactDTO> = emptyList()
    ) : LoadContactsViewState
    data class Error(val message: String) : LoadContactsViewState
}

sealed interface CreateContactViewState {
    data object Loading : CreateContactViewState
    data object Success : CreateContactViewState
    data class Error(val message: String) : CreateContactViewState
}

sealed interface CreateGroupViewState {
    data object Loading : CreateGroupViewState
    data object Success : CreateGroupViewState //ToDo: add conversation id as parameter
    data class Error(val message: String) : CreateGroupViewState
}