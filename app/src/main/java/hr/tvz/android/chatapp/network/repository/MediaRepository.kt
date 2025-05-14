package hr.tvz.android.chatapp.network.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.IOException
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val httpClient: HttpClient,
    @ApplicationContext private val applicationContext: Context
) {
    private val baseUrl = "http://10.0.2.2:8080/api/media"

    suspend fun uploadMedia(mediaUri: Uri): String {
        val contentResolver = applicationContext.contentResolver
        val fileName = getFileNameFromUri(mediaUri) ?: "image_${System.currentTimeMillis()}"
        val contentType = contentResolver.getType(mediaUri) ?: "image/*"

        val inputStream = contentResolver.openInputStream(mediaUri)
            ?: throw IOException("Failed to open input stream")

        val response: HttpResponse = httpClient.submitFormWithBinaryData(
            url = "$baseUrl/upload_image",
            formData = formData {
                append(
                    "file",
                    inputStream.readBytes(),
                    Headers.build {
                        append(HttpHeaders.ContentType, contentType)
                        append(HttpHeaders.ContentDisposition, "filename=$fileName")
                    }
                )
            }
        )

        if (response.status == HttpStatusCode.OK) {
            return response.bodyAsText()
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

}