package com.example.glassweather.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.example.glassweather.R
import com.example.glassweather.domain.City
import com.example.glassweather.domain.DailyWeather
import com.example.glassweather.domain.Forecast
import com.example.glassweather.domain.HourlyWeather
import com.example.glassweather.domain.WeatherCondition
import com.example.glassweather.ui.components.GlassCard
import com.example.glassweather.ui.components.GlassChip
import com.example.glassweather.ui.components.GlassSearchField
import com.example.glassweather.ui.components.SectionTitle
import com.example.glassweather.ui.components.SkyBackground
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherRoute(viewModel: WeatherViewModel = hiltViewModel()) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val search = viewModel.search.collectAsStateWithLifecycle().value
    WeatherScreen(
        state = state,
        search = search,
        onRefresh = viewModel::refresh,
        onQueryChange = viewModel::onQueryChange,
        onCitySelected = viewModel::onCitySelected,
        onRemoveCity = viewModel::onRemoveCity,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    state: WeatherUiState,
    search: SearchUiState,
    onRefresh: () -> Unit,
    onQueryChange: (String) -> Unit,
    onCitySelected: (City) -> Unit,
    onRemoveCity: (City) -> Unit,
) {
    val current = state.forecast?.current
    val layoutDirection = LocalLayoutDirection.current
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val contentPadding = PaddingValues(
        start = insets.calculateStartPadding(layoutDirection) + 16.dp,
        top = insets.calculateTopPadding() + 16.dp,
        end = insets.calculateEndPadding(layoutDirection) + 16.dp,
        bottom = insets.calculateBottomPadding() + 24.dp,
    )

    CompositionLocalProvider(LocalContentColor provides Color.White) {
        Box(Modifier.fillMaxSize()) {
            SkyBackground(
                condition = current?.condition,
                isDay = current?.isDay ?: false,
                modifier = Modifier.fillMaxSize(),
            )
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item(key = "search") {
                        GlassSearchField(
                            value = search.query,
                            onValueChange = onQueryChange,
                            placeholder = stringResource(R.string.search_hint),
                        )
                    }

                    if (search.isActive) {
                        item(key = "search-results") {
                            SearchResults(search, onCitySelected)
                        }
                    } else {
                        item(key = "cities") {
                            SavedCities(state.savedCities, onCitySelected, onRemoveCity)
                        }
                    }

                    if (state.refreshFailed) {
                        item(key = "error") {
                            Text(
                                text = stringResource(R.string.refresh_failed),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    val forecast = state.forecast
                    if (forecast != null && !search.isActive) {
                        item(key = "header") { Header(state.city, forecast) }
                        item(key = "hourly") { HourlyCard(forecast.hourly) }
                        item(key = "daily") { DailyCard(forecast.daily) }
                        item(key = "details") { DetailsRow(forecast) }
                    } else if (forecast == null && !search.isActive) {
                        item(key = "loading") {
                            Text(
                                text = stringResource(R.string.loading),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(search: SearchUiState, onCitySelected: (City) -> Unit) {
    GlassCard(Modifier.fillMaxWidth()) {
        when {
            search.isLoading -> Text(stringResource(R.string.search_loading))
            search.failed -> Text(stringResource(R.string.search_failed))
            search.results.isEmpty() -> Text(stringResource(R.string.search_no_results))
            else -> search.results.forEachIndexed { index, city ->
                if (index > 0) Spacer(Modifier.height(4.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCitySelected(city) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                ) {
                    Text(city.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = listOfNotNull(city.region, city.country.ifBlank { null }).joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedCities(
    cities: List<City>,
    onSelect: (City) -> Unit,
    onRemove: (City) -> Unit,
) {
    if (cities.isEmpty()) return
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(cities, key = { it.id }) { city ->
            val isSelected = city == cities.first()
            GlassChip(
                text = city.name,
                selected = isSelected,
                onClick = { onSelect(city) },
                onRemove = if (cities.size > 1 && !isSelected) ({ onRemove(city) }) else null,
                removeDescription = stringResource(R.string.remove_city),
            )
        }
    }
}

@Composable
private fun Header(city: City?, forecast: Forecast) {
    val current = forecast.current
    val today = forecast.daily.firstOrNull()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = city?.name.orEmpty(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium,
        )
        city?.let {
            val subtitle = listOfNotNull(it.region, it.country.ifBlank { null }).joinToString(", ")
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        }
        WeatherIcon(current.condition, current.isDay, size = 128.dp)
        Text(
            text = "${current.temperature.roundToInt()}°",
            fontSize = 88.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
            text = stringResource(current.condition.labelRes()),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.feels_like, current.feelsLike.roundToInt()),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.75f),
        )
        today?.let {
            Text(
                text = stringResource(R.string.high_low, it.max.roundToInt(), it.min.roundToInt()),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f),
            )
        }
    }
}

@Composable
private fun HourlyCard(hours: List<HourlyWeather>) {
    GlassCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 16.dp)) {
        SectionTitle(stringResource(R.string.hourly_title), Modifier.padding(horizontal = 16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(hours.size, key = { hours[it].time }) { index ->
                val hour = hours[index]
                Column(
                    modifier = Modifier.width(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = if (index == 0) stringResource(R.string.now) else hour.time.takeLast(5),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                    WeatherIcon(hour.condition, hour.isDay, size = 36.dp)
                    Text("${hour.temperature.roundToInt()}°", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun DailyCard(days: List<DailyWeather>) {
    if (days.isEmpty()) return
    val weekMin = days.minOf { it.min }
    val weekMax = days.maxOf { it.max }
    GlassCard(Modifier.fillMaxWidth()) {
        SectionTitle(stringResource(R.string.daily_title))
        days.forEachIndexed { index, day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (index == 0) stringResource(R.string.today) else weekdayName(day.date),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.width(92.dp),
                )
                WeatherIcon(day.condition, isDay = true, size = 32.dp)
                Spacer(Modifier.width(12.dp))
                Text(
                    "${day.min.roundToInt()}°",
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.End,
                    color = Color.White.copy(alpha = 0.7f),
                )
                TempRangeBar(
                    min = day.min,
                    max = day.max,
                    weekMin = weekMin,
                    weekMax = weekMax,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                )
                Text("${day.max.roundToInt()}°", modifier = Modifier.width(36.dp))
            }
        }
    }
}

@Composable
private fun TempRangeBar(
    min: Double,
    max: Double,
    weekMin: Double,
    weekMax: Double,
    modifier: Modifier = Modifier,
) {
    val span = (weekMax - weekMin).coerceAtLeast(1.0)
    val start = ((min - weekMin) / span).toFloat().coerceIn(0f, 1f)
    val width = ((max - min) / span).toFloat().coerceAtLeast(0.04f)
    val end = (1f - start - width).coerceAtLeast(0.001f)
    Row(
        modifier
            .height(5.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.18f)),
    ) {
        Spacer(Modifier.weight(start.coerceAtLeast(0.001f)))
        Box(
            Modifier
                .weight(width)
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.85f)),
        )
        Spacer(Modifier.weight(end))
    }
}

@Composable
private fun DetailsRow(forecast: Forecast) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailCard(
            title = stringResource(R.string.humidity),
            value = stringResource(R.string.humidity_value, forecast.current.humidity),
            icon = "💧",
            modifier = Modifier.weight(1f),
        )
        DetailCard(
            title = stringResource(R.string.wind),
            value = stringResource(R.string.wind_value, forecast.current.windSpeed.roundToInt()),
            icon = "💨",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailCard(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    GlassCard(modifier) {
        SectionTitle("$icon  $title")
        Text(value, style = MaterialTheme.typography.headlineSmall)
    }
}

/** Іконка погоди: Coil завантажує картинку, а до завантаження (або при помилці) показуємо emoji. */
@Composable
private fun WeatherIcon(condition: WeatherCondition, isDay: Boolean, size: androidx.compose.ui.unit.Dp) {
    val fallback = @Composable {
        Box(Modifier.size(size), contentAlignment = Alignment.Center) {
            Text(condition.emoji(isDay), fontSize = (size.value * 0.7f).sp)
        }
    }
    SubcomposeAsyncImage(
        model = condition.iconUrl(isDay),
        contentDescription = null,
        modifier = Modifier.size(size),
        contentScale = ContentScale.Fit,
        loading = { fallback() },
        error = { fallback() },
    )
}

private fun weekdayName(isoDate: String): String = runCatching {
    LocalDate.parse(isoDate).dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { it.uppercase() }
}.getOrDefault(isoDate)
