package hr.tvz.android.chatapp.model.dto

import java.util.Date


data class ConversationDTO(
    val id: String,
    val name: String,
    val lastMessage: String?,
    val lastMessageDate: Date,
    val imageFileId: String?
)