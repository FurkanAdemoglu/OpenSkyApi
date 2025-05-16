package com.example.openskyapicase.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.openskyapicase.R
import com.example.openskyapicase.base.BaseFragment
import com.example.openskyapicase.databinding.FragmentFlightMapBinding
import com.example.openskyapicase.domain.model.Flight
import com.example.openskyapicase.presentation.viewModel.FlightMapUiState
import com.example.openskyapicase.presentation.viewModel.FlightMapViewModel
import com.example.openskyapicase.util.extension.isInternetAvailable
import com.example.openskyapicase.util.extension.vectorToBitmap
import com.example.openskyapicase.util.helper.MapRefreshHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlightMapFragment : BaseFragment<FragmentFlightMapBinding>(R.layout.fragment_flight_map),
    OnMapReadyCallback {

    private val viewModel: FlightMapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var mapRefreshHelper: MapRefreshHelper
    private var isCameraMoved = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!requireContext().isInternetAvailable()){
            showErrorDialog("İnternet bağlantısı bulunamadı. Lütfen bağlantınızı kontrol edin.")
            return
        }
        childFragmentManager
            .findFragmentById(R.id.map_fragment_container)
            ?.let { it as? SupportMapFragment }
            ?.getMapAsync(this)

        initObservers()
        initSpinnerListener()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
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
                        showErrorDialog(state.message)
                    }
                }
            }
        }
    }

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


    private fun initSpinnerListener() {
        binding.countrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    val selected = parent.getItemAtPosition(position) as String
                    viewModel.selectCountry(if (selected == "Tümü") null else selected)
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
            onRefresh = { viewModel.fetchFlights() }
        )
    }

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

        if (!isCameraMoved && flights.isNotEmpty()) {
            val first = flights.first()
            val lat = first.latitude ?: 0.0
            val lng = first.longitude ?: 0.0
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 6f))
            isCameraMoved = true
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hata")
            .setMessage(message)
            .setPositiveButton("Tamam", null)
            .show()
    }

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


