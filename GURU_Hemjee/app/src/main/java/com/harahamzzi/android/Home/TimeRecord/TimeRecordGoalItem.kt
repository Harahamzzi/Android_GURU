package com.harahamzzi.android.Home.TimeRecord

// 기록화면의 세부 목표 생성 어댑터 아이템
class TimeRecordGoalItem {

    private var isComplete = false          // 완료여부 판별
    private var iconName: String = ""       // 아이콘 이미지 이름
    private var iconColor: String = ""      // 아이콘 색상 이름
    private var goalName: String = ""       // 세부목표 이름

    // isComplete getter, setter 함수
    fun getIsComplete(): Boolean {
        return isComplete
    }
    fun setIsComplete(isComplete: Boolean) {
        this.isComplete = isComplete
    }

    // iconResID getter, setter 함수
    fun getIconName(): String {
        return iconName
    }
    fun setIconName(id: String) {
        this.iconName = id
    }

    // iconColor getter, setter 함수
    fun getIconColor(): String {
        return iconColor
    }
    fun setIconColor(color: String) {
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