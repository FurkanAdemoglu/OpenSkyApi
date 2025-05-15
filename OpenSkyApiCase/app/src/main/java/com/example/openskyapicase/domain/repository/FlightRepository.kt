package com.example.openskyapicase.domain.repository

import com.example.openskyapicase.data.remote.requestmodel.CoordinatesRequestModel
import com.example.openskyapicase.domain.model.Flight

interface FlightRepository {
    suspend fun getFlights(coordinatesRequestModel: CoordinatesRequestModel): List<Flight>
}