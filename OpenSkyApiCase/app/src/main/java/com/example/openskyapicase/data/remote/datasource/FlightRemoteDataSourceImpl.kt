package com.example.openskyapicase.data.remote.datasource

import com.example.openskyapicase.data.OpenSkyApiService
import com.example.openskyapicase.data.mapper.toFlightList
import com.example.openskyapicase.data.remote.requestmodel.CoordinatesRequestModel
import com.example.openskyapicase.domain.model.Flight
import javax.inject.Inject

class FlightRemoteDataSourceImpl @Inject constructor(
    private val api: OpenSkyApiService
) : FlightRemoteDataSource {

    override suspend fun getAllFlights(coordinatesRequestModel: CoordinatesRequestModel): List<Flight> {
        val response = api.getAllStates(lomin = coordinatesRequestModel.lomin, lomax = coordinatesRequestModel.lomax, lamax = coordinatesRequestModel.lamax, lamin = coordinatesRequestModel.lamin)
        return response.states?.toFlightList() ?: emptyList()
    }
}