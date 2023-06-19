package com.respirar.model

class Station(
    name: String,
    id: String,
    temperature: Float,
    reliability: Float,
    pm1: Int,
    pm10: Int,
    pm25: Int,
    coordinates: Coordinates,
) {
    var name = name
    var id = id
    var temperature = temperature
    var reliability = reliability
    var pm1 = pm1
    var pm10 = pm10
    var pm25 = pm25
    var coordinates = coordinates
}