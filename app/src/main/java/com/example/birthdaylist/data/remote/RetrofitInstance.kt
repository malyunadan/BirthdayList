package com.example.birthdaylist.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance: En "Singleton" (objekt), der opretter forbindelsen til internettet.
// Jeg bruger et objekt, så jeg kun opretter forbindelsen én gang, hvilket sparer på telefonens ressourcer.
object RetrofitInstance {

    // Base URL: Start-adressen på Anders's API.
    // Alle vores kald (som /Persons) bliver lagt oven på denne adresse.
    private const val BASE_URL = "https://birthdaysrest.azurewebsites.net/api/"

    // 'by lazy' betyder, at forbindelsen først bliver oprettet i det øjeblik, appen rent faktisk skal bruge den.
    val api: PersonApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Fortæller Retrofit hvor den skal ringe hen
            .addConverterFactory(GsonConverterFactory.create()) // Fortæller Retrofit at den skal oversætte JSON til Kotlin-objekter
            .build() // Bygger selve Retrofit-maskinen
            .create(PersonApi::class.java) // Skaber den færdige forbindelse baseret på vores PersonApi interface
    }
}
