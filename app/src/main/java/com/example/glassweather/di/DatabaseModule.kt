package com.example.glassweather.di

import android.content.Context
import androidx.room.Room
import com.example.glassweather.data.local.CityDao
import com.example.glassweather.data.local.ForecastDao
import com.example.glassweather.data.local.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(context, WeatherDatabase::class.java, "weather.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCityDao(db: WeatherDatabase): CityDao = db.cityDao()

    @Provides
    fun provideForecastDao(db: WeatherDatabase): ForecastDao = db.forecastDao()
}
