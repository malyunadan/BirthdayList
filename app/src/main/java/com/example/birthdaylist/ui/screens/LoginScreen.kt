package com.example.birthdaylist.ui.screens

// Her henter vi de nødvendige biblioteker til UI, Navigation og Dependency Injection (Koin)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.birthdaylist.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

//---------------------------------------------------------------
//  LOGIN SCREEN (HOVEDSKÆRM)
//---------------------------------------------------------------
/**
 * Denne skærm fungerer som en "container". Den henter logikken (ViewModel)
 * og sender den videre til selve designet (LoginContent).
 */
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel() // Henter AuthViewModel automatisk via Koin
) {
    // den overvåger (observerer) fejlbeskeder fra Firebase gennem vores ViewModel
    val errorMessage by authViewModel.errorMessage.collectAsState()
    
    // så kaldes LoginContent og fortæller den hvad der skal ske når man klikker
    LoginContent(
        navController = navController,
        errorMessage = errorMessage,
        onLoginClick = { email, password ->
            // Kalder ViewModel'ens login-funktion
            authViewModel.login(email, password) { success ->
                if (success) {
                    // Hvis login lykkedes, naviger til hjemmeskærmen og ryd historikken
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        },
        onNavigateToRegister = { navController.navigate("register") }
    )
}

//---------------------------------------------------------------
//  LOGIN CONTENT (UI DESIGN)
//---------------------------------------------------------------
/**
 * Denne funktion indeholder selve designet af skærmen. 
 * Den er adskilt fra ViewModel logikken, så vi nemt kan vise den i et @Preview.
 */
@Composable
fun LoginContent(
    navController: NavController,
    errorMessage: String?, // Eventuel fejlbesked fra Firebase
    onLoginClick: (String, String) -> Unit, // Funktion der kører ved klik på Log ind
    onNavigateToRegister: () -> Unit // Funktion der skifter til opret-konto
) {
    // Lokal tilstand til at huske hvad brugeren taster i felterne
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Column placerer elementerne oven på hinanden lodret
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center, // Centrerer indholdet lodret
        horizontalAlignment = Alignment.CenterHorizontally // Centrerer indholdet vandret
    ) {
        // OVERSKRIFT
        Text(
            text = "Log ind",
            style = MaterialTheme.typography.headlineMedium,
            // testTag bruges af vores UI-test til at finde dette specifikke element
            modifier = Modifier.testTag("login_title")
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- EMAIL INPUT ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it }, // Opdaterer efter hver bogstav når man taster
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().testTag("email_field"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Viser @ på tastaturet
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- PASSWORD INPUT ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Adgangskode") },
            modifier = Modifier.fillMaxWidth().testTag("password_field"),
            // Skjuler koden med prikker (stjerner)
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- LOGIN KNAP ---
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth().testTag("login_button")
        ) {
            Text("Log ind")
        }

        // --- FEJLMEDDELELSE ---
        // Hvis errorMessage ikke er null (der er sket en fejl), viser den i rød
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("error_message")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- NAVIGERING TIL OPRET KONTO ---
        TextButton(onClick = onNavigateToRegister) {
            Text("Har du ikke en konto? Opret en her")
        }
    }
}

//---------------------------------------------------------------
//  PREVIEW (VISUALISERING I ANDROID STUDIO)
//---------------------------------------------------------------
/**
 * Dette gør at du kan se skærmen i "Design" fanen i Android Studio 
 * uden at skulle starte appen på en emulator hver gang.
 */
@Preview(showBackground = true) //
@Composable
fun LoginScreenPreview() {
    LoginContent(
        navController = rememberNavController(),
        errorMessage = null,
        onLoginClick = { _, _ -> },
        onNavigateToRegister = {}
    )
}
