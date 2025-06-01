package hr.tvz.android.chatapp.data.model

import hr.tvz.android.chatapp.data.payload.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ChatMessage(
    val id: String?,
    val senderId: String?,
    val conversationId: String?,
    val content: String?,
    val mediaFileId: String? = null,
    val mediaFileType: String? = null,
    val replyTo: String? = null,
    @Serializable(with = InstantSerializer::class) val timestamp: Instant?,
    val reactions: Map<String, Int>? = null
)