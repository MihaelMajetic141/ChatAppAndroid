package hr.tvz.android.chatapp.data.payload.request

import kotlinx.serialization.Serializable

// ToDo: Check if type Map<String, String>
@Serializable
data class GoogleAuthRequest(val idToken: String)
