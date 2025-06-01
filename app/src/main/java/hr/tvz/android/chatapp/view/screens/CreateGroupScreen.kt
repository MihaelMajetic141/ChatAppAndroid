package hr.tvz.android.chatapp.view.screens

import android.R.attr.data
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import hr.tvz.android.chatapp.BuildConfig
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.view.components.ContactListItem
import hr.tvz.android.chatapp.view.components.TopBarWithBackArrow
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.viewmodel.CreateGroupViewState
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel

@Composable
fun CreateGroupScreen(
    navController: NavController,
    topAppBarState: MutableState<TopAppBarState>,
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val currentUserId by dataStore.userId.collectAsState("")
    val username by dataStore.userName.collectAsState(initial = "")
    val email by dataStore.userEmail.collectAsState(initial = "")
    val accessToken by dataStore.accessToken.collectAsState(initial = "")
    val loadContactsViewState by newConversationViewModel.loadContactsViewState.collectAsState()
    val createGroupViewState by newConversationViewModel.createGroupViewState.collectAsState()
    val groupName by newConversationViewModel.groupName.collectAsState()
    val groupImageUri by newConversationViewModel.groupImageUri.collectAsState()
    val groupMembers by newConversationViewModel.selectedContacts.collectAsState()

    LaunchedEffect(key1 = currentUserId != "") {
        newConversationViewModel.getContacts(currentUserId ?: "")
    }

    TopBarWithBackArrow(
        topAppBarState = topAppBarState,
        navController = navController,
        title = "Create Group"
    )

    Row(Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
        ImageInput(
            onImageSelected = { uri -> newConversationViewModel.setGroupImage(uri) },
            modifier = Modifier.size(50.dp).weight(1f)
        )
        OutlinedTextField(
            value = groupName,
            onValueChange = { newConversationViewModel.setGroupName(it) },
            label = { Text("Group name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }

    when(val viewState = loadContactsViewState) {
        is LoadContactsViewState.Error -> {
            Text(viewState.message)
        }
        is LoadContactsViewState.Loading -> {
            CircularProgressIndicator()
        }
        is LoadContactsViewState.Success -> {
            // ToDo: Check if this should be in ViewModel.
            val selectedContacts = remember { mutableStateListOf<ContactDTO>() }

            Column {
                // ToDo: If not working replace with AnimatedVisibility(selectedContacts.isNotEmpty())
                if (selectedContacts.isNotEmpty()) {
                    Text("Selected Contacts:")
                    LazyRow {
                        items(selectedContacts) { contact ->
                            Column {
                                SubcomposeAsyncImage(
                                    model = "${BuildConfig.SERVER_IP}/api/media/${contact.imageFileId}",
                                    contentDescription = "Contact image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable(onClick = { }) // ToDo: Remove from selected
                                        .padding(8.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                                Text(contact.username)
                            }
                        }
                    }
                }

                //ToDo: Check if empty
                LazyColumn {
                    items(viewState.contactList) { contact ->
                        val isSelected = selectedContacts.contains(contact)
                        ContactListItem(
                            contactDTO = contact,
                            subHeader = contact.status ?: "",
                            isSelected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    selectedContacts.remove(contact)
                                } else {
                                    selectedContacts.add(contact)
                                }
                            },
                            onLongClick = {},
                            modifier = Modifier
                        )
                    }
                }

                IconButton(
                    modifier = Modifier.size(50.dp),
                    onClick = {
                        val adminIdList = mutableListOf(currentUserId ?: "") // ToDo: Add others later
                        newConversationViewModel.createGroup(
                            groupName = groupName,
                            groupImage = groupImageUri,
                            groupMemberIds = groupMembers.map { it.userInfoId },
                            adminIds = adminIdList.toList()
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Create group button",
                    )
                }

                when(createGroupViewState) {
                    is CreateGroupViewState.Error -> {
                        Text("Error: " +
                                (createGroupViewState as CreateGroupViewState.Error).message)
                    }
                    is CreateGroupViewState.Loading -> {
                        Text("Loading...")
                    }
                    is CreateGroupViewState.Success -> {
                        Toast.makeText(
                            LocalContext.current,
                            "New group created!",
                            Toast.LENGTH_SHORT).show()
                        navController.navigate(
                            route = Routes.Chat.route +
                                "/${(createGroupViewState as CreateGroupViewState.Success).groupId}"
                        ) {
                            popUpTo(Routes.Home.route) {
                                inclusive = true
                            }
                        }
                    }
                }

            }

        }
    }



    // ToDo: Participants section.

    // ToDo: create group floating button
    

}


@Composable
fun ImageInput(
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                onImageSelected(it) // Notify parent of the selected URI
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }
        Spacer(modifier = modifier.height(16.dp))
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(data),
                contentDescription = "Selected Image",
                modifier = modifier.size(200.dp)
            )
        } ?: Text("No image selected")
    }
}