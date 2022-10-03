package com.harahamzzi.android.Home.Store

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.ContainerMarketItemBinding

// 씨앗 상점 및 나의 햄찌 관리 화면
class StoreViewHolder(val context: Context, val binding: ContainerMarketItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(storeItem: StoreItem) {
        val itemIv = binding.containerMarketItemIv
        val seedTv = binding.containerMarketItemTv
        val spaceView = binding.containerMarketItemView

        // 뷰에 데이터 넣기
        itemIv.setImageResource(storeItem.imgId)

        // 씨앗 상점 아이템 이라면 씨앗 개수 넣기
        if (storeItem.isStore) {
            seedTv.text = storeItem.price.toString()
        }
        // 아니라면 숨기기
        else {
            seedTv.visibility = View.INVISIBLE
        }

        // 착용 중인 아이템 이라면
        if (storeItem.isSelected) {
            itemIv.setBackgroundResource(R.drawable.solid_market_selected_box)
        }
        // 아니라면
        else {
            itemIv.setBackgroundResource(R.drawable.solid_market_unselected_box)
        }

        // 해상도에 맞게 아이템 크기 조절
        var deviceWidth = context.resources.displayMetrics.widthPixels // 가로
        deviceWidth = (deviceWidth - 65) / 4

        spaceView.layoutParams.width = deviceWidth
        spaceView.requestLayout() // 변경사항 적용
    }
}