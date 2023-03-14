package com.kevin.simplesearchfieldcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kevin.simplesearchfieldcompose.ui.theme.SearchFieldComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Set up the theme for the app
            SearchFieldComposeTheme {
                val viewModel = viewModel<MainViewModel>()
                // Collect the current search text from the view model as a state
                val searchText by viewModel.searchText.collectAsState()
                // Collect the current list of persons that match the search query from the view model as a state
                val persons by viewModel.persons.collectAsState()
                // Collect the current search status (whether a search is in progress) from the view model as a state
                val isSearching by viewModel.isSearching.collectAsState()
                // Collect the current isNoResults boolean from the view model as a state
                val isNoResults by viewModel.isNoResultsFound.collectAsState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Text field for entering search query
                    TextField(
                        value = searchText,
                        onValueChange = viewModel::onSearchTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "Search") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // If a search is in progress, show a progress indicator
                    if (isSearching) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else if (isNoResults) {
                        //if the query entered is not found, show a text indicator
                        Text("Not found... please retry another name")
                    } else {
                        // Otherwise, show a lazy column with the list of persons that match the search query
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(persons) { person ->
                                Text(
                                    text = "${person.firstName} ${person.lastName}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}