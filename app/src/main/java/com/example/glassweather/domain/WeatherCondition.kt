package com.example.glassweather.domain

/** Спрощена класифікація WMO weather codes (https://open-meteo.com/en/docs). */
enum class WeatherCondition {
    Clear, MainlyClear, PartlyCloudy, Overcast, Fog, Drizzle, Rain,
    FreezingRain, Snow, Showers, SnowShowers, Thunderstorm;

    companion object {
        fun fromCode(code: Int): WeatherCondition = when (code) {
            0 -> Clear
            1 -> MainlyClear
            2 -> PartlyCloudy
            3 -> Overcast
            45, 48 -> Fog
            in 51..57 -> Drizzle
            66, 67 -> FreezingRain
            in 61..65 -> Rain
            in 71..77 -> Snow
            in 80..82 -> Showers
            85, 86 -> SnowShowers
            in 95..99 -> Thunderstorm
            else -> Overcast
        }
    }
}
