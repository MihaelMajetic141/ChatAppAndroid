package hr.tvz.android.chatapp.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

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
    @Contextual //toDo: Double check this
    val createdAt: Date? = null,
)