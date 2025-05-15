package com.example.openskyapicase.data.remote.datasource

import com.example.openskyapicase.domain.model.Flight

interface FlightRemoteDataSource {
    suspend fun getAllFlights(): List<Flight>
}