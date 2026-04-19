package com.example.birthdaylist.data.remote

import com.example.birthdaylist.data.model.PersonDto
import retrofit2.Response
import retrofit2.http.*

interface PersonApi {
    @GET("Persons")
    suspend fun getPersons(
        @Query("userId") userId: String,
        @Query("name") name: String? = null,
        @Query("age") age: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null
    ): Response<List<PersonDto>>

    @GET("Persons/{id}")
    suspend fun getPersonById(@Path("id") id: Int): Response<PersonDto>

    @POST("Persons")
    suspend fun addPerson(@Body person: PersonDto): Response<PersonDto>

    @PUT("Persons/{id}")
    suspend fun updatePerson(@Path("id") id: Int, @Body person: PersonDto): Response<PersonDto>

    @DELETE("Persons/{id}")
    suspend fun deletePerson(@Path("id") id: Int): Response<Unit>
}
