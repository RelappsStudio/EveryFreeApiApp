package com.relapps.everythingyouneed.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.models.calendarificModels.CalendarificResponse
import com.relapps.everythingyouneed.services.CalendarificService
import kotlinx.android.synthetic.main.activity_calendarific.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class CalendarificActivity : AppCompatActivity() {

    private var country: String? = null
    private var year: Int? = null
    private var day: Int? = null
    private var month: Int? = null
    private var type: String? = null

    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendarific)

        setVariablesToCurrent()

        getHolidays()

    }

    private fun setVariablesToCurrent()
    {
        country = (Locale.getDefault().language).capitalize()

        val sdf = SimpleDateFormat("dd,MM,yyyy ")
        val currentDate = sdf.format(Date())
        day = 25
        month = 12
        year = 2020
        Log.e(" C DATE is" , "$currentDate")
        Log.e(" set date is" , "$day/$month/$year")

    }

    private fun getHolidays()
    {
        if (Constants.isNetworkAvailable(this))
        {
            val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.calendarific_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(CalendarificService::class.java)
            val listCall = service.getHolidays(getString(R.string.calendarific_api_key), "$country,", "$year,", "$day,", "$month,", type)

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<CalendarificResponse>
            {
                override fun onResponse(
                    call: Call<CalendarificResponse>?,
                    response: Response<CalendarificResponse>?
                ) {
                    if (response!!.code() == 200)
                    {
                        hideProgressDialog()

                        val calendarificResponse = response.body()

                        Log.e("Response= ", "$calendarificResponse")

                        setupResponseUI(calendarificResponse)
                    }
                    else
                    {
                        when(response.code())
                        {
                            401 -> Log.e("Error ", "Unauthorized Missing or incorrect API token in header.")
                            422 -> Log.e("Error ", "Un-processable Entity meaning something with the message isn’t quite right, this could be malformed JSON or incorrect fields. In this case, the response body contains JSON with an API error code and message containing details on what went wrong.")
                            429 -> Log.e("Error ", "Too many requests. API limits reached.")
                            500 -> Log.e("Error ", "Internal Server Error This is an issue with Calendarific's servers processing your request. In most cases the message is lost during the process, and we are notified so that we can investigate the issue.")
                            503 -> Log.e("Error ", "Service Unavailable During planned service outages, Calendarific API services will return this HTTP response and associated JSON body.")
                            600 -> Log.e("Error ", "Maintenance The Calendarific API is offline for maintenance.")
                            601 -> Log.e("Error ", "Unauthorized Missing or incorrect API token.")
                            602 -> Log.e("Error ", "Invalid query parameters.")
                            603 -> Log.e("Error ", "Authorized Subscription level required.")
                        }
                    }
                }

                override fun onFailure(call: Call<CalendarificResponse>?, t: Throwable?) {
                    Log.e("Fatal Error", "Not able to make a call: ${t!!.message}")
                }

            })
        }
    }

    private fun setupResponseUI(response: CalendarificResponse)
    {
        for (i in response.response.holidays.indices)
        {
            tvTest.text = response.response.holidays[i].description
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