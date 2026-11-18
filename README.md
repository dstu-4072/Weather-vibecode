# Скляна погода (Glass Weather)

Android-додаток прогнозу погоди у стилі «рідкого скла».

**Стек:** Kotlin · Jetpack Compose (Material 3) · Hilt · Retrofit + kotlinx.serialization · Room · Coil · MVVM.
**Дані:** [Open-Meteo](https://open-meteo.com) (прогноз і геокодинг, API-ключ не потрібен).

## Архітектура

```
ui (Compose, ViewModel)  →  domain (моделі)  ←  data (Retrofit, Room, Repository)
```

- Room — єдине джерело правди: екран підписаний на кешований прогноз, мережа лише оновлює кеш (працює офлайн).
- `WeatherViewModel` віддає два `StateFlow`: `WeatherUiState` та `SearchUiState`.
- Вибране місто = місто з найбільшим `lastUsedAt` у таблиці `cities`.

## Збірка

Локально (потрібні JDK 25 + Android SDK): `gradle assembleDebug`.
Або через GitHub Actions: workflow `.github/workflows/android.yml` збирає debug APK і
викладає його як artifact `glass-weather-debug-apk`.
