package hr.tvz.android.chatapp.view.screens

import android.R.attr.data
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import hr.tvz.android.chatapp.BottomNavItem
import hr.tvz.android.chatapp.R
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.data.routes.Routes
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.viewmodel.CreateGroupViewState
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    navController: NavController,
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
) {
    val dataStore = DataStoreManager(LocalContext.current)
    val currentUserId by dataStore.userId.collectAsState("")

    val loadContactsViewState by newConversationViewModel.loadContactsViewState.collectAsState()
    val createGroupViewState by newConversationViewModel.createGroupViewState.collectAsState()
    val groupName by newConversationViewModel.groupName.collectAsState()
    val groupImageUri by newConversationViewModel.groupImageUri.collectAsState()
    val selectedGroupMembers by newConversationViewModel.selectedContacts.collectAsState()

    val showSearchBar = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val showErrorPopUp = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = currentUserId != "") {
        newConversationViewModel.getContacts(currentUserId ?: "")
    }

    when (val viewState = loadContactsViewState) {
        is LoadContactsViewState.Loading -> {
            CircularProgressIndicator()
        }

        is LoadContactsViewState.Success -> {
            val contacts by remember(searchQuery.value, viewState.contactList) {
                derivedStateOf {
                    if (searchQuery.value.isBlank()) {
                        viewState.contactList.toList()
                    } else {
                        viewState.contactList.filter {
                            it.username.contains(searchQuery.value.trim(), ignoreCase = true)
                        }
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                Column {
                    TopBarWithContactSearch(
                        showSearchBar = showSearchBar,
                        searchQuery = searchQuery,
                        contacts = contacts,
                        onBackClick = { navController.popBackStack() }
                    )
                    CreateGroupInputFields(
                        newConversationViewModel = newConversationViewModel,
                        groupName = groupName
                    )
                    SelectedContactsRow(
                        selectedContacts = selectedGroupMembers,
                        onRemoveContact = { contact ->
                            newConversationViewModel.removeContactFromList(contact)
                        },
                        modifier = Modifier.padding(top = 5.dp)
                    )
                    ContactsLazyRow(
                        contacts = contacts,
                        selectedContacts = selectedGroupMembers,
                        onToggleContact = { contact ->
                            newConversationViewModel.toggleSelectedContact(contact)
                        }
                    )
                }

                FloatingActionButton(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.BottomEnd)
                        .offset((-25).dp, (-85).dp),
                    onClick = {
                        if (groupName.isNotEmpty() && selectedGroupMembers.isNotEmpty()) {
                            newConversationViewModel.createGroup(
                                groupName = groupName,
                                groupImage = groupImageUri,
                                groupMemberIds = (selectedGroupMembers.map { it.userInfoId })
                                        + listOfNotNull(currentUserId),
                                adminIds = listOfNotNull(currentUserId)
                            )
                        } else {
                            navController.navigate(Routes.NewConversation.route) {
                                popUpTo(0)
                            }
                        }
                    }
                ) {
                    if (groupName.isNotEmpty() && selectedGroupMembers.isNotEmpty()) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Create group icon")
                    } else {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Navigate back icon")
                    }
                }
            }
        }

        is LoadContactsViewState.Error -> {
            Text(viewState.message)
        }
    }
    when(createGroupViewState) {
        is CreateGroupViewState.Error -> {
            AnimatedVisibility(
                visible = showErrorPopUp.value
            ) {
                BasicAlertDialog(
                    onDismissRequest = { showErrorPopUp.value = false },
                    modifier = Modifier,
                    properties = DialogProperties(),
                    content = {
                        Text((createGroupViewState as CreateGroupViewState.Error).message)
                    }
                )
            }
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


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBarWithContactSearch(
    showSearchBar: MutableState<Boolean>,
    searchQuery: MutableState<String>,
    contacts: List<ContactDTO>,
    onBackClick: () -> Unit
) {
    AnimatedVisibility(visible = !showSearchBar.value) {
        TopAppBar(
            title = {
                Text("Create contact")
            },
            navigationIcon = {
                IconButton(
                    onClick = { onBackClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back arrow"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        showSearchBar.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Toggle search bar",
                    )
                }
            }
        )
    }
    AnimatedVisibility(visible = showSearchBar.value) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            state = rememberSearchBarState(),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery.value,
                    onQueryChange = { searchQuery.value = it },
                    onSearch = { },
                    expanded = true,
                    onExpandedChange = { },
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                showSearchBar.value = false
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = "Back arrow"
                                )
                            }
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                contacts.filter {
                                    it.username.contains(
                                        searchQuery.value.trim(),
                                        ignoreCase = true
                                    )
                                }
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search button"
                                )
                            }
                        )
                    }
                )
            }
        )
    }
}


@Composable
fun ImageInput(
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                onImageSelected(it)
            }
        }
    )

    AnimatedVisibility(selectedImageUri != null) {
        IconButton(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = modifier.size(75.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )
        }
    }

    AnimatedVisibility(selectedImageUri == null) {
        IconButton(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = modifier.size(100.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.user),
                contentDescription = "Select group image",
                modifier = Modifier.size(100.dp)
            )
        }
    }

}

@Composable
fun CreateGroupInputFields(
    newConversationViewModel: NewConversationViewModel,
    groupName: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.height(80.dp)
    ) {
        ImageInput(
            onImageSelected = { uri -> newConversationViewModel.setGroupImage(uri) },
            modifier = Modifier.padding(5.dp)
        )
        OutlinedTextField(
            value = groupName,
            onValueChange = { newConversationViewModel.setGroupName(it) },
            label = { Text("Group name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        )
    }
}

@Composable
fun SelectedContactsRow(
    selectedContacts: List<ContactDTO>,
    onRemoveContact: (ContactDTO) -> Unit,
    modifier: Modifier
) {
    AnimatedVisibility(visible = selectedContacts.isNotEmpty()) {
        LazyRow {
            items(selectedContacts) { contact ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(10.dp)
                        .defaultMinSize(minWidth = 33.dp)
                ) {
//                            SubcomposeAsyncImage(
//                                model = "${BuildConfig.SERVER_IP}/api/media/${contact.imageFileId}",
//                                contentDescription = "Contact image",
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                                    .clickable(onClick = { })
//                                    .padding(8.dp)
//                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
//                            )
                    Box(contentAlignment = Alignment.TopEnd) {
                        Image(
                            painter = painterResource(R.drawable.user),
                            contentDescription = "",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onRemoveContact(contact) }
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove selected contact",
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Text(contact.username)
                }
            }
        }
    }
}

@Composable
fun ContactsLazyRow(
    contacts: List<ContactDTO>,
    selectedContacts: List<ContactDTO>,
    onToggleContact: (ContactDTO) -> Unit,
) {
    LazyColumn {
        contacts.forEach() { contact ->
            val isSelected = selectedContacts.contains(contact)
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable { onToggleContact(contact) }
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.user),
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Column(Modifier.padding(horizontal = 10.dp)) {
                        Text(
                            text = contact.username,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                        )
                        Text(
                            text = contact.status ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}


@Preview
@Composable
fun CreateGroupScreenPreview() {
    val selectedContacts = remember { mutableStateListOf<String>() }
    val allContacts = remember {
        mutableStateListOf<String>().apply {
            for (i in 0..5) {
                add("Contact $i")
            }
        }
    }

    Box(Modifier.fillMaxSize().background(color = Color.White)) {
        Column(Modifier.padding(horizontal = 5.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.height(50.dp)
            ) {
                ImageInput(
                    onImageSelected = {  },
                    modifier = Modifier
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {  },
                    label = { Text("Group name") },
                    modifier = Modifier
                )
            }

            AnimatedVisibility(visible = selectedContacts.isNotEmpty()) {
                Row {
                    selectedContacts.forEach() { contact ->
                        Column(Modifier.padding(10.dp)) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterHorizontally)
                                    .clickable { selectedContacts.remove(contact) }
                            )
                            Text(contact)
                        }
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                items(allContacts) { contact ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            if (!selectedContacts.contains(contact)) {
                                selectedContacts.add(contact)
                            } else selectedContacts.remove(contact)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Column(Modifier.padding(horizontal = 10.dp)) {
                            Text(
                                text = contact,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                            )
                            Text(
                                text = "Status",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.BottomEnd)
                .offset((-25).dp, (-75).dp),
            onClick = { },
        ) {
            Icon(Icons.Filled.Add, "Localized description")
        }

    }
}