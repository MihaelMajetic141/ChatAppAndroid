package hr.tvz.android.chatapp.data.model

import hr.tvz.android.chatapp.data.payload.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ReadStatus(
    val id: String,
    val userId: String,
    val conversationId: String,
    val lastReadMessageId: String,
    @Serializable(with = InstantSerializer::class) val lastUpdated: Instant
)
