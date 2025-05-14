package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.model.Conversation
import hr.tvz.android.chatapp.model.dto.ContactDTO
import hr.tvz.android.chatapp.model.routes.Routes
import hr.tvz.android.chatapp.network.DataStoreManager
import hr.tvz.android.chatapp.view.components.ContactListItem
import hr.tvz.android.chatapp.view.components.TopBarWithBackArrow
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState

@Composable
fun NewConversationScreen(
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val username by dataStore.userName.collectAsState(initial = "")
    val email by dataStore.userEmail.collectAsState(initial = "")
    val accessToken by dataStore.accessToken.collectAsState(initial = "")
    val newConversationViewState by newConversationViewModel.loadContactsViewState.collectAsState()

    TopBarWithBackArrow(
        topAppBarState = topAppBarState,
        navController = navController,
        title = "Create Group"
    )

    LaunchedEffect(Unit) {
        newConversationViewModel.getContacts()
    }

    when(val viewState = newConversationViewState){
        is LoadContactsViewState.Loading -> {
            Text(text = "Loading...")
        }

        // ToDo: TopBar & SearchBar

        is LoadContactsViewState.Success -> {
            CreateConversationLazyColumn(
                contactList = viewState.contactList,
                navController = navController
            )
        }
        is LoadContactsViewState.Error -> {
            Text(text = viewState.message)
        }
    }
}


@Composable
fun CreateConversationLazyColumn(
    contactList: List<ContactDTO>,
    navController: NavController,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                IconButton(onClick = { navController.navigate(Routes.CreateGroup.route) }) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = "New group",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
                Text(text = "New Group", style = MaterialTheme.typography.headlineSmall)
            }
        }
        item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                IconButton(onClick = { navController.navigate("/createDM") }) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(text = "New Contact", style = MaterialTheme.typography.headlineSmall)
            }
        }
        item {
            HorizontalDivider()
            Text(
                text = "Contacts",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider()
        }
        items(contactList) { contact ->
            ContactListItem(
                contactDTO = contact,
                subHeader = contact.status,
                isSelected = false,
                onClick = { navController.navigate("chat/${contact.userInfoId}") },
                onLongClick = {  }
            )
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
//    SubcomposeAsyncImage(
//        model = conversation.imageFileId,
//        contentDescription = "Conversation image",
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(8.dp)
//    )

    Text(
        text = conversation.name ?: "Unknown",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable { onClick() }
    )
}