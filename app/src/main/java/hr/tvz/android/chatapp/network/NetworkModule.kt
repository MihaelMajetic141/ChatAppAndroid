package hr.tvz.android.chatapp.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.data.payload.request.RefreshTokenRequest
import hr.tvz.android.chatapp.data.payload.response.JwtResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthHttpClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class NoAuthHttpClient


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = BuildConfig.SERVER_IP

    @Provides
    @Singleton
    @NoAuthHttpClient
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                config {
                    connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)
                    retryOnConnectionFailure(true)
                }
            }
            headers {
                append(HttpHeaders.Connection, "close")
            }

            install(WebSockets)

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    @Provides
    @Singleton
    @AuthHttpClient
    fun provideAuthHttpClient(
        @NoAuthHttpClient httpClient: HttpClient,
        dataStoreManager: DataStoreManager
    ): HttpClient {
        return HttpClient(OkHttp) {
            install(WebSockets)
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
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

                    refreshTokens {
                        runBlocking {
                            val refreshToken = dataStoreManager.getRefreshToken() ?: return@runBlocking null
                            val response = httpClient.post("$BASE_URL/api/auth/refreshToken") {
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                setBody(RefreshTokenRequest(refreshToken))
                            }
                            if (response.status == HttpStatusCode.OK) {
                                val jwtResponse = response.body<JwtResponse>()
                                dataStoreManager.saveJwtResponseData(
                                    accessToken = jwtResponse.accessToken,
                                    refreshToken = jwtResponse.refreshToken
                                )
                                BearerTokens(
                                    jwtResponse.accessToken,
                                    jwtResponse.refreshToken
                                )
                            } else {
                                null // ToDo: login screen
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