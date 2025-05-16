package com.example.openskyapicase.util.helper

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MapRefreshHelper(
    private val googleMap: GoogleMap,
    private val scope: CoroutineScope,
    lifecycle: Lifecycle,
    private val refreshDelayMillis: Long = 10_000L,
    private val onRefresh: () -> Unit
):DefaultLifecycleObserver {

    private var refreshJob: Job? = null
    private var isCameraIdle = false
    private var isLifecycleResumed = true

    init {
        lifecycle.addObserver(this)
        setupMapListeners()
    }

    private fun setupMapListeners() {
        //Eğer kamera hareket ederse false a çekiyor
        googleMap.setOnCameraMoveListener {
            isCameraIdle = false
            refreshJob?.cancel()
        }
        //Eğer kamera sabit bırakılırsa değer true olduğu için 10 saniyede bir istek atıyor
        googleMap.setOnCameraIdleListener {
            if (!isCameraIdle) {
                isCameraIdle = true
                startRefreshing()
            }
        }
    }

    private fun startRefreshing() {
        refreshJob = scope.launch {
            while (isActive && isCameraIdle && isLifecycleResumed) {
                delay(refreshDelayMillis)
                onRefresh()
            }
        }
    }

    fun cleanup() {
        refreshJob?.cancel()
    }

    override fun onPause(owner: LifecycleOwner) {
        isLifecycleResumed = false
        refreshJob?.cancel()
    }

    override fun onResume(owner: LifecycleOwner) {
        isLifecycleResumed = true
        if (isCameraIdle) {
            startRefreshing()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cleanup()
    }
}
