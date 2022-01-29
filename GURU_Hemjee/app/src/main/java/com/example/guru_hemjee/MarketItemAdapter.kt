package com.example.guru_hemjee

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MarketItemAdapter(val context: Context, private val items: ArrayList<MarketItem>, val itemClick: (MarketItem, Boolean) -> Unit) :
    RecyclerView.Adapter<MarketItemAdapter.ViewHolder>() {
    //item>>바인딩 될 데이터 객체 배열

    //바인딩 당할 item 설정하기(뒤의 랍다식은 클릭처리를 위함)
    inner class ViewHolder(view: View?, itemClick: (MarketItem, Boolean) -> Unit) : RecyclerView.ViewHolder(view!!) {
        private val background = view!!.findViewById<ImageView>(R.id.marketItemBgImageView)
        private val price = view?.findViewById<TextView>(R.id.marketItemSeedTextView)

        fun bind(item: MarketItem, cont: Context){
            background?.setImageResource(item.picSource)
            price?.text = item.price.toString()

            var isClicked = item.isClicked
            if(isClicked)
                background.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF756557"))

            //클릭 설정(itemClick을 재설정 한다.)
            itemView.setOnClickListener {
                itemClick(item, !isClicked)

                //선택시 배경에 강조 효과 넣기
                if(isClicked){
                    background.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00756557"))
                } else {
                    background.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF756557"))
                }

                isClicked = !isClicked
            }
        }
    }

    //바인드할 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //바인딩 당할 item xml 파일명 지정
        val view = LayoutInflater.from(context).inflate(R.layout.listview_market_item, parent, false)
        return ViewHolder(view, itemClick)
    }

    //바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //데이터를 추가한 순서대로 바인딩
        holder.bind(items[position], context)
    }

    //어댑터로 바인딩된 아이템 개수 반환
    override fun getItemCount() = items.size

}