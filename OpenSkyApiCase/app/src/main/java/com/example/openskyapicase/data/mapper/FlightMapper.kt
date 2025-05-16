package com.example.openskyapicase.data.mapper

import com.example.openskyapicase.domain.model.Flight

//Apiden dönen response u kullanılabilen bir data class a çeviriyor ve liste döndürüyor
fun List<List<Any>>.toFlightList(): List<Flight> {
    return this.mapNotNull { state ->
        try {
            Flight(
                icao24 = state.getOrNull(0) as String,
                callsign = state.getOrNull(1) as? String,
                originCountry = state.getOrNull(2) as String,
                longitude = state.getOrNull(5) as? Double,
                latitude = state.getOrNull(6) as? Double,
                altitude = state.getOrNull(7) as? Double,
                track =state.getOrNull(10).toString().toFloat(),
            )
        } catch (e: Exception) {
            null
        }
    }
}