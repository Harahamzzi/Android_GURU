package com.example.guru_hemjee.Home.Store

data class StoreItem(
    val itemName: String, // 아이템 이름
    val category: String, // 카테고리
    val price: Int, // 가격
    val imgId: Int, // drawable에 저장되어 있는 아이템의 int값
    var isSelected: Boolean, // 0 : 착용X, 1 : 착용O
    var isStore: Boolean // true : 상점 아이템, false : 나의 햄찌 관리 화면 아이템
)
