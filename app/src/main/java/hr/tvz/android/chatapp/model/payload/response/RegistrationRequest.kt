package hr.tvz.android.chatapp.model.payload.response

data class RegistrationRequest(
    val username: String?,
    val email: String?,
    val password: String?,
)