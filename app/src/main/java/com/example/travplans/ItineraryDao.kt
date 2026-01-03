package com.example.travplans

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itinerary_days ORDER BY id ASC")
    suspend fun getAll(): List<ItineraryDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itineraryDay: ItineraryDay)
}