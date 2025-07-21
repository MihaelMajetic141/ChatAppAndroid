package hr.tvz.android.chatapp.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.data.DataStoreManager
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.view.components.TopBarWithContactsSearch
import hr.tvz.android.chatapp.viewmodel.LoadContactsViewState
import hr.tvz.android.chatapp.viewmodel.NewConversationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContactScreen(
    navController: NavController,
    newConversationViewModel: NewConversationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val currentUserId by dataStore.userId.collectAsState(initial = "")
    val loadContactsViewState by newConversationViewModel.loadContactsViewState.collectAsState()
    // val selectedContacts by newConversationViewModel.selectedContacts.collectAsState()

    val showSearchBar = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val selectedContacts = remember { mutableStateListOf<ContactDTO>() }

    //ToDo: Create contact form, for now just all users list with checkboxes

    LaunchedEffect(Unit) {
        newConversationViewModel.getAllUsers()
    }

    when(val viewState = loadContactsViewState) {
        is LoadContactsViewState.Error -> {
            Text(text = viewState.message)
        }

        LoadContactsViewState.Loading -> {
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

            Box(modifier = Modifier.fillMaxSize()) {
                Column() {
                    TopBarWithContactsSearch(
                        title = "Create contact",
                        showSearchBar = showSearchBar,
                        searchQuery = searchQuery,
                        contactList = viewState.contactList,
                        navController = navController
                    )
                    AnimatedVisibility(visible = selectedContacts.isNotEmpty()) {
                        Row {
                            selectedContacts.forEach() { contact ->
                                Column(Modifier.padding(horizontal = 10.dp)) {
                                    // SubcomposeAsyncImage(model = "...", contentDescription = "")
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .align(Alignment.CenterHorizontally)
                                            .clickable { selectedContacts.remove(contact) }
                                    )
                                    Text(contact.username)
                                }
                            }
                        }
                    }
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                    ) {
                        contacts.forEach() {
                            item {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        if (!selectedContacts.contains(it)) {
                                            selectedContacts.add(it)
                                        } else selectedContacts.remove(it)
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
                                            text = it.username,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                        )
                                        Text(
                                            text = it.status ?: "",
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
                FloatingActionButton(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.BottomEnd)
                        .offset((-75).dp, (-75).dp),
                    onClick = {
                        newConversationViewModel.addNewContacts(
                            contactIdList = selectedContacts.map { it.userInfoId },
                            currentUserId = currentUserId ?: "")
                    },
                ) {
                    Icon(Icons.Filled.Add, "Localized description")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateContactScreenPreview() {
    val showSearchBar = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val selectedContacts = remember { mutableStateListOf<String>() }
    val allContacts = remember {
        mutableStateListOf<String>().apply {
            for (i in 0..5) {
                add("Contact $i")
            }
        }
    }
    val contacts by remember(searchQuery.value, allContacts) {
        derivedStateOf {
            if (searchQuery.value.isBlank()) {
                allContacts.toList()
            } else {
                allContacts.filter { it.contains(searchQuery.value.trim(), ignoreCase = true) }
            }
        }
    }

    Box(Modifier
        .fillMaxSize()
        .background(Color.White),
    ) {
        Column() {
            AnimatedVisibility(visible = !showSearchBar.value) {
                TopAppBar(
                    title = {
                        Text("Create contact")
                    },
                    navigationIcon = {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                showSearchBar.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "",
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
                                        allContacts.filter {
                                            it.contains(searchQuery.value)
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
            AnimatedVisibility(visible = selectedContacts.isNotEmpty()) {
                Row {
                    selectedContacts.forEach() { contact ->
                        Column(Modifier.padding(horizontal = 10.dp)) {
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
                contacts.forEach() {
                    item {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                if (!selectedContacts.contains(it)) {
                                    selectedContacts.add(it)
                                } else selectedContacts.remove(it)
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
                                    text = it,
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