package com.example.travplans

class ItineraryRepository(private val itineraryDao: ItineraryDao) {

    suspend fun getAll(): List<ItineraryDay> {
        return itineraryDao.getAll()
    }

    suspend fun insert(itineraryDay: ItineraryDay) {
        itineraryDao.insert(itineraryDay)
    }
}