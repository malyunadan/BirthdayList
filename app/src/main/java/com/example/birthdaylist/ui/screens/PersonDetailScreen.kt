package com.example.birthdaylist.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birthdaylist.data.model.PersonDto
import com.example.birthdaylist.viewmodel.PersonsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonDetailScreen(
    personId: Int,
    navController: NavController,
    viewModel: PersonsViewModel = koinViewModel()
) {
    var person by remember { mutableStateOf<PersonDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(personId) {
        viewModel.loadPersonById(personId) { fetchedPerson ->
            person = fetchedPerson
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detaljer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                    }
                },
                actions = {
                    // REDIGER KNAP
                    IconButton(onClick = { navController.navigate("edit/$personId") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Rediger")
                    }
                    // SLET KNAP
                    IconButton(onClick = {
                        viewModel.deletePerson(personId) { success ->
                            if (success) {
                                navController.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Slet")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (person != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(text = person!!.name, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailRow(label = "Fødselsdato:", value = person!!.birthday)
                DetailRow(label = "Alder:", value = "${person!!.age ?: "Ukendt"} år")
                DetailRow(label = "Dage til fødselsdag:", value = "${person!!.daysUntilBirthday} dage")
                
                if (!person!!.remarks.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Bemærkninger:", style = MaterialTheme.typography.titleMedium)
                    Text(text = person!!.remarks!!, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Kunne ikke finde personen.")
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
