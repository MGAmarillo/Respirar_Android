package com.respirar.service

import com.respirar.model.Station
import retrofit2.Call
import retrofit2.http.GET

interface StationService {

    @GET("stations")
    fun getStations(): Call<List<Station>>

}