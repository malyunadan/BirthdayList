package com.example.birthdaylist.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.birthdaylist.data.model.PersonDto
import com.example.birthdaylist.viewmodel.PersonsViewModel
import org.koin.androidx.compose.koinViewModel

// Denne skærm gør det muligt at redigere en eksisterende person i listen.
// Den ligner AddBirthdayScreen, men med den forskel at felterne er forudfyldt med data.
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditPersonScreen(
    personId: Int, // ID på den person der skal redigeres, sendes med fra navigationen
    navController: NavController,
    viewModel: PersonsViewModel = koinViewModel()
) {
    // --- LOKAL UI STATE (HOLDER PÅ INDTASTNINGERNE) ---
    // Vi bruger 'remember', så Compose husker teksten i felterne undervejs.
    var name by remember { mutableStateOf("") }
    var birthDay by remember { mutableStateOf("") }
    var birthMonth by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") } // Vi skal gemme userId så det ikke går tabt ved opdatering

    // To forskellige loading-tilstande:
    // 1. Når vi henter den eksisterende data fra API'et
    var isLoadingData by remember { mutableStateOf(true) }
    // 2. Når vi gemmer de nye ændringer tilbage til API'et
    val isLoadingSave by viewModel.isLoading.collectAsState()

    // --- HENT DATA NÅR SKÆRMEN STARTER ---
    // 'LaunchedEffect' kører én gang når skærmen åbner.
    LaunchedEffect(personId) {
        viewModel.loadPersonById(personId) { person ->
            // Hvis vi finder personen, udfylder vi felterne med den nuværende data
            person?.let {
                name = it.name
                birthDay = it.birthDayOfMonth.toString()
                birthMonth = it.birthMonth.toString()
                birthYear = it.birthYear.toString()
                remarks = it.remarks ?: ""
                userId = it.userId
            }
            // Stop med at vise loading-spinner for selve hentningen
            isLoadingData = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rediger person") },
                // En tilbage-pil i øverste venstre hjørne
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        // Hvis vi stadig henter data fra API'et, vis en spinner i midten
        if (isLoadingData) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Selve formularen med input-felter
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // NAVN FELT
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Navn") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoadingSave
                )

                // RÆKKE MED DAG, MÅNED OG ÅR
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = birthDay,
                        onValueChange = { birthDay = it },
                        label = { Text("Dag") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoadingSave
                    )
                    OutlinedTextField(
                        value = birthMonth,
                        onValueChange = { birthMonth = it },
                        label = { Text("Måned") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoadingSave
                    )
                    OutlinedTextField(
                        value = birthYear,
                        onValueChange = { birthYear = it },
                        label = { Text("År") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isLoadingSave
                    )
                }

                // BEMÆRKNINGER FELT
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Bemærkninger") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoadingSave
                )

                Spacer(modifier = Modifier.height(8.dp))

                // GEM-KNAP
                Button(
                    onClick = {
                        // Vi opretter et nyt PersonDto objekt med de rettede data.
                        // Vigtigt: Vi sender personId med, så serveren ved hvem der skal opdateres.
                        val updatedPerson = PersonDto(
                            id = personId,
                            userId = userId,
                            name = name,
                            birthYear = birthYear.toIntOrNull() ?: 0,
                            birthMonth = birthMonth.toIntOrNull() ?: 0,
                            birthDayOfMonth = birthDay.toIntOrNull() ?: 0,
                            remarks = if (remarks.isBlank()) null else remarks
                        )
                        // Kalder ViewModel'ens update funktion
                        viewModel.updatePerson(personId, updatedPerson) { success ->
                            if (success) {
                                // Hvis det lykkedes, går vi tilbage til den forrige skærm
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    // Knappen er deaktiveret hvis vi er ved at gemme, eller hvis navnet er tomt
                    enabled = !isLoadingSave && name.isNotBlank()
                ) {
                    if (isLoadingSave) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Gem ændringer")
                    }
                }
            }
        }
    }
}
