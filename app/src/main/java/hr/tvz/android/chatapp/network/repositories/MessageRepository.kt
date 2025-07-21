package hr.tvz.android.chatapp.network.repositories

import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.data.model.ChatMessage
import hr.tvz.android.chatapp.network.AuthHttpClient
import hr.tvz.android.chatapp.network.NoAuthHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import javax.inject.Inject

class MessageRepository @Inject constructor(
    @AuthHttpClient private val authHttpClient: HttpClient,
    @NoAuthHttpClient private val noAuthHttpClient: HttpClient,
) {
    private val baseUrl = "http://${BuildConfig.SERVER_IP}/api/messages"

    suspend fun getMessagesByConversationId(conversationId: String): List<ChatMessage> {
        val response: HttpResponse = authHttpClient.get("$baseUrl/get") {
            parameter("conversationId", conversationId)
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.bodyAsText().let { Json.decodeFromString(it) }
            else -> throw Exception("Failed to fetch messages: ${response.status}")
        }
    }

    // ToDo: Check if ByteArray is correct type for GridFsResource
    suspend fun getMedia(fileId: String): ByteArray {
        val response: HttpResponse = authHttpClient.get("$baseUrl/media/$fileId")
        return when (response.status) {
            HttpStatusCode.OK -> response.readBytes()
            HttpStatusCode.NotFound -> throw Exception("Media not found: $fileId")
            else -> throw Exception("Failed to fetch media: ${response.status}")
        }
    }


}