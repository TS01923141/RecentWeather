package com.example.recentweather.model.network

//wx -> 24
//feelTemp -> 24
//temp -> 24
//weatherDescription -> 24
//rainRate -> 11or12

data class TwoDayWeatherEntity(
    val locationName: String,
    val lat: Float,
    val lon: Float,
    val weatherDataList: List<WeatherData>
) {
    companion object {
        val empty = TwoDayWeatherEntity(
            "",
            -1f,
            -1f,
            listOf()
        )
    }
}

data class WeatherData(
    val time: String,
    val temp: Int,
    val feelTemp : Int,
    val wx: String,
    val rainRate: Int,
    val weatherDescription: String
)