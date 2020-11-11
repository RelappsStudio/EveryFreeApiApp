package com.relapps.everythingyouneed.activities

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.models.CountryNameResponse
import com.relapps.everythingyouneed.models.calendarificModels.CalendarificResponse
import com.relapps.everythingyouneed.services.CalendarificService
import com.relapps.everythingyouneed.services.CountryNameService
import kotlinx.android.synthetic.main.activity_calendarific.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarificActivity : AppCompatActivity() {

    private var country: String? = null
    private var date: String? = null
    private var year: String? = null
    private var day: String? = null
    private var month: String? = null
    private var type: String? = null

    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendarific)

        setVariablesToCurrent()

        getCountries()

        btnCalendar.setOnClickListener {
            datePicker()
        }



    }

    private fun setVariablesToCurrent()
    {
        country = (Locale.getDefault().language).capitalize()

        val sdf = SimpleDateFormat("dd,MM,yyyy ")
        date = (sdf.format(Date())).toString()
        day = "${date!![0]}${date!![1]}"
        month = "${date!![3]}${date!![4]}"
        year = "${date!![6]}${date!![7]}${date!![8]}${date!![9]}"
        Log.e(" C DATE is" , "$date")
        Log.e(" set date is" , "$day,$month,$year")

    }

    private fun getCountries() {

        if (Constants.isNetworkAvailable(this))
        {
            val retrofit = Retrofit.Builder()
                    .baseUrl(getString(R.string.rest_countries_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service = retrofit.create(CountryNameService::class.java)
            val listCall = service.getCountryNames("name;alpha2Code")

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<List<CountryNameResponse>>
            {


                override fun onResponse(call: Call<List<CountryNameResponse>>?, response: Response<List<CountryNameResponse>>?) {
                    if (response!!.isSuccessful)
                    {

                        hideProgressDialog()
                        val countrynames = response.body()
                        val countrynameList = ArrayList<String>()
                        countrynameList.add("Current")
                        countrynameList.add("Random")

                        Log.e("names response = ", countrynames.toString())

                        for (i in countrynames.indices)
                        countrynameList.add(countrynames[i].name + ", " + countrynames[i].alpha2Code)


                        spinnerHandler(countrynameList)
                    }
                    else
                    {
                        hideProgressDialog()
                        Log.e("Names response=", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<List<CountryNameResponse>>?, t: Throwable?) {
                    hideProgressDialog()
                    Log.e("Fatal Error names", "Not able to make a call: ${t!!.message}")
                }

            })
        }
    }

    private fun spinnerHandler(countries: ArrayList<String>)
    {
        var mySpinner: Spinner = spinnerCountry

        var adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countries)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        mySpinner.adapter = adapter
        mySpinner.prompt = "Country selection"

        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {

                if (countries[position] == "Random")
                {
                        var random = kotlin.random.Random.nextInt(0, countries.size)
                    country = ("${(countries[random])[(countries[random].length - 2)]}${(countries[random])[(countries[random].length - 1)]}").capitalize()
                    getHolidays(countries[position])
                }
                else if (countries[position] == "Current")
                {
                    if (country != (Locale.getDefault().language).capitalize() )
                    {
                        country = (Locale.getDefault().language).capitalize()
                    }

                    getHolidays(countries[position])

                }
                else
                {
                    country = ("${(countries[position])[(countries[position].length - 2)]}${(countries[position])[(countries[position].length - 1)]}").capitalize()
                    getHolidays(countries[position])

                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


    }


    private fun getHolidays(chosenCountry: String)
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

                        setupResponseUI(calendarificResponse, chosenCountry)
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
                    Log.e("Fatal Error Holidays", "Not able to make a call: ${t!!.message}")
                }

            })
        }
    }

    private fun setupResponseUI(response: CalendarificResponse, chosenCountry: String)
    {
        if (response.response.holidays.isNullOrEmpty())
        {
            tvHolidayDescription.text = "Sadly, no holidays today in ${chosenCountry.subSequence(0, chosenCountry.length - 4)}. Work is mandatory"
        }
        else
        {
            for (i in response.response.holidays.indices)
            {
                tvHolidayTitle.text = response.response.holidays[i].name
                tvHolidayDescription.text = response.response.holidays[i].description
                tvHolidayType.text = response.response.holidays[i].type.toString()
            }

        }

    }

    private fun datePicker()
    {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->


            this.day = "$selectedDay"
            this.month = "${selectedMonth + 1}"
            this.year = "$selectedYear"
            getHolidays(country!!)


        }, year, month, day)

        dpd.datePicker.setMaxDate(Date().time - 86400000)
        dpd.show()
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