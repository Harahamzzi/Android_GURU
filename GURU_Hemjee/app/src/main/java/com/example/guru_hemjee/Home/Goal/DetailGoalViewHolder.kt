package com.example.guru_hemjee.Home.Goal

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ContainerDetailGoalItemRecyclerviewBinding

class DetailGoalViewHolder(val binding: ContainerDetailGoalItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind (detailGoalItem: DetailGoalItem?) {
        val detailIcon = binding.detailGoalItemIconIv
        val detailTitle = binding.detailGoalItemDetailGoalTv

        try {
            // 세부목표 icon 색상 저장
            var iconColor = 0
            when (detailGoalItem?.color) {
                "Orange" -> R.color.Orange
                "BrightYellow" -> R.color.BrightYellow
                "Yellow" -> iconColor = R.color.Yellow
                "Apricot" -> iconColor = R.color.Apricot
                "DarkBrown" -> iconColor = R.color.DarkBrown
                "SeedBrown" -> iconColor = R.color.SeedBrown
                "NoteYellow" -> iconColor = R.color.NoteYellow
                "LightGreen" -> iconColor = R.color.LightGreen
                "Green" -> iconColor = R.color.Green
                "LightBlue" -> iconColor = R.color.LightBlue
                "Blue" -> iconColor = R.color.Blue
                "Purple" -> iconColor = R.color.Purple
                "Pink" -> iconColor = R.color.Pink
            }

            val iconId = DBConvert.iconConvert(
                detailGoalItem?.detailIcon,
                MyApplication.applicationContext()
            )
            detailIcon.setImageResource(iconId)
            detailIcon.setColorFilter(ContextCompat.getColor(MyApplication.applicationContext(), iconColor))

            detailTitle.text = detailGoalItem?.detailTitle
        } catch (e: Exception) {
            Log.e("DetailGoalViewHolder", "오류 : " + e.message)
            detailTitle.text = detailGoalItem?.detailTitle
        }
    }
}