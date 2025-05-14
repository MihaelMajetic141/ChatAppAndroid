package hr.tvz.android.chatapp.model

import java.util.Date

data class ReadStatus(
    val id: String,
    val userId: String,
    val conversationId: String,
    val lastReadMessageId: String,
    val lastUpdated: Date
)
