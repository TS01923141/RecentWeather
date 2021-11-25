package com.example.recentweather.model.network

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

//wx -> 24
//feelTemp -> 24
//temp -> 24
//weatherDescription -> 24
//rainRate -> 11or12

@Entity
@Parcelize
@TypeConverters(TwoDayWeatherConverters::class)
data class TwoDayWeatherEntity(
    @PrimaryKey val locationName: String = "",
    val lat: Float = -1f,
    val lon: Float = -1f,
    val weatherDataList: List<WeatherData> = mutableListOf()
) : Parcelable {
    companion object {
        val empty = TwoDayWeatherEntity(
            "",
            -1f,
            -1f,
            listOf()
        )
    }
}

@Entity
@Parcelize
data class WeatherData(
    val time: String = "",
    val temp: Int = -1,
    val feelTemp : Int = -1,
    val wx: String = "",
    val rainRate: Int = -1,
    val weatherDescription: String = ""
) : Parcelable