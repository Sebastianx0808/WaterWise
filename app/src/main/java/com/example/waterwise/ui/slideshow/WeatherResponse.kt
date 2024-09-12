package com.example.waterwise.ui.slideshow

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind:Wind
)

data class Main(
    val temp: Double,
)

data class Weather(
    val description: String
)
data class Wind(
    val speed:Double
)

