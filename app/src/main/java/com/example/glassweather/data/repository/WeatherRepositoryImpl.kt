package com.example.glassweather.data.repository

import com.example.glassweather.data.local.CityDao
import com.example.glassweather.data.local.ForecastDao
import com.example.glassweather.data.local.ForecastEntity
import com.example.glassweather.data.remote.WeatherApi
import com.example.glassweather.data.toDomain
import com.example.glassweather.data.toEntity
import com.example.glassweather.domain.City
import com.example.glassweather.domain.Forecast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val cityDao: CityDao,
    private val forecastDao: ForecastDao,
    private val json: Json,
) : WeatherRepository {

    override val cities: Flow<List<City>> =
        cityDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeForecast(cityId: Long): Flow<Forecast?> =
        forecastDao.observe(cityId)
            .map { entity ->
                entity?.let { runCatching { json.decodeFromString<Forecast>(it.json) }.getOrNull() }
            }
            .flowOn(Dispatchers.Default)

    override suspend fun refresh(city: City): Result<Unit> = suspendRunCatching {
        val forecast = api.getForecast(city.latitude, city.longitude)
            .toDomain(updatedAt = System.currentTimeMillis())
        forecastDao.upsert(
            ForecastEntity(
                cityId = city.id,
                json = json.encodeToString(forecast),
                updatedAt = forecast.updatedAt,
            ),
        )
    }

    override suspend fun searchCities(query: String): Result<List<City>> = suspendRunCatching {
        api.searchCity(query.trim()).results.map { it.toDomain() }
    }

    override suspend fun selectCity(city: City) {
        cityDao.upsert(city.toEntity(lastUsedAt = System.currentTimeMillis()))
    }

    override suspend fun removeCity(cityId: Long) {
        cityDao.delete(cityId)
        forecastDao.delete(cityId)
    }

    override suspend fun ensureDefaultCity() {
        if (cityDao.count() == 0) {
            selectCity(
                City(
                    id = 703448,
                    name = "Київ",
                    country = "Україна",
                    region = null,
                    latitude = 50.4501,
                    longitude = 30.5234,
                ),
            )
        }
    }
}

private inline fun <T> suspendRunCatching(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
