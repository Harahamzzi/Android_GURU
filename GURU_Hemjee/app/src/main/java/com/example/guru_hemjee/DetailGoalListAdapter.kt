package com.example.guru_hemjee

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class DetailGoalListAdapter(val context: Context, private val items: ArrayList<DetailGoalItem>): RecyclerView.Adapter<DetailGoalListAdapter.ViewHolder>(){

    inner class ViewHolder(view: View?): RecyclerView.ViewHolder(view!!){
        val icon = view!!.findViewById<ImageView>(R.id.goalIconImageView)
        val name = view!!.findViewById<TextView>(R.id.detailGoalListNameTextView)

        fun bind(item: DetailGoalItem, context: Context){
            icon.setImageResource(item.picSource)
            icon.setColorFilter(item.picColor, PorterDuff.Mode.SRC_IN)
            name.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_lock_detail_gaol, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailGoalListAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    override fun getItemCount() = items.size

}