package com.example.recentweather.model.network

import retrofit2.Response
import retrofit2.http.GET

interface WeatherService {
    @GET("F-D0047-089?Authorization=rdec-key-123-45678-011121314")
    suspend fun getTwoDayWeather(): Response<TwoDayWeatherResponse>
}