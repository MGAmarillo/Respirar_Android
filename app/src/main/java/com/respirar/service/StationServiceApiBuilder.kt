package com.respirar.service
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StationServiceApiBuilder {
    // hardcodeado, solo anda en emuladores
    private val BASE_URL = "http://10.0.2.2:3001/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun create(): StationService {
        return retrofit.create(StationService::class.java)
    }
}