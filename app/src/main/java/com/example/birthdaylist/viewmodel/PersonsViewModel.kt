package com.example.birthdaylist.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birthdaylist.data.model.PersonDto
import com.example.birthdaylist.data.repository.PersonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * PersonsViewModel fungerer som bindeleddet mellem din UI og dit Repository.
 * Det er herfra vi henter listen over venner og håndterer sortering og filtrering.
 */
class PersonsViewModel(
    private val repository: PersonRepository // Repository bliver injected via Koin
) : ViewModel() {

    // --- STATEFLOWS (UI observerer disse for ændringer) ---

    // Den aktuelle liste af personer der skal vises.
    private val _persons = MutableStateFlow<List<PersonDto>>(emptyList())
    val persons: StateFlow<List<PersonDto>> = _persons

    // Fejlbeskeder fra API-kald (fx hvis netværket er nede).
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Indikerer om appen er ved at hente eller gemme data.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Henter alle personer for en specifik bruger fra API'et.
     * Indeholder logik til både Backend og Frontend filtrering/sortering.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPersons(
        userId: String,
        name: String? = null,
        age: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // 1. her prøver jeg først at lade API'et (Backend) klare filtreringen
            val response = repository.getPersons(userId, name, age, sortBy, sortOrder)

            if (response.isSuccessful) {
                var list = response.body() ?: emptyList()

                // 2. KOTLIN FILTRERING (Frontend)
                // Hvis serveren sender for mange resultater (fx "Hans" når man søger på "Jane"),
                // så kan man filtrerer listen manuelt her for at sikre et korrekt resultat.
                if (!name.isNullOrBlank()) {
                    list = list.filter { it.name.contains(name, ignoreCase = true) }
                }
                
                // til at sikrer os også at alder-filteret virker ved at filtrere manuelt
                if (age != null) {
                    list = list.filter { it.age == age }
                }

                // 3. KOTLIN SORTERING (Frontend)
                // Hvis serveren ignorerer sorterings-ønsket, sorterer vi selv listen her.
                list = when (sortBy) {
                    "name" -> if (sortOrder == "DESC") list.sortedByDescending { it.name } else list.sortedBy { it.name }
                    "age" -> if (sortOrder == "DESC") list.sortedByDescending { it.age } else list.sortedBy { it.age }
                    "birthMonth" -> if (sortOrder == "DESC") list.sortedByDescending { it.daysUntilBirthday } else list.sortedBy { it.daysUntilBirthday }
                    else -> list
                }

                _persons.value = list // Opdater UI med den færdige liste
            } else {
                _errorMessage.value = "Fejl: ${response.code()} - ${response.message()}"
            }
            _isLoading.value = false
        }
    }

    /**
     * Henter data for én specifik person ud fra deres ID.
     */
    fun loadPersonById(id: Int, onResult: (PersonDto?) -> Unit) {
        viewModelScope.launch {
            val response = repository.getPersonById(id)
            if (response.isSuccessful) {
                onResult(response.body())
            } else {
                onResult(null)
            }
        }
    }

    /**
     * Opretter en ny person via API'et.
     */
    fun addPerson(person: PersonDto, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.addPerson(person)
            _isLoading.value = false
            onDone(response.isSuccessful)
        }
    }

    /**
     * Opdaterer (retter) en eksisterende person via API'et.
     */
    fun updatePerson(id: Int, person: PersonDto, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = repository.updatePerson(id, person)
            onDone(response.isSuccessful)
        }
    }

    /**
     * Sletter en person fra API'et.
     */
    fun deletePerson(id: Int, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = repository.deletePerson(id)
            onDone(response.isSuccessful)
        }
    }
}
