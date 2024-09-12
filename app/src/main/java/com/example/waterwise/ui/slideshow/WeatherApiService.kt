package com.example.waterwise.ui.slideshow

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"  // or "imperial" for Fahrenheit
    ): Call<WeatherResponse>
}
