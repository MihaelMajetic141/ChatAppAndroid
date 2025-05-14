package hr.tvz.android.chatapp.model.dto

import java.util.Date

data class UserDTO(
    val id: String,
    val username: String,
    val email: String,
    val imageFileId: String?,
    val isOnline: Boolean = false,
    val lastOnline: Date,
    val contactIds: List<String>
)