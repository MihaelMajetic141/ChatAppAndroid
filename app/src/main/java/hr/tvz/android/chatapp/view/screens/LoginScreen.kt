package hr.tvz.android.chatapp.view.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.tvz.android.chatapp.data.payload.request.LoginRequest
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.viewmodel.AuthViewModel
import hr.tvz.android.chatapp.view.components.AuthTextField
import hr.tvz.android.chatapp.viewmodel.AuthState


@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val username by authViewModel.username.collectAsState()
    val password by authViewModel.password.collectAsState()
    val authViewState by authViewModel.authState.collectAsState()

    // ToDo: Replace Toasts with Popup screens

    // Box(modifier = Modifier.fillMaxSize())
    Box(
        modifier = Modifier
            .padding(28.dp)
            .alpha(0.7f)
            .clip(CutCornerShape(10.dp))
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeader(headerString = "Sign in")
            LoginFields(
                username = username.text,
                password = password.text,
                onUsernameChange = { value -> authViewModel.setUsername(value) },
                onPasswordChange = { value -> authViewModel.setPassword(value) },
                onForgotPasswordClick = {},
                isErrorUsername = username.errorMsg != null,
                isErrorPassword = password.errorMsg != null,
                errorLabelUsername = "Username cannot be empty",
                errorLabelPassword = "Password cannot be empty"
            )
            LoginFooter(
                onSignInClick = {
                    if(authViewModel.validateUsername() && authViewModel.validatePassword()) {
                        authViewModel.login(
                            LoginRequest(
                                username.text,
                                password.text
                            ), context)
                    }
                },
                onSignUpClick = { navController.navigate(Routes.Register.route) }
            )
            // ToDo: GoogleSignInButton(authViewModel = authViewModel)

            when (authViewState) {
                is AuthState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AuthState.LoggedIn -> {
                    navController.navigate(Routes.Home.route)
                }

                is AuthState.LoggedOut -> { }
                is AuthState.Error -> {
                    val error = (authViewState as AuthState.Error).errorResponse
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    Text(error)
                }

                AuthState.RegistrationSuccess -> {}
            }
        }
    }
}


@Composable
fun LoginHeader(headerString: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = headerString,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Sign in to continue",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LoginFields(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    isErrorUsername: Boolean,
    isErrorPassword: Boolean,
    errorLabelPassword: String,
    errorLabelUsername: String
) {
    Column {
        AuthTextField(
            value = username,
            label = "Username",
            placeholder = "Enter your username",
            onValueChange = onUsernameChange,
            leadingIcon = {
                Icon(
                    Icons.Default.AccountBox ,
                    contentDescription = "Username"
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            isError = isErrorUsername,
            errorLabel = errorLabelUsername
        )
        Spacer(modifier = Modifier.height(10.dp))
        AuthTextField(
            value = password,
            label = "Password",
            placeholder = "Enter your password",
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Password"
                )
            },
            isError = isErrorPassword,
            errorLabel = errorLabelPassword
        )

        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Forgot Password?")
        }

    }
}

@Composable
fun LoginFooter(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(
            onClick = onSignInClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign In")
        }
        TextButton(onClick = onSignUpClick) {
            Text(
                text = "Don't have an account? Sign up here."
            )
        }
    }
}