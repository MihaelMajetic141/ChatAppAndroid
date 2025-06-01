package hr.tvz.android.chatapp.data.payload.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val token: String
)