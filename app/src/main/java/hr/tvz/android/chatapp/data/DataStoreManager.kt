package hr.tvz.android.chatapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


val Context.dataStore by preferencesDataStore(name = "user_data")
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val ID_KEY = stringPreferencesKey("id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        // private val PICTURE_KEY = stringPreferencesKey("picture")
    }

    // ToDo: check if remaining preferences stay the same
    suspend fun saveJwtResponseData(
        accessToken: String,
        refreshToken: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveAuthResponseData(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        username: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[ID_KEY] = userId
            preferences[EMAIL_KEY] = email
            preferences[NAME_KEY] = username
            // preferences[PICTURE_KEY] = picture
        }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.first()[ACCESS_TOKEN]
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.first()[REFRESH_TOKEN]
    }

    val accessToken: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ACCESS_TOKEN] ?: "" }

    val refreshToken: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[REFRESH_TOKEN] ?: "" }

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ID_KEY] ?: "" }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[EMAIL_KEY] ?: "" }

    val userName: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[NAME_KEY] ?: "" }

//    val userPicture: Flow<String?> = context.dataStore.data
//        .map { preferences -> preferences[PICTURE_KEY] ?: "" }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}