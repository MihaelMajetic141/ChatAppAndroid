package hr.tvz.android.chatapp.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FollowTheSigns
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.R
import hr.tvz.android.chatapp.data.model.ChatMessage
import hr.tvz.android.chatapp.data.model.Conversation
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.viewmodel.ChatViewModel
import hr.tvz.android.chatapp.viewmodel.ConnectionState
import hr.tvz.android.chatapp.viewmodel.LoadConversationState
import hr.tvz.android.chatapp.viewmodel.LoadMessagesState
import java.time.Instant


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatScreen(
    chatId: String,
    navController: NavController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val currentUserId by dataStore.userId.collectAsState(initial = "")
    val currentUserName by dataStore.userName.collectAsState(initial = "")
    val loadConversationState by chatViewModel.loadConversationState.collectAsState()
    val loadMessagesState by chatViewModel.loadMessagesState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                chatViewModel.connectToChat(conversationId = chatId)
            } else if (event == Lifecycle.Event.ON_STOP) {
                chatViewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = currentUserId != "") {
        chatViewModel.loadConversation(
            conversationId = chatId,
            currentUserId = currentUserId!!
        )
    }
    LaunchedEffect(key1 = currentUserId != "") {
        chatViewModel.loadMessages(conversationId = chatId)
    }


    when (val viewState = loadConversationState) {
        is LoadConversationState.Error -> {}
        is LoadConversationState.Loading -> {}
        is LoadConversationState.Success -> {
            when (val messagesState = loadMessagesState) {
                is LoadMessagesState.Error -> {}
                is LoadMessagesState.Loading -> {}
                is LoadMessagesState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TopBarChatScreen(
                            conversation = viewState.conversation,
                            navController = navController
                        )
                        MessagesLazyColumn(
                            messagesList = messagesState.messageList,
                            currentUserId = currentUserId!!,
                            modifier = Modifier.weight(1f)
                        )
                        MessageInput(
                            conversationId = viewState.conversation.id ?: "",
                            currentUserId = currentUserId ?: "",
                            currentUserName = currentUserName ?: "",
                            onSend = { chatMessage ->
                                chatViewModel.sendMessage(chatMessage)
                            },
                            // ToDo:
                            onOpenEmojis = {},
                            onAddAttachment = {},
                            onOpenCamera = {},
                            modifier = Modifier
                        )
                    }
                }
            }
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
fun MessagesLazyColumn(
    messagesList: List<ChatMessage>,
    currentUserId: String,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        reverseLayout = true,
        content = {
            items(messagesList) { message ->
                ChatMessageItem(
                    message = message,
                    currentUserId = currentUserId,
                    modifier = Modifier
                )
            }
        }
    )
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    currentUserId: String,
    modifier: Modifier
) {
    val isOwnMessage = (message.senderId == currentUserId)
    val primaryColor: Color = MaterialTheme.colorScheme.primary
    val secondaryColor: Color = MaterialTheme.colorScheme.secondary
    Box(
        contentAlignment = if (isOwnMessage) {
            Alignment.CenterEnd
        } else Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Column(
            modifier = modifier
                .width(200.dp)
                .drawBehind {
                    val cornerRadius = 10.dp.toPx()
                    val triangleHeight = 10.dp.toPx()
                    val triangleWidth = 25.dp.toPx()
                    val trianglePath = Path().apply {
                        if (isOwnMessage) {
                            moveTo(
                                x = size.width,
                                y = size.height - cornerRadius
                            )
                            lineTo(
                                x = size.width,
                                y = size.height + triangleHeight
                            )
                            lineTo(
                                x = size.width - triangleWidth,
                                y = size.height - cornerRadius
                            )
                            close()
                        } else {
                            moveTo(
                                x = 0f,
                                y = size.height - cornerRadius
                            )
                            lineTo(
                                x = 0f,
                                y = size.height + triangleHeight
                            )
                            lineTo(
                                x = triangleWidth,
                                y = size.height - cornerRadius
                            )
                            close()
                        }
                    }
                    drawPath(
                        path = trianglePath,
                        color = if (isOwnMessage) primaryColor
                        else secondaryColor
                    )
                }
                .background(
                    color = if (isOwnMessage) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.senderId ?: "sender username",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = message.content ?: "",
                color = Color.White
            )
            Text(
                text = message.timestamp.toString(),
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
fun MessageInput(
    conversationId: String,
    currentUserId: String,
    currentUserName: String,
    onSend: (ChatMessage) -> Unit,
    onOpenEmojis: () -> Unit,
    onAddAttachment: () -> Unit,
    onOpenCamera: () -> Unit,
    modifier: Modifier
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            label = { Text("Message") },
            shape = RoundedCornerShape(24.dp),
            leadingIcon = @Composable {
                IconButton(
                    onClick = { onOpenEmojis() }
                ) {
                    Icon(
                        imageVector = Icons.Default.InsertEmoticon,
                        contentDescription = "")
                }
            },
            trailingIcon = @Composable {
                Row {
                    IconButton(
                        onClick = { onAddAttachment() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = ""
                        )
                    }
                    IconButton(
                        onClick = { onOpenCamera() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = ""
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .clickable(
                    onClick = {
                        if (text.isNotBlank()) {
                            val message = ChatMessage(
                                senderId = currentUserId,
                                username = currentUserName,
                                conversationId = conversationId,
                                content = text,
                                timestamp = Instant.now()
                            )
                            onSend(message)
                            text = ""
                        }
                    }
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send message",
            )
        }
    }
}

@Composable
fun TopBarChatScreen(
    conversation: Conversation,
    navController: NavController
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Navigate back"
            )
        }
        IconButton(
            onClick = { /* ToDo: Open image popUp */ },
            shape = CircleShape,
            modifier = Modifier.fillMaxHeight()
        ) {
            if (conversation.imageFileId != null && conversation.imageFileId != "") {
                SubcomposeAsyncImage(
                    model = "${BuildConfig.SERVER_IP}/api/media/download/${conversation.imageFileId}",
                    contentDescription = "Conversation image",
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(3.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User icon"
                )
            }
        }
        Text(
            text = conversation.name,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
                .basicMarquee(
                    initialDelayMillis = 5000,
                    repeatDelayMillis = 3000,
                    iterations = 2
                )
        )

        IconButton(
            onClick = { /* ToDo: Implement message search function */ }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Options"
            )
        }
        IconButton(
            onClick = { /* TODO: show DropDownMenu */ }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options"
            )
        }
    }
    HorizontalDivider()
}

@Preview
@Composable
fun ChatScreenPreview() {

    val currentUserId = "currentUserId"
    val conversation = Conversation(
        name = "testGroup",
        imageFileId = "", // 68790ff16e63990fb37a9cc8
        isDirectMessage = false,
        inviteLink = "",
        adminIds = listOf("6829ee6f9c805e652dd3e32c"),
        memberIds = listOf("6829ee6f9c805e652dd3e32c", "683adccdb3000443d259e27e"),
        createdAt = Instant.now(),
    )
    val messagesList = listOf(
        ChatMessage(
            senderId = "currentUserId",
            username = "Username",
            conversationId = "conversationId",
            content = "Message 1",
            timestamp = Instant.now()
        ),
        ChatMessage(
            senderId = "currentUserId",
            username = "Username",
            conversationId = "conversationId",
            content = "Message 2 Message 2 Message 2 Message 2 Message 2 Message 2 Message 2" +
                    "Message 2 Message 2 Message 2 Message 2 Message 2 Message 2 Message 2 ",
            timestamp = Instant.now()
        ),
        ChatMessage(
            senderId = "otherUserId",
            username = "Username",
            conversationId = "conversationId",
            content = "Message 1",
            timestamp = Instant.now()
        ),
        ChatMessage(
            senderId = "otherUserId",
            username = "Username",
            conversationId = "conversationId",
            content = "Message2Message2Message2Message2Message2Message2Message2" +
                    "Message2Message2Message2essage2essag2Mesessa2",
            timestamp = Instant.now()
        ),
        ChatMessage(
            senderId = "currentUserId",
            username = "Username",
            conversationId = "conversationId",
            content = "Message 2 Message 2 Message 2 Message 2 Message 2 Message 2 Message 2" +
                    "Message 2 Message 2 Message 2 Message 2 Message 2 Message 2 Message 2 ",
            timestamp = Instant.now()
        ),
        ChatMessage(
            senderId = "otherUserId",
            username = "Username",
            conversationId = "conversationId",
            content = "Message 1",
            timestamp = Instant.now()
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
            IconButton(
                onClick = { /* ToDo: Open image popUp */ },
                shape = CircleShape,
                modifier = Modifier.fillMaxHeight()
            ) {
                if (conversation.imageFileId != null && conversation.imageFileId != "") {
                    SubcomposeAsyncImage(
                        model = "${BuildConfig.SERVER_IP}/api/media/download/${conversation.imageFileId}",
                        contentDescription = "Conversation image",
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(3.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "User icon"
                    )
                }
            }
            Text(
                text = conversation.name,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
                    .basicMarquee(
                        initialDelayMillis = 5000,
                        repeatDelayMillis = 3000,
                        iterations = 2
                    )
            )

            IconButton(
                onClick = { /* ToDo: Implement message search function */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Options"
                )
            }
            IconButton(
                onClick = { /* TODO: show DropDownMenu */ }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options"
                )
            }
        }
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f),
            reverseLayout = true,
            content = {
                items(messagesList) { message ->
                    val isOwnMessage = (message.senderId == currentUserId)
                    val primaryColor: Color = MaterialTheme.colorScheme.primary
                    val secondaryColor: Color = MaterialTheme.colorScheme.secondary
                    Box(
                        contentAlignment = if (isOwnMessage) {
                            Alignment.CenterEnd
                        } else Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .width(200.dp)
                                .drawBehind {
                                    val cornerRadius = 10.dp.toPx()
                                    val triangleHeight = 10.dp.toPx()
                                    val triangleWidth = 25.dp.toPx()
                                    val trianglePath = Path().apply {
                                        if (isOwnMessage) {
                                            moveTo(
                                                x = size.width,
                                                y = size.height - cornerRadius
                                            )
                                            lineTo(
                                                x = size.width,
                                                y = size.height + triangleHeight
                                            )
                                            lineTo(
                                                x = size.width - triangleWidth,
                                                y = size.height - cornerRadius
                                            )
                                            close()
                                        } else {
                                            moveTo(
                                                x = 0f,
                                                y = size.height - cornerRadius
                                            )
                                            lineTo(
                                                x = 0f,
                                                y = size.height + triangleHeight
                                            )
                                            lineTo(
                                                x = triangleWidth,
                                                y = size.height - cornerRadius
                                            )
                                            close()
                                        }
                                    }
                                    drawPath(
                                        path = trianglePath,
                                        color = if (isOwnMessage) primaryColor
                                        else secondaryColor
                                    )
                                }
                                .background(
                                    color = if (isOwnMessage) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = message.username ?: "sender id",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = message.content ?: "",
                                color = Color.White
                            )
                            Text(
                                text = message.timestamp.toString(),
                                color = Color.White,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        )

        MessageInput(
            conversationId = conversation.id ?: "",
            currentUserId = currentUserId,
            currentUserName = "Username",
            onSend = {},
            onAddAttachment = {},
            onOpenCamera = {},
            onOpenEmojis = {},
            modifier = Modifier
        )
    }
}