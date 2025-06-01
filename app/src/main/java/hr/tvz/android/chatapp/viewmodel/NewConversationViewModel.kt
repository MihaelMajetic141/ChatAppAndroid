package hr.tvz.android.chatapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.network.repositories.ContactRepository
import hr.tvz.android.chatapp.network.repositories.ConversationRepository
import hr.tvz.android.chatapp.network.repositories.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
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

    private val _selectedContacts = MutableStateFlow<List<ContactDTO>>(emptyList())
    val selectedContacts = _selectedContacts.asStateFlow()

    private val _groupName = MutableStateFlow("")
    val groupName = _groupName.asStateFlow()

    private val _groupImageUri = MutableStateFlow<Uri>(Uri.EMPTY)
    val groupImageUri = _groupImageUri.asStateFlow()

    fun getContacts(currentUserId: String) {
        viewModelScope.launch {
            try {
                _loadContactsViewState.update { LoadContactsViewState.Loading }
                val contacts = contactRepository.getUserContacts(currentUserId)
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

    // ToDo: Delete later
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

    fun addContacts(contactIdList: List<String>, currentUserId: String) {
        viewModelScope.launch {
            try {
                _createContactViewState.update { CreateContactViewState.Loading }
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

    fun createGroup(groupName: String, groupImage: Uri, groupMemberIds: List<String>,
                    adminIds: List<String>) {
        viewModelScope.launch {
            try {
                _createGroupViewState.update { CreateGroupViewState.Loading }
                val imageFileId = mediaRepository.uploadMedia(groupImage)
                val conversation = hr.tvz.android.chatapp.data.model.Conversation(
                    id = "",
                    name = groupName,
                    imageFileId = imageFileId,
                    memberIds = groupMemberIds,
                    isDirectMessage = false,
                    description = "",
                    inviteLink = "",
                    adminIds = adminIds,
                    createdAt = Instant.now(),
                )
                val newConversation = conversationRepository.createNewGroupChat(conversation)
                _createGroupViewState.update {
                    CreateGroupViewState.Success(newConversation.id ?: "")
                }
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

    fun addContactToList(userId: String) {
        viewModelScope.launch {
            val contact = contactRepository.getContactByUserId(userId)
            contact?.let {
                val updatedList = _selectedContacts.value.toMutableList()
                if (!updatedList.any { it.userInfoId == contact.userInfoId }) {
                    updatedList.add(contact)
                    _selectedContacts.value = updatedList
                }
            }
        }
    }
    fun removeContactFromList(contactId: String) {
        val updatedList = _selectedContacts.value.filterNot { it.userInfoId == contactId }
        _selectedContacts.value = updatedList
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
        return _selectedContacts.value.size > 1
    }

}

sealed interface LoadContactsViewState {
    data object Loading : LoadContactsViewState
    data class Success(val contactList: List<ContactDTO>) : LoadContactsViewState
    data class Error(val message: String) : LoadContactsViewState
}

sealed interface CreateContactViewState {
    data object Loading : CreateContactViewState
    data object Success : CreateContactViewState
    data class Error(val message: String) : CreateContactViewState
}

sealed interface CreateGroupViewState {
    data object Loading : CreateGroupViewState
    data class Success(val groupId: String) : CreateGroupViewState
    data class Error(val message: String) : CreateGroupViewState
}