package hr.tvz.android.chatapp.data.model

import hr.tvz.android.chatapp.data.payload.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant


@Serializable
data class MediaMetadata(
    val id: String,
    val originalName: String?,
    val mimeType: String?,
    val ownerId: String?,
    val size: Long,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant?
)