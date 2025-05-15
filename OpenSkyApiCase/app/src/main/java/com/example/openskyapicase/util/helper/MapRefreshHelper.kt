package com.example.openskyapicase.util.helper

import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MapRefreshHelper(
    private val googleMap: GoogleMap,
    private val scope: CoroutineScope,
    private val refreshDelayMillis: Long = 10_000L,
    private val onRefresh: () -> Unit
) {

    private var refreshJob: Job? = null
    private var isCameraIdle = false

    init {
        setupMapListeners()
    }

    private fun setupMapListeners() {
        googleMap.setOnCameraMoveListener {
            isCameraIdle = false
            refreshJob?.cancel()
        }

        googleMap.setOnCameraIdleListener {
            if (!isCameraIdle) {
                isCameraIdle = true
                startRefreshing()
            }
        }
    }

    private fun startRefreshing() {
        refreshJob = scope.launch {
            while (isActive && isCameraIdle) {
                delay(refreshDelayMillis)
                onRefresh()
            }
        }
    }

    fun cleanup() {
        refreshJob?.cancel()
    }
}
