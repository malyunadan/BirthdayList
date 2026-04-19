package com.example.birthdaylist.data.repository

import com.example.birthdaylist.data.model.PersonDto
import retrofit2.Response

interface PersonRepository {
    suspend fun getPersons(
        userId: String,
        name: String? = null,
        age: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ): Response<List<PersonDto>>

    suspend fun getPersonById(id: Int): Response<PersonDto>
    suspend fun addPerson(person: PersonDto): Response<PersonDto>
    suspend fun updatePerson(id: Int, person: PersonDto): Response<PersonDto>
    suspend fun deletePerson(id: Int): Response<Unit>
}
