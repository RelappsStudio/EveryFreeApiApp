package com.relapps.everythingyouneed.services.randomAnimalsServices

import com.relapps.everythingyouneed.models.randomAnimalPictures.RandomBirdResponse
import retrofit2.Call
import retrofit2.http.GET

interface RandomBirdService {
    @GET("img/birb")
    fun getBird(): Call<RandomBirdResponse>
}