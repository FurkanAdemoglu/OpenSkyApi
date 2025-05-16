package com.example.openskyapicase.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.openskyapicase.MainDispatcherRule
import com.example.openskyapicase.common.State
import com.example.openskyapicase.domain.repository.FlightRepository
import com.example.openskyapicase.domain.usecase.GetFlightsUseCase
import com.example.openskyapicase.presentation.flightmap.FlightMapViewModel
import com.example.openskyapicase.usecase.testData
import com.example.openskyapicase.util.CityLocation
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlightMapViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<FlightRepository>()
    private lateinit var useCase: GetFlightsUseCase
    private lateinit var viewModel: FlightMapViewModel

    @Before
    fun setup() {
        useCase = GetFlightsUseCase(repository)
        viewModel = FlightMapViewModel(useCase)
    }

    @Test
    fun `invoke returns success state with data`() = runTest {
        val fakeFlights = listOf(testData)
        coEvery { repository.getFlights(any()) } returns fakeFlights


        val result = useCase(CityLocation.getIstanbulLocation()).toList()

        assertTrue(result[0] is State.Loading)
        assertTrue(result[1] is State.Success)
        assertEquals(fakeFlights, (result[1] as State.Success).data)
    }
}