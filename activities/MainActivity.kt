package com.relapps.everythingyouneed.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.adapters.MenuItemAdapter
import com.relapps.everythingyouneed.models.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when(Constants.menuItemsTagSelection)
        {
            0 -> setUpRecyclerView(Constants.getAvailableMenuItems())

            1 -> setUpRecyclerView(Constants.getToolsMenuItems())

            2 -> setUpRecyclerView(Constants.getEntertainmentMenuItems())
        }



        setupBottomBar()


        if (!Constants.isNetworkAvailable(this))
        {
            showWarningDialog()
        }


    }

    private fun setupBottomBar() {
        bottom_navigation.setOnNavigationItemReselectedListener {
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

    private fun showWarningDialog() {
         AlertDialog.Builder(this)
                .setTitle("No network available")
                .setMessage("This app requires internet connection to work properly. Please turn on Wi-Fi or cellular network.")
                .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    startActivity(Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS))
                })
                .show()
    }

    private fun setUpRecyclerView(menuItemsList: ArrayList<MenuItem>) {

        rvMainMenu.layoutManager = GridLayoutManager(this, 1)

        val menuAdapter = MenuItemAdapter(this, menuItemsList)

        rvMainMenu.adapter = menuAdapter

        menuAdapter.setOnClickListener(object : MenuItemAdapter.OnClickListener
        {
            override fun onClick(position: Int, model: MenuItem) {
                when(model.id)
                {
                    1 -> startActivity(Intent(this@MainActivity, BoredApiActivity::class.java))

                    2 -> startActivity(Intent(this@MainActivity, CalendarificActivity::class.java))

                    3 -> startActivity(Intent(this@MainActivity, WeatherActivity::class.java))

                    4 -> startActivity(Intent(this@MainActivity, GameSalesActivity::class.java))

                    5 -> startActivity(Intent(this@MainActivity, RandomAnimalPicturesActivity::class.java))
                }
            }

        })

    }
}