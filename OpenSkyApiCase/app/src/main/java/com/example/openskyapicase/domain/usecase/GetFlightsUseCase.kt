package com.example.openskyapicase.domain.usecase

import com.example.openskyapicase.base.BaseUseCase
import com.example.openskyapicase.data.remote.requestmodel.CoordinatesRequestModel
import com.example.openskyapicase.domain.model.Flight
import com.example.openskyapicase.domain.repository.FlightRepository
import javax.inject.Inject

class GetFlightsUseCase @Inject constructor(
    private val repository: FlightRepository
) :BaseUseCase<CoordinatesRequestModel,List<Flight>?>(){
    override suspend fun execute(param: CoordinatesRequestModel?): List<Flight>? {
        return param?.let { repository.getFlights(it) }
    }
}