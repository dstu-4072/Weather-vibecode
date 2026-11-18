package com.example.glassweather

import com.example.glassweather.data.remote.CurrentDto
import com.example.glassweather.data.remote.DailyDto
import com.example.glassweather.data.remote.ForecastResponse
import com.example.glassweather.data.remote.HourlyDto
import com.example.glassweather.data.toDomain
import com.example.glassweather.domain.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherMappingTest {

    @Test
    fun `wmo codes map to conditions`() {
        assertEquals(WeatherCondition.Clear, WeatherCondition.fromCode(0))
        assertEquals(WeatherCondition.Fog, WeatherCondition.fromCode(48))
        assertEquals(WeatherCondition.Rain, WeatherCondition.fromCode(63))
        assertEquals(WeatherCondition.FreezingRain, WeatherCondition.fromCode(66))
        assertEquals(WeatherCondition.Snow, WeatherCondition.fromCode(73))
        assertEquals(WeatherCondition.Thunderstorm, WeatherCondition.fromCode(95))
        assertEquals(WeatherCondition.Overcast, WeatherCondition.fromCode(999))
    }

    @Test
    fun `hourly forecast starts at current hour and is capped at 24 entries`() {
        val times = (0 until 48).map { "2024-05-%02dT%02d:00".format(1 + it / 24, it % 24) }
        val response = ForecastResponse(
            current = CurrentDto(
                time = "2024-05-01T10:15",
                temperature = 18.4,
                apparentTemperature = 17.0,
                humidity = 55,
                weatherCode = 2,
                windSpeed = 12.0,
                isDay = 1,
            ),
            hourly = HourlyDto(
                time = times,
                temperature = times.map { 10.0 },
                weatherCode = times.map { 0 },
                isDay = times.map { 1 },
            ),
            daily = DailyDto(
                time = listOf("2024-05-01"),
                weatherCode = listOf(2),
                temperatureMax = listOf(20.0),
                temperatureMin = listOf(9.0),
            ),
        )

        val forecast = response.toDomain(updatedAt = 1L)

        assertEquals(24, forecast.hourly.size)
        assertEquals("2024-05-01T10:00", forecast.hourly.first().time)
        assertTrue(forecast.current.isDay)
        assertEquals(WeatherCondition.PartlyCloudy, forecast.current.condition)
        assertEquals(1, forecast.daily.size)
    }
}
