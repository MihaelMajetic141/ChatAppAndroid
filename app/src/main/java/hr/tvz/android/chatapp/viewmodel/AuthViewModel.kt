package hr.tvz.android.chatapp.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import hr.tvz.android.chatapp.data.payload.request.LoginRequest
import hr.tvz.android.chatapp.data.payload.request.RegistrationRequest
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.network.repositories.AuthRepository
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import hr.tvz.android.chatapp.data.payload.request.GoogleAuthRequest
import hr.tvz.android.chatapp.data.payload.response.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface AuthState {
    data object LoggedOut : AuthState
    data object Loading : AuthState
    data object LoggedIn : AuthState
    data object RegistrationSuccess : AuthState
    data class Error(val responseCode: Int,val errorResponse:String) : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _username = MutableStateFlow(LoginField(""))
    var username = _username.asStateFlow()

    private val _email = MutableStateFlow(LoginField(""))
    var email = _email.asStateFlow()

    private val _password = MutableStateFlow(LoginField(""))
    var password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow(LoginField(""))
    var confirmPassword = _confirmPassword.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(false)
    var isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    var authState = _authState.asStateFlow()

    fun login(loginRequest: LoginRequest, context: Context) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(loginRequest)
                saveLoginResponseToDataStore(response, DataStoreManager(context))
                // Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                resetAllFields()
                _isUserLoggedIn.update { return@update true }
                _authState.update { return@update AuthState.LoggedIn }
            } catch (e: Exception) {
                 _authState.value = AuthState.Error(401, e.message.toString())
            }
        }
    }

    fun register(registrationRequest: RegistrationRequest) {
        viewModelScope.launch {
            try {
                if (validateConfirmPassword()) {
                    authRepository.register(registrationRequest)
                    resetAllFields()
                    _authState.update { AuthState.RegistrationSuccess }
                    // Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                } else {
                    _authState.update { AuthState.Error(
                        responseCode = 400,
                        errorResponse = "Passwords do not match!"
                    ) }
                    // Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _authState.update { AuthState.Error(401, e.message.toString()) }
            }
        }
    }

    fun loginWithGoogle(
        task: Task<GoogleSignInAccount>,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val account = task.result
                val idToken = account?.idToken
                if (idToken != null) {
                    val authResponse = authRepository.loginWithGoogle(
                        GoogleAuthRequest(
                            idToken
                        )
                    )
                    saveLoginResponseToDataStore(authResponse, DataStoreManager(context))
                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                    _isUserLoggedIn.update { return@update true }
                    _authState.update { return@update AuthState.LoggedIn }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google Sign-In failed: ${e.localizedMessage}")
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            try {
                authRepository.logout(DataStoreManager(context).refreshToken.toString())
                DataStoreManager(context).clear()
                _isUserLoggedIn.update { false }
                _authState.update { AuthState.LoggedOut }
                Toast.makeText(context, "Logout Successful!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout failed: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun saveLoginResponseToDataStore(
        response: AuthResponse,
        dataStoreManager: DataStoreManager
    ) {
        dataStoreManager.saveAuthResponseData(
            accessToken = response.jwtResponse.accessToken,
            refreshToken = response.jwtResponse.refreshToken,
            userId = response.userInfo.id,
            email = response.userInfo.email,
            username = response.userInfo.username,
            // picture = response.userInfo.imageFileId
        )
    }

    fun validateEmail() : Boolean {
        if(email.value.text.isNotBlank()) return true
        _email.value = email.value.copy(errorMsg = "Email cannot be empty")
        return false
    }

    fun validateUsername() : Boolean {
        if(username.value.text.isNotBlank()) return true
        _username.value = username.value.copy(errorMsg = "Username cannot be empty")
        return false
    }

    fun validatePassword() : Boolean {
        if (password.value.text.isNotBlank()) return true
        _password.value = password.value.copy(errorMsg = "Password cannot be empty")
        return false
    }

    fun validateConfirmPassword() : Boolean {
        return (confirmPassword.value.text.isNotBlank()
                && confirmPassword.value == password.value)
    }

    fun setEmail(value: String){
        _email.value = email.value.copy(text = value)
    }

    fun setUsername(value: String){
        _username.value = username.value.copy(text = value)
    }

    fun setPassword(value: String){
        _password.value = password.value.copy(text = value)
    }

    fun setConfirmPassword(value: String){
        _confirmPassword.value = confirmPassword.value.copy(text = value)
    }

    private fun resetAllFields() {
        setEmail("")
        setUsername("")
        setPassword("")
        setConfirmPassword("")
    }

}

data class LoginField(val text: String, val errorMsg: String? = null)