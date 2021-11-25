package com.example.recentweather.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recentweather.model.network.TwoDayWeatherEntity

@Dao
interface TwoDayWeatherDao {
    @Query("select * from TwoDayWeatherEntity")
    fun getLiveDataList() : LiveData<List<TwoDayWeatherEntity>>

    @Query("select * from TwoDayWeatherEntity")
    fun getList() : List<TwoDayWeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(twoDayWeatherEntityList: List<TwoDayWeatherEntity>)
}