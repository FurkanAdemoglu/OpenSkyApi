package com.example.openskyapicase.util

import com.example.openskyapicase.data.remote.requestmodel.CoordinatesRequestModel

object CityLocation {
    fun getIstanbulLocation():CoordinatesRequestModel{
        return CoordinatesRequestModel(
            lomin = 27.3445316488,
            lamin = 40.226013967,
            lomax = 30.7411966586,
            lamax = 41.6004635693
        )
    }
}

