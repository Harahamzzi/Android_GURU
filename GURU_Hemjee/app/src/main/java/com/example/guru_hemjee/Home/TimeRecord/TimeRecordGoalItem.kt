package com.example.guru_hemjee.Home.TimeRecord

// 기록화면의 세부 목표 생성 어댑터 아이템
class TimeRecordGoalItem {

    private var iconResID: Int = 0      // 아이콘 이미지 ID
    private var iconColor: Int = 0      // 아이콘 색상
    private var goalName: String = ""   // 세부목표 이름

    // iconResID getter, setter 함수
    fun getIconResID(): Int {
        return iconResID
    }
    fun setIconResID(id: Int) {
        this.iconResID = id
    }

    // iconColor getter, setter 함수
    fun getIconColor(): Int {
        return iconColor
    }
    fun setIconColor(color: Int) {
        this.iconColor = color
    }

    // goalName getter, setter 함수
    fun getGoalName(): String {
        return goalName
    }
    fun setGoalName(name: String) {
        this.goalName = name
    }
}