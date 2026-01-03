package com.example.travplans

import java.io.Serializable

data class ActivityItem(
    var description: String,
    var startTime: String,
    var endTime: String,
    var imageUri: String? = null
) : Serializable