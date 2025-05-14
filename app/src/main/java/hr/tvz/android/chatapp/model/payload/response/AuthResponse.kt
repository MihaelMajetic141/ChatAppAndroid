package hr.tvz.android.chatapp.model.payload.response

import hr.tvz.android.chatapp.model.dto.UserDTO

data class AuthResponse(
    val jwtResponse: JwtResponse,
    val userInfo: UserDTO
)
