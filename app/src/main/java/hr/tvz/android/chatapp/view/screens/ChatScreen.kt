package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.tvz.android.chatapp.model.ChatMessage
import hr.tvz.android.chatapp.model.Conversation
import hr.tvz.android.chatapp.network.DataStoreManager
import hr.tvz.android.chatapp.viewmodel.ChatViewModel
import hr.tvz.android.chatapp.viewmodel.ConnectionState
import java.time.Instant
import java.util.Date


@Composable
fun ChatScreen(
    chatId: String,
    navController: NavController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val username by dataStore.userName.collectAsState(initial = "")
    val currentUserId by dataStore.userId.collectAsState(initial = "")
    val messages by chatViewModel.messages.collectAsState()
    val conversation by chatViewModel.conversation.collectAsState()
    val isDirectMessage by chatViewModel.isDirectMessage.collectAsState()
    val connectionState by chatViewModel.connectionState.collectAsState()

    LaunchedEffect(key1 = currentUserId != "") {
        currentUserId?.let { chatViewModel.loadConversation(chatId, it) }
    }
    LaunchedEffect(chatId) {
        chatViewModel.loadMessages(chatId)
    }
    LaunchedEffect(Unit) {
        chatViewModel.startWebSocket()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ConversationHeader(
            conversation = conversation,
            onBackClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .weight(1f)
                .padding(bottom = 8.dp)
        )
        ConnectionStateIndicator(state = connectionState)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true,
            content = {
                items(messages) { message ->
                    ChatMessageItem(message = message)
                }
            }
        )
        MessageInput(onSend = {
            chatMessage -> chatViewModel.sendMessage(chatMessage)
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationHeader(
    conversation: Conversation?,
    onBackClick: () -> Unit,
    modifier: Modifier
) {
    Row {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        // SubcomposeAsyncImage(painter = conversation.imageFileId, contentDescription = "Conversation image")
        Text(
            text = conversation?.name ?: "Unknown",
            modifier = modifier
                .widthIn(max = 200.dp)
                .basicMarquee()
        )
        Spacer(modifier = modifier.weight(1f))
        IconButton(onClick = { /* TODO: show DropDownMenu */ }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.More,
                contentDescription = "Options")
        }

    }
}

@Composable
fun ConnectionStateIndicator(state: ConnectionState) {
    Box(modifier = Modifier.fillMaxWidth()) {
        when (state) {
            ConnectionState.CONNECTING -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 128.dp),
                    color = Color.Gray
                )
            }
            ConnectionState.CONNECTED -> {
                Text(
                    text = "Connected",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ConnectionState.DISCONNECTED -> {
                Text(
                    text = "Disconnected",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is ConnectionState.ERROR -> {
                Text(
                    text = "Error: ${state.exception.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            ConnectionState.DISCONNECTING -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 128.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // ToDo: User image
        // Box with username, message, timestamp, and isSeen icon

        Text(text = "${message.senderId}: ${message.content}")
    }
}

@Composable
fun MessageInput(onSend: (ChatMessage) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            label = { Text("Type a message") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            if (text.isNotBlank()) {
                val message = ChatMessage(
                    id = "",
                    senderId = "senderId",
                    conversationId = "receiverId",
                    content = text,
                    timestamp = Date.from(Instant.now()),
//                    mediaFileType = "",
//                    mediaFileId = "",
//                    replyTo = "",
//                    reactions =
                )
                onSend(message)
                text = ""
            }
        }) {
            Text("Send")
        }
    }
}
