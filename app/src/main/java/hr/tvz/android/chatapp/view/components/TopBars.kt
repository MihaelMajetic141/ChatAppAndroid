package hr.tvz.android.chatapp.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import hr.tvz.android.chatapp.TopAppBarState
import hr.tvz.android.chatapp.data.dto.ContactDTO
import hr.tvz.android.chatapp.data.dto.ConversationDTO


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithConversationsSearch(
    title: String,
    showSearchBar: MutableState<Boolean>,
    searchQuery: MutableState<String>,
    conversationList: List<ConversationDTO>,
    navController: NavController
) {
    AnimatedVisibility(visible = !showSearchBar.value) {
        TopAppBar(
            title = {
                Text(title)
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate(0) }
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
                                conversationList.filter {
                                    it.name.contains(searchQuery.value)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithContactsSearch(
    title: String,
    showSearchBar: MutableState<Boolean>,
    searchQuery: MutableState<String>,
    contactList: List<ContactDTO>,
    navController: NavController
) {
    AnimatedVisibility(visible = !showSearchBar.value) {
        TopAppBar(
            title = {
                Text(title)
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate(0) }
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
                                contactList.filter {
                                    it.username.contains(searchQuery.value)
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
fun TopBarWithBackArrow(
    topAppBarState: MutableState<TopAppBarState>,
    navController: NavController,
    title: String,
) {
    topAppBarState.value = TopAppBarState(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                content = {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back Button")
                }
            )
        },
        actions = {}
    )
}


