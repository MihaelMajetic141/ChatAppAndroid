package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.tvz.android.chatapp.BottomNavItem
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.data.model.Conversation
import hr.tvz.android.chatapp.view.components.ContactListItem
import hr.tvz.android.chatapp.viewmodel.CreateDMViewState
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel

@Composable
fun NewConversationScreen(
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val currentUserId by dataStore.userId.collectAsState(initial = "")
    val loadConversationsViewState by newConversationViewModel.loadConversationsViewState.collectAsState()
    val loadContactsViewState by newConversationViewModel.loadContactsViewState.collectAsState()
    val showSearchBar = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(currentUserId != "") {
        newConversationViewModel.getContacts(currentUserId ?: "")
    }

    when(val viewState = loadContactsViewState){
        is LoadContactsViewState.Loading -> {
            Text(text = "Loading...")
        }

        is LoadContactsViewState.Success -> {
            Column(Modifier.fillMaxSize()) {
                TopBarWithContactSearch(
                    showSearchBar = showSearchBar,
                    searchQuery = searchQuery,
                    contacts = viewState.contactList,
                    navController = navController
                )
                CreateConversationLazyColumn(
                    contactList = viewState.contactList,
                    currentUserId = currentUserId ?: "",
                    newConversationViewModel = newConversationViewModel,
                    navController = navController
                )
            }
        }
        is LoadContactsViewState.Error -> {
            Text(text = viewState.message)
        }
    }
}


@Composable
fun CreateConversationLazyColumn(
    contactList: List<ContactDTO>,
    currentUserId: String,
    newConversationViewModel: NewConversationViewModel,
    navController: NavController,
) {
    val createDMViewState by newConversationViewModel.createDMViewState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                ) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                ) {
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
                Text(
                    text = "Contacts",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
                HorizontalDivider()
            }
            if (contactList.isNotEmpty()) {
                contactList.forEach { contact ->
                    item {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                val conversationId = newConversationViewModel.createDM(
                                    currentUserId = currentUserId,
                                    user2 = contact.userInfoId
                                )

                                when (createDMViewState) {
                                    is CreateDMViewState.Success -> {
                                        navController
                                            .navigate("${Routes.Chat.route}/$conversationId")
                                    }
                                    is CreateDMViewState.Loading -> {}
                                    is CreateDMViewState.Error -> {}
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Column(Modifier.padding(horizontal = 10.dp)) {
                                Text(
                                    text = contact.username,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                Text(
                                    text = contact.status ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            } else {
                item { Text(text = "Contact list is empty") }
            }
        }
    }
}

@Preview
@Composable
fun NewConversationScreenPreview() {
    val contacts = remember {
        mutableStateListOf<String>().apply {
            for (i in 0..5) {
                add("Contact $i")
            }
        }
    }

    Column(Modifier.fillMaxSize().background(color = Color.White)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                ) {
                    IconButton(onClick = {}) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                ) {
                    IconButton(onClick = {}) {
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
                Text(
                    text = "Contacts",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
                HorizontalDivider()
            }

            contacts.forEach() { contact ->
                item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Column(Modifier.padding(horizontal = 10.dp)) {
                            Text(
                                text = contact,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                            )
                            Text(
                                text = "Status",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }


        }


    }
}