package hr.tvz.android.chatapp.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class ContactDTO(
    val userInfoId: String,
    val username: String,
    val email: String,
    val imageFileId: String?,
    val status: String? = ""
)