package hr.tvz.android.chatapp.view.components

import android.app.appsearch.SearchResult
import android.app.appsearch.SearchResults
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hr.tvz.android.chatapp.TopAppBarState
import kotlinx.coroutines.launch
import kotlin.math.exp


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




@RequiresApi(Build.VERSION_CODES.S)
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
        title = { Text(title) },
        actions = {
            val searchBarState = rememberSearchBarState()
            val textFieldState = rememberTextFieldState()
            val scope = rememberCoroutineScope()

            val inputField =
                @Composable {
                    SearchBarDefaults.InputField(
                        modifier = Modifier,
                        searchBarState = searchBarState,
                        textFieldState = textFieldState,
                        onSearch = { scope.launch {
                            searchBarState.animateToCollapsed()
                            // searchViewModel.search(textFieldState.text)
                            // keyboardController?.hide()
                        } },
                        placeholder = { Text("Search...") },
                        leadingIcon = {
                            if (searchBarState.currentValue == SearchBarValue.Expanded) {
                                IconButton(
                                    onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                                ) {
                                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                                }
                            } else {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        },
                        trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = "More") },
                    )
                }
            SearchBar(
                state = searchBarState,
                inputField = inputField
            )
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = inputField
            ) {

            }
//            SearchBar(
//                inputField = {
//                    val textFieldState = rememberTextFieldState()
//                    val scope = rememberCoroutineScope()
//                    SearchBarDefaults.InputField(
//                        modifier = Modifier,
//                        query = searchInputText,
//                        onQueryChange = { searchInputText = it },
//                        expanded = isSearchIconClicked,
//                        onExpandedChange = { isSearchIconClicked = !isSearchIconClicked },
//                        onSearch = {
//                            // searchViewModel.search(searchInputText)
//                            keyboardController?.hide()
//                            // scope.launch { searchBarState.animateToCollapsed() }
//                        },
//                        placeholder = { Text("Search...") },
//                        leadingIcon = {
//                            if (activeSearch) {
//                                IconButton(
//                                    onClick = {
//                                        if (searchInputText.isNotEmpty()) {
//                                            searchInputText = ""
//                                        } else {
//                                            activeSearch = false
//                                            isSearchIconClicked = false
//                                        }
////                                        scope.launch { searchBarState.animateToCollapsed() }
//                                    }
//                                ) {
//                                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
//                                }
//                            } else {
//                                Icon(Icons.Default.Search, contentDescription = null)
//                            }
//                        },
//                        trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
//                    )
//                },
//                expanded = isSearchIconClicked,
//                onExpandedChange = {
//
//                },
//                content = {
//
//                }
//            )

//            AnimatedVisibility(visible = isSearchIconClicked.not()) {
//                Row(modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//                ) {
//                    Spacer(modifier = Modifier.weight(1f))
//                    IconButton(
//                        onClick = {
//                            isSearchIconClicked = true
//                        },
//                        modifier = Modifier.padding(10.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Search,
//                            contentDescription = "Search icon",
//                            modifier = Modifier.height(33.dp)
//                        )
//                    }
//                }
//            }
//            AnimatedVisibility(visible = isSearchIconClicked) {
//                SearchBar(
//                    inputField = {
//
//                    },
//                    expanded = true,
//                    onExpandedChange = {
//
//                    },
//                    content = {
//
//                    }
//                )
//
//                SearchBar(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    query = searchInputText,
//                    onQueryChange = { searchInputText = it },
//                    onSearch = {
////                        searchViewModel.search(searchInputText)
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


