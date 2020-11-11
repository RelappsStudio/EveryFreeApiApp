package com.relapps.everythingyouneed.models

import java.io.Serializable

data class CountryNameResponse (
        val name: String,
        val alpha2Code: String
):Serializable

