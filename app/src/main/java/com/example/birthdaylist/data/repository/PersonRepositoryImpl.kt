package com.example.birthdaylist.data.repository

import com.example.birthdaylist.data.model.PersonDto
import com.example.birthdaylist.data.remote.PersonApi
import retrofit2.Response

class PersonRepositoryImpl(
    private val api: PersonApi
) : PersonRepository {

    override suspend fun getPersons(
        userId: String,
        name: String?,
        age: Int?,
        sortBy: String?,
        sortOrder: String?
    ): Response<List<PersonDto>> {
        return api.getPersons(userId, name, age, sortBy, sortOrder)
    }

    override suspend fun getPersonById(id: Int): Response<PersonDto> {
        return api.getPersonById(id)
    }

    override suspend fun addPerson(person: PersonDto): Response<PersonDto> {
        return api.addPerson(person)
    }

    override suspend fun updatePerson(id: Int, person: PersonDto): Response<PersonDto> {
        return api.updatePerson(id, person)
    }

    override suspend fun deletePerson(id: Int): Response<Unit> {
        return api.deletePerson(id)
    }
}
