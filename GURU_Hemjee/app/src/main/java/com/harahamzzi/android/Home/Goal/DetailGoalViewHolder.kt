package com.harahamzzi.android.Home.Goal

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.DBConvert
import com.harahamzzi.android.MyApplication
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.ContainerDetailGoalItemRecyclerviewBinding

class DetailGoalViewHolder(val binding: ContainerDetailGoalItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind (detailGoalItem: DetailGoalItem?) {
        val detailIcon = binding.detailGoalItemIconIv
        val detailTitle = binding.detailGoalItemDetailGoalTv

        try {
            // 세부목표 icon 색상 저장
            var iconColor = 0
            when (detailGoalItem?.color) {
                "Scarlet" -> iconColor = R.color.Scarlet
                "Orange" -> iconColor = R.color.Orange
                "NoteYellow" -> iconColor = R.color.NoteYellow
                "Apricot" -> iconColor = R.color.Apricot
                "DarkBrown" -> iconColor = R.color.DarkBrown
                "SeedBrown" -> iconColor = R.color.SeedBrown
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