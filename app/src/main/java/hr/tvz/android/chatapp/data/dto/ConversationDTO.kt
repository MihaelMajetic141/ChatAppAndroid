package hr.tvz.android.chatapp.data.dto

import hr.tvz.android.chatapp.data.payload.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ConversationDTO(
    val id: String,
    val name: String,
    val lastMessage: String?,
    @Serializable(with = InstantSerializer::class)
    val lastMessageDate: Instant?,
    val imageFileId: String = ""
)