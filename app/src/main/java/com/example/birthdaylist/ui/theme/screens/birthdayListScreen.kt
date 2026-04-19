package com.example.birthdaylist.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayListScreen(modifier: Modifier = Modifier) {

    // Midlertidig dummy-data (REST API kommer senere)
    val persons = listOf(
        "Malyun – 23/07",
        "Signe – 2/01"

    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Birthdays") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add friend */ }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(persons) { person ->
                BirthdayRow(name = person)
            }
        }
    }
}

@Composable
fun BirthdayRow(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(16.dp)
        )
    }
}