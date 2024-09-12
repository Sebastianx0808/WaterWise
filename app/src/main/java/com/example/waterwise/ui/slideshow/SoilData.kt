package com.example.waterwise.ui.slideshow

data class SoilData(
    val moisture: Float,       // Soil moisture level (0.0 to 1.0)
    val temperature: Float,    // Soil temperature in Celsius
    val lastWatered: String
)
