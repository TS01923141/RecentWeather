package com.example.recentweather.model.di

import android.content.Context
import androidx.room.Room
import com.example.recentweather.model.database.TwoDayWeatherDatabase
import com.example.recentweather.model.network.WeatherService
import com.example.recentweather.model.utils.GpsUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://opendata.cwb.gov.tw/api/v1/rest/datastore/")
        .client(createClient())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private fun createClient(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
//            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1))
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherService = provideRetrofit().create(WeatherService::class.java)

    @Provides
    @Singleton
    fun provideGpsUtil(@ApplicationContext appContext: Context): GpsUtil = GpsUtil(appContext)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TwoDayWeatherDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            TwoDayWeatherDatabase::class.java,
            "twoDayWeathers"
        ).build()
}