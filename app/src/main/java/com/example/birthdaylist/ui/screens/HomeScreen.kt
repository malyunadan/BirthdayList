package com.example.birthdaylist.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birthdaylist.viewmodel.AuthViewModel
import com.example.birthdaylist.viewmodel.PersonsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
// @RequiresApi sikrer, at koden kun kører på enheder med Android Oreo (API 24) eller nyere pga. datohåndtering
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel(),
    personsViewModel: PersonsViewModel = koinViewModel()
) {
    val persons by personsViewModel.persons.collectAsState() 
    val isLoading by personsViewModel.isLoading.collectAsState() 
    val errorMessage by personsViewModel.errorMessage.collectAsState() 

    var searchQuery by remember { mutableStateOf("") }
    var ageFilter by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("name") } 
    var sortOrder by remember { mutableStateOf("ASC") }

    val userId = authViewModel.getUserId() ?: ""

    // Hjælpefunktion til at hente data
    val refreshData = {
        personsViewModel.loadPersons(
            userId = userId,
            name = if (searchQuery.isBlank()) null else searchQuery,
            age = ageFilter.toIntOrNull(),
            sortBy = sortBy,
            sortOrder = sortOrder
        )
    }

    // Automatisk opdatering ved søgning/sortering
    LaunchedEffect(searchQuery, ageFilter, sortBy, sortOrder) {
        if (searchQuery.isNotEmpty() || ageFilter.isNotEmpty()) {
            delay(300) 
        }
        refreshData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Fødselsdage", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = {
                authViewModel.logout()
                navController.navigate("login") { popUpTo("home") { inclusive = true } }
            }) {
                Text("Log ud")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FILTRERING
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(2f),
                placeholder = { Text("Navn...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            OutlinedTextField(
                value = ageFilter,
                onValueChange = { ageFilter = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Alder") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SORTERING
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = sortBy == "name", onClick = { sortBy = "name" }, label = { Text("Navn") })
            FilterChip(selected = sortBy == "age", onClick = { sortBy = "age" }, label = { Text("Alder") })
            FilterChip(selected = sortBy == "birthMonth", onClick = { sortBy = "birthMonth" }, label = { Text("Dato") })
            
            Spacer(modifier = Modifier.weight(1f))
            
            TextButton(onClick = { sortOrder = if (sortOrder == "ASC") "DESC" else "ASC" }) {
                Text(if (sortOrder == "ASC") "↑" else "↓")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PULL TO REFRESH BOX
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { refreshData() },
            modifier = Modifier
                .weight(1f)
                .testTag("pull_to_refresh")
        ) {
            // Update kunne ikke lige nå at få den til at virke
            // Jeg bruger en LazyColumn til ALT indholdet, så hele arealet altid er scrollbart.
            // Dette sikrer at Pull-to-refresh virker hver gang.
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                
                if (errorMessage != null) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                } else if (persons.isEmpty() && !isLoading) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Ingen resultater fundet.")
                        }
                    }
                } else {
                    // Vis listen af fødselsdage
                    items(persons) { person ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { navController.navigate("details/${person.id}") }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(person.name, style = MaterialTheme.typography.titleMedium)
                                Text("Fødselsdag: ${person.birthday}")
                                Text("Alder: ${person.age} år")
                                Text("Om ${person.daysUntilBirthday} dage")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("addBirthday") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tilføj fødselsdag")
        }
    }
}
