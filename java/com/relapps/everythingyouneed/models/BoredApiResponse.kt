package com.relapps.everythingyouneed.models

import java.io.Serializable

data class BoredApiResponse (
    val activity: String,
    val asccessibility: Double,
    val type: String,
    val participants: Int,
    val price: Double,
    val link: String,
    val key: String
):Serializable

