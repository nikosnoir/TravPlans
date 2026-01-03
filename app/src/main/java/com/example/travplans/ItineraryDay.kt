package com.example.travplans

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "itinerary_days")
data class ItineraryDay(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: String,
    var date: String = "",
    @TypeConverters(Converters::class)
    val activities: MutableList<ActivityItem>
) : Serializable