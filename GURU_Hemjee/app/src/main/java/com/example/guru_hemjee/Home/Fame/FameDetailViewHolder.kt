package com.example.guru_hemjee.Home.Fame

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.databinding.ContainerFameDetailItemRvBinding

class FameDetailViewHolder(val context: Context, val binding: ContainerFameDetailItemRvBinding): RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(fameDetailItem: FameDetailItem) {
        val icon = binding.containerFameDetailIconIv
        val detailGoal = binding.containerFameDetailGoalTv
        val count = binding.containerFameDetailCountTv

        // 뷰에 데이터 적용
        val iconId = DBConvert.iconConvert(fameDetailItem.icon, context)
        icon.setImageResource(iconId)
        DBConvert.colorConvert(icon, fameDetailItem.color, context) // 색상 적용

        detailGoal.text = fameDetailItem.detailGoal

        val int_count = fameDetailItem.count
        if (int_count > 1000)
            count.text = "1000+회"
        else
            count.text = int_count.toString() + "회"
    }
}
