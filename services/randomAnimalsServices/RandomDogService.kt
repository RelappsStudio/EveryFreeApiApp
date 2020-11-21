package com.relapps.everythingyouneed.services.randomAnimalsServices

import com.relapps.everythingyouneed.models.randomAnimalPictures.DogApiResponse
import retrofit2.Call
import retrofit2.http.GET

interface RandomDogService {

    @GET("breeds/image/random")
    fun getDog(): Call<DogApiResponse>


}