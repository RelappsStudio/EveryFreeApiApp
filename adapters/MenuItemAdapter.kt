package com.relapps.everythingyouneed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.relapps.everythingyouneed.R
import com.relapps.everythingyouneed.models.MenuItem
import kotlinx.android.synthetic.main.menu_item.view.*

open class MenuItemAdapter (private val context: Context, private var list: ArrayList<MenuItem>):
RecyclerView.Adapter<RecyclerView.ViewHolder>()

{
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val menuItemModel = list[position]

        if (holder is MyViewHolder)
        {
            holder.itemView.iv_menu_item_image.setImageResource(menuItemModel.icon)
            holder.itemView.tv_menu_item_title.text = menuItemModel.title

            holder.itemView.setOnClickListener {
                if (onClickListener != null)
                {
                    onClickListener!!.onClick(position, menuItemModel)
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    interface OnClickListener
    {
        fun onClick(position: Int, model: MenuItem)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}


