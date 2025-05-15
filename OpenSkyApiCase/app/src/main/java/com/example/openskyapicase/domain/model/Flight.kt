package com.example.openskyapicase.domain.model

data class Flight(
    val icao24: String,
    val callsign: String?,
    val originCountry: String,
    val longitude: Double?,
    val latitude: Double?,
    val altitude: Double?
)
