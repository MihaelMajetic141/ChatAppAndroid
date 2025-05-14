package hr.tvz.android.chatapp.network.repository

import hr.tvz.android.chatapp.model.ChatMessage
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
    private val httpClient: HttpClient
) {
    private val baseUrl = "http://10.0.2.2:8080/api/messages"

    suspend fun getMessagesByConversationId(conversationId: String): List<ChatMessage> {
        val response: HttpResponse = httpClient.get("$baseUrl/get") {
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
        val response: HttpResponse = httpClient.get("$baseUrl/media/$fileId")
        return when (response.status) {
            HttpStatusCode.OK -> response.readBytes()
            HttpStatusCode.NotFound -> throw Exception("Media not found: $fileId")
            else -> throw Exception("Failed to fetch media: ${response.status}")
        }
    }


}