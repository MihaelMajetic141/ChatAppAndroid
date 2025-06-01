package hr.tvz.android.chatapp.data.dto

import hr.tvz.android.chatapp.data.payload.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class UserInfoDTO(
    val id: String,
    val username: String,
    val email: String,
    val imageFileId: String?,
    @SerialName("online")
    val isOnline: Boolean = false,
    @Serializable(with = InstantSerializer::class)
    val lastOnline: Instant?,
    val contactIds: List<String>?
)