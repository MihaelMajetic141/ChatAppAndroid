package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.data.dto.ConversationDTO
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.view.components.TopBarWithBackArrow
import hr.tvz.android.chatapp.view.components.TopBarWithConversationsSearch
import hr.tvz.android.chatapp.viewmodel.AuthViewModel
import hr.tvz.android.chatapp.viewmodel.ChatListViewModel
import hr.tvz.android.chatapp.viewmodel.ChatListViewState
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel

@Composable
fun ChatListScreen(
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
    authViewModel: AuthViewModel,
    chatListViewModel: ChatListViewModel = hiltViewModel(),
) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    if (!isUserLoggedIn) {
        LoggedOutScreen(navController, topAppBarState)
    } else {
        LoggedInScreen(chatListViewModel, navController)
    }
}

@Composable
fun LoggedOutScreen(
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
    newConversationViewModel: NewConversationViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TopBarWithBackArrow(
                title = "ChatApp",
                topAppBarState = topAppBarState,
                navController = navController
            )
            Button(
                modifier = Modifier,
                onClick = { navController.navigate(Routes.Login.route) }
            ) {
                Text("Login")
            }
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
    val userId by dataStore.userId.collectAsState(initial = "")
    val username by dataStore.userName.collectAsState(initial = "")
    val email by dataStore.userEmail.collectAsState(initial = "")
    val accessToken by dataStore.accessToken.collectAsState(initial = "")
    val chatListViewState by chatListViewModel.viewState.collectAsState()
    val showSearchBar = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(key1 = userId != "") {
        chatListViewModel.fetchConversationsByUserId(userId ?: "")
    }

    when (val viewState = chatListViewState) {
        is ChatListViewState.Loading -> {
            Text(text = "Loading...")
        }
        is ChatListViewState.Success -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    TopBarWithConversationsSearch(
                        title = "ChatApp",
                        showSearchBar = showSearchBar,
                        searchQuery = searchQuery,
                        conversationList = viewState.conversationDTOList,
                        navController = navController
                    )
                    if (viewState.conversationDTOList.isNotEmpty()) {
                        ChatList(
                            conversationDTOs = viewState.conversationDTOList,
                            navController = navController,
                            modifier = Modifier
                        )
                    } else {
                        EmptyChatList(
                            navController = navController,
                            modifier = Modifier
                        )
                    }
                }
                FloatingActionButton(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.BottomEnd)
                        .offset((-25).dp, (-75).dp),
                    onClick = {
                        navController.navigate(Routes.NewConversation.route)
                    },
                ) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, "New Chat")
                }
            }
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
fun EmptyChatList(
    navController: NavController,
    modifier: Modifier
) {
    Box(contentAlignment = Alignment.Center) {
        TextButton(
            onClick = { navController.navigate(Routes.NewConversation.route) }
        ) {
            Text("No Conversations yet..")
        }
    }
}

@Composable
fun ChatList(
    conversationDTOs: List<ConversationDTO>,
    navController: NavController,
    modifier: Modifier
) {
    LazyColumn {
        items(conversationDTOs) { conversation ->
            ChatListItem(
                conversationDto = conversation,
                navController = navController,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ChatListItem(
    conversationDto: ConversationDTO,
    navController: NavController,
    modifier: Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate(Routes.Chat.route + "/${conversationDto.id}") }
    ) {
        Row {
            SubcomposeAsyncImage(
                model = "${BuildConfig.SERVER_IP}/api/media/${conversationDto.imageFileId}",
                contentDescription = "Conversation image"
            )
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