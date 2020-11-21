package com.relapps.everythingyouneed.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.models.randomAnimalPictures.DogApiResponse
import com.relapps.everythingyouneed.models.randomAnimalPictures.RandomBirdResponse
import com.relapps.everythingyouneed.models.randomAnimalPictures.RandomFoxResponse
import com.relapps.everythingyouneed.models.randomAnimalPictures.RequestKittensResponse
import com.relapps.everythingyouneed.services.randomAnimalsServices.RandomBirdService
import com.relapps.everythingyouneed.services.randomAnimalsServices.RandomCatService
import com.relapps.everythingyouneed.services.randomAnimalsServices.RandomDogService
import com.relapps.everythingyouneed.services.randomAnimalsServices.RandomFoxService
import kotlinx.android.synthetic.main.activity_random_animal_pictures.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RandomAnimalPicturesActivity : AppCompatActivity(), View.OnClickListener {

    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_animal_pictures)


        llCatsDogs.visibility = View.VISIBLE
        llFoxesGoats.visibility = View.VISIBLE
        cvRandomAnimalPic.visibility = View.GONE

        cvGetRandomCat.setOnClickListener(this)
        cvGetRandomDog.setOnClickListener(this)
        cvGetRandomFox.setOnClickListener(this)
        cvGetRandomGoat.setOnClickListener(this)
        ivReturnToAnimalPicturesActivity.setOnClickListener(this)



        setupBottomBar()
    }


    private fun setupBottomBar() {
        bottom_navigation_animals.setOnNavigationItemReselectedListener {
            when(it.itemId)
            {
                R.id.action_home ->
                {
                    Constants.menuItemsTagSelection = 0
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.action_tools ->
                {
                    Constants.menuItemsTagSelection = 1
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.action_entertainment ->
                {
                    Constants.menuItemsTagSelection = 2
                    startActivity(Intent(this, MainActivity::class.java))
                }

            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.cvGetRandomCat -> getRandomCat()
            R.id.cvGetRandomDog -> getRandomDog()
            R.id.cvGetRandomFox -> getRandomFox()
            R.id.cvGetRandomGoat -> getRandomBird()
            R.id.ivReturnToAnimalPicturesActivity ->
            {
                llCatsDogs.visibility = View.VISIBLE
                llFoxesGoats.visibility = View.VISIBLE
                cvRandomAnimalPic.visibility = View.GONE
            }
        }
    }

    private fun getRandomCat() {
        if (Constants.isNetworkAvailable(this))
        {
            val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.random_cat_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(RandomCatService::class.java)
            val listCall = service.getCats()

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<List<RequestKittensResponse>> {
                override fun onResponse(
                    call: Call<List<RequestKittensResponse>>?,
                    response: Response<List<RequestKittensResponse>>?
                ) {
                    if (response!!.isSuccessful) {
                        Log.i("Cat Response = ", "${response.body()}")

                        val catResponse = response.body()

                        llCatsDogs.visibility = View.GONE
                        llFoxesGoats.visibility = View.GONE
                        cvRandomAnimalPic.visibility = View.VISIBLE
                        Glide.with(this@RandomAnimalPicturesActivity).load(catResponse[0].url).into(ivRandomAnimalPicture)
                        hideProgressDialog()
                    }
                }

                override fun onFailure(call: Call<List<RequestKittensResponse>>?, t: Throwable?) {
                    Log.e("Cat Failure = ", "${t!!.message}")
                }

            })
        }
        else
        {
            Toast.makeText(this, "No internet Connection!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getRandomDog() {
        if (Constants.isNetworkAvailable(this))
        {
            val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.random_dog_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(RandomDogService::class.java)
            val listCall = service.getDog()

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<DogApiResponse> {
                override fun onResponse(
                    call: Call<DogApiResponse>?,
                    response: Response<DogApiResponse>?
                ) {
                    if (response!!.isSuccessful) {
                        Log.i("Dog Response = ", "${response.body()}")
                        val response = response.body()

                        llCatsDogs.visibility = View.GONE
                        llFoxesGoats.visibility = View.GONE
                        cvRandomAnimalPic.visibility = View.VISIBLE
                        Glide.with(this@RandomAnimalPicturesActivity).load("${response.message}").into(ivRandomAnimalPicture)
                        hideProgressDialog()
                    }
                }

                override fun onFailure(call: Call<DogApiResponse>?, t: Throwable?) {
                    Log.e("Dog Failure = ", "${t!!.message}")
                }

            })
        }
        else
        {
            Toast.makeText(this, "No internet Connection!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getRandomFox() {
        if (Constants.isNetworkAvailable(this))
        {
            val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.random_fox_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(RandomFoxService::class.java)
            val listCall = service.getFox()

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<RandomFoxResponse> {
                override fun onResponse(
                    call: Call<RandomFoxResponse>?,
                    response: Response<RandomFoxResponse>?
                ) {
                    if (response!!.isSuccessful) {


                        val response = response.body()
                        Log.i("Fox Response = ", "${response}")

                        llCatsDogs.visibility = View.GONE
                        llFoxesGoats.visibility = View.GONE
                        cvRandomAnimalPic.visibility = View.VISIBLE
                        Glide.with(this@RandomAnimalPicturesActivity).load("${response.image}").into(ivRandomAnimalPicture)
                        hideProgressDialog()
                    }
                }

                override fun onFailure(call: Call<RandomFoxResponse>?, t: Throwable?) {
                    Log.e("Fox Failure = ", "${t!!.message}")
                }

            })
        }
        else
        {
            Toast.makeText(this, "No internet Connection!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getRandomBird() {
        if (Constants.isNetworkAvailable(this))
        {

            val retrofit = Retrofit.Builder()
                    .baseUrl(getString(R.string.random_bird_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service = retrofit.create(RandomBirdService::class.java)
            val listCall = service.getBird()

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<RandomBirdResponse>
            {
                override fun onResponse(call: Call<RandomBirdResponse>?, response: Response<RandomBirdResponse>?) {

                    if (response!!.isSuccessful)
                    {
                        llCatsDogs.visibility = View.GONE
                        llFoxesGoats.visibility = View.GONE
                        cvRandomAnimalPic.visibility = View.VISIBLE

                        val birdResponse = response.body()
                        Glide.with(this@RandomAnimalPicturesActivity).load(birdResponse.link).into(ivRandomAnimalPicture)
                        hideProgressDialog()
                    }

                }

                override fun onFailure(call: Call<RandomBirdResponse>?, t: Throwable?) {

                }

            })

        }
        else
        {
            Toast.makeText(this, "No internet Connection!", Toast.LENGTH_LONG).show()
        }
    }


    private fun showCustomProgressDialog()
    {
        dialog = Dialog(this)

        dialog!!.setContentView(R.layout.custom_progress_dialog)

        dialog!!.setCancelable(false)

        dialog!!.show()
    }

    private fun hideProgressDialog()
    {
        if (dialog != null)
        {
            dialog!!.dismiss()
        }
    }
}