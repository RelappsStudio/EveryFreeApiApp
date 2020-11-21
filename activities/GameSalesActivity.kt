package com.relapps.everythingyouneed.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.relapps.everythingyouneed.Constants
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.adapters.GameSaleAdapter
import com.relapps.everythingyouneed.models.CountryNameResponse
import com.relapps.everythingyouneed.models.GameSalesModel
import com.relapps.everythingyouneed.models.MenuItem
import com.relapps.everythingyouneed.services.CountryNameService
import com.relapps.everythingyouneed.services.GameSaleService
import kotlinx.android.synthetic.main.activity_game_sales.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_game_sale_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameSalesActivity : AppCompatActivity() {

    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_sales)

        getOffers()

        setupBottomBar()
    }

    private fun setupBottomBar() {
        bottom_navigation_games.setOnNavigationItemReselectedListener {
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

    private fun  getOffers()
    {
        if (Constants.isNetworkAvailable(this))
        {
            val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.game_sales_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(GameSaleService::class.java)
            val listCall = service.getSales()

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<List<GameSalesModel>>
            {
                override fun onResponse(
                    call: Call<List<GameSalesModel>>?,
                    response: Response<List<GameSalesModel>>?
                ) {
                    if (response!!.isSuccessful)
                    {
                        hideProgressDialog()
                        val salesResponse = response.body()
                        Log.i("gameSalesResponse= ", "$salesResponse")

                        setupUI(salesResponse)

                    }
                }

                override fun onFailure(call: Call<List<GameSalesModel>>?, t: Throwable?) {
                    hideProgressDialog()
                    Log.e("Fatal error", "${t!!.message}")
                }

            })
        }
        else
        {
            Toast.makeText(this, "No network available", Toast.LENGTH_LONG).show()
        }

    }

    private fun setupUI(response: List<GameSalesModel>) {
        rvGamesList.layoutManager = GridLayoutManager(this, 1)
        val gamesAdapter = GameSaleAdapter(this, response)
        rvGamesList.adapter = gamesAdapter

        gamesAdapter.setOnClickListener(object : GameSaleAdapter.OnClickListener
        {
            override fun onClick(position: Int, model: GameSalesModel) {
                showFullSaleInfo(model)
            }

        })
    }

    private fun showFullSaleInfo(model: GameSalesModel) {
        var dialog = Dialog(this, R.style.Theme_MaterialComponents_Dialog)
            dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_game_sale_details)

        tvTest.text = model.title






            dialog.show()
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