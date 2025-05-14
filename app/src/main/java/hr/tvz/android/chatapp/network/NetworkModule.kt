package hr.tvz.android.chatapp.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.tvz.android.chatapp.model.payload.response.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Singleton


    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {
        private val BASE_URL = "10.0.2.2:8080"

        @Provides
        @Singleton
        fun provideHttpClient(dataStoreManager: DataStoreManager): HttpClient {
            return HttpClient(OkHttp) {
                install(WebSockets)
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }

                install(Auth) {
                    bearer {
                        loadTokens {
                            runBlocking {
                                val accessToken = dataStoreManager.getAccessToken()
                                val refreshToken = dataStoreManager.getRefreshToken()
                                if (accessToken != null && refreshToken != null) {
                                    BearerTokens(accessToken, refreshToken)
                                } else {
                                    null
                                }
                            }
                        }

                        // Refresh tokens on 401 response
                        refreshTokens {
                            runBlocking {
                                val refreshToken =
                                    dataStoreManager.getRefreshToken() ?: return@runBlocking null

                                // Separate client for refresh requests to avoid loops
                                val refreshClient = HttpClient(OkHttp) {
                                    install(ContentNegotiation) {
                                        json(Json { ignoreUnknownKeys = true })
                                    }
                                }

                                val response = refreshClient
                                    .post("$BASE_URL/auth/refreshToken") {
                                        parameter("refresh_token", refreshToken)
                                    }

                                if (response.status == HttpStatusCode.OK) {
                                    val authResponse = response.body<AuthResponse>()
                                    dataStoreManager.saveAuthData(
                                        accessToken = authResponse.jwtResponse.accessToken,
                                        refreshToken = authResponse.jwtResponse.refreshToken,
                                        userId = authResponse.userInfo.id,
                                        email = authResponse.userInfo.email,
                                        username = authResponse.userInfo.username
                                    )
                                    BearerTokens(
                                        authResponse.jwtResponse.accessToken,
                                        authResponse.jwtResponse.refreshToken
                                    )
                                } else {
                                    // Refresh failed (e.g., log out user)
                                    null
                                }
                            }
                        }
                    }
                }
            }
        }


        @Provides
        @Singleton
        fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
            return DataStoreManager(context)
        }
    }