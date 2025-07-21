package hr.tvz.android.chatapp.network

import android.util.Log
import hr.tvz.android.chatapp.BuildConfig
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
import io.ktor.client.request.url

class WebSocketClient @Inject constructor(
    @AuthHttpClient private val authHttpClient: HttpClient,
    @NoAuthHttpClient private val noAuthHttpClient: HttpClient,
) {

    private val WEBSOCKET_URL = "ws://${BuildConfig.SERVER_IP}/chat"
    private var webSocketSession: WebSocketSession? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    private val _messages = MutableSharedFlow<ChatMessage>()
    val messages: SharedFlow<ChatMessage> = _messages

    suspend fun initSession(): Resource<Unit> {
        return try {
            webSocketSession = authHttpClient.webSocketSession {
                url(urlString = WEBSOCKET_URL)
            }
            if(webSocketSession?.isActive ?: false) {
                Resource.Success(Unit)
            } else Resource.Error("Couldn't establish a connection.")
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    fun observeTextMessages(): Flow<ChatMessage> {
        return try {
            webSocketSession?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val message = Json.decodeFromString<ChatMessage>(json)
                    message
                } ?: flow {  }
        } catch (e: Exception) {
            e.printStackTrace()
            flow {  }
        }
    }

    suspend fun sendTextMessage(message: ChatMessage) {
        try {
            val json = Json.encodeToString(value = message)
            webSocketSession?.send(Frame.Text(text = json))
                ?: println("Cannot send message: WebSocket session is not active")
        } catch(e: Exception) {
            Log.d("Error", e.message ?: "Send text message error")
        }
    }

    fun closeSession() {
        scope.launch {
            _connectionState.update { ConnectionState.DISCONNECTING }
            webSocketSession?.close()
            webSocketSession = null
            _connectionState.update { ConnectionState.DISCONNECTED }
        }
    }



//    fun initSessionAndObserveFrames() {
//        scope.launch {
//            _connectionState.update { ConnectionState.CONNECTING }
//            try {
//                authHttpClient.webSocket(WEBSOCKET_URL) {
//                    webSocketSession = this
//                    _connectionState.update { ConnectionState.CONNECTED }
//                    try {
//                        for (frame in incoming) {
//                            when (frame) {
//                                is Frame.Text -> {
//                                    val text = frame.readText()
//                                    try {
//                                        val chatMessage = Json.decodeFromString<ChatMessage>(
//                                            string = text
//                                        )
//                                        _messages.emit(value = chatMessage)
//                                    } catch (e: SerializationException) {
//                                        Log.d("Error", e.message.toString())
//                                    }
//                                }
//                                is Frame.Binary -> {
//                                    /* ToDo: Handle binary frames
//                                    * A frame containing binary data, like images, files, or other non-text content.
//                                    * frame.readBytes() can be used to read the data.
//                                     */
//                                }
//                                is Frame.Close -> {
//                                    _connectionState.update { ConnectionState.DISCONNECTED }
//                                }
//                                is Frame.Ping -> {
//                                    /*
//                                    * A control frame sent by the server to check if the client is still alive.
//                                    * The client should respond with a Pong frame.
//                                    * Ktor might handle this automatically, but you could log it for debugging.
//                                    * Example Use: Confirm the client is responsive.
//                                     */
//                                    println("Received Ping frame")
//                                }
//                                is Frame.Pong -> {
//                                    /*
//                                    * Usually, you don’t need to handle this manually unless you’re implementing custom keep-alive logic.
//                                    * Example Use: Log it to monitor connection health.
//                                     */
//                                }
//                            }
//                        }
//                    } finally {
//                        closeSession()
//                    }
//                }
//            } catch (e: Exception) {
//                _connectionState.value = ConnectionState.ERROR(e)
//            }
//        }
//    }

}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}