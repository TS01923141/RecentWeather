package com.example.recentweather.model.network

import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TwoDayWeatherResponse"
fun TwoDayWeatherResponse.asEntityList(): List<TwoDayWeatherEntity> {
    val entityList = mutableListOf<TwoDayWeatherEntity>()
    record.locationResult.forEach {
        it.locations.forEach {
            val weatherDatas = mutableListOf<WeatherData>()
            //build weather data
            var temp: List<Time> = mutableListOf()
            var feelTemp: List<Time> = mutableListOf()
            var wx: List<Time> = mutableListOf()
            var pop6h: List<Time> = mutableListOf()
            var weatherDescription: List<Time> = mutableListOf()
            it.weatherElements.forEach {
                when (it.elementName) {
                    "T" -> temp = it.times
                    "AT" -> feelTemp = it.times
                    "Wx" -> wx = it.times
                    "PoP6h" -> pop6h = it.times
                    "WeatherDescription" -> weatherDescription = it.times
                }
                if (temp.isNotEmpty() && feelTemp.isNotEmpty() && wx.isNotEmpty()
                    && pop6h.isNotEmpty() && weatherDescription.isNotEmpty()
                ) {
                    //降雨6小時一次，所以這邊*2
                    var rainRate: MutableList<Time> = mutableListOf()
                    pop6h.forEach {
                        rainRate.add(it)
                        rainRate.add(it)
                    }
                    //pop6h有可能只有11個，*2不足24個，在這邊補上
                    if (rainRate.size < 24){
                        for (i in 1..(24-rainRate.size)){
                            rainRate.add(pop6h.last())
                        }
                    }
                    val times = mutableListOf<String>()
                    temp.forEach {
                        //2021-11-23 12:00:00
                        val originFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN)
                        val newFormatter = SimpleDateFormat("HH:mm", Locale.TAIWAN)
                        times.add(newFormatter.format(originFormatter.parse(it.dataTime)))
                    }
                    for (i in 0..23) {
                        val weatherData = WeatherData(
                            time = times[i],
                            temp = temp[i].elementValues[0].value.toInt(),
                            feelTemp = feelTemp[i].elementValues[0].value.toInt(),
                            wx = wx[i].elementValues[0].value,
                            rainRate = rainRate[i].elementValues[0].value.toInt(),
                            weatherDescription = weatherDescription[i].elementValues[0].value
                        )
                        //add weather data
                        weatherDatas.add(weatherData)
                    }
                }
            }
            val entity = TwoDayWeatherEntity(
                locationName = it.locationName,
                lat = it.lat,
                lon = it.lon,
                weatherDataList = weatherDatas
            )
            entityList.add(entity)
        }
    }
    return entityList
}

@JsonClass(generateAdapter = true)
data class TwoDayWeatherResponse(
    val success: String,
    val result: Result,
    @Json(name = "records") val record: Record
)

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "resource_id") val resourceId: String,
    val fields: List<Field>
)

@JsonClass(generateAdapter = true)
data class Field(
    val id: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class Record(
    @Json(name = "locations") val locationResult: List<LocationResult>
)

@JsonClass(generateAdapter = true)
data class LocationResult(
    val datasetDescription: String,
    val locationsName: String,
    @Json(name = "dataid") val dataId: String,
    @Json(name = "location") val locations: List<Location>
)

@JsonClass(generateAdapter = true)
data class Location(
    val locationName: String,
    val geocode: Long,
    val lat: Float,
    val lon: Float,
    @Json(name = "weatherElement") val weatherElements: List<WeatherElement>
)

@JsonClass(generateAdapter = true)
data class WeatherElement(
    val elementName: String,
    val description: String,
    @Json(name = "time") val times: List<Time>,
)

@JsonClass(generateAdapter = true)
data class Time(
    val dataTime: String?,
    val startTime: String?,
    val endTime: String?,
    @Json(name = "elementValue") val elementValues: List<ElementValue>
)

@JsonClass(generateAdapter = true)
data class ElementValue(
    val value: String,
    val measures: String
)