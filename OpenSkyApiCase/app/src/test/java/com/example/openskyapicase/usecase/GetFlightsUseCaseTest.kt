package com.example.openskyapicase.usecase

import com.example.openskyapicase.common.State
import com.example.openskyapicase.domain.model.Flight
import com.example.openskyapicase.domain.repository.FlightRepository
import com.example.openskyapicase.domain.usecase.GetFlightsUseCase
import com.example.openskyapicase.util.CityLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetFlightsUseCaseTest {
    private val repository = mockk<FlightRepository>()
    private lateinit var useCase:GetFlightsUseCase

    @Before
    fun setup(){
        useCase = GetFlightsUseCase(repository)
    }

    @Test
    fun `invoke returns success state with data`() = runTest {
        val flights = listOf(testData)
        coEvery { repository.getFlights(any()) } returns flights
        val result = useCase(CityLocation.getIstanbulLocation()).first{it is State.Success}
        assert(result is State.Success && result.data == flights)
    }

    @Test
    fun `invoke returns error state when exception occurs`() = runTest {
        coEvery { repository.getFlights(any()) } throws RuntimeException("Error")
        val result = useCase(CityLocation.getIstanbulLocation()).first{it is State.Error}
        assert(result is State.Error && result.message == "Error")
    }
}