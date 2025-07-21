package hr.tvz.android.chatapp.data.model

import hr.tvz.android.chatapp.data.payload.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.intellij.lang.annotations.Identifier
import java.time.Instant
import javax.annotation.processing.Generated

@Serializable
data class Conversation (
    @Identifier
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val imageFileId: String?,
    @SerialName("directMessage") val isDirectMessage: Boolean,
    val inviteLink: String? = null,
    val adminIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    @Serializable(with = InstantSerializer::class) val createdAt: Instant
)