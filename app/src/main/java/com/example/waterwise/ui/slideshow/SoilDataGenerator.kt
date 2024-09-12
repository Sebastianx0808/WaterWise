package com.example.waterwise.ui.slideshow

// SoilDataGenerator.kt
import java.text.SimpleDateFormat
import java.util.*

object SoilDataGenerator {

    private val random = Random()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun generateDummySoilData(): SoilData {
        val moisture = random.nextFloat() // Random float between 0.0 and 1.0
        val temperature = 15 + random.nextFloat() * 15 // Random float between 15 and 30
        val lastWatered = dateFormat.format(Date()) // Current timestamp

        return SoilData(moisture, temperature, lastWatered)
    }
}
