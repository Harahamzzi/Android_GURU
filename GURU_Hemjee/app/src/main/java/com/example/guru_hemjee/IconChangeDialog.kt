package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup

//세부 목표 아이콘 변경 팝업
class IconChangeDialog(val context: Context, val goalColor: Int, val originIcon: Int) {
    private val dialog = Dialog(context)

    //확인 취소 버튼
    private lateinit var cancelImageButton: ImageButton
    private lateinit var okImageButton: ImageButton

    //RadioGroup들
    private lateinit var pop_iconGroup1: RadioGroup
    private lateinit var pop_iconGroup2: RadioGroup
    private lateinit var pop_iconGroup3: RadioGroup

    //RadioButton들
    private lateinit var pop_icon1: RadioButton
    private lateinit var pop_icon2: RadioButton
    private lateinit var pop_icon3: RadioButton
    private lateinit var pop_icon4: RadioButton
    private lateinit var pop_icon5: RadioButton
    private lateinit var pop_icon6: RadioButton
    private lateinit var pop_icon7: RadioButton
    private lateinit var pop_icon8: RadioButton
    private lateinit var pop_icon9: RadioButton
    private lateinit var pop_icon10: RadioButton
    private lateinit var pop_icon11: RadioButton
    private lateinit var pop_icon12: RadioButton
    private lateinit var pop_icon13: RadioButton
    private lateinit var pop_icon14: RadioButton
    private lateinit var pop_icon15: RadioButton

    //icon
    private var icon: Int = originIcon

    //아이콘 변경 팝업
    fun iconPopUp() {
        dialog.show()
        dialog.setContentView(R.layout.popup_icon_list)

        //라디오 버튼 그룹 연결
        pop_iconGroup1 = dialog.findViewById(R.id.pop_iconGroup1)
        pop_iconGroup2 = dialog.findViewById(R.id.pop_iconGroup2)
        pop_iconGroup3 = dialog.findViewById(R.id.pop_iconGroup3)

        //라디오 버튼 연결
        pop_icon1 = dialog.findViewById(R.id.pop_icon1)
        pop_icon2 = dialog.findViewById(R.id.pop_icon2)
        pop_icon3 = dialog.findViewById(R.id.pop_icon3)
        pop_icon4 = dialog.findViewById(R.id.pop_icon4)
        pop_icon5 = dialog.findViewById(R.id.pop_icon5)
        pop_icon6 = dialog.findViewById(R.id.pop_icon6)
        pop_icon7 = dialog.findViewById(R.id.pop_icon7)
        pop_icon8 = dialog.findViewById(R.id.pop_icon8)
        pop_icon9 = dialog.findViewById(R.id.pop_icon9)
        pop_icon10 = dialog.findViewById(R.id.pop_icon10)
        pop_icon11 = dialog.findViewById(R.id.pop_icon11)
        pop_icon12 = dialog.findViewById(R.id.pop_icon12)
        pop_icon13 = dialog.findViewById(R.id.pop_icon13)
        pop_icon14 = dialog.findViewById(R.id.pop_icon14)
        pop_icon15 = dialog.findViewById(R.id.pop_icon15)

        //라디오 버튼이 미리 설정된 버튼과 같으면 강조 표시
        if(R.drawable.ic_outline_menu_book_24==icon){
            pop_icon1.isChecked = true
            pop_icon1.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_play_lesson_24==icon){
            pop_icon2.isChecked = true
            pop_icon2.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_school_24==icon){
            pop_icon3.isChecked = true
            pop_icon3.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.dumble_icon==icon){
            pop_icon4.isChecked = true
            pop_icon4.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_forest_24==icon){
            pop_icon5.isChecked = true
            pop_icon5.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_sports_esports_24==icon){
            pop_icon6.isChecked = true
            pop_icon6.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_computer_24==icon){
            pop_icon7.isChecked = true
            pop_icon7.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_piano_24==icon){
            pop_icon8.isChecked = true
            pop_icon8.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_ramen_dining_24==icon){
            pop_icon9.isChecked = true
            pop_icon9.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_local_cafe_24==icon){
            pop_icon10.isChecked = true
            pop_icon10.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_business_center_24==icon){
            pop_icon11.isChecked = true
            pop_icon11.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_storefront_24==icon){
            pop_icon12.isChecked = true
            pop_icon12.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_draw_24==icon){
            pop_icon13.isChecked = true
            pop_icon13.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_savings_24==icon){
            pop_icon14.isChecked = true
            pop_icon14.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_query_stats_24==icon){
            pop_icon15.isChecked = true
            pop_icon15.backgroundTintList = ColorStateList.valueOf(goalColor)
        }

        //그룹에 버튼 연결, 버튼 연결 시 설정
        pop_iconGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId){
                R.id.pop_icon1 -> {
                    colorGray()
                    pop_iconGroup3.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon1.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_menu_book_24
                }
                R.id.pop_icon2 -> {
                    colorGray()
                    pop_iconGroup3.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon2.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_play_lesson_24
                }
                R.id.pop_icon3 -> {
                    colorGray()
                    pop_iconGroup3.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon3.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_school_24
                }
                R.id.pop_icon4 -> {
                    colorGray()
                    pop_iconGroup3.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon4.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.dumble_icon
                }
                R.id.pop_icon5 -> {
                    colorGray()
                    pop_iconGroup3.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon5.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_forest_24
                }
            }
        }
        pop_iconGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.pop_icon6 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup3.clearCheck()
                    pop_icon6.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_sports_esports_24
                }
                R.id.pop_icon7 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup3.clearCheck()
                    pop_icon7.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_computer_24
                }
                R.id.pop_icon8 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup3.clearCheck()
                    pop_icon8.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_piano_24
                }
                R.id.pop_icon9 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup3.clearCheck()
                    pop_icon9.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_ramen_dining_24
                }
                R.id.pop_icon10 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup3.clearCheck()
                    pop_icon10.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_local_cafe_24
                }
            }
        }
        pop_iconGroup3.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.pop_icon11 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon11.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_business_center_24
                }
                R.id.pop_icon12 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon12.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_storefront_24
                }
                R.id.pop_icon13 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon13.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_draw_24
                }
                R.id.pop_icon14 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon14.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_savings_24
                }
                R.id.pop_icon15 -> {
                    colorGray()
                    pop_iconGroup1.clearCheck()
                    pop_iconGroup2.clearCheck()
                    pop_icon15.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_query_stats_24
                }
            }
        }

        //취소 버튼
        cancelImageButton = dialog.findViewById(R.id.pop_iconCancelImageButton)
        cancelImageButton.setOnClickListener {
            onClickListener.onClick(false, originIcon)
            dialog.dismiss()
        }

        //확인 버튼
        okImageButton = dialog.findViewById(R.id.pop_iconOkImageButton)
        okImageButton.setOnClickListener {
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
        pop_icon1.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon2.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon3.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon4.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon5.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon6.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon7.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon8.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon9.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon10.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon11.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon12.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon13.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon14.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        pop_icon15.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
    }

}