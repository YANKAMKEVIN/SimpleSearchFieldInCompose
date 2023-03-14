package com.kevin.simplesearchfieldcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
class MainViewModel: ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _persons = MutableStateFlow(allPersons)
    val persons = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_persons) { text, persons ->
            if(text.isBlank()) {
                persons
            } else {
                delay(2000L)
                persons.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _persons.value
        )

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