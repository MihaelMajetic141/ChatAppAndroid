package hr.tvz.android.chatapp.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.VerticalDivider
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
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.view.components.ContactListItem
import hr.tvz.android.chatapp.view.components.TopBarWithBackArrow
import hr.tvz.android.chatapp.view.components.TopBarWithSearchAndBackArrow
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NewConversationScreen(
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val currentUserId by dataStore.userId.collectAsState(initial = "")
    val newConversationViewState by newConversationViewModel.loadContactsViewState.collectAsState()

    LaunchedEffect(currentUserId != "") {
        currentUserId?.let { newConversationViewModel.getContacts(it) }
    }

    // ToDo: TopBar with SearchBar
//    TopBarWithSearchAndBackArrow(
//        topAppBarState = topAppBarState,
//        navController = navController,
//        title = "New Conversation"
//    )
    when(val viewState = newConversationViewState){
        is LoadContactsViewState.Loading -> {
            Text(text = "Loading...")
        }

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
            HorizontalDivider(Modifier.height(100.dp))
        }
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
                IconButton(onClick = { navController.navigate(Routes.CreateContact.route) }) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "New direct message",
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
        if (contactList.isNotEmpty()) {
            items(contactList) { contact ->
                ContactListItem(
                    contactDTO = contact,
                    subHeader = contact.status ?: "",
                    isSelected = false,
                    onClick = { navController.navigate("${Routes.Chat.route}/${contact.userInfoId}") }, // ToDo: conversationId
                    onLongClick = { },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: hr.tvz.android.chatapp.data.model.Conversation,
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