package com.relapps.everythingyouneed

import com.relapps.everythingyouneed.models.MenuItem

object Constants {

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