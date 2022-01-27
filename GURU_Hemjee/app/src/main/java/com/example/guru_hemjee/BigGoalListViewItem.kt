package com.example.guru_hemjee

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView

// 대표 목록 커스텀 리스트뷰에 들어갈 요소들 (색깔, 대표 목표, 화살표 이미지)
data class BigGoalListViewItem(val colorImg : Drawable, val bigGoalText : String, val rightImg : Drawable) {
}

/*class BigGoalListViewItem {
    val color : ImageView? = null
    var bigGoal : TextView? = null
    var rightImage : ImageView? = null
}*/