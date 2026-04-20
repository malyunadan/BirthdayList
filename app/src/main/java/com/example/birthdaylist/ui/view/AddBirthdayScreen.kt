package com.example.birthdaylist.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birthdaylist.data.model.PersonDto
import com.example.birthdaylist.viewmodel.AuthViewModel
import com.example.birthdaylist.viewmodel.PersonsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddBirthdayScreen(
    navController: NavController,
    personsViewModel: PersonsViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var birthDay by remember { mutableStateOf("") }
    var birthMonth by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val isLoading by personsViewModel.isLoading.collectAsState()
    val errorMessage by personsViewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tilføj fødselsdag") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Navn") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = birthDay,
                    onValueChange = { birthDay = it },
                    label = { Text("Dag (1-31)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = birthMonth,
                    onValueChange = { birthMonth = it },
                    label = { Text("Måned (1-12)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = birthYear,
                    onValueChange = { birthYear = it },
                    label = { Text("År") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Bemærkninger (valgfrit)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val userId = authViewModel.getUserId() ?: ""
                    val newPerson = PersonDto(
                        userId = userId,
                        name = name,
                        birthYear = birthYear.toIntOrNull() ?: 0,
                        birthMonth = birthMonth.toIntOrNull() ?: 0,
                        birthDayOfMonth = birthDay.toIntOrNull() ?: 0,
                        remarks = if (remarks.isBlank()) null else remarks
                    )
                    personsViewModel.addPerson(newPerson) { success ->
                        if (success) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && name.isNotBlank() && birthDay.isNotBlank() && birthMonth.isNotBlank() && birthYear.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Gem fødselsdag")
                }
            }
        }
    }
}
