package com.respirar.model

class Station( id: String,  temperature: Float,  reliability: Float,  pm1: Int,  pm10: Int,  pm25: Int, coordinates: Coordinates ){
    val id = id
    val temperature = temperature
    val reliability= reliability
    val pm1 = pm1
    val pm10 = pm10
    val pm25 = pm25
    val coordinates = coordinates
}
