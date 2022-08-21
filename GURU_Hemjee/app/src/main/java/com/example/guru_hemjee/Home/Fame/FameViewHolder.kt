package com.example.guru_hemjee.Home.Fame

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.Home.MainActivity
import com.example.guru_hemjee.databinding.ContainerFameItemRvBinding

// 명예의 전당
class FameViewHolder(val context: Context, val binding: ContainerFameItemRvBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(fameItem: FameItem) {
        var color = binding.containerFameGoalColorIv
        var title = binding.containerFameGoalTitleTv
        var time = binding.containerFameGoalTimeTv
        var date = binding.containerFameGoalDateTv
        var view = binding.containerFameSpaceView

        // 뷰에 데이터 적용
        DBConvert.colorConvert(color, fameItem.color, context) // 색상 적용
        title.text = fameItem.title
        time.text = fameItem.time
        date.text = fameItem.totalDate

        // 해상도에 맞게 아이템 크기 조절
        val displayMetrics = DisplayMetrics()
        (context as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)

        var deviceWidth = displayMetrics.widthPixels // 가로
        deviceWidth = (deviceWidth - 48) / 2

        view.layoutParams.width = deviceWidth
        view.requestLayout() // 변경사항 적용
    }
}
