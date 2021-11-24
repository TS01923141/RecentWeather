package com.example.recentweather.ui

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

private const val TAG = "MainViewModel"
@HiltViewModel
class MainViewModel @Inject constructor(application: Application, private val repository: WeatherRepository): AndroidViewModel(application) {
    private var area = ""
    private var twoDayWeatherEntityList = mutableStateListOf<TwoDayWeatherEntity>()

    var currentWeatherEntity = mutableStateOf(TwoDayWeatherEntity.empty)
        private set

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
            refreshCurrentWeatherEntity()
        }
    }

    fun refreshCurrentWeatherEntity() {
        if (area == "" || twoDayWeatherEntityList.isEmpty()) return
        val entity = filterWeatherEntityByArea(area)
        if (entity != TwoDayWeatherEntity.empty) currentWeatherEntity.value = entity
    }

    fun updateAreaNameByLocation(location: Location) {
        val geocoder = Geocoder(getApplication(), Locale.TAIWAN)
        val address: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//        Log.d(TAG, "setAreaNameByLocation: address.size: ${address.size}")
//        Log.d(TAG, "setAreaNameByLocation: address[0]: ${address[0]}")
//        return address[0].adminArea ?: address[0].subAdminArea ?: ""
        area = (address[0].adminArea ?: address[0].subAdminArea ?: "").replace('台', '臺')
        refreshCurrentWeatherEntity()
    }

    fun filterWeatherEntityByArea(area: String): TwoDayWeatherEntity {
//        Log.d(TAG, "filterWeatherEntityByArea: area: $area")
//        twoDayWeatherEntityList.forEach {
//            Log.d(TAG, "filterWeatherEntityByArea: it.locationName: ${it.locationName}")
//        }
        return twoDayWeatherEntityList.find {
            it.locationName == area
        } ?: TwoDayWeatherEntity.empty
    }
}