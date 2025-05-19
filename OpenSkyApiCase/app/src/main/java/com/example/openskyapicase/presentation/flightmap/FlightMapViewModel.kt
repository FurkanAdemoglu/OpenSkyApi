package com.example.openskyapicase.presentation.flightmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openskyapicase.common.State
import com.example.openskyapicase.data.remote.requestmodel.CoordinatesRequestModel
import com.example.openskyapicase.domain.model.Flight
import com.example.openskyapicase.domain.usecase.GetFlightsUseCase
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
    var isCameraMoved = false

    fun fetchFlights(
        lomin: Double,
        lamin: Double,
        lomax: Double,
        lamax: Double
    ) {
        viewModelScope.launch {
            getFlightsUseCase(
                CoordinatesRequestModel(
                    lomin = lomin,
                    lamin = lamin,
                    lomax = lomax,
                    lamax = lamax
                )
            )
                .collect { state ->
                    when (state) {
                        is State.Loading -> {
                            _uiState.value = FlightMapUiState.Loading
                        }

                        is State.Success -> {
                            allFlights = state.data ?: emptyList()
                            val countries = getDistinctCountries(allFlights)
                            //Eğer seçilen ülke sonraki istekte giderse seçilen country null oluyor ve case tümü ne geçiyor
                            if (_selectedCountry.value != null && _selectedCountry.value !in countries) {
                                _selectedCountry.value = null
                            }
                            updateUiState(countries)
                        }

                        is State.Error -> {
                            _uiState.value = FlightMapUiState.Error(state.message)
                        }
                    }
                }
        }
    }

    private fun updateUiState(countries: List<String>) {
        val filteredFlights = filterFlights(allFlights, _selectedCountry.value)
        _uiState.value = FlightMapUiState.Success(
            flights = filteredFlights,
            countries = countries,
            selectedCountry = _selectedCountry.value
        )
    }

    //Uçuşların filtrelendiği alan
    private fun filterFlights(flights: List<Flight>, country: String?): List<Flight> {
        return if (country.isNullOrEmpty()) flights else flights.filter { it.originCountry == country }
    }
    //Tekrarlı olmuycak şekilde ülkeler eklendi
    private fun getDistinctCountries(flights: List<Flight>): List<String> =
        flights.mapNotNull { it.originCountry }.distinct().sorted()

    //Spinnerdan ülke seçme
    fun selectCountry(country: String?) {
        _selectedCountry.value = country
        val countries = getDistinctCountries(allFlights)
        updateUiState(countries)
    }
}

