package com.example.guru_hemjee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.FieldPosition

class MarketItemAdapter(val context: Context, private val items: ArrayList<MarketItem>) :
    RecyclerView.Adapter<MarketItemAdapter.ViewHolder>() {
    //item>>바인딩 될 데이터 객체 배열

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val background = view?.findViewById<ImageView>(R.id.marketItemBgImageView)
        val price = view?.findViewById<TextView>(R.id.marketItemSeedTextView)

        fun bind(item: MarketItem, cont: Context){
            background?.setImageResource(item.picSource)
            price?.text = item.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_market_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    override fun getItemCount() = items.size

}