package com.kevin.simplesearchfieldcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
class MainViewModel: ViewModel() {
    // Create a private mutable state flow to hold the user's search text
    private val _searchText = MutableStateFlow("")

    // Create a public immutable state flow to expose the user's search text
    val searchText = _searchText.asStateFlow()

    // Create a private mutable state flow to hold the search status
    private val _isSearching = MutableStateFlow(false)

    // Create a public immutable state flow to expose the search status
    val isSearching = _isSearching.asStateFlow()

    // Create a private mutable state flow to hold the result status
    private val _isNoResultsFound = MutableStateFlow(false)

    // Create a public immutable state flow to hold the results status status
    val isNoResultsFound = _isNoResultsFound.asStateFlow()

    // Create a private mutable state flow to hold the list of persons
    private val _persons = MutableStateFlow(allPersons)

    // Create a public immutable state flow to expose the filtered list of persons
    val persons = searchText
        // Debounce the search text flow for 1 second to reduce the number of queries made while the user types
        .debounce(1000L)
        .onEach { _isSearching.update { true } }

        // Combine the search text and persons flows to filter the list of persons based on the search text
        .combine(_persons) { text, persons ->
            if(text.isBlank()) {
                persons
            } else {
                // If the search text is not empty, delay for 2 seconds to reduce the number of queries made while the user types,
                // then filter the list of persons to only include those that match the search text
                delay(2000L)
                val matchingPersons= persons.filter {
                    it.doesMatchSearchQuery(text)
                }
                _isNoResultsFound.value=matchingPersons.isEmpty()
                matchingPersons
            }
        }
        .onEach { _isSearching.update { false } }
        // Create an immutable state flow that shares the latest value with all subscribers,
        // and uses the last value of the private mutable state flow as the initial value
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _persons.value
        )

    // Create a function to update the search text
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}

// Definition of the Person data class with two String properties: first name and last name
data class Person(
    val firstName: String,
    val lastName: String
) {
    // Function that takes a search query string as input and returns a boolean indicating whether the person matches the query
    fun doesMatchSearchQuery(query: String): Boolean {
        // List of string combinations that could match the person
        val matchingCombinations = listOf(
            "$firstName$lastName",
            "$firstName $lastName",

            // Combination of the first initial and last initial with a space between them
            "${firstName.first()} ${lastName.first()}",
        )

        // Check if the query string is contained in any of the generated string combinations
        // If the query string is contained in any of the combinations, return true, otherwise return false
        // The comparison is performed by ignoring the case of the query string using the ignoreCase = true option
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}


private val allPersons = listOf(
    Person(
        firstName = "Kevin",
        lastName = "Yankam"
    ),
    Person(
        firstName = "Emmanuel",
        lastName = "Macron"
    ),
    Person(
        firstName = "Johnny",
        lastName = "Bravo"
    ),
    Person(
        firstName = "Barack",
        lastName = "Obama"
    ),    Person(
        firstName = "Julien",
        lastName = "Sizorn"
    ),    Person(
        firstName = "Pierre",
        lastName = "Issartel"
    ),    Person(
        firstName = "Omar",
        lastName = "Arab"
    ),    Person(
        firstName = "Laetitia",
        lastName = "Casta"
    ),    Person(
        firstName = "Celine",
        lastName = "Dion"
    ),    Person(
        firstName = "Samuel",
        lastName = "Eto"
    ),
)