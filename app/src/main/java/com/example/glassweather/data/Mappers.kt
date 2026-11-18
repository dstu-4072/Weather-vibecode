package com.example.glassweather.data

import com.example.glassweather.data.local.CityEntity
import com.example.glassweather.data.remote.ForecastResponse
import com.example.glassweather.data.remote.GeoCityDto
import com.example.glassweather.domain.City
import com.example.glassweather.domain.CurrentWeather
import com.example.glassweather.domain.DailyWeather
import com.example.glassweather.domain.Forecast
import com.example.glassweather.domain.HourlyWeather

fun CityEntity.toDomain() = City(id, name, country, region, latitude, longitude)

fun City.toEntity(lastUsedAt: Long) =
    CityEntity(id, name, country, region, latitude, longitude, lastUsedAt)

fun GeoCityDto.toDomain() = City(
    id = id,
    name = name,
    country = country.orEmpty(),
    region = admin1?.takeIf { it != name },
    latitude = latitude,
    longitude = longitude,
)

fun ForecastResponse.toDomain(updatedAt: Long): Forecast {
    // Показуємо наступні 24 години, починаючи з поточної (рядки ISO порівнюються лексикографічно).
    val currentHour = current.time.take(13) // "2024-05-01T14"
    val hours = hourly.time.indices
        .filter { hourly.time[it].take(13) >= currentHour }
        .take(24)
        .map { i ->
            HourlyWeather(
                time = hourly.time[i],
                temperature = hourly.temperature[i],
                weatherCode = hourly.weatherCode[i],
                isDay = hourly.isDay[i] == 1,
            )
        }
    val days = daily.time.indices.map { i ->
        DailyWeather(
            date = daily.time[i],
            weatherCode = daily.weatherCode[i],
            max = daily.temperatureMax[i],
            min = daily.temperatureMin[i],
        )
    }
    return Forecast(
        current = CurrentWeather(
            temperature = current.temperature,
            feelsLike = current.apparentTemperature,
            humidity = current.humidity,
            windSpeed = current.windSpeed,
            weatherCode = current.weatherCode,
            isDay = current.isDay == 1,
        ),
        hourly = hours,
        daily = days,
        updatedAt = updatedAt,
    )
}
