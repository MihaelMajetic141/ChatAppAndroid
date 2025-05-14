package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.model.dto.ContactDTO
import hr.tvz.android.chatapp.network.DataStoreManager
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
    val username by dataStore.userName.collectAsState(initial = "")
    val email by dataStore.userEmail.collectAsState(initial = "")
    val accessToken by dataStore.accessToken.collectAsState(initial = "")
    val loadContactsViewState by newConversationViewModel.loadContactsViewState.collectAsState()
    val selectedContacts = rememberSaveable { mutableStateListOf<ContactDTO>() }

    //ToDo: DataStoreManager

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
                items(viewState.contactList) { contact ->
                    val isSelected = selectedContacts.contains(contact)
                    ContactListItem(
                        contactDTO = contact,
                        subHeader = contact.status,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedContacts.remove(contact)
                            } else {
                                selectedContacts.add(contact)
                            }
                        },
                        onLongClick = { /* Do nothing for now */ }
                    )
                }
            }
        }
    }

    // ToDo: Save contact button
    IconButton(
        modifier = Modifier.size(50.dp),
        onClick = {
            newConversationViewModel.addContacts(
                selectedContacts.map { it.userInfoId },
                email.toString()
            )
        }
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = "Create group button",
        )
    }

}