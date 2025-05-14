package hr.tvz.android.chatapp.network.repository

import hr.tvz.android.chatapp.model.dto.ContactDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "http://10.0.2.2:8080/api/contacts"

    suspend fun getContactDTO(userId: String): ContactDTO {
        val response = httpClient.get("$baseUrl/get") {
            parameter("userId", userId)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user info: ${response.status}")
        }
    }

    // ToDo: Might become unnecessary
    suspend fun getAllUsers(): List<ContactDTO> {
        val response = httpClient.get("$baseUrl/get_all")
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user info: ${response.status}")
        }
    }

    suspend fun getContactByEmail(email: String): ContactDTO {
        val response = httpClient.get("$baseUrl/get_by_email") {
            parameter("email", email)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user info: ${response.status}")
        }
    }

    suspend fun newContact(
        contactId: String,
        currentUserId: String
    ): ContactDTO {
        val response = httpClient.get("$baseUrl/new/$contactId}") {
            parameter("currentUserId", currentUserId)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user info: ${response.status}")
        }
    }
}