package com.relapps.everythingyouneed.services.randomAnimalsServices

import com.relapps.everythingyouneed.models.randomAnimalPictures.RandomFoxResponse
import retrofit2.Call
import retrofit2.http.GET

interface RandomFoxService {

    @GET("floof/")
    fun getFox(): Call<RandomFoxResponse>
}