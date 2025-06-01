package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.view.components.ContactListItem
import hr.tvz.android.chatapp.view.components.TopBarWithBackArrow
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel


@Composable
fun CreateContactScreen(
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val currentUserId by dataStore.userId.collectAsState(initial = "")
    val loadContactsViewState by newConversationViewModel.loadContactsViewState.collectAsState()
    val selectedContacts by newConversationViewModel.selectedContacts.collectAsState()

    TopBarWithBackArrow(
        topAppBarState,
        navController,
        title = "Create Contact"
    )

    //ToDo: Create contact form, for now just all user's list with checkboxes
    LaunchedEffect(Unit) {
        newConversationViewModel.getAllUsers()
    }

    when(val viewState = loadContactsViewState) {
        is LoadContactsViewState.Error -> {
            Text(text = viewState.message)
        }

        LoadContactsViewState.Loading -> {
            CircularProgressIndicator()
        }

        is LoadContactsViewState.Success -> {
            LazyColumn {
                item {
                    HorizontalDivider(Modifier.height(100.dp))
                }
                viewState.contactList.listIterator().forEach { contact ->
                    item {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    if (!selectedContacts.contains(contact)) {
                                        newConversationViewModel.addContactToList(contact.userInfoId)
                                    } else {
                                        newConversationViewModel.removeContactFromList(contact.userInfoId)
                                    }
                                }
                        ) {
                            SubcomposeAsyncImage(
                                model = "${BuildConfig.SERVER_IP}/api/media/${contact.imageFileId}",
                                contentDescription = "Conversation image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                            Column {
                                Text(
                                    text = contact.username,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                )
                                Text(
                                    text = contact.status ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
                item {
                    FloatingActionButton(
                        modifier = Modifier.size(50.dp),
                        onClick = {
                            newConversationViewModel.addContacts(
                                contactIdList = selectedContacts.map { it.userInfoId },
                                currentUserId = currentUserId ?: ""
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Create group button",
                        )
                    }
                }


//                items(viewState.contactList) { contact ->
//                    val isSelected = selectedContacts.contains(contact)
//                    ContactListItem(
//                        contactDTO = contact,
//                        subHeader = contact.status ?: "",
//                        isSelected = isSelected,
//                        onClick = { },
//                        onLongClick = { },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                if (isSelected) {
//                                    newConversationViewModel.addContactToList(contact.userInfoId)
//                                } else {
//                                    newConversationViewModel.removeContactFromList(contact.userInfoId)
//                                }
//                            }
//                    )
//                }


            }
        }
    }
}