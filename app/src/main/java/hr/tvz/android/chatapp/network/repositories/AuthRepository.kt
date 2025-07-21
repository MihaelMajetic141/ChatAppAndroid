package hr.tvz.android.chatapp.network.repositories;

import android.os.Build
import android.util.Log
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.data.payload.request.GoogleAuthRequest
import hr.tvz.android.chatapp.data.payload.request.LoginRequest
import hr.tvz.android.chatapp.data.payload.request.RegistrationRequest
import hr.tvz.android.chatapp.data.payload.response.AuthResponse
import hr.tvz.android.chatapp.network.AuthHttpClient
import hr.tvz.android.chatapp.network.NoAuthHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject


class AuthRepository @Inject constructor(
    @AuthHttpClient private val authHttpClient: HttpClient,
    @NoAuthHttpClient private val noAuthHttpClient: HttpClient
) {
    private val baseUrl = "http://${BuildConfig.SERVER_IP}/api/auth"

    suspend fun login(loginRequest: LoginRequest): AuthResponse {
        val response = noAuthHttpClient.post(urlString = "$baseUrl/login") {
            setBody(loginRequest)
            contentType(ContentType.Application.Json)
        }
        Log.d("Network", "Response status: ${response.status}")
        Log.d("Network", "Raw response: ${response.bodyAsText()}")
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to authenticate: ${response.status}")
        }
    }

    suspend fun register(registrationRequest: RegistrationRequest): String {
        val response = noAuthHttpClient.post(urlString ="$baseUrl/register") {
            setBody(registrationRequest)
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to register: ${response.status}")
        }
    }

    suspend fun logout(refreshToken: String): String {
        return noAuthHttpClient.post("$baseUrl/logout") {
            setBody(refreshToken)
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun loginWithGoogle(googleAuthRequest: GoogleAuthRequest): AuthResponse {
        val response = noAuthHttpClient.post("api/auth/google") {
            setBody(googleAuthRequest)
            contentType(ContentType.Application.Json)
        }
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Authentication failed: ${response.status}")
        }
    }
}