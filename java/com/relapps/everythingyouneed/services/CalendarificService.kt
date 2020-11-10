package com.relapps.everythingyouneed.services

import com.relapps.everythingyouneed.models.calendarificModels.CalendarificResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CalendarificService {

    @GET("api/v2/holidays")
    fun getHolidays(

        @Query("api_key")  api_key: String?,
        @Query("country") country: String?,
        @Query("year") year: String?,
        @Query("day") day: String?,
        @Query("month") month: String?,
        @Query("type") type: String?


    ): Call<CalendarificResponse>

}