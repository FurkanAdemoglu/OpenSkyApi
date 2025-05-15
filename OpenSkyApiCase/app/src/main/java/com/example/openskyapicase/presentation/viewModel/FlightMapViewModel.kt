package com.example.openskyapicase.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openskyapicase.common.State
import com.example.openskyapicase.domain.model.Flight
import com.example.openskyapicase.domain.usecase.GetFlightsUseCase
import com.example.openskyapicase.util.CityLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FlightMapUiState {
    object Loading : FlightMapUiState()
    data class Success(
        val flights: List<Flight>,
        val countries: List<String>,
        val selectedCountry: String?
    ) : FlightMapUiState()

    data class Error(val message: String) : FlightMapUiState()
}

@HiltViewModel
class FlightMapViewModel @Inject constructor(
    private val getFlightsUseCase: GetFlightsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FlightMapUiState>(FlightMapUiState.Loading)
    val uiState: StateFlow<FlightMapUiState> = _uiState.asStateFlow()

    private val _selectedCountry = MutableStateFlow<String?>(null)

    private var allFlights: List<Flight> = emptyList()

    init {
        fetchFlights()
    }

    fun fetchFlights() {
        viewModelScope.launch {
            getFlightsUseCase(CityLocation.getIstanbulLocation())
                .collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _uiState.value = FlightMapUiState.Loading
                        }

                        is State.Success -> {
                            allFlights = state.data ?: emptyList()
                            val countries =
                                allFlights.mapNotNull { it.originCountry }.distinct().sorted()
                            if (_selectedCountry.value != null && _selectedCountry.value !in countries) {
                                _selectedCountry.value = null
                            }
                            val filteredFlights = filterFlights(allFlights, _selectedCountry.value)
                            _uiState.value = FlightMapUiState.Success(
                                flights = filteredFlights,
                                countries = countries,
                                selectedCountry = _selectedCountry.value
                            )
                        }

                        is State.Error -> {
                            _uiState.value = FlightMapUiState.Error(state.message)
                        }
                    }
                }
        }
    }

    private fun filterFlights(flights: List<Flight>, country: String?): List<Flight> {
        return if (country.isNullOrEmpty()) flights else flights.filter { it.originCountry == country }
    }

    fun selectCountry(country: String?) {
        _selectedCountry.value = country
        val filteredFlights = filterFlights(allFlights, country)
        val countries = allFlights.mapNotNull { it.originCountry }.distinct().sorted()
        _uiState.value = FlightMapUiState.Success(
            flights = filteredFlights,
            countries = countries,
            selectedCountry = country
        )
    }
}

