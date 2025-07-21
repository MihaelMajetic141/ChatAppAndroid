package hr.tvz.android.chatapp.network.repositories

import android.util.Log
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.network.AuthHttpClient
import hr.tvz.android.chatapp.network.NoAuthHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject

class ContactRepository @Inject constructor(
    @AuthHttpClient private val authHttpClient: HttpClient,
    @NoAuthHttpClient private val noAuthHttpClient: HttpClient
) {
    private val baseUrl = "http://${BuildConfig.SERVER_IP}/api/contacts"

    suspend fun getUserContacts(userId: String): List<ContactDTO> {
        val response = authHttpClient.get("$baseUrl/get") {
            contentType(ContentType.Application.Json)
            parameter("userId", userId)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user info: ${response.status}")
        }
    }

    // ToDo: Might become unnecessary
    suspend fun getAllUsers(): List<ContactDTO> {
        val response = authHttpClient.get("$baseUrl/get_all") {
            contentType(ContentType.Application.Json)
        }
        Log.d("Network", "Response status: ${response.status}")
        val rawBody = response.bodyAsText()
        Log.d("Network", "Raw response: $rawBody")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user info: ${response.status}")
        }
    }

    suspend fun getContactByEmail(email: String): ContactDTO {
        val response = authHttpClient.get("$baseUrl/email/$email") {
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch contact by email: ${response.status}")
        }
    }

    suspend fun getContactByUserId(userId: String): ContactDTO? {
        val response = authHttpClient.get("$baseUrl/id/$userId") {
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user info: ${response.status}")
        }
    }

    suspend fun addNewContact(contactId: String, currentUserId: String): ContactDTO {
        val response = authHttpClient.post("$baseUrl/new/$contactId}") {
            contentType(ContentType.Application.Json)
            parameter("currentUserId", currentUserId)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to create new contact: ${response.status}")
        }
    }

    suspend fun addNewContacts(contactIdList: List<String>, currentUserId: String): ContactDTO {
        val response = authHttpClient.post("$baseUrl/new") {
            contentType(ContentType.Application.Json)
            setBody(contactIdList)
            parameter("currentUserId", currentUserId)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to create new contact: ${response.status}")
        }
    }
}