package com.example.openskyapicase.data

import retrofit2.http.GET

interface OpenSkyApiService {
    @GET("states/all")
    suspend fun getAllStates(): StateResponseDto
}