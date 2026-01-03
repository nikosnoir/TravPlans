package com.example.travplans

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromActivityItemList(value: List<ActivityItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ActivityItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toActivityItemList(value: String): MutableList<ActivityItem> {
        val gson = Gson()
        val type = object : TypeToken<List<ActivityItem>>() {}.type
        val list: List<ActivityItem> = gson.fromJson(value, type)
        return list.toMutableList()
    }
}