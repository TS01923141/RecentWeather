package com.example.recentweather.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.recentweather.model.database.TwoDayWeatherDatabase
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.network.WeatherService
import com.example.recentweather.model.network.asEntityList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

private const val TAG = "WeatherRepository"
class WeatherRepository @Inject constructor(private val service: WeatherService, private val database: TwoDayWeatherDatabase) {
//    val twoDayWeatherEntityList : List<TwoDayWeatherEntity> = database.twoDayWeatherDao.getList()
    val twoDayWeatherEntityList : LiveData<List<TwoDayWeatherEntity>> = database.twoDayWeatherDao.getLiveDataList()

    suspend fun refreshTwoDayWeather() = withContext(Dispatchers.IO) {
        val twoDayWeatherEntityList = getTwoDayWeatherEntities()
        if (twoDayWeatherEntityList.isNotEmpty()) {
            database.twoDayWeatherDao.insertAll(twoDayWeatherEntityList)
//            database.twoDayWeatherDao.getList().forEach {
//                Log.d(TAG, "refreshTwoDayWeather: twoDayWeatherEntity: ${it.toString()}")
//            }
        }
    }

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