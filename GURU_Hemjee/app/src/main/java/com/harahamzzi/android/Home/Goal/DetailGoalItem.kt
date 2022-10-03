package com.harahamzzi.android.Home.Goal

// 목표 설정화면에서 사용하는 세부목표 데이터 클래스
data class DetailGoalItem(
    var detailTitle: String,    // 세부목표
    var detailIcon: String,     // 아이콘
    var color: String           // 색상
)