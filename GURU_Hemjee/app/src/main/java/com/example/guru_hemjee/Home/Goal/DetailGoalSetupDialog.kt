package com.example.guru_hemjee.Home.Goal

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.guru_hemjee.R
import com.google.android.material.button.MaterialButton

//세부 목표 팝업
class DetailGoalSetupDialog(val context: Context, val goalColor: Int, val originIcon: Int) {
    private val dialog = Dialog(context)

    //확인 취소 버튼
    private lateinit var cancelBtn: MaterialButton
    private lateinit var confirmBtn: MaterialButton

    // 라디오 그룹
    private lateinit var rGroup1: RadioGroup
    private lateinit var rGroup2: RadioGroup
    private lateinit var rGroup3: RadioGroup

    // 라디오 버튼
    private lateinit var bookRBtn: RadioButton
    private lateinit var lessonRBtn: RadioButton
    private lateinit var schoolRBtn: RadioButton
    private lateinit var dumbleRBtn: RadioButton
    private lateinit var forestRBtn: RadioButton
    private lateinit var sportsRBtn: RadioButton
    private lateinit var computerRBtn: RadioButton
    private lateinit var pianoRBtn: RadioButton
    private lateinit var foodRBtn: RadioButton
    private lateinit var cafeRBtn: RadioButton
    private lateinit var businessRBtn: RadioButton
    private lateinit var storeRBtn: RadioButton
    private lateinit var drawingRBtn: RadioButton
    private lateinit var savingsRBtn: RadioButton
    private lateinit var stockRBtn: RadioButton

    //icon
    private var icon: Int = originIcon

    //아이콘 변경 팝업
    fun iconPopUp() {
        dialog.show()
        dialog.setContentView(R.layout.popup_add_detail_goal)

        // 위젯 연결
        rGroup1 = dialog.findViewById(R.id.pop_detailgoal_icon_rgroup1)
        rGroup2 = dialog.findViewById(R.id.pop_detailgoal_icon_rgroup2)
        rGroup3 = dialog.findViewById(R.id.pop_detailgoal_icon_rgroup3)

        bookRBtn = dialog.findViewById(R.id.pop_detailgoal_book_rbtn)
        lessonRBtn = dialog.findViewById(R.id.pop_detailgoal_lesson_rbtn)
        schoolRBtn = dialog.findViewById(R.id.pop_detailgoal_school_rbtn)
        dumbleRBtn = dialog.findViewById(R.id.pop_detailgoal_dumble_rbtn)
        forestRBtn = dialog.findViewById(R.id.pop_detailgoal_forest_rbtn)
        sportsRBtn = dialog.findViewById(R.id.pop_detailgoal_sports_rbtn)
        computerRBtn = dialog.findViewById(R.id.pop_detailgoal_computer_rbtn)
        pianoRBtn = dialog.findViewById(R.id.pop_detailgoal_piano_rbtn)
        foodRBtn = dialog.findViewById(R.id.pop_detailgoal_food_rbtn)
        cafeRBtn = dialog.findViewById(R.id.pop_detailgoal_cafe_rbtn)
        businessRBtn = dialog.findViewById(R.id.pop_detailgoal_business_rbtn)
        storeRBtn = dialog.findViewById(R.id.pop_detailgoal_store_rbtn)
        drawingRBtn = dialog.findViewById(R.id.pop_detailgoal_drawing_rbtn)
        savingsRBtn = dialog.findViewById(R.id.pop_detailgoal_savings_rbtn)
        stockRBtn = dialog.findViewById(R.id.pop_detailgoal_stock_rbtn)

        //라디오 버튼이 미리 설정된 버튼과 같으면 강조 표시
        if(R.drawable.ic_book_24 ==icon){
            bookRBtn.isChecked = true
            bookRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_lesson_24 ==icon){
            lessonRBtn.isChecked = true
            lessonRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_school_24 ==icon){
            bookRBtn.isChecked = true
            bookRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.dumble_icon ==icon){
            dumbleRBtn.isChecked = true
            dumbleRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_forest_24 ==icon){
            forestRBtn.isChecked = true
            forestRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_sports_24 ==icon){
            sportsRBtn.isChecked = true
            sportsRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_computer_24 ==icon){
            computerRBtn.isChecked = true
            computerRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_piano_24 ==icon){
            pianoRBtn.isChecked = true
            pianoRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_food_24 ==icon){
            foodRBtn.isChecked = true
            foodRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_cafe_24 ==icon){
            cafeRBtn.isChecked = true
            cafeRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_business_24 ==icon){
            businessRBtn.isChecked = true
            businessRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_store_24 ==icon){
            storeRBtn.isChecked = true
            storeRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_drawing_24 ==icon){
            drawingRBtn.isChecked = true
            drawingRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_savings_24 ==icon){
            savingsRBtn.isChecked = true
            savingsRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_stock_24 ==icon){
            forestRBtn.isChecked = true
            forestRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
        }

        //그룹에 버튼 연결, 버튼 연결 시 설정
        rGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId){
                R.id.pop_detailgoal_book_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    bookRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_book_24
                }
                R.id.pop_detailgoal_lesson_rbtn -> {
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    lessonRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_lesson_24
                }
                R.id.pop_detailgoal_school_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    schoolRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_school_24
                }
                R.id.pop_detailgoal_dumble_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    dumbleRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.dumble_icon
                }
                R.id.pop_detailgoal_forest_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    forestRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_forest_24
                }
            }
        }
        rGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.pop_detailgoal_sports_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    sportsRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_sports_24
                }
                R.id.pop_detailgoal_computer_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    computerRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_computer_24
                }
                R.id.pop_detailgoal_piano_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    pianoRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_piano_24
                }
                R.id.pop_detailgoal_food_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    foodRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_food_24
                }
                R.id.pop_detailgoal_cafe_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    cafeRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_cafe_24
                }
            }
        }
        rGroup3.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.pop_detailgoal_business_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    businessRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_business_24
                }
                R.id.pop_detailgoal_store_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    storeRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_store_24
                }
                R.id.pop_detailgoal_drawing_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    drawingRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_drawing_24
                }
                R.id.pop_detailgoal_savings_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    savingsRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_savings_24
                }
                R.id.pop_detailgoal_stock_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    stockRBtn.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_stock_24
                }
            }
        }

        //취소 버튼
        cancelBtn.setOnClickListener {
            onClickListener.onClick(false, originIcon)
            dialog.dismiss()
        }

        //확인 버튼
        confirmBtn.setOnClickListener {
            onClickListener.onClick(true, icon)
            dialog.dismiss()
        }

    }

    interface ButtonClickListener {
        fun onClick(isChanged: Boolean, changedIcon: Int)
    }

    private  lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickListener = listener
    }

    //버튼 초기화(회색 처리)
    private fun colorGray() {
        bookRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        lessonRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        schoolRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        dumbleRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        forestRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        sportsRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        computerRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pianoRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        foodRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        cafeRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        businessRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        storeRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        drawingRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        savingsRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        stockRBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
    }

}