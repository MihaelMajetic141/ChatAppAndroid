package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.model.dto.ConversationDTO
import hr.tvz.android.chatapp.model.routes.Routes
import hr.tvz.android.chatapp.network.DataStoreManager
import hr.tvz.android.chatapp.viewmodel.AuthState
import hr.tvz.android.chatapp.viewmodel.AuthViewModel
import hr.tvz.android.chatapp.viewmodel.ChatListViewModel
import hr.tvz.android.chatapp.viewmodel.ChatListViewState

@Composable
fun ChatListScreen(
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
    authViewModel: AuthViewModel,
    chatListViewModel: ChatListViewModel = hiltViewModel(),
) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

    if (!isUserLoggedIn) {
        LoggedOutScreen(navController)
    } else {
        LoggedInScreen(
            chatListViewModel,
            navController
        )
    }
}

@Composable
fun LoggedOutScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.size(58.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier,
            onClick = { navController.navigate(Routes.Login.route) }
        ) {
            Text("Login")
        }
    }
}

@Composable
fun LoggedInScreen(
    chatListViewModel: ChatListViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val username by dataStore.userName.collectAsState(initial = "")
    val email by dataStore.userEmail.collectAsState(initial = "")
    val accessToken by dataStore.accessToken.collectAsState(initial = "")
    val chatListViewState by chatListViewModel.viewState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        chatListViewModel.fetchConversationsByUserId("userId") // ToDo: Get userId from auth
    }

    when (val viewState = chatListViewState) {
        is ChatListViewState.Loading -> {
            Text(text = "Loading...")
        }
        is ChatListViewState.Success -> {
            ChatList(
                viewState.conversationDTOList,
                navController
            )
            NewConversationButton(
                onClick = { navController.navigate(Routes.NewConversation.route) },
                modifier = Modifier.padding(8.dp)
            )
        }
        is ChatListViewState.Empty -> {
            Text(text = viewState.message)
        }
        is ChatListViewState.Error -> {
            Text(text = viewState.message)
        }
    }
}

@Composable
fun ChatList(
    conversationDTOs: List<ConversationDTO>,
    navController: NavController
) {

    // ToDo: Check if empty.

    LazyColumn {
        items(conversationDTOs) { chatGroup ->
            ChatListItem(navController, chatGroup)
        }
    }
}

@Composable
fun ChatListItem(
    navController: NavController,
    conversationDto: ConversationDTO
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("/chat/{$conversationDto.id}") }
    ) {
        Row {
            // ToDo: Image
            Column() {
                Text(
                    text = conversationDto.name,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = conversationDto.lastMessage ?: "",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun NewConversationButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        modifier = modifier.size(80.dp),
        onClick = { onClick() }
    ) {
        Icon(
            imageVector = Icons.Rounded.AddCircle,
            contentDescription = "New conversation button"
        )
    }
}