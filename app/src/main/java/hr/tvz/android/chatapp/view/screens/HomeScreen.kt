package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import hr.tvz.android.chatapp.BottomNavItem
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.R
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
import java.time.Instant

@Composable
fun ChatListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    chatListViewModel: ChatListViewModel = hiltViewModel(),
) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    if (!isUserLoggedIn) {
        LoggedOutScreen(
            navController = navController,
        )
    } else {
        LoggedInScreen(
            chatListViewModel = chatListViewModel,
            authViewModel = authViewModel,
            navController = navController)
    }
}

@Composable
fun LoggedOutScreen(
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBarWithBackArrow(
            title = "ChatApp",
            navController = navController
        )
        Button(
            modifier = Modifier.size(100.dp),
            onClick = { navController.navigate(Routes.Login.route) }
        ) {
            Text("Login")
        }
    }
}

@Composable
fun LoggedInScreen(
    chatListViewModel: ChatListViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val userId by dataStore.userId.collectAsState(initial = "")
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
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
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        EmptyChatList(
                            navController = rememberNavController(),
                            modifier = Modifier
                        )
                    }
                    BottomAppBar {
                        BottomNavItem.items.listIterator().forEach { item ->
                            NavigationBarItem(
                                selected = navController.currentBackStackEntryAsState()
                                    .value?.destination?.route == item.route,
                                onClick = {
                                    navController.navigate(item.route) { popUpTo(0) }
                                },
                                icon = {
                                    Image(
                                        painter = painterResource(item.icon),
                                        contentDescription = item.label,
                                        modifier = Modifier.size(33.dp)
                                    )
                                },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.BottomEnd)
                        .offset((-10).dp, (-95).dp),
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
            onClick = { navController.navigate(Routes.NewConversation.route) },
            modifier = Modifier.size(100.dp)
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
    LazyColumn(modifier = modifier) {
        items(conversationDTOs) { conversation ->
            ChatListItem(
                conversationDto = conversation,
                navController = navController,
                modifier = Modifier
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .height(48.dp)
            .fillMaxWidth()
            .clickable { navController.navigate(Routes.Chat.route + "/${conversationDto.id}") }
    ) {
        if (conversationDto.imageFileId == "" || conversationDto.imageFileId == null) {
            Image(
                painter = painterResource(R.drawable.user),
                contentDescription = "Conversation icon",
                modifier = Modifier
            )
        } else {
            SubcomposeAsyncImage(
                model = "${BuildConfig.SERVER_IP}/api/media/download/${conversationDto.imageFileId}",
                contentDescription = "Conversation image",
                modifier = Modifier
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = conversationDto.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
            )
            Text(
                text = conversationDto.lastMessage ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
        Text(
            text = "24/07/25",
            maxLines = 1,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    val showSearchBar = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val conversationDtoList = listOf(
        ConversationDTO(
            id = "conversationId1",
            name = "convoName1",
            lastMessage = "Last message",
            lastMessageDate = Instant.now(),
            imageFileId = "",
        ),
        ConversationDTO(
            id = "conversationId2",
            name = "convoName2",
            lastMessage = "Last message Last message Last messageLast messageLast " +
                    "messageLast messageLast messageLast messageLast message",
            lastMessageDate = Instant.now(),
            imageFileId = "",
        ),
        ConversationDTO(
            id = "conversationId3",
            name = "convoName3",
            lastMessage = "Last message",
            lastMessageDate = Instant.now(),
            imageFileId = "",
        ),
        ConversationDTO(
            id = "conversationId4",
            name = "convoName4",
            lastMessage = "Last message",
            lastMessageDate = Instant.now(),
            imageFileId = "",
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopBarWithConversationsSearch(
                title = "ChatApp",
                showSearchBar = showSearchBar,
                searchQuery = searchQuery,
                conversationList = conversationDtoList,
                navController = navController
            )
            if (conversationDtoList.isNotEmpty()) {
                ChatList(
                    conversationDTOs = conversationDtoList,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                EmptyChatList(
                    navController = rememberNavController(),
                    modifier = Modifier
                )
            }
            BottomAppBar {
                BottomNavItem.items.listIterator().forEach { item ->
                    NavigationBarItem(
                        selected = navController.currentBackStackEntryAsState()
                            .value?.destination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) { popUpTo(0) }
                        },
                        icon = {
                            Image(
                                painter = painterResource(item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(33.dp)
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.BottomEnd)
                .offset((-10).dp, (-95).dp),
            onClick = {
                navController.navigate(Routes.NewConversation.route)
            },
        ) {
            Icon(Icons.AutoMirrored.Filled.PlaylistAdd, "New Chat")
        }
    }
}