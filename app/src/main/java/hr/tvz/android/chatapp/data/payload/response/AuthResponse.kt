package hr.tvz.android.chatapp.data.payload.response

import hr.tvz.android.chatapp.data.dto.UserInfoDTO
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val jwtResponse: JwtResponse,
    val userInfo: UserInfoDTO
)
