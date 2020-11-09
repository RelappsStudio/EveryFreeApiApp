package com.relapps.everythingyouneed

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.relapps.everythingyouneed.models.MenuItem

object Constants {

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
R.drawable.bored_api,
            "Bored? Check me out!"
        )

        menuItemsList.add(menuItem1)


        return menuItemsList
    }
}