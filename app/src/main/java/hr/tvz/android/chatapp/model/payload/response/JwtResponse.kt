package hr.tvz.android.chatapp.model.payload.response

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String
)