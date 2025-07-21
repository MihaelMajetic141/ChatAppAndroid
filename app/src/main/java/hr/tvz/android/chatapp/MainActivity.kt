package hr.tvz.android.chatapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.ui.theme.ChatAppTheme
import hr.tvz.android.chatapp.view.screens.ChatListScreen
import hr.tvz.android.chatapp.view.screens.ChatScreen
import hr.tvz.android.chatapp.view.screens.CreateContactScreen
import hr.tvz.android.chatapp.view.screens.CreateGroupScreen
import hr.tvz.android.chatapp.view.screens.LoginScreen
import hr.tvz.android.chatapp.view.screens.NewConversationScreen
import hr.tvz.android.chatapp.view.screens.RegistrationScreen
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
                val authViewModel: AuthViewModel = hiltViewModel()
                val navController = rememberNavController()

                Scaffold(
                    content = {
                        MainNavigation(
                            authViewModel = authViewModel,
                            navController = navController
                        )
                    },
//                    bottomBar = {
//                        // ToDo: Show bottom bar only if user is logged in.
//                        BottomAppBar {
//                            BottomNavItem.items.listIterator().forEach { item ->
//                                NavigationBarItem(
//                                    selected = navController.currentBackStackEntryAsState()
//                                        .value?.destination?.route == item.route,
//                                    onClick = {
//                                        if (authViewModel.isUserLoggedIn.value) {
//                                            navController.navigate(item.route) { popUpTo(0) }
//                                        } else {
//                                            navController.navigate(Routes.Login.route) { popUpTo(0) }
//                                        }
//                                    },
//                                    icon = {
//                                        Image(
//                                            painter = painterResource(item.icon),
//                                            contentDescription = item.label,
//                                            modifier = Modifier.size(33.dp)
//                                        )
//                                    },
//                                    label = { Text(item.label) }
//                                )
//                            }
//                        }
//                    }
                )
            }
        }
    }
}

@Composable
private fun MainNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
    ) {
        composable(route = Routes.Home.route) {
            ChatListScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(route = Routes.NewConversation.route) {
            NewConversationScreen(
                navController = navController,
            )
        }

        composable(route = Routes.CreateGroup.route) {
            CreateGroupScreen(
                navController = navController,
            )
        }

        composable(route = Routes.CreateContact.route) {
            CreateContactScreen(
                navController = navController,
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

        composable(route = Routes.Login.route) {
            LoginScreen(
                navController,
                authViewModel
            )
        }

        composable(route = Routes.Register.route) {
            RegistrationScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}

data class TopAppBarState(
    val title: @Composable () -> Unit = {},
    val navigationIcon: @Composable () -> Unit = {},
    val actions: @Composable () -> Unit = {}
)

sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    data object Home : BottomNavItem(
        Routes.Home.route,
        R.drawable.home,
        "Home"
    )
    data object New : BottomNavItem(
        Routes.NewConversation.route,
        R.drawable.add_new,
        "New Chat"
    )
    data object SignIn : BottomNavItem(
        Routes.Login.route,
        R.drawable.profile,
        "Profile"
    )

    companion object {
        val items = listOf(Home, New, SignIn)
    }
}