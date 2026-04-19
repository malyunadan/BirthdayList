package com.example.birthdaylist.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * AuthViewModel håndterer alt omkring Firebase Authentication.
 * Det er herfra vi styrer login, registrering og logout.
 */
class AuthViewModel(
    private val auth: FirebaseAuth // FirebaseAuth bliver injected via Koin fra AppModule
) : ViewModel() {

    // --- STATEFLOWS (Det som UI'et observerer) ---

    // Fortæller om brugeren er logget ind lige nu.
    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Fejlbeskeder (fx "Wrong password" eller "Email already in use").
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Fortæller om vi er i gang med at snakke med Firebase (viser spinner i UI).
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Forsøger at logge ind med email og password.
     */
    fun login(email: String, password: String, onResult: (Boolean) -> Unit ) {
        _isLoading.value = true // Start loading
        _errorMessage.value = null // Ryd gamle fejl

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false // Stop loading uanset resultat
                if (task.isSuccessful) {
                    _isLoggedIn.value = true
                    onResult(true) // Giv besked til UI om succes
                } else {
                    // Hvis det fejler, gemmer vi fejlbeskeden så UI kan vise den
                    _errorMessage.value = task.exception?.message
                    onResult(false)
                }
            }
    }

    /**
     * Opretter en helt ny bruger i Firebase.
     */
    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    _errorMessage.value = task.exception?.message
                    onResult(false)
                }
            }
    }

    // Manuelt sætte en fejl (fx hvis passwords ikke matcher i UI)
    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    /**
     * Logger brugeren ud og nulstiller tilstanden.
     */
    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
    }

    /**
     * Henter det unikke ID (UID) på den loggede bruger.
     * Dette ID bruges til at hente de rigtige personer fra REST API'et.
     */
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }
}
