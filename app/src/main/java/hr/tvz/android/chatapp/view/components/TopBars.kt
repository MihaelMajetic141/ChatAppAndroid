package hr.tvz.android.chatapp.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hr.tvz.android.chatapp.TopAppBarState


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithSearchAndBackArrow(
    topAppBarState: MutableState<TopAppBarState>,
    navController: NavController,
    // ToDo: searchViewModel: SearchViewModel,
    title: String,
) {
    var isSearchIconClicked by remember { mutableStateOf(false) }
    var searchInputText by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current


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
        actions = {
            AnimatedVisibility(visible = isSearchIconClicked.not()) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            isSearchIconClicked = true
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            modifier = Modifier.height(33.dp)
                        )
                    }
                }
            }

//            AnimatedVisibility(visible = isSearchIconClicked) {
//                SearchBar(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    query = searchInputText,
//                    onQueryChange = { searchInputText = it },
//                    onSearch = {
//                        searchViewModel.search(searchInputText)
//                        keyboardController?.hide()
//                    },
//                    active = activeSearch,
//                    onActiveChange = { activeSearch = it },
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.Search,
//                            contentDescription = "Search",
//                            Modifier.clickable(onClick = {
//                                searchViewModel.search(searchInputText)
//                                keyboardController?.hide()
//                            })
//                        )
//                    },
//                    trailingIcon = {
//                        if (activeSearch) {
//                            Icon(
//                                imageVector = Icons.Default.Close,
//                                contentDescription = "Clear",
//                                modifier = Modifier.clickable {
//                                    if (searchInputText.isNotEmpty()) {
//                                        searchInputText = ""
//                                    } else {
//                                        activeSearch = false
//                                        isSearchIconClicked = false
//                                    }
//                                }
//                            )
//                        }
//                    },
//                    content = {
//
//                    }
//                )
//            }
        }
    )
}


