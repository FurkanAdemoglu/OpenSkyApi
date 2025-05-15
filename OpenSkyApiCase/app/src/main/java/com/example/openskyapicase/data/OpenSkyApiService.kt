package com.example.openskyapicase.data

import com.example.openskyapicase.data.remote.dto.StateResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenSkyApiService {
    @GET("states/all")
    suspend fun getAllStates(
        @Query("lomin") lomin: Double,
        @Query("lamin") lamin: Double,
        @Query("lomax") lomax: Double,
        @Query("lamax") lamax: Double
    ): StateResponseDto
}