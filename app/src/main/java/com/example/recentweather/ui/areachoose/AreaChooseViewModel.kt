package com.example.recentweather.ui.areachoose

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "AreaChooseViewModel"
@HiltViewModel
class AreaChooseViewModel @Inject constructor(private val repository: WeatherRepository): ViewModel() {
    val twoDayWeatherEntityList: LiveData<List<TwoDayWeatherEntity>> = repository.twoDayWeatherEntityList
    var stateTwoDayWeatherEntityList: MutableState<List<TwoDayWeatherEntity>> = mutableStateOf(mutableListOf())
}