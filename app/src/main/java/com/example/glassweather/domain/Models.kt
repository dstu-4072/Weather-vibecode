package com.example.glassweather.domain

import kotlinx.serialization.Serializable

data class City(
    val id: Long,
    val name: String,
    val country: String,
    val region: String?,
    val latitude: Double,
    val longitude: Double,
)

@Serializable
data class Forecast(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val updatedAt: Long,
)

@Serializable
data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val isDay: Boolean,
) {
    val condition: WeatherCondition get() = WeatherCondition.fromCode(weatherCode)
}

@Serializable
data class HourlyWeather(
    /** Локальний час міста у форматі ISO, напр. 2024-05-01T14:00 */
    val time: String,
    val temperature: Double,
    val weatherCode: Int,
    val isDay: Boolean,
) {
    val condition: WeatherCondition get() = WeatherCondition.fromCode(weatherCode)
}

@Serializable
data class DailyWeather(
    /** ISO-дата, напр. 2024-05-01 */
    val date: String,
    val weatherCode: Int,
    val max: Double,
    val min: Double,
) {
    val condition: WeatherCondition get() = WeatherCondition.fromCode(weatherCode)
}
