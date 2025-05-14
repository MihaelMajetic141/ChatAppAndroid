package hr.tvz.android.chatapp.network.repository

import hr.tvz.android.chatapp.model.dto.ConversationDTO
import hr.tvz.android.chatapp.model.Conversation
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "http://10.0.2.2:8080/api/conversations"

    suspend fun getConversation(
        conversationId: String,
        currentUserId: String
    ): Conversation {
        val response: HttpResponse = httpClient.get(
            urlString = "$baseUrl/get/$conversationId/$currentUserId"
        ) {
            // parameter("conversationId", conversationId)
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.bodyAsText().let { Json.decodeFromString(it) }
            else -> throw Exception("Failed to fetch conversation: ${response.status}")
        }
    }

    suspend fun getAllConversationsByUserId(userId: String): List<ConversationDTO> {
        val response: HttpResponse = httpClient.get("$baseUrl/$userId") {
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.bodyAsText().let { Json.decodeFromString(it) }
            else -> throw Exception("Failed to fetch conversations: ${response.status}")
        }
    }



    suspend fun createNewGroupChat(conversation: Conversation) : Conversation {
        val response: HttpResponse = httpClient.post("$baseUrl/create_group") {
            contentType(ContentType.Application.Json)
            setBody(conversation)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.bodyAsText().let { Json.decodeFromString(it) }
            else -> throw Exception("Failed to create group: ${response.status}")
        }
    }

    suspend fun createNewDM(
        senderId: String,
        receiverId: String
    ): Conversation {
        val response: HttpResponse = httpClient.post("$baseUrl/create_dm") {
            contentType(ContentType.Application.Json)
            parameter("senderId", senderId)
            parameter("receiverId", receiverId)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.bodyAsText().let { Json.decodeFromString(it) }
            else -> throw Exception("Failed to create DM: ${response.status}")
        }
    }

}