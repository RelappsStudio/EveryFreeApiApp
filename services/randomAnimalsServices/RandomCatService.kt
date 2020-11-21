package com.relapps.everythingyouneed.services.randomAnimalsServices

import com.relapps.everythingyouneed.models.randomAnimalPictures.RequestKittensResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RandomCatService {
    @GET("v1/images/search/")
    fun getCats(
        //@Query("limit") limit: Int,
       // @Query("api_key") api_key: String
        //@Header("x-api-key") api_key: String
    ):Call<List<RequestKittensResponse>>
}