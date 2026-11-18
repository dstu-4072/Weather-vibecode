package com.example.glassweather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glassweather.data.repository.WeatherRepository
import com.example.glassweather.domain.City
import com.example.glassweather.domain.Forecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val city: City? = null,
    val savedCities: List<City> = emptyList(),
    val forecast: Forecast? = null,
    val isRefreshing: Boolean = false,
    val refreshFailed: Boolean = false,
)

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<City> = emptyList(),
    val failed: Boolean = false,
) {
    val isActive: Boolean get() = query.trim().length >= MIN_QUERY_LENGTH

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val refreshFailed = MutableStateFlow(false)

    private val _search = MutableStateFlow(SearchUiState())
    val search: StateFlow<SearchUiState> = _search.asStateFlow()
    private var searchJob: Job? = null

    private val selectedCity = repository.cities
        .map { it.firstOrNull() }
        .distinctUntilChanged()

    private val forecast = selectedCity.flatMapLatest { city ->
        if (city == null) flowOf(null) else repository.observeForecast(city.id)
    }

    val uiState: StateFlow<WeatherUiState> = combine(
        repository.cities,
        forecast,
        isRefreshing,
        refreshFailed,
    ) { cities, forecast, refreshing, failed ->
        WeatherUiState(
            city = cities.firstOrNull(),
            savedCities = cities,
            forecast = forecast,
            isRefreshing = refreshing,
            refreshFailed = failed,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeatherUiState())

    init {
        viewModelScope.launch {
            repository.ensureDefaultCity()
            // Автоматично оновлюємо прогноз щоразу, коли змінюється вибране місто.
            selectedCity
                .filterNotNull()
                .distinctUntilChangedBy { it.id }
                .collectLatest { refreshCity(it) }
        }
    }

    fun refresh() {
        val city = uiState.value.city ?: return
        viewModelScope.launch { refreshCity(city) }
    }

    fun onQueryChange(query: String) {
        _search.update { it.copy(query = query, failed = false) }
        searchJob?.cancel()
        if (query.trim().length < SearchUiState.MIN_QUERY_LENGTH) {
            _search.update { it.copy(isLoading = false, results = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _search.update { it.copy(isLoading = true) }
            repository.searchCities(query)
                .onSuccess { cities ->
                    _search.update { it.copy(isLoading = false, results = cities, failed = false) }
                }
                .onFailure {
                    _search.update { it.copy(isLoading = false, results = emptyList(), failed = true) }
                }
        }
    }

    fun onCitySelected(city: City) {
        searchJob?.cancel()
        _search.value = SearchUiState()
        viewModelScope.launch { repository.selectCity(city) }
    }

    fun onRemoveCity(city: City) {
        if (uiState.value.savedCities.size <= 1) return
        viewModelScope.launch { repository.removeCity(city.id) }
    }

    private suspend fun refreshCity(city: City) {
        isRefreshing.value = true
        refreshFailed.value = false
        try {
            repository.refresh(city).onFailure { refreshFailed.value = true }
        } finally {
            isRefreshing.value = false
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 350L
    }
}
