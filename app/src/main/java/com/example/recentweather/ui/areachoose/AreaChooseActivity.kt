package com.example.recentweather.ui.areachoose

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.ui.main.NoPermissionScreen
import com.example.recentweather.ui.theme.RecentWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AreaChooseActivity : AppCompatActivity() {
    private val viewModel : AreaChooseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(viewModel) {
            twoDayWeatherEntityList.observe(this@AreaChooseActivity, ::handleTwoDayWeatherEntityList)
            selectedArea.observe(this@AreaChooseActivity, ::handleSelectedArea)
        }

        setContent {
            RecentWeatherTheme {
                AreaChooseScreen(
                    viewModel,
                    twoDayWeatherList = viewModel.stateTwoDayWeatherEntityList.value,
                    onBackClicked = { onBackPressed() })
            }
        }
    }

    private fun handleSelectedArea(area: String?) {
        if (area == null || area.isBlank() || area.isBlank()) return
        val intent = Intent().putExtra("location_name", area)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun handleTwoDayWeatherEntityList(entityList: List<TwoDayWeatherEntity>?) {
        if (entityList == null || entityList.isEmpty()) return
        viewModel.stateTwoDayWeatherEntityList.value = entityList
    }
}