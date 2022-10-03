package com.harahamzzi.android.Home.Fame

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// 명예의 전당 리스트 데이터
@Parcelize
data class FameItem(
    var color: String, // 색상
    var title: String, // 대표목표
    var time: String,  // 시간
    var totalDate: String, // 날짜
    var totalPeriod: String, // 기간
    var createdDate: String // 생성 날짜
): Parcelable