package com.example.glassweather.ui

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.glassweather.R
import com.example.glassweather.domain.WeatherCondition

@StringRes
fun WeatherCondition.labelRes(): Int = when (this) {
    WeatherCondition.Clear -> R.string.condition_clear
    WeatherCondition.MainlyClear -> R.string.condition_mainly_clear
    WeatherCondition.PartlyCloudy -> R.string.condition_partly_cloudy
    WeatherCondition.Overcast -> R.string.condition_overcast
    WeatherCondition.Fog -> R.string.condition_fog
    WeatherCondition.Drizzle -> R.string.condition_drizzle
    WeatherCondition.Rain -> R.string.condition_rain
    WeatherCondition.FreezingRain -> R.string.condition_freezing_rain
    WeatherCondition.Snow -> R.string.condition_snow
    WeatherCondition.Showers -> R.string.condition_showers
    WeatherCondition.SnowShowers -> R.string.condition_snow_showers
    WeatherCondition.Thunderstorm -> R.string.condition_thunderstorm
}

/** Запасна іконка, поки (або якщо) не завантажилась картинка через Coil. */
fun WeatherCondition.emoji(isDay: Boolean): String = when (this) {
    WeatherCondition.Clear -> if (isDay) "☀️" else "🌙"
    WeatherCondition.MainlyClear -> if (isDay) "🌤️" else "🌙"
    WeatherCondition.PartlyCloudy -> if (isDay) "⛅" else "☁️"
    WeatherCondition.Overcast -> "☁️"
    WeatherCondition.Fog -> "🌫️"
    WeatherCondition.Drizzle -> "🌦️"
    WeatherCondition.Rain, WeatherCondition.Showers -> "🌧️"
    WeatherCondition.FreezingRain, WeatherCondition.SnowShowers -> "🌨️"
    WeatherCondition.Snow -> "❄️"
    WeatherCondition.Thunderstorm -> "⛈️"
}

/** Іконки Meteocons (basmilius/weather-icons). Якщо URL недоступний — показується emoji. */
fun WeatherCondition.iconUrl(isDay: Boolean): String {
    val name = when (this) {
        WeatherCondition.Clear -> if (isDay) "clear-day" else "clear-night"
        WeatherCondition.MainlyClear, WeatherCondition.PartlyCloudy ->
            if (isDay) "partly-cloudy-day" else "partly-cloudy-night"
        WeatherCondition.Overcast -> "overcast"
        WeatherCondition.Fog -> "fog"
        WeatherCondition.Drizzle -> "drizzle"
        WeatherCondition.Rain, WeatherCondition.Showers -> "rain"
        WeatherCondition.FreezingRain -> "sleet"
        WeatherCondition.Snow, WeatherCondition.SnowShowers -> "snow"
        WeatherCondition.Thunderstorm -> "thunderstorms-rain"
    }
    return "https://raw.githubusercontent.com/basmilius/weather-icons/dev/production/fill/png/512/$name.png"
}

data class SkyPalette(
    val top: Color,
    val bottom: Color,
    val orbA: Color,
    val orbB: Color,
)

fun skyPalette(condition: WeatherCondition?, isDay: Boolean): SkyPalette = when {
    condition == null || !isDay && condition in clearish -> NightSky
    !isDay -> StormySky.copy(top = Color(0xFF0B1230), bottom = Color(0xFF1B2A55))
    condition in clearish -> DaySky
    condition == WeatherCondition.Snow || condition == WeatherCondition.SnowShowers -> SnowSky
    condition in wet -> StormySky
    else -> CloudySky
}

private val clearish = setOf(WeatherCondition.Clear, WeatherCondition.MainlyClear, WeatherCondition.PartlyCloudy)
private val wet = setOf(
    WeatherCondition.Drizzle, WeatherCondition.Rain, WeatherCondition.FreezingRain,
    WeatherCondition.Showers, WeatherCondition.Thunderstorm,
)

private val NightSky = SkyPalette(Color(0xFF0B1230), Color(0xFF25306B), Color(0xFF6A4CFF), Color(0xFF00B4D8))
private val DaySky = SkyPalette(Color(0xFF2E7BEA), Color(0xFF6CCBFF), Color(0xFFFFC857), Color(0xFF00E5FF))
private val CloudySky = SkyPalette(Color(0xFF55688A), Color(0xFF9DB1CC), Color(0xFFB8C6E0), Color(0xFF7AA2F7))
private val StormySky = SkyPalette(Color(0xFF1E2A44), Color(0xFF3F5272), Color(0xFF4C6FFF), Color(0xFF2EC4B6))
private val SnowSky = SkyPalette(Color(0xFF6C86B8), Color(0xFFC9D8F2), Color(0xFFFFFFFF), Color(0xFFA5C8FF))
