package com.example.recentweather.ui.areachoose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recentweather.R
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.ui.theme.RecentWeatherTheme

@Composable
fun AreaChooseScreen(twoDayWeatherList: List<TwoDayWeatherEntity>?, onBackClicked: () -> Unit, onItemClicked: (String) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = contentColorFor(MaterialTheme.colors.background),
            title = { Text(text = stringResource(R.string.title_area_choose),
                modifier = Modifier.fillMaxWidth()) },
            navigationIcon = {
                IconButton(onClick = { onBackClicked }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back_ios_new_24),
                        contentDescription = null
                    )
                }
            }
        )
    }) {
        if (twoDayWeatherList != null) {
            LazyColumn {
                items(twoDayWeatherList) {
                    AreaItem(area = it.locationName) {
//                        viewModel.selectedArea.value = it.locationName
                        onItemClicked(it.locationName)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AreaItem(area: String, onItemClicked: () -> Unit) {
    Card(
        onClick = onItemClicked,
        modifier = Modifier
            .padding(start = 8.dp)
            .padding(end = 8.dp)
            .padding(top = 4.dp)
            .padding(bottom = 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = area,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "AreaItem(Light)", showBackground = true)
@Preview(name = "AreaItem(Dark)", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAreaItem() {
    RecentWeatherTheme {
        AreaItem("臺北市", {})
    }
}

@Preview(name = "AreaChooseScreen(Light)", showBackground = true)
@Preview(name = "AreaChooseScreen(Dark)", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAreaChooseScreen() {
    val twoDayWeatherList = listOf<TwoDayWeatherEntity>(TwoDayWeatherEntity.test, TwoDayWeatherEntity.test)
    RecentWeatherTheme {
        AreaChooseScreen(twoDayWeatherList, {}, {})
    }
}

