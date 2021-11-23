package com.example.recentweather.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: WeatherRepository): ViewModel() {
    var twoDayWeatherEntityList = mutableStateListOf<TwoDayWeatherEntity>()

    var checkPermissionResult : MutableState<Int> = mutableStateOf(-1)
        private set

    fun setCheckPermissionResult(int: Int) {
        checkPermissionResult.value = int
    }

    suspend fun refreshTwoDayWeatherEntityList() = withContext(Dispatchers.IO) {
        val list = repository.getTwoDayWeatherEntities()
        if (list.isNotEmpty()) {
            twoDayWeatherEntityList.clear()
            twoDayWeatherEntityList.addAll(list)
        }
    }
}