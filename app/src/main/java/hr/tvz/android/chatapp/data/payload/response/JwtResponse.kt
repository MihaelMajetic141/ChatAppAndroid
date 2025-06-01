package hr.tvz.android.chatapp.data.payload.response

import kotlinx.serialization.Serializable

@Serializable
data class JwtResponse(
    val accessToken: String,
    val refreshToken: String
)