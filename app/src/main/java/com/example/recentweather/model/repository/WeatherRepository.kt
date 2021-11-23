package com.example.recentweather.model.repository

import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.network.WeatherService
import com.example.recentweather.model.network.asEntityList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val service: WeatherService) {

    suspend fun getTwoDayWeatherEntities() : List<TwoDayWeatherEntity> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getTwoDayWeather()
                if (response.isSuccessful) {
                    response.body()?.asEntityList() ?: mutableListOf()
                } else {
                    mutableListOf()
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                mutableListOf()
            }
        }
}