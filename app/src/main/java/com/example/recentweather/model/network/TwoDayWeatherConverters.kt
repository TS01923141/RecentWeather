package com.example.recentweather.model.network

import androidx.room.TypeConverter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types


class TwoDayWeatherConverters {
    @TypeConverter
    fun fromWeatherDataList(weatherDataList: List<WeatherData>): String {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val listMyData = Types.newParameterizedType(
            List::class.java,
            WeatherData::class.java
        )
        val jsonAdapter: JsonAdapter<List<WeatherData>> = moshi.adapter(listMyData)
        return jsonAdapter.toJson(weatherDataList)
    }

    @TypeConverter
    fun toWeatherDataList(stringOfWeatherDataLsit: String): List<WeatherData> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val listMyData = Types.newParameterizedType(
            List::class.java,
            WeatherData::class.java
        )
        val jsonAdapter: JsonAdapter<List<WeatherData>> = moshi.adapter(listMyData)
        return jsonAdapter.fromJson(stringOfWeatherDataLsit)?: mutableListOf()
    }
}