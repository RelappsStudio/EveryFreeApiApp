package com.relapps.everythingyouneed.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.activities.GameSalesActivity
import com.relapps.everythingyouneed.models.GameSalesModel
import com.relapps.everythingyouneed.models.MenuItem
import kotlinx.android.synthetic.main.discounted_game_item.view.*
import kotlinx.android.synthetic.main.menu_item.view.*
import retrofit2.http.Url
import java.net.URL

open class GameSaleAdapter (private val context: Context, private val gamesList: List<GameSalesModel>):
RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GamesViewHolder(
            LayoutInflater.from(context).inflate(R.layout.discounted_game_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val gameItemModel = gamesList[position]
        val url = gamesList[position].thumb

        if (holder is GamesViewHolder)
        {
            Glide.with(context).load(url).into(object: CustomTarget<Drawable>()
            {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    holder.itemView.ivGameThumbnail.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    holder.itemView.ivGameThumbnail.setImageResource(R.drawable.bored_api)
                }

            })

            holder.itemView.tvGameTitle.text =  gameItemModel.title

            if (gameItemModel.metacriticScore == "0")
            {
                if (gameItemModel.steamRatingText != null)
                {
                    holder.itemView.llGameScore.visibility = View.VISIBLE
                    holder.itemView.ivScorePlatform.setImageResource(R.drawable.steam_icon_logo)
                    holder.itemView.tvMetacriticScore.text = gameItemModel.steamRatingText
                }
                else
                {
                    holder.itemView.llGameScore.visibility = View.GONE
                }

            }
            else
            {
                holder.itemView.llGameScore.visibility = View.VISIBLE
                holder.itemView.ivScorePlatform.setImageResource(R.drawable.metacritic_logo_original)
                holder.itemView.tvMetacriticScore.text = gameItemModel.metacriticScore
            }

            holder.itemView.tvSalePercentage.text = "${gameItemModel.savings.toDouble().toInt()}% discount"
        }
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    interface OnClickListener
    {
        fun onClick(position: Int, model: GameSalesModel)
    }

    private class GamesViewHolder(view: View) : RecyclerView.ViewHolder(view)
}