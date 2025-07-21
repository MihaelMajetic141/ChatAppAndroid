package hr.tvz.android.chatapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.data.dto.ConversationDTO
import hr.tvz.android.chatapp.data.model.Conversation
import hr.tvz.android.chatapp.network.repositories.ContactRepository
import hr.tvz.android.chatapp.network.repositories.ConversationRepository
import hr.tvz.android.chatapp.network.repositories.MediaRepository
import io.ktor.client.statement.bodyAsText
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

    private val _loadContactsViewState = MutableStateFlow<LoadContactsViewState>(
        value = LoadContactsViewState.Loading)
    val loadContactsViewState = _loadContactsViewState.asStateFlow()

    private val _loadConversationsViewState = MutableStateFlow<LoadConversationsViewState>(
        value = LoadConversationsViewState.Loading)
    val loadConversationsViewState = _loadConversationsViewState.asStateFlow()

    private val _createContactViewState = MutableStateFlow<CreateContactViewState>(
        value = CreateContactViewState.Loading)
    val createContactViewState = _createContactViewState.asStateFlow()

    private val _createGroupViewState = MutableStateFlow<CreateGroupViewState>(
        value = CreateGroupViewState.Loading)
    val createGroupViewState = _createGroupViewState.asStateFlow()

    private val _createDMViewState = MutableStateFlow<CreateDMViewState>(
        value = CreateDMViewState.Loading)
    val createDMViewState = _createDMViewState.asStateFlow()

    private val _selectedContacts = MutableStateFlow<List<ContactDTO>>(value = emptyList())
    val selectedContacts = _selectedContacts.asStateFlow()

    private val _groupName = MutableStateFlow(value = "")
    val groupName = _groupName.asStateFlow()

    private val _groupImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
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

    fun addNewContact(contactId: String, currentUserId: String) {
        viewModelScope.launch {
            try {
                contactRepository.addNewContact(contactId, currentUserId)
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

    fun addNewContacts(contactIdList: List<String>, currentUserId: String) {
        viewModelScope.launch {
            try {
                _createContactViewState.update { CreateContactViewState.Loading }
                contactRepository.addNewContacts(
                    contactIdList = contactIdList,
                    currentUserId = currentUserId
                )
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

    fun createGroup(
        groupName: String,
        groupImage: Uri?,
        groupMemberIds: List<String>,
        adminIds: List<String>
    ) {
        viewModelScope.launch {
            try {
                _createGroupViewState.update { CreateGroupViewState.Loading }
                val imageMetadata = mediaRepository.uploadMedia(groupImage ?: Uri.EMPTY)
                val conversation = Conversation(
                    name = groupName,
                    description = "",
                    imageFileId = imageMetadata.id,
                    isDirectMessage = false,
                    adminIds = adminIds,
                    memberIds = groupMemberIds,
                    createdAt = Instant.now(),
                )
                Log.d("convBeforePost", conversation.toString())
                val newConversation = conversationRepository.createNewGroupChat(conversation)
                Log.d("tag", newConversation.toString())
                _createGroupViewState.update {
                    CreateGroupViewState.Success(groupId = newConversation.id ?: "")
                }
            } catch (e: Exception) {
                _createGroupViewState.update {
                    CreateGroupViewState.Error(e.message ?: "Error creating group")
                }
            }
        }
    }

    fun createDM(currentUserId: String, user2: String) {
        viewModelScope.launch {
            try {
                val newConversation = conversationRepository.createNewDM(currentUserId, user2)
                _createDMViewState.update {
                    CreateDMViewState.Success(conversationId = newConversation.id ?: "")
                }
            } catch (e: Exception) {
                _createDMViewState.update {
                    CreateDMViewState.Error(e.message?: "Error")
                }
            }
        }
    }

    fun addContactToList(contact: ContactDTO) {
        _selectedContacts.value = _selectedContacts.value + contact

//        viewModelScope.launch {
//            val contact = contactRepository.getContactByUserId(userId)
//            contact?.let {
//                val updatedList = _selectedContacts.value.toMutableList()
//                if (!updatedList.any { it.userInfoId == contact.userInfoId }) {
//                    updatedList.add(contact)
//                    _selectedContacts.value = updatedList
//                }
//            }
//        }
    }
    fun removeContactFromList(contact: ContactDTO) {
        _selectedContacts.value = _selectedContacts.value.filterNot { it == contact }
    }

    fun toggleSelectedContact(contact: ContactDTO) {
        if (_selectedContacts.value.contains(contact)) {
            removeContactFromList(contact)
        } else {
            addContactToList(contact)
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
        return _selectedContacts.value.size > 1
    }

}

sealed interface LoadContactsViewState {
    data object Loading : LoadContactsViewState
    data class Success(val contactList: List<ContactDTO>) : LoadContactsViewState
    data class Error(val message: String) : LoadContactsViewState
}

sealed interface LoadConversationsViewState {
    data object Loading: LoadConversationsViewState
    data class Success(val conversationList: List<ConversationDTO>): LoadConversationsViewState
    data class Error(val message: String): LoadConversationsViewState
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

sealed interface CreateDMViewState {
    data object Loading : CreateDMViewState
    data class Success(val conversationId: String) : CreateDMViewState
    data class Error(val message: String) : CreateDMViewState
}