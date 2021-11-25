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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

private const val TAG = "MainViewModel"
private const val LAST_MODIFIED = "last_modified"
@HiltViewModel
class MainViewModel @Inject constructor(application: Application, private val repository: WeatherRepository): AndroidViewModel(application) {
    private val sharedPreferences by lazy {
        getApplication<Application>().getSharedPreferences("database", Application.MODE_PRIVATE) }

    private val _isRefreshing = MutableStateFlow(false)

    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    var area = MutableLiveData("")
//    private var twoDayWeatherEntityList = mutableStateListOf<TwoDayWeatherEntity>()
    var twoDayWeatherEntityList = repository.twoDayWeatherEntityList

    var currentWeatherEntity = mutableStateOf(TwoDayWeatherEntity.empty)
        private set

    var checkPermissionResult : MutableState<Int> = mutableStateOf(-1)
        private set

    fun setCheckPermissionResult(int: Int) {
        checkPermissionResult.value = int
    }

    fun refreshTwoDayWeatherEntityList(forceRefresh: Boolean = false) = viewModelScope.launch {
        val lastModified = sharedPreferences.getLong(LAST_MODIFIED, -1)
//        Log.d(TAG, "refreshTwoDayWeatherEntityList: Calendar.getInstance().timeInMillis - lastModified: ${Calendar.getInstance().timeInMillis - lastModified}")
        if (forceRefresh || Calendar.getInstance().timeInMillis - lastModified > 30 * 60 * 1000) {
            _isRefreshing.emit(true)
            repository.refreshTwoDayWeather()
            sharedPreferences.edit().putLong(LAST_MODIFIED, Calendar.getInstance().timeInMillis).commit()
//            Log.d(TAG, "refreshTwoDayWeatherEntityList: lastModified: ${lastModified}")
            _isRefreshing.emit(false)
        }
    }

    fun refreshCurrentWeatherEntity() {
//        Log.d(TAG, "refreshCurrentWeatherEntity: area: ${area.value}")
//        Log.d(TAG, "refreshCurrentWeatherEntity: twoDayWeatherEntityList.value: ${twoDayWeatherEntityList.value}")
        if (area.value == null || area.value!! == "" || twoDayWeatherEntityList.value == null || twoDayWeatherEntityList.value!!.isEmpty()) return
        val entity = filterWeatherEntityByArea(area.value!!)
        if (entity != TwoDayWeatherEntity.empty) currentWeatherEntity.value = entity
    }

    fun updateAreaNameByLocation(location: Location) {
        val geocoder = Geocoder(getApplication(), Locale.TAIWAN)
        val address: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//        Log.d(TAG, "setAreaNameByLocation: address.size: ${address.size}")
//        Log.d(TAG, "setAreaNameByLocation: address[0]: ${address[0]}")
//        return address[0].adminArea ?: address[0].subAdminArea ?: ""
        area.value = (address[0].adminArea ?: address[0].subAdminArea ?: "").replace('台', '臺')
//        refreshCurrentWeatherEntity()
    }

    fun filterWeatherEntityByArea(area: String): TwoDayWeatherEntity {
//        Log.d(TAG, "filterWeatherEntityByArea: area: $area")
//        twoDayWeatherEntityList.value?.forEach {
//            Log.d(TAG, "filterWeatherEntityByArea: it.locationName: ${it.locationName}")
//        }
        return twoDayWeatherEntityList.value?.find {
            it.locationName == area
        } ?: TwoDayWeatherEntity.empty
    }
}