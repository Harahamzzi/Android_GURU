package com.example.guru_hemjee

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MyHamsterAdapter(val context: Context, private val items:ArrayList<MyHamsterItem>, val itemClick: (MyHamsterItem, Boolean) -> Unit):
    RecyclerView.Adapter<MyHamsterAdapter.ViewHolder>(){
    //item>>바인딩 될 데이터 객체 배열

    //바인딩 당할 item 설정하기
    inner class ViewHolder(view: View?, itemClick: (MyHamsterItem, Boolean) -> Unit): RecyclerView.ViewHolder(view!!){
        val background = view!!.findViewById<ImageView>(R.id.myHItemBgImageView)

        fun bind(item: MyHamsterItem, context: Context){
            background?.setImageResource(item.picSource)

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHamsterAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.container_my_hamster_item, parent, false)
        return ViewHolder(view, itemClick)
    }

    //바인딩
    override fun onBindViewHolder(holder: MyHamsterAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    //어댑터로 바인딩된 아이템 개수 반환
    override fun getItemCount() = items.size
}