package com.example.guru_hemjee.Home.Store

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.Home.MainActivity
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ContainerMarketItemBinding

// 씨앗 상점
class StoreViewHolder(val context: Context, val binding: ContainerMarketItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(storeItem: StoreItem) {
        val itemIv = binding.containerMarketItemIv
        val seedTv = binding.containerMarketItemTv
        val spaceView = binding.containerMarketItemView

        // 뷰에 데이터 넣기
        itemIv.setImageResource(storeItem.imgId)
        seedTv.text = storeItem.price.toString()

        // 착용 중인 아이템 이라면
        if (storeItem.isSelected) {
            itemIv.setBackgroundResource(R.drawable.solid_market_selected_box)
        }
        // 아니라면
        else {
            itemIv.setBackgroundResource(R.drawable.solid_market_unselected_box)
        }

        // 해상도에 맞게 아이템 크기 조절
        val displayMetrics = DisplayMetrics()
        (context as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)

        var deviceWidth = displayMetrics.widthPixels // 가로
        deviceWidth = (deviceWidth - 65) / 4

        spaceView.layoutParams.width = deviceWidth
        spaceView.requestLayout() // 변경사항 적용
    }
}