package com.example.openskyapicase.data.remote.datasource

import com.example.openskyapicase.data.OpenSkyApiService
import com.example.openskyapicase.domain.model.Flight
import javax.inject.Inject

class FlightRemoteDataSourceImpl @Inject constructor(
    private val api: OpenSkyApiService
) : FlightRemoteDataSource {

    override suspend fun getAllFlights(): List<Flight> {
        val response = api.getAllStates()
        return response.states?.toFlightList() ?: emptyList()
    }
}