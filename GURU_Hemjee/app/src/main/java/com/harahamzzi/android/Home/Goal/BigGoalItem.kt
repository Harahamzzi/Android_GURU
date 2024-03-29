package com.harahamzzi.android.Home.Goal

// 목표 설정화면에서 사용하는 대표목표 데이터 클래스
data class BigGoalItem(
        var title: String,                                              // 대표 목표
        var color: String,                                              // 대표목표 색상
        var isExpanded: Boolean = false,                                // 확장상태 여부
        var iconArray: ArrayList<String> = ArrayList(),                 // 세부목표 아이콘 리스트
        var detailGoalList: ArrayList<DetailGoalItem> = ArrayList()     // 세부목표 리스트
)