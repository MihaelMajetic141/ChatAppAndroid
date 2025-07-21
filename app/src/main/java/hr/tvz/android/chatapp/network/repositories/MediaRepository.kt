package hr.tvz.android.chatapp.network.repositories

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.data.model.MediaMetadata
import hr.tvz.android.chatapp.network.AuthHttpClient
import hr.tvz.android.chatapp.network.NoAuthHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class MediaRepository @Inject constructor(
    @AuthHttpClient private val authHttpClient: HttpClient,
    @NoAuthHttpClient private val noAuthHttpClient: HttpClient,
    @ApplicationContext private val applicationContext: Context
) {
    private val baseUrl = "http://${BuildConfig.SERVER_IP}/api/media"

    suspend fun uploadMedia(mediaUri: Uri): MediaMetadata {
        val contentResolver = applicationContext.contentResolver
        val fileName = getFileNameFromUri(mediaUri) ?: "media_${System.currentTimeMillis()}"
        val contentType = contentResolver.getType(mediaUri) ?: "media/*"
        val fileSize = getFileSize(mediaUri)

        val inputStream = contentResolver.openInputStream(mediaUri)
            ?: throw IOException("Failed to open input stream")

        val response = authHttpClient.post(urlString = "$baseUrl/upload") {
            setBody(MultiPartFormDataContent(
                parts = formData {
                    append(
                        key = "\"file\"",
                        value = InputProvider {
                            buildPacket { writeFully(inputStream.readBytes()) }
                        },
                        headers = Headers.build {
                            append(
                                name = HttpHeaders.ContentDisposition,
                                value = """filename="$fileName""""
                            )
//                            append(
//                                name = HttpHeaders.ContentDisposition,
//                                value = "filename=\"$fileName\""
//                                    .withParameter(
//                                        key = ContentDisposition.Parameters.Name,
//                                        value = "file"
//                                    )
//                                    .withParameter(
//                                        key = ContentDisposition.Parameters.FileName,
//                                        value = fileName
//                                    ).toString()
//                            )
                            append(
                                name = HttpHeaders.ContentType,
                                value = contentType
                            )
                        }
                    )
                }
            ))
        }

        withContext(Dispatchers.IO) {
            inputStream.close()
        }

        if (response.status == HttpStatusCode.OK) {
            return response.body()
            // return response.bodyAsText().let { Json.decodeFromString(it) }
        } else {
            throw Exception("Image upload failed: ${response.status}")
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val contentResolver = applicationContext.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        }
    }

    private fun getFileSize(uri: Uri): Long? {
        return try {
            val parcelFileDescriptor = applicationContext.contentResolver
                .openFileDescriptor(uri, "r")
            val size = parcelFileDescriptor?.statSize
            parcelFileDescriptor?.close()
            size
        } catch (e: Exception) {
            null
        }
    }

}

/*
                    appendInput(
                        key = "file",
                        headers = Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                ContentDisposition.File
                                    .withParameter(
                                    ContentDisposition.Parameters.Name, "file")
                                    .withParameter(
                                    ContentDisposition.Parameters.FileName, fileName
                                ).toString()
                            )
                        },
                        size = fileSize
                    ) {
                        buildPacket {
                            writeFully(inputStream.readBytes())
//                            val buffer = ByteArray(8192)
//                            var bytesRead: Int
//                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                                writeFully(buffer, 0, bytesRead)
//                            }
                        }
                    }
                }
 */