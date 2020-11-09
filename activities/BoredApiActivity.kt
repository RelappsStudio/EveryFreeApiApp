package com.relapps.everythingyouneed.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import com.relapps.everythingyouneed.services.BoredApiService
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.models.BoredApiResponse
import kotlinx.android.synthetic.main.activity_bored_api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoredApiActivity : AppCompatActivity() {

    private var key: Long? = null
    private var type: String? = null
    private var participants: Int? = null
    private var price: Double? = null
    private var accessibility: Double? = null
    private var dialog: Dialog? = null

    val categories = arrayOf("random", "education", "music", "recreational", "busywork", "social", "relaxation", "charity", "diy", "cooking")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bored_api)

        getSomethingToDo()

        spinnerHandler()

        btnRefresh.setOnClickListener {
            getSomethingToDo()
        }



    }

    private fun spinnerHandler() {
        var mySpinner: Spinner = mySpinner

        var adapter: ArrayAdapter<String> =ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        mySpinner.adapter = adapter
        mySpinner.prompt = "Category"

        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (categories[position] == "random")
                {

                }
                else
                {
                    type = categories[position]
                    tvType.text = categories[position]
                    tvParticipants.text = ""
                    tvLink.text = ""
                    tvCost.text = ""
                    tvTask.text = ""
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


    }

    private fun getSomethingToDo() {
        if (Constants.isNetworkAvailable(this))
        {
            val retrofit = Retrofit.Builder()
                    .baseUrl(getString(R.string.bored_api_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service = retrofit.create(BoredApiService::class.java)
            val listCall: Call<BoredApiResponse> = service.getActivity(
                    key, type, participants, price, accessibility,
            )

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<BoredApiResponse>
            {
                override fun onResponse(call: Call<BoredApiResponse>?, response: Response<BoredApiResponse>?) {
                    hideProgressDialog()

                    if (response!!.isSuccessful)
                    {
                        val boredResponse = response.body()

                        Log.i("Response = ","$boredResponse")

                        setupResponseUI(boredResponse)
                    }


                }

                override fun onFailure(call: Call<BoredApiResponse>?, t: Throwable?) {
                    hideProgressDialog()

                    Log.e("Fatal Error", "Not able to make a call: ${t!!.message}")
                }

            })
        }
        else
        {
            notifyUser("No internet connection. Loaded previously obtained data.")
        }
    }



    private fun setupResponseUI(response: BoredApiResponse) {
        tvTask.text = response.activity
        tvCost.text = response.price.toString()
        tvParticipants.text = response.participants.toString()

        if (response.participants == 1)
        {
            tvParticipants.text = "For ${response.participants} person"
        }
        else
        {
            tvParticipants.text = "For ${response.participants} people"
        }

        when(response.type)
        {
            "education" -> {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.education)
            }

            "recreational" ->
            {
                tvType.text = "recreation"
                ivType.setImageResource(R.drawable.recreation)
            }

            "busywork" ->
            {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.work)
            }

            "social" ->
            {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.friends)
            }

            "relaxation" ->
            {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.relaxation)
            }

            "music" ->
            {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.music)
            }

            "charity" ->
            {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.charity)
            }

            "diy" ->
            {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.diy)
            }

            "cooking" ->
            {
                tvType.text = response.type
                ivType.setImageResource(R.drawable.cooking)
            }
        }

        if (response.link.isNullOrEmpty())
        {
            tvLink.text = "Nothing to show here"
        }
        else
        {
            tvLink.text = response.link
        }

    }


    private fun showDialogForPermissions() {
        AlertDialog.Builder(this)
                .setMessage("Some permissions are off")
                .setPositiveButton("Go to settings")
                { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)

                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }

                }
                .setNegativeButton("Cancel")
                { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
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



    private fun notifyUser(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}