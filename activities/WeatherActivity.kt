package com.relapps.everythingyouneed.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.R.*
import com.relapps.everythingyouneed.services.WeatherService
import com.relapps.weatherapp.models.WeatherResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherActivity : AppCompatActivity() {

    private var dialog: Dialog? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_weather)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        setupUI()


        if (!isLocationEnabled()) {
            notifyUser("Please turn on network or gps to enable automatic location set")

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            checkPermissions()
        }

        setupBottomBar()

    }



    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.layout.weather_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
           id.action_refresh ->
            {
                requestLocationData()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }


    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    private fun checkPermissions() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        requestLocationData()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showDialogForPermissions()
                }

            }).onSameThread().check()
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            val mLastLocation: Location = p0!!.lastLocation

            val latitude = mLastLocation.latitude
            val longitude = mLastLocation.longitude

            getLocationWeatherDetails(latitude, longitude)
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun showDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("Your location permissions are off")
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

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double)
    {
        if (Constants.isNetworkAvailable(this))
        {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(getString(string.weather_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service : WeatherService = retrofit.create(WeatherService::class.java)
            val listCall: Call<WeatherResponse> = service.getWeather(
                latitude, longitude, null,  getString(string.weather_api_key)
            )

            showCustomProgressDialog()

            listCall.enqueue(object: Callback<WeatherResponse>
            {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(call: Call<WeatherResponse>?, response: Response<WeatherResponse>?) {
                    if (response!!.isSuccessful)
                    {
                        hideProgressDialog()

                        val weatherList: WeatherResponse = response.body()

                        val weatherResponseJsonString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()

                        setupUI()

                        Log.i("Response result", "$weatherList")
                    }
                    else
                    {
                        when(response.code())
                        {
                            400 -> Log.e("Error 400", "Bad connection")
                            404 -> Log.e("Error 404", "Not found")
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
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

    private fun showCustomProgressDialog()
    {
        dialog = Dialog(this)

        dialog!!.setContentView(layout.custom_progress_dialog)

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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupUI()
    {
        val weatherResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")

        if (!weatherResponseJsonString.isNullOrEmpty()) {
            val  weatherResponse = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)



            for (i in weatherResponse.weather.indices) {
                Log.i("Weather name", weatherResponse.weather.toString())

                tvWeather.text = weatherResponse.weather[i].main
                tvWeatherDescription.text = weatherResponse.weather[i].description
                tvTemperature.text = getTemp(application.resources.configuration.locales.toString(), weatherResponse.main.temp) + "°C"
                tvHumidity.text = "${weatherResponse.main.humidity}%"
                tvWindSpeed.text = "${getWindSpeed(weatherResponse.wind.speed)} KM/H "
                tvSunrise.text = unixTime(weatherResponse.sys.sunrise)
                tvSunset.text = unixTime(weatherResponse.sys.sunset)
                tvLocation.text = "${weatherResponse.name} ${weatherResponse.sys.country}"

                when (weatherResponse.weather[i].icon) {
                    "01d" -> ivWeather.setImageResource(drawable.sunny)

                    "02d" -> ivWeather.setImageResource(drawable.cloud)

                    "03d" -> ivWeather.setImageResource(drawable.cloud)

                    "04d" -> ivWeather.setImageResource(drawable.cloud)

                    "09d" -> ivWeather.setImageResource(drawable.rain)

                    "10d" -> ivWeather.setImageResource(drawable.rain)

                    "11d" -> ivWeather.setImageResource(drawable.storm)

                    "13d" -> ivWeather.setImageResource(drawable.snowflake)

                    "50d" -> ivWeather.setImageResource(drawable.mist)

                    "01n" -> ivWeather.setImageResource(drawable.sunny)

                    "02n" -> ivWeather.setImageResource(drawable.cloud)

                    "03n" -> ivWeather.setImageResource(drawable.cloud)

                    "04n" -> ivWeather.setImageResource(drawable.cloud)

                    "09n" -> ivWeather.setImageResource(drawable.rain)

                    "10n" -> ivWeather.setImageResource(drawable.rain)

                    "11n" -> ivWeather.setImageResource(drawable.storm)

                    "13n" -> ivWeather.setImageResource(drawable.snowflake)

                    "50n" -> ivWeather.setImageResource(drawable.mist)
                }
            }

        }
    }

    private fun getTemp(localID: String, tempKelwin: Double): String
    {
        var finalTemp: Double = 0.0

        finalTemp = if ("US" == localID || "LR" == localID || "MM" == localID) {
            (tempKelwin * (9/5)) - 459.67
        } else {
            tempKelwin - 273.15
        }

        return finalTemp.roundToInt().toString()
    }

    private fun getWindSpeed(meterPerSec: Double): String
    {
        return (meterPerSec * 3.6).roundToInt().toString()
    }

    private fun unixTime(timex: Long): String
    {
        val date = Date(timex * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.UK)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    private fun setupBottomBar() {
        bottom_navigation_weather.setOnNavigationItemReselectedListener {
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



    private fun notifyUser(message:String)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
