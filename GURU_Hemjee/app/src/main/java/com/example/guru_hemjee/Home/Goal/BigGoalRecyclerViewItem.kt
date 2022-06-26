package com.example.guru_hemjee.Home.Goal

// 대표 목록 커스텀 리사이클러뷰에 들어갈 요소들
data class BigGoalRecyclerViewItem(
        val colorImg: Int,                   // 대표목표 색상
        val bigGoalText: String,             // 대표 목표
        val iconArray: ArrayList<Int>        // 세부목표 아이콘 리스트
)