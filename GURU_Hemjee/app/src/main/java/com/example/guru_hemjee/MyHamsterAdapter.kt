package com.example.guru_hemjee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MyHamsterAdapter(val context: Context, private val items:ArrayList<MyHamsterItem>):
    RecyclerView.Adapter<MyHamsterAdapter.ViewHolder>(){

    inner class ViewHolder(view: View?): RecyclerView.ViewHolder(view!!){
        val background = view?.findViewById<ImageView>(R.id.myHItemBgImageView)

        fun bind(items: MyHamsterItem, context: Context){
            background?.setImageResource(items.picSource)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHamsterAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_my_hamster_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyHamsterAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    override fun getItemCount() = items.size
}