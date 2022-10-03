package com.harahamzzi.android.Home.Goal

import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.DBConvert
import com.harahamzzi.android.MyApplication
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.ContainerGoalItemRecyclerviewBinding

class BigGoalViewHolder(val binding: ContainerGoalItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root) {
    val detailGoalRV: RecyclerView = binding.goalItemDetailGoalRecyclerView
    val context = MyApplication.applicationContext()

    fun bind (bigGoalItem: BigGoalItem) {
        val color = binding.goalItemColorIv
        val bigGoalTitle = binding.goalItemBigGoalTv
        val iconLayout = binding.goalItemIconLLayout

        iconLayout.removeAllViews()

        DBConvert.colorConvert(color, bigGoalItem.color, context)
        try {
            val iconList: ArrayList<String> = bigGoalItem.iconArray

            // 아이콘 레이아웃 만들기 (아이콘 사이즈, 아이콘의 마진값)
            val iconLayoutParams = LinearLayout.LayoutParams(48, 48)
            iconLayoutParams.gravity = Gravity.CENTER
            iconLayoutParams.marginEnd = 32

            // 레이아웃 적용
            for (i in 0 until iconList.size) {
                // 아이콘이 6개 이상일 경우, ... 아이콘 보이기
                if (i == 6) {
                    val moreIcon = ImageView(context)
                    moreIcon.setImageResource(R.drawable.ic_sebumenu)
                    DBConvert.colorConvert(moreIcon, bigGoalItem.color, context)
                    val moreIconLayoutParams = LinearLayout.LayoutParams(48, 48)
                    moreIconLayoutParams.marginStart = -8
                    moreIcon.layoutParams = moreIconLayoutParams
                    iconLayout.addView(moreIcon)
                    break
                }
                val iconId = context.resources.getIdentifier(iconList[i], "drawable", context.packageName)
                val icon = ImageView(context) // 동적 이미지 객체 생성
                icon.setImageResource(iconId)
                icon.layoutParams = iconLayoutParams
                DBConvert.colorConvert(icon, bigGoalItem.color, context)

                iconLayout.addView(icon)
            }
            bigGoalTitle.text = bigGoalItem.title // 대표목표 텍스트 적용
        } catch (e: java.lang.Exception) {
            Log.d("BigGoalItemAdapter", "오류 : " + e.message)
            bigGoalTitle.text = bigGoalItem.title
        }
    }
}