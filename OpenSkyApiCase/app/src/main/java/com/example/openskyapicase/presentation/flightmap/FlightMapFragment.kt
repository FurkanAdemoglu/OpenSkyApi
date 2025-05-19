package com.example.openskyapicase.presentation.flightmap

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.openskyapicase.R
import com.example.openskyapicase.base.BaseFragment
import com.example.openskyapicase.databinding.FragmentFlightMapBinding
import com.example.openskyapicase.domain.model.Flight
import com.example.openskyapicase.util.extension.vectorToBitmap
import com.example.openskyapicase.util.helper.MapRefreshHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlightMapFragment : BaseFragment<FragmentFlightMapBinding>(R.layout.fragment_flight_map),
    OnMapReadyCallback {

    private val viewModel: FlightMapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var mapRefreshHelper: MapRefreshHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Harita için fragment SupportMapFragment oluşturuldu
        childFragmentManager
            .findFragmentById(R.id.map_fragment_container)
            ?.let { it as? SupportMapFragment }
            ?.getMapAsync(this)

        initObservers()
        initSpinnerListener()
    }

    //viewModelde state i observe eden yapı
    private fun initObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is FlightMapUiState.Loading -> Unit

                        is FlightMapUiState.Success -> {
                            binding.countrySpinner.visibility = View.VISIBLE
                            updateCountrySpinner(state.countries, state.selectedCountry)
                            updateMapMarkers(state.flights)
                        }

                        is FlightMapUiState.Error -> {
                            binding.countrySpinner.visibility = View.GONE
                            showAppDialog(title = getString(R.string.error), message = state.message)
                        }
                    }
                }
            }
        }

    }

    //Spinner a country listesini ekleyen yapı
    private fun updateCountrySpinner(countries: List<String>, selected: String?) {
        val countryList = listOf(ALL_COUNTRIES_OPTION) + countries
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countryList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.countrySpinner.adapter = adapter

        val selection = selected ?: ALL_COUNTRIES_OPTION
        val index = countryList.indexOf(selection)
        if (index >= 0) {
            binding.countrySpinner.setSelection(index)
        }
    }

    //Spinner listener set for filter
    private fun initSpinnerListener() {
        binding.countrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    val selected = parent.getItemAtPosition(position) as String
                    viewModel.selectCountry(if (selected == ALL_COUNTRIES_OPTION) null else selected)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    viewModel.selectCountry(null)
                }
            }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapRefreshHelper = MapRefreshHelper(
            googleMap = map,
            lifecycle = viewLifecycleOwner.lifecycle,
            scope = viewLifecycleOwner.lifecycleScope,
            onRefresh = { fetchFlightsFromMapBounds()}
        )
        fetchFlightsFromMapBounds()
    }

    //Markerları ekranda koordinatlarına göre gösteren yapı
    private fun updateMapMarkers(flights: List<Flight>) {
        googleMap?.clear()
        val airplaneIcon = requireContext().vectorToBitmap(R.drawable.ic_airplane)

        flights.forEach { flight ->
            val lat = flight.latitude
            val lng = flight.longitude
            val rotation = flight.track
            if (lat != null && lng != null) {
                val position = LatLng(lat, lng)
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(position)
                        .icon(airplaneIcon)
                        .rotation(rotation ?: 0f)
                        .title(flight.callsign ?: "No Callsign")
                )
            }
        }
        //Kameranın zoom olan kısmı sadece ilk ekran göründüğünde zoom oluyor sonrası için olmuyor
        if (!viewModel.isCameraMoved && flights.isNotEmpty()) {
            val first = flights.first()
            val lat = first.latitude ?: 0.0
            val lng = first.longitude ?: 0.0
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 7f))
            viewModel.isCameraMoved = true
        }
    }

    private fun fetchFlightsFromMapBounds() {
        val bounds = googleMap?.projection?.visibleRegion?.latLngBounds ?: return
        val lomin = bounds.southwest.longitude
        val lamin = bounds.southwest.latitude
        val lomax = bounds.northeast.longitude
        val lamax = bounds.northeast.latitude

        viewModel.fetchFlights(lomin, lamin, lomax, lamax)
    }



    //OnPause ve onDestroyda 10 saniyede bir yenileme özelliği kapanıyor uygulama arka plana geçtiği için
    override fun onPause() {
        super.onPause()
        mapRefreshHelper.cleanup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapRefreshHelper.cleanup()
    }

    companion object {
        private const val ALL_COUNTRIES_OPTION = "Tümü"
    }
}


