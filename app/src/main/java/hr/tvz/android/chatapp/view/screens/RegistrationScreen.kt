package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
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
import hr.tvz.android.chatapp.data.payload.request.RegistrationRequest
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.view.components.AuthTextField
import hr.tvz.android.chatapp.viewmodel.AuthState
import hr.tvz.android.chatapp.viewmodel.AuthViewModel


@Composable
fun RegistrationScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val email by authViewModel.email.collectAsState()
    val username by authViewModel.username.collectAsState()
    val password by authViewModel.password.collectAsState()
    val authViewState by authViewModel.authState.collectAsState()
    val confirmPassword by authViewModel.confirmPassword.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize())
    Box(
        modifier = Modifier
            .padding(28.dp)
            .alpha(0.7f)
            .clip(CutCornerShape(10.dp))
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RegistrationHeader(headerString = "Sign up")
            RegistrationFields(
                email = email.text,
                username = username.text,
                password = password.text,
                confirmPassword = confirmPassword.text,
                onEmailChange = { value -> authViewModel.setEmail(value)},
                onUsernameChange = { value -> authViewModel.setUsername(value)},
                onPasswordChange = { value -> authViewModel.setPassword(value)},
                onConfirmPasswordChange = { value -> authViewModel.setConfirmPassword(value)},
                onForgotPasswordClick = {},
                isErrorEmail = email.errorMsg != null,
                isErrorUsername = username.errorMsg != null,
                isErrorPassword = password.errorMsg != null,
                errorLabelEmail = "Email cannot be empty",
                errorLabelPassword = "Password cannot be empty",
                errorLabelUsername = "Username cannot be empty"
            )
            RegistrationFooter(
                onSignUpClick = {
                    if (authViewModel.validateEmail() && authViewModel.validatePassword()
                        && authViewModel.validateUsername()
                    ) {
                        authViewModel.register(
                            RegistrationRequest(
                                username.text,
                                email.text,
                                password.text,
                            )
                        )
                    }
                },
                onSignInClick = { navController.navigate(Routes.Login.route) } // ToDo: Clear input fields.
            )
//            GoogleSignInButton(
//                context = LocalContext.current,
//                authViewModel = authViewModel
//            )
            when(val viewState = authViewState){
                is AuthState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                AuthState.RegistrationSuccess -> {
                    navController.navigate(Routes.Login.route) { popUpTo(0) }
                }
                is AuthState.LoggedIn -> {}
                is AuthState.LoggedOut -> {}
                is AuthState.Error -> {
                    Text("${viewState.responseCode}, ${viewState.errorResponse}") // ToDo: edit this, maybe show popup. outline text field that caused error.
                }
            }
        }
    }
}

@Composable
fun RegistrationHeader(headerString: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = headerString,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = "Sign up to continue",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun RegistrationFields(
    email: String,
    username: String,
    password: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    isErrorEmail: Boolean,
    isErrorUsername: Boolean,
    isErrorPassword: Boolean,
    errorLabelEmail: String,
    errorLabelPassword: String,
    errorLabelUsername: String
) {
    AuthTextField(
        value = email,
        label = "Email",
        placeholder = "Enter your email address",
        onValueChange = onEmailChange,
        leadingIcon = {
            Icon(
                Icons.Default.Email,
                contentDescription = "Email")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        isError = isErrorEmail,
        errorLabel = errorLabelEmail
    )
    AuthTextField(
        value = username,
        label = "Username",
        placeholder = "Enter your username",
        onValueChange = onUsernameChange,
        leadingIcon = {
            Icon(
                Icons.Default.AccountBox,
                contentDescription = "Username"
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        isError = isErrorUsername,
        errorLabel = errorLabelUsername
    )
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
    AuthTextField(
        value = confirmPassword,
        label = "Confirm Password",
        placeholder = "Confirm password",
        onValueChange = onConfirmPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Go
        ),
        leadingIcon = {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Confirm Password"
            )
        },
        isError = isErrorPassword,
        errorLabel = errorLabelPassword
    )
    TextButton(
        onClick = onForgotPasswordClick
    ) {
        Text(text = "Forgot Password?")
    }
}
@Composable
fun RegistrationFooter(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onSignUpClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Sign Up")
        }
        TextButton(onClick = onSignInClick) {
            Text(
                text = "Already have an account? Sign in here."
            )
        }
    }
}