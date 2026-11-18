package com.example.glassweather.data.repository

import com.example.glassweather.domain.City
import com.example.glassweather.domain.Forecast
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    /** Збережені міста; перше — вибране (останнє використане). */
    val cities: Flow<List<City>>

    /** Кешований прогноз із Room (single source of truth). */
    fun observeForecast(cityId: Long): Flow<Forecast?>

    /** Завантажує прогноз із мережі та оновлює кеш. */
    suspend fun refresh(city: City): Result<Unit>

    suspend fun searchCities(query: String): Result<List<City>>

    /** Додає місто (якщо його ще немає) і робить його вибраним. */
    suspend fun selectCity(city: City)

    suspend fun removeCity(cityId: Long)

    suspend fun ensureDefaultCity()
}
