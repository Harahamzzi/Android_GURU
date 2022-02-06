package com.example.guru_hemjee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceiptAdapter(val context: Context, private val items: ArrayList<ReceiptItem>):
    RecyclerView.Adapter<ReceiptAdapter.ViewHolder>(){

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val type = view!!.findViewById<ImageView>(R.id.receiptItemTypeImageView)
        val background = view!!.findViewById<ImageView>(R.id.receiptItemBgImageView)
        val price = view!!.findViewById<TextView>(R.id.receiptItemSeedTextView)

        fun bind(item: ReceiptItem, context: Context) {
            when(item.type){
                "clo" -> type?.setImageResource(R.drawable.ic_outline_checkroom_24)
                "furni" -> type?.setImageResource(R.drawable.ic_outline_chair_24)
                "bg" -> type?.setImageResource(R.drawable.ic_outline_wallpaper_24)
            }
            background?.setImageResource(item.picSource)
            price.text = item.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.container_market_reciept_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    override fun getItemCount() = items.size
}