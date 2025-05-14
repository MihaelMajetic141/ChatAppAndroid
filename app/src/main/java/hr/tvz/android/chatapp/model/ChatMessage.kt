package hr.tvz.android.chatapp.model

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ChatMessage(
    val id: String?,
    val senderId: String?,
    val conversationId: String?,
    val content: String?,
    val mediaFileId: String? = null,
    val mediaFileType: String? = null,
    val replyTo: String? = null,
    val timestamp: Date?,
    val reactions: Map<String, Int>? = null
)