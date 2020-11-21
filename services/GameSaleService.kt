package com.relapps.everythingyouneed.services

import com.relapps.everythingyouneed.models.GameSalesModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GameSaleService {

    @GET("api/1.0/deals")
    fun getSales(

    ):Call<List<GameSalesModel>>
}