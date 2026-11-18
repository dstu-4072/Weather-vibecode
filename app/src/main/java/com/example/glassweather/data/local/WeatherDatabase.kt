package com.example.glassweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CityEntity::class, ForecastEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun forecastDao(): ForecastDao
}
