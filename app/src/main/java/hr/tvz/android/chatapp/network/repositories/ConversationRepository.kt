package hr.tvz.android.chatapp.network.repositories

import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.data.dto.ConversationDTO
import hr.tvz.android.chatapp.network.AuthHttpClient
import hr.tvz.android.chatapp.network.NoAuthHttpClient
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
    @AuthHttpClient private val authHttpClient: HttpClient,
    @NoAuthHttpClient private val noAuthHttpClient: HttpClient
) {
    private val baseUrl = "${BuildConfig.SERVER_IP}/api/conversations"

    suspend fun getConversation(
        conversationId: String,
        currentUserId: String
    ): hr.tvz.android.chatapp.data.model.Conversation {
        val response: HttpResponse = authHttpClient.get(
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
        val response: HttpResponse = authHttpClient.get("$baseUrl/$userId") {
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.bodyAsText().let { Json.decodeFromString(it) }
            else -> throw Exception("Failed to fetch conversations: ${response.status}")
        }
    }



    suspend fun createNewGroupChat(conversation: hr.tvz.android.chatapp.data.model.Conversation) : hr.tvz.android.chatapp.data.model.Conversation {
        val response: HttpResponse = authHttpClient.post("$baseUrl/create_group") {
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
    ): hr.tvz.android.chatapp.data.model.Conversation {
        val response: HttpResponse = authHttpClient.post("$baseUrl/create_dm") {
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