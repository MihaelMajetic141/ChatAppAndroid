package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.tvz.android.chatapp.model.payload.request.LoginRequest
import hr.tvz.android.chatapp.model.routes.Routes
import hr.tvz.android.chatapp.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Gray)) { }

    Box(
        modifier = Modifier
            .padding(28.dp)
            .alpha(0.7f)
            .clip(CutCornerShape(10.dp))
            .background(Color.White)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeader(headerString = "Sign in")
            LoginFields(
                email = email.text,
                password = password.text,
                onEmailChange = { value -> authViewModel.setEmail(value) },
                onPasswordChange = { value -> authViewModel.setPassword(value) },
                onForgotPasswordClick = {},
                isErrorEmail = email.errorMsg != null,
                isErrorPassword = password.errorMsg != null,
                errorLabelEmail = "Email cannot be empty",
                errorLabelPassword = "Password cannot be empty"
            )
            LoginFooter(
                onSignInClick = {
                    if(authViewModel.validateUsername() && authViewModel.validatePassword()) {
                        authViewModel.login(LoginRequest(email.text, password.text), context)
                    }
                },
                onSignUpClick = { navController.navigate(Routes.Register.route) }
            )
        }
    }


}


@Composable
fun LoginHeader(headerString: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = headerString,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Sign in to continue",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun RegistrationHeader(headerString: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = headerString,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Sign up to continue",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun LoginFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    isErrorEmail: Boolean,
    isErrorPassword: Boolean,
    errorLabelPassword: String,
    errorLabelEmail: String
) {
    Column {
        TextField(
            value = email,
            label = "Username",
            placeholder = "Enter your username",
            onValueChange = onEmailChange,
            leadingIcon = {
                Icon(Icons.Default.AccountBox , contentDescription = "Email", tint = Color.White)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            isError = isErrorEmail,
            errorLabel = errorLabelEmail
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
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
                Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color.White)
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
        Button(onClick = onSignInClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Sign In")
        }
        TextButton(onClick = onSignUpClick) {
            Text(
                text = "Don't have an account? Sign up here."
            )
        }
    }
}

@Composable
fun TextField(
    value: String,
    label: String,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorLabel: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
        ),
    )

    if(isError && errorLabel.isNotEmpty()){
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = errorLabel, color = Color.Red, fontSize = 11.sp)
    }
}