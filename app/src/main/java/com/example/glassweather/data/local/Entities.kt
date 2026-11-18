package com.example.glassweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val country: String,
    val region: String?,
    val latitude: Double,
    val longitude: Double,
    /** Місто з найбільшим значенням вважається вибраним. */
    val lastUsedAt: Long,
)

@Entity(tableName = "forecasts")
data class ForecastEntity(
    @PrimaryKey val cityId: Long,
    /** Forecast, серіалізований у JSON. */
    val json: String,
    val updatedAt: Long,
)
