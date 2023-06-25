package com.respirar.service

import com.respirar.model.LoginCredentials
import com.respirar.model.LoginResponse
import com.respirar.model.Station
import com.respirar.model.StationHistory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StationService {

    @GET("stations")
    fun getStations(): Call<List<Station>>

    @GET("stations/{stationId}/history")
    fun getStationHistory(@Path("stationId") stationId: String,
                          @Query("fromDate") fromDate:String,
                          @Query("toDate") toDate:String,
                          @Query("parameter") parameter:String)
    : Call<StationHistory>

    @POST("users/login")
    fun login(@Body loginCredentials: LoginCredentials): Call<LoginResponse>

}