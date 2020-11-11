package com.relapps.everythingyouneed.services

import com.relapps.everythingyouneed.models.CountryNameResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CountryNameService {

    @GET("all/")
    fun getCountryNames(
            @Query("fields") fields: String,
    ):Call<List<CountryNameResponse>>
}