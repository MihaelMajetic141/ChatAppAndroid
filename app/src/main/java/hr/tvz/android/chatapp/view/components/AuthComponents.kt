package hr.tvz.android.chatapp.view.components

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import hr.tvz.android.chatapp.R
import hr.tvz.android.chatapp.viewmodel.AuthViewModel

@Composable
fun GoogleSignInButton (
    context: Context = LocalContext.current,
    authViewModel: AuthViewModel
) {
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("")
                .requestEmail()
                .build()
        )
    }
    googleSignInClient.revokeAccess()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        authViewModel.loginWithGoogle(task, context)
    }

    Button(
        onClick = {
            val signInIntent = googleSignInClient.signInIntent.apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            launcher.launch(signInIntent)
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            Color.White
        ),
        modifier = Modifier.padding(16.dp).width(250.dp).height(41.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_icon),
            contentDescription = "Google Icon"
        )
        Text(text = "Sign in with Google", )
    }
}

@Composable
fun AuthTextField(
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
//        colors = OutlinedTextFieldDefaults.colors(
//            unfocusedBorderColor = Color.White,
//            focusedBorderColor = Color.White,
//            unfocusedTextColor = Color.White,
//            focusedTextColor = Color.White,
//        ),
    )
    if(isError && errorLabel.isNotEmpty()){
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = errorLabel, color = Color.Red, fontSize = 11.sp)
    }
}