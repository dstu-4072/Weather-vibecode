package com.example.glassweather.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM cities ORDER BY lastUsedAt DESC")
    fun observeAll(): Flow<List<CityEntity>>

    @Query("SELECT COUNT(*) FROM cities")
    suspend fun count(): Int

    @Upsert
    suspend fun upsert(city: CityEntity)

    @Query("DELETE FROM cities WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecasts WHERE cityId = :cityId")
    fun observe(cityId: Long): Flow<ForecastEntity?>

    @Upsert
    suspend fun upsert(forecast: ForecastEntity)

    @Query("DELETE FROM forecasts WHERE cityId = :cityId")
    suspend fun delete(cityId: Long)
}
