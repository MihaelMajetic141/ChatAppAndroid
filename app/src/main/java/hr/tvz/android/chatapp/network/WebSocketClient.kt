package hr.tvz.android.chatapp.network

import hr.tvz.android.chatapp.data.model.ChatMessage
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import javax.inject.Inject
import hr.tvz.android.chatapp.viewmodel.ConnectionState

class WebSocketClient @Inject constructor(
    @AuthHttpClient private val authHttpClient: HttpClient,
    @NoAuthHttpClient private val noAuthHttpClient: HttpClient,
) {
    private val WEBSOCKET_URL = "ws://10.0.2.2:8080/chat"

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    // Incoming messages flow
    private val _messages = MutableSharedFlow<hr.tvz.android.chatapp.data.model.ChatMessage>()
    val messages: SharedFlow<hr.tvz.android.chatapp.data.model.ChatMessage> = _messages

    private var webSocketSession: WebSocketSession? = null

    fun start() {
        scope.launch {
            _connectionState.update { ConnectionState.CONNECTING }
            try {
                authHttpClient.webSocket(WEBSOCKET_URL) {
                    webSocketSession = this
                    _connectionState.update { ConnectionState.CONNECTED }
                    try {
                        // Handle incoming messages
                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val text = frame.readText()
                                    try {
                                        val chatMessage = Json.decodeFromString<hr.tvz.android.chatapp.data.model.ChatMessage>(text)
                                        _messages.emit(chatMessage)
                                    } catch (e: SerializationException) {
                                        // Log or handle deserialization error
                                    }
                                }
                                is Frame.Binary -> {
                                    /* ToDo: Handle binary frames
                                    * A frame containing binary data, like images, files, or other non-text content.
                                    * frame.readBytes() can be used to read the data.
                                     */
                                }
                                is Frame.Close -> {
                                    _connectionState.update { ConnectionState.DISCONNECTED }
                                }
                                is Frame.Ping -> {
                                    /*
                                    * A control frame sent by the server to check if the client is still alive.
                                    * The client should respond with a Pong frame.
                                    * Ktor might handle this automatically, but you could log it for debugging.
                                    * Example Use: Confirm the client is responsive.
                                     */
                                    println("Received Ping frame")
                                }
                                is Frame.Pong -> {
                                    /*
                                    * Usually, you don’t need to handle this manually unless you’re implementing custom keep-alive logic.
                                    * Example Use: Log it to monitor connection health.
                                     */
                                }
                            }
                        }
                    } finally {
                        // Cleanup when the WebSocket closes
                        webSocketSession = null
                        _connectionState.update { ConnectionState.DISCONNECTED }
                    }
                }
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.ERROR(e)
            }
        }
    }

    suspend fun send(message: hr.tvz.android.chatapp.data.model.ChatMessage) {
        val json = Json.encodeToString(message)
        webSocketSession?.send(Frame.Text(json))
            ?: // Optionally log or throw an exception if session is null
            println("Cannot send message: WebSocket session is not active")
    }

    fun stop() {
        scope.launch {
            _connectionState.update { ConnectionState.DISCONNECTING }
            webSocketSession?.close()
            webSocketSession = null
            _connectionState.update { ConnectionState.DISCONNECTED }
            // Note: The DISCONNECTED state will be set by the WebSocket block's finally clause
        }
    }
}