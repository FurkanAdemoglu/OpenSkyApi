package com.example.openskyapicase.data.remote.dto

data class StateResponseDto(
    val time: Long,
    val states: List<List<Any>>?
)
