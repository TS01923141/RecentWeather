package com.example.recentweather.ui

import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recentweather.R
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.network.WeatherData
import com.example.recentweather.ui.theme.RecentWeatherTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

private const val TAG = "MainScreen"
@Composable
fun MainScreen(viewModel: MainViewModel) {
    if (viewModel.checkPermissionResult.value == PackageManager.PERMISSION_GRANTED){
        if (viewModel.currentWeatherEntity.value != TwoDayWeatherEntity.empty) WeatherScreen(viewModel, viewModel.currentWeatherEntity.value)
    } else {
        NoPermissionScreen()
    }
}

@Composable
fun WeatherScreen(viewModel: MainViewModel = viewModel(), twoDayWeatherEntity: TwoDayWeatherEntity) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refreshTwoDayWeatherEntityList(forceRefresh = true) }) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(start = 8.dp)
                        .padding(end = 8.dp)
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)){
                    Text(
                        text = twoDayWeatherEntity.locationName,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.h4
                    )
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight()){
                        Text(
                            text = SimpleDateFormat("最後更新時間 MM/dd HH:mm", Locale.TAIWAN).format(Date(viewModel.getLastModifiedTime())),
                            fontSize = 12.sp
                        )
                    }
                }
                LazyColumn {
                    items(twoDayWeatherEntity.weatherDataList) {
                        WeatherItem(weatherData = it)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherItem(weatherData: WeatherData) {
    var isExpanded by remember { mutableStateOf(false)}
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colors.onBackground),
//        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier
            .animateContentSize()
            .padding(8.dp)) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (time, temp, feelTemp, rain) = createRefs()
                Text(text = weatherData.time, modifier = Modifier.constrainAs(time) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                })
                Text(text = "${weatherData.temp}°C", modifier = Modifier.constrainAs(temp) {
                    start.linkTo(time.end)
                    end.linkTo(feelTemp.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                })
                Text(
                    text = "體感${weatherData.feelTemp}°C",
                    modifier = Modifier.constrainAs(feelTemp) {
                        start.linkTo(temp.end)
                        end.linkTo(rain.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    })
                Text(text = "降雨機率${weatherData.rainRate}%", modifier = Modifier.constrainAs(rain) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                })
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(weatherData.weatherDescription)
            }
        }
    }
}

@Composable
fun NoPermissionScreen() {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_location_off_24),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(128.dp)
            )
            Text(
                text = "No location permission to locate current location",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
    }

}

@Preview(name = "WeatherItem(Light)")
@Preview(name = "WeatherItem(Dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewWeatherItem() {
    val weatherData = WeatherData(
        "18:00",
        18,
        16,
        "陰天",
        49,
        "陰。降雨機率 20%。溫度攝氏19度。稍有寒意。東北風 平均風速2-3級(每秒5公尺)。相對濕度85%。"
    )
    RecentWeatherTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                WeatherItem(weatherData)
                WeatherItem(weatherData)
            }
        }
    }
}

@Preview(name = "WeatherScreen(Light)")
@Preview(name = "WeatherScreen(Dark)")
@Composable
fun PreviewWeatherScreen() {
    val TwoDayWeatherEntity = TwoDayWeatherEntity(
        "臺北市",
        25.035095f,
        121.558742f,
        listOf(WeatherData(
            "18:00",
            18,
            16,
            "陰天",
            49,
            "陰。降雨機率 20%。溫度攝氏19度。稍有寒意。東北風 平均風速2-3級(每秒5公尺)。相對濕度85%。"),
            WeatherData(
                "18:00",
                18,
                16,
                "陰天",
                49,
                "陰。降雨機率 20%。溫度攝氏19度。稍有寒意。東北風 平均風速2-3級(每秒5公尺)。相對濕度85%。")
        )
    )
    RecentWeatherTheme {
        WeatherScreen(viewModel(), TwoDayWeatherEntity)
    }
}

@Preview(name = "NoPermissionScreen(Light)")
@Preview(name = "NoPermissionScreen(Dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNoPermissionScreen() {
    RecentWeatherTheme {
        NoPermissionScreen()
    }
}