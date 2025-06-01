package hr.tvz.android.chatapp.data.payload.request

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val username: String?,
    val email: String?,
    val password: String?,
)