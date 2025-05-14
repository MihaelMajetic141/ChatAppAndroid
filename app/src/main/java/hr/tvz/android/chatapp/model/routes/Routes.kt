package hr.tvz.android.chatapp.model.routes

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Home : Routes("home")
    data object CreateGroup : Routes("create_group")
    data object CreateContact : Routes("create_contact")
    data object Chat : Routes("chat")
    data object NewConversation : Routes("new_conversation")
    data object Profile : Routes("profile")
    data object Settings : Routes("settings")
}