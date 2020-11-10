package com.relapps.everythingyouneed.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.adapters.MenuItemAdapter
import com.relapps.everythingyouneed.models.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var menuItemsList = Constants.getAvailableMenuItems()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView(menuItemsList)
    }

    private fun setUpRecyclerView(menuItemsList: ArrayList<MenuItem>) {

        rvMainMenu.layoutManager = GridLayoutManager(this, 1)

        val menuAdapter = MenuItemAdapter(this, menuItemsList)

        rvMainMenu.adapter = menuAdapter

        menuAdapter.setOnClickListener(object : MenuItemAdapter.OnClickListener
        {
            override fun onClick(position: Int, model: MenuItem) {
                when(position)
                {
                    0 -> startActivity(Intent(this@MainActivity, BoredApiActivity::class.java))

                    1 -> startActivity(Intent(this@MainActivity, CalendarificActivity::class.java))

                    2-> startActivity(Intent(this@MainActivity, WeatherActivity::class.java))
                }
            }

        })

    }
}