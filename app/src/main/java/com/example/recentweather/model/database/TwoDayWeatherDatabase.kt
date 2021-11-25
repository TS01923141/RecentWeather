package com.example.recentweather.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recentweather.model.network.TwoDayWeatherEntity

@Database(entities = [TwoDayWeatherEntity::class], version = 1)
abstract class TwoDayWeatherDatabase: RoomDatabase() {
    abstract val twoDayWeatherDao: TwoDayWeatherDao
}