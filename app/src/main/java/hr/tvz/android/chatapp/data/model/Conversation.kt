package hr.tvz.android.chatapp.data.model

import hr.tvz.android.chatapp.data.payload.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Conversation (
    val id: String?,
    val name: String?,
    val description: String?,
    val imageFileId: String?,
    val isDirectMessage: Boolean,
    val inviteLink: String?,
    val adminIds: List<String>?,
    val memberIds: List<String>?,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant?
)