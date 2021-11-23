package com.example.recentweather.model.repository

import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.network.WeatherService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class WeatherRepositoryTest{

    private lateinit var repository: WeatherRepository

    @MockK private lateinit var service: WeatherService
    @MockK private lateinit var twoDayWeatherEntityList: List<TwoDayWeatherEntity>

    private val emptyTwoDayWeatherEntityList = mutableListOf<TwoDayWeatherEntity>()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = WeatherRepository(service)
    }

    @Test
    fun `should return null when throw UnknownHostException`() {
        coEvery { service.getTwoDayWeather() } throws UnknownHostException()
        runBlocking {
            twoDayWeatherEntityList = repository.getTwoDayWeatherEntities()
            assertEquals(twoDayWeatherEntityList, emptyTwoDayWeatherEntityList)
        }
    }

    @Test
    fun `should return null when response not successful`() {
        coEvery { service.getTwoDayWeather().isSuccessful } returns false
        runBlocking {
            twoDayWeatherEntityList = repository.getTwoDayWeatherEntities()
            assertEquals(twoDayWeatherEntityList, emptyTwoDayWeatherEntityList)
        }
    }

    @Test
    fun `should return twoDayWeatherEntityList from service`() {
        coEvery { service.getTwoDayWeather().isSuccessful } returns true
        coEvery { service.getTwoDayWeather().body() } returns null
        runBlocking {
            twoDayWeatherEntityList = repository.getTwoDayWeatherEntities()
            assertEquals(twoDayWeatherEntityList, emptyTwoDayWeatherEntityList)
        }
    }

}