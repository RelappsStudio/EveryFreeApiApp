package com.relapps.everythingyouneed

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.relapps.everythingyouneed.models.MenuItem

object Constants {

    const val PREFERENCE_NAME = "WeatherAppPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"
     var menuItemsTagSelection = 0

    fun isNetworkAvailable(context: Context): Boolean
    {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when
            {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

                else -> false
            }
        }
        else
        {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }


    fun getAvailableMenuItems(): ArrayList<MenuItem>
    {
        val menuItemsList = ArrayList<MenuItem>()

        val menuItem1 =  MenuItem(
            1,
R.drawable.bored,
            "Bored? Check me out!",
                "Entertainment"
        )

        menuItemsList.add(menuItem1)

        val menuItem2 =  MenuItem(
            2,
            R.drawable.holidays,
            "Holidays of the world",
                "Entertainment"
        )

        menuItemsList.add(menuItem2)

        val menuItem3 =  MenuItem(
            3,
            R.drawable.snowflake,
            "How's the weather today?",
                "Tool"
        )

        menuItemsList.add(menuItem3)

        val menuItem4 =  MenuItem(
                4,
                R.drawable.discount,
                "Games currently on sale",
                "Tool"
        )

        menuItemsList.add(menuItem4)

        val menuItem5 =  MenuItem(
            5,
            R.drawable.bored_api,
            "Random animal pictures",
            "Entertainment"
        )

        menuItemsList.add(menuItem5)
        
        return menuItemsList
    }

    fun getToolsMenuItems(): ArrayList<MenuItem>
    {

        val menuItemsList = ArrayList<MenuItem>()

        val menuItem3 =  MenuItem(
            3,
            R.drawable.snowflake,
            "How's the weather today?",
            "Tool"
        )
        menuItemsList.add(menuItem3)


        val menuItem4 =  MenuItem(
            4,
            R.drawable.discount,
            "Games currently on sale",
            "Tool"
        )

        menuItemsList.add(menuItem4)

        return menuItemsList

    }


    fun getEntertainmentMenuItems(): ArrayList<MenuItem>
    {

        val menuItemsList = ArrayList<MenuItem>()



        val menuItem1 =  MenuItem(
            1,
            R.drawable.bored,
            "Bored? Check me out!",
            "Entertainment"
        )

        menuItemsList.add(menuItem1)

        val menuItem2 =  MenuItem(
            2,
            R.drawable.holidays,
            "Holidays of the world",
            "Entertainment"
        )

        menuItemsList.add(menuItem2)

        val menuItem5 =  MenuItem(
            5,
            R.drawable.bored_api,
            "Random animal pictures",
            "Entertainment"
        )

        menuItemsList.add(menuItem5)





        return menuItemsList

    }
}