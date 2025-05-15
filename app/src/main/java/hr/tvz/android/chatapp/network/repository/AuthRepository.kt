package hr.tvz.android.chatapp.network.repository;

import hr.tvz.android.chatapp.model.payload.request.GoogleAuthRequest
import hr.tvz.android.chatapp.model.payload.request.LoginRequest
import hr.tvz.android.chatapp.model.payload.response.AuthResponse
import hr.tvz.android.chatapp.model.payload.response.RegistrationRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val httpClient: HttpClient
) {
    private val baseUrl = "http://10.0.2.2:8080/api/auth"

    suspend fun login(loginRequest: LoginRequest): AuthResponse {
        val response = httpClient.post(urlString ="$baseUrl/login") {
            setBody(loginRequest)
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to authenticate: ${response.status}")
        }
    }

    suspend fun register(registrationRequest: RegistrationRequest): String {
        val response = httpClient.post(urlString ="$baseUrl/register") {
            setBody(registrationRequest)
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to register: ${response.status}")
        }
    }

    suspend fun logout(refreshToken: String): String {
        return httpClient.post("$baseUrl/logout") {
            setBody(refreshToken)
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun loginWithGoogle(googleAuthRequest: GoogleAuthRequest): AuthResponse {
        val response = httpClient.post("api/auth/google") {
            setBody(googleAuthRequest)
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Authentication failed: ${response.status}")
        }
    }
}