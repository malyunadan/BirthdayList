package com.example.birthdaylist.di

import com.example.birthdaylist.data.remote.PersonApi
import com.example.birthdaylist.data.remote.RetrofitInstance
import com.example.birthdaylist.data.repository.PersonRepository
import com.example.birthdaylist.data.repository.PersonRepositoryImpl
import com.example.birthdaylist.viewmodel.AuthViewModel
import com.example.birthdaylist.viewmodel.PersonsViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Koin-modul: Dette er appens "opskriftsbog". 
// Den fortæller appen hvordan den skal oprette alle de vigtige dele, så vi ikke skal gøre det manuelt hver gang.
val appModule = module {

    // 'Single' betyder at der kun findes ÉN instans af dette i hele appen (Singleton).
    // Vi opretter FirebaseAuth én gang, så alle skærme bruger den samme logind-tjeneste.
    single { FirebaseAuth.getInstance() }

    // Vi fortæller Koin, at når nogen beder om 'PersonRepository', skal de have 'PersonRepositoryImpl'.
    // 'get()' betyder at Koin selv finder ud af at give den 'PersonApi' med i købet.
    single<PersonRepository> { PersonRepositoryImpl(get()) }

    // Henter vores Retrofit API-instans så vi kan tale med REST-servicen.
    single<PersonApi> { RetrofitInstance.api }

    // 'viewModel' fortæller Koin hvordan den skal lave vores ViewModels.
    // AuthViewModel bruges til Firebase-login.
    viewModel { AuthViewModel(get()) }

    // PersonsViewModel bruges til at håndtere listen af fødselsdage fra API'et.
    viewModel { PersonsViewModel(get()) }
}
