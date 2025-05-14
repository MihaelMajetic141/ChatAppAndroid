package hr.tvz.android.chatapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState


@Composable
fun CreateGroupScreen2(
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
    navController: NavController,
) {
    val createGroupViewState by newConversationViewModel.loadContactsViewState.collectAsState()
    var groupName by remember { mutableStateOf("") }
    var groupDescription by rememberSaveable { mutableStateOf("") }
    var isPrivate by rememberSaveable { mutableStateOf(false) }
    var groupAdminIds = rememberSaveable { mutableStateListOf<String>() }
    var groupSubscriberIds = rememberSaveable { mutableStateListOf<String>() }
    var showPopup by rememberSaveable { mutableStateOf(false) }

    // ToDo: SearchBar for adding participants
    // ToDo: Next screen for group name, image and description

    PopupBox(
        popupWidth = 500F,
        popupHeight = 500F,
        showPopup = showPopup,
        onClickOutside = { showPopup = false },
        content = {
            PopupBoxContent(
                popupWidth = 500F,
                popupHeight = 500F,
                onDismiss = { showPopup = false },
                onNext = { name, imageUrl ->
                    // Handle next action
                    showPopup = false
                },
                groupName = groupName,
                groupImageUrl = null,
                onGroupNameChange = { groupName = it }
            )
        }
    )

    when (val viewState = createGroupViewState) {
        is LoadContactsViewState.Error -> {

        }
        LoadContactsViewState.Loading -> {
            // Show loading indicator
            LoadingState()
        }
        is LoadContactsViewState.Success -> {
//            navController.navigate("chat/${viewState.newGroup.id}") {
//                popUpTo("chatList") {
//                    inclusive = true
//                }
//            }
        }
    }

    Box(modifier = Modifier
        .padding(100.dp)
        .fillMaxWidth()
    ) {

//        val id: String? = null,
//        val name: String? = "",
//        val description: String? = "",
//        val imageUrl: String? = "",
//        val isPrivate: Boolean? = false,
//        val inviteLink: String? = "",
//        val adminIds: List<String>? = emptyList(),
//        val subscriberIds: List<String>? = emptyList(),
//        val messages: List<ChatMessage>? = emptyList(),
//        val isDirectMessage: Boolean? = false,

//        chatGroup.isDirectMessage(),
//        chatGroup.getName(),
//        chatGroup.getDescription(),
//        chatGroup.getImageUrl(),
//        chatGroup.getSubscriberIds(),
//        chatGroup.getAdminIds())


        // name
        // description
        // imageUrl
        // isPrivate
        // adminIds = current user id
        // subscriberIds = current user id + button to add users

        // ToDo: Image input

//        OutlinedTextField(
//            value = groupName,
//            onValueChange = { groupName = it },
//            label = { Text("Group Name") },
//            keyboardOptions = KeyboardOptions(
//                imeAction = ImeAction.Next
//            ),
//        )
    }
}

@Composable
fun PopupBox(
    popupWidth: Float,
    popupHeight:Float,
    showPopup: Boolean,
    onClickOutside: () -> Unit,
    content: @Composable() () -> Unit
) {
    if (showPopup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green) // Dim background()
                .zIndex(10F),
            contentAlignment = Alignment.Center
        ) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(excludeFromSystemGesture = true),
                onDismissRequest = { onClickOutside() }
            ) {
                content()
            }
        }
    }

    // ToDo: Add participants
//    Box(modifier = Modifier.zIndex(10F)) {
//        Popup(
//            alignment = Alignment.Center,
//            properties = PopupProperties(
//                excludeFromSystemGesture = true,
//            ),
//            // to dismiss on click outside
//            onDismissRequest = { onClickOutside() }
//        ) {
//
//        }
//     }
}

@Composable
fun PopupBoxContent(
    popupWidth: Float,
    popupHeight: Float,
    onDismiss: () -> Unit,
    onNext: (String, String?) -> Unit,
    groupName: String,
    groupImageUrl: String?,
    onGroupNameChange: (String) -> Unit = {},
) {

    Box(
        Modifier
            .width(popupWidth.dp)
            .height(popupHeight.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create New Group",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Group Image Selection
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clickable {
                            // In a real app, this would open an image picker
                            // groupImageUrl = "https://example.com/placeholder.jpg"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (groupImageUrl == null) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    } else {
                        // In a real app, you would load the actual image here
                        // For now, we'll just show a placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "IMG",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "Tap to add group photo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Group Name Input
                var isNameError by rememberSaveable { mutableStateOf(false) }
                OutlinedTextField(
                    value = groupName,
                    onValueChange = {
                        onGroupNameChange(it)
                        isNameError = false
                    },
                    label = { Text("Group Name") },
                    singleLine = true,
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text(
                                text = "Group name is required",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Cancel")
                    }

                    Button(
                        onClick = {
                            if (groupName.isBlank()) {
                                isNameError = true
                            } else {
                                onNext(groupName, groupImageUrl)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Next")
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 128.dp),
        color = Color.Gray
    )
}