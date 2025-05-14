package hr.tvz.android.chatapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hr.tvz.android.chatapp.model.routes.Routes
import hr.tvz.android.chatapp.ui.theme.ChatAppTheme
import hr.tvz.android.chatapp.view.screens.ChatListScreen
import hr.tvz.android.chatapp.view.screens.ChatScreen
import hr.tvz.android.chatapp.view.screens.CreateContactScreen
import hr.tvz.android.chatapp.view.screens.CreateGroupScreen
import hr.tvz.android.chatapp.view.screens.LoginScreen
import hr.tvz.android.chatapp.view.screens.NewConversationScreen
import hr.tvz.android.chatapp.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChatAppTheme {
                val topAppBarState = remember { mutableStateOf(TopAppBarState()) }
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 56.dp),
                    content = { NavBarItems(navController = navController) }
                )

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { topAppBarState.value.title },
                            navigationIcon = { topAppBarState.value.navigationIcon },
                            actions = { topAppBarState.value.actions },
                        )
                    },
                    floatingActionButton = {
                        IconButton(
                            onClick = { /* TODO: Open create group screen */ },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddCircle,
                                contentDescription = "New conversation",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    content = {
                        MainNavigation(
                            topAppBarState = topAppBarState,
                            authViewModel = authViewModel,
                            navController = navController
                        )
                    },
                )
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    MainNavigation()
//                }
            }
        }
    }
}

@Composable
private fun MainNavigation(
    navController: NavHostController,
    topAppBarState: MutableState<TopAppBarState>,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
    ) {
        composable(route = Routes.Home.route) {
            ChatListScreen(
                navController = navController,
                topAppBarState = topAppBarState,
                authViewModel = authViewModel
            )
        }

        composable(route = Routes.NewConversation.route) {
            NewConversationScreen(
                navController = navController,
                topAppBarState = topAppBarState
            )
        }

        composable(
            route = Routes.Chat.route + "/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) {
            val chatId = it.arguments?.getString("chatId") ?: ""
            ChatScreen(
                chatId = chatId,
                navController = navController
            )
        }

        composable(route = Routes.CreateGroup.route) {
            CreateGroupScreen(
                navController = navController,
                topAppBarState = topAppBarState
            )
        }

        composable(route = Routes.CreateContact.route) {
            CreateContactScreen(
                navController = navController,
                topAppBarState = topAppBarState
            )

        }

        composable(route = Routes.Login.route) {
            LoginScreen(
                navController,
                authViewModel
            )
        }
    }
}

@Composable
fun NavBarItems(
    navController: NavController,
) {
    IconButton(
        onClick = { navController.navigate(Routes.Home.route) { popUpTo(0) } }
    ) {
        Icon(Icons.Default.Home, contentDescription = "Home")
        Text("Home")
    }
    IconButton(
        onClick = { navController.navigate(Routes.Login.route) }
    ) {
        Icon(Icons.Default.Home, contentDescription = "Login")
        Text("Login")
    }
}

data class TopAppBarState(
    val title: @Composable () -> Unit = {},
    val navigationIcon: @Composable () -> Unit = {},
    val actions: @Composable () -> Unit = {}
)