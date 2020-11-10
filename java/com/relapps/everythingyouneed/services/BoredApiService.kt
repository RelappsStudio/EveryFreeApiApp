package com.relapps.everythingyouneed.services

import com.relapps.everythingyouneed.models.BoredApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BoredApiService {

    @GET("/api/activity/")
    fun getActivity(
        @Query("key") key: Long?,
        @Query("type") type: String?,
        @Query("participants") participants: Int?,
        @Query("price") price: Double?,
        //TODO: add minprice and maxprice options
        @Query("accessibility") accessibility: Double?
    //TODO: add minaccessibility and maxaccessibility options
    ): Call<BoredApiResponse>
}