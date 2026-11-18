package com.example.glassweather.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

private const val CURRENT_FIELDS =
    "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m,is_day"
private const val HOURLY_FIELDS = "temperature_2m,weather_code,is_day"
private const val DAILY_FIELDS = "weather_code,temperature_2m_max,temperature_2m_min"

interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("hourly") hourly: String = HOURLY_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
    ): ForecastResponse

    /** Абсолютний URL перевизначає baseUrl Retrofit. */
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 8,
        @Query("language") language: String = "uk",
    ): GeocodingResponse
}
