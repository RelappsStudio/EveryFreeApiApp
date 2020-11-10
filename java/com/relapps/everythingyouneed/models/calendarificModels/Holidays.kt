package com.relapps.everythingyouneed.models.calendarificModels

import java.io.Serializable

data class Holidays (
    val name: String,
    val description: String,
    val date: Date,
    val type: List<String>

):Serializable
