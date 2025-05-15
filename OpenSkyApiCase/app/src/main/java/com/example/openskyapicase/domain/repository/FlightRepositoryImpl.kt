package com.example.openskyapicase.domain.repository

import com.example.openskyapicase.data.remote.datasource.FlightRemoteDataSource
import com.example.openskyapicase.data.remote.requestmodel.CoordinatesRequestModel
import com.example.openskyapicase.domain.model.Flight
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepositoryImpl @Inject constructor(
    private val remoteDataSource: FlightRemoteDataSource
) : FlightRepository {

    override suspend fun getFlights(coordinatesRequestModel: CoordinatesRequestModel): List<Flight> {
        return remoteDataSource.getAllFlights(coordinatesRequestModel)
    }
}