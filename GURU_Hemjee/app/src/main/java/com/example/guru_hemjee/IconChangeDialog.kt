package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup

class IconChangeDialog(val context: Context, val goalColor: Int, val originIcon: Int) {
    private val dialog = Dialog(context)

    //확인 취소 버튼
    private lateinit var cancelImageButton: ImageButton
    private lateinit var okImageButton: ImageButton

    //RadioGroup들
    private lateinit var iconGroup1: RadioGroup
    private lateinit var iconGroup2: RadioGroup
    private lateinit var iconGroup3: RadioGroup

    //RadioButton들
    private lateinit var icon1RadioButton: RadioButton
    private lateinit var icon2RadioButton: RadioButton
    private lateinit var icon3RadioButton: RadioButton
    private lateinit var icon4RadioButton: RadioButton
    private lateinit var icon5RadioButton: RadioButton
    private lateinit var icon6RadioButton: RadioButton
    private lateinit var icon7RadioButton: RadioButton
    private lateinit var icon8RadioButton: RadioButton
    private lateinit var icon9RadioButton: RadioButton
    private lateinit var icon10RadioButton: RadioButton
    private lateinit var icon11RadioButton: RadioButton
    private lateinit var icon12RadioButton: RadioButton
    private lateinit var icon13RadioButton: RadioButton
    private lateinit var icon14RadioButton: RadioButton
    private lateinit var icon15RadioButton: RadioButton

    //icon
    private var icon: Int = originIcon

    fun iconPopUp() {
        dialog.show()
        dialog.setContentView(R.layout.popup_icon_list)

        iconGroup1 = dialog.findViewById(R.id.iconGroup1)
        iconGroup2 = dialog.findViewById(R.id.iconGroup2)
        iconGroup3 = dialog.findViewById(R.id.iconGroup3)

        icon1RadioButton = dialog.findViewById(R.id.icon1)
        icon2RadioButton = dialog.findViewById(R.id.icon2)
        icon3RadioButton = dialog.findViewById(R.id.icon3)
        icon4RadioButton = dialog.findViewById(R.id.icon4)
        icon5RadioButton = dialog.findViewById(R.id.icon5)
        icon6RadioButton = dialog.findViewById(R.id.icon6)
        icon7RadioButton = dialog.findViewById(R.id.icon7)
        icon8RadioButton = dialog.findViewById(R.id.icon8)
        icon9RadioButton = dialog.findViewById(R.id.icon9)
        icon10RadioButton = dialog.findViewById(R.id.icon10)
        icon11RadioButton = dialog.findViewById(R.id.icon11)
        icon12RadioButton = dialog.findViewById(R.id.icon12)
        icon13RadioButton = dialog.findViewById(R.id.icon13)
        icon14RadioButton = dialog.findViewById(R.id.icon14)
        icon15RadioButton = dialog.findViewById(R.id.icon15)

        if(R.drawable.ic_outline_menu_book_24==icon){
            icon1RadioButton.isChecked = true
            icon1RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_play_lesson_24==icon){
            icon2RadioButton.isChecked = true
            icon2RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_school_24==icon){
            icon3RadioButton.isChecked = true
            icon3RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.dumble_icon==icon){
            icon4RadioButton.isChecked = true
            icon4RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_forest_24==icon){
            icon5RadioButton.isChecked = true
            icon5RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_sports_esports_24==icon){
            icon6RadioButton.isChecked = true
            icon6RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_computer_24==icon){
            icon7RadioButton.isChecked = true
            icon7RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_piano_24==icon){
            icon8RadioButton.isChecked = true
            icon8RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_ramen_dining_24==icon){
            icon9RadioButton.isChecked = true
            icon9RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_local_cafe_24==icon){
            icon10RadioButton.isChecked = true
            icon10RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_business_center_24==icon){
            icon11RadioButton.isChecked = true
            icon11RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_storefront_24==icon){
            icon12RadioButton.isChecked = true
            icon12RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_draw_24==icon){
            icon13RadioButton.isChecked = true
            icon13RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_savings_24==icon){
            icon14RadioButton.isChecked = true
            icon14RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }
        else if(R.drawable.ic_outline_query_stats_24==icon){
            icon15RadioButton.isChecked = true
            icon15RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
        }

        iconGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId){
                R.id.icon1 -> {
                    colorGray()
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                    icon1RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_menu_book_24
                }
                R.id.icon2 -> {
                    colorGray()
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                    icon2RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_play_lesson_24
                }
                R.id.icon3 -> {
                    colorGray()
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                    icon3RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_school_24
                }
                R.id.icon4 -> {
                    colorGray()
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                    icon4RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.dumble_icon
                }
                R.id.icon5 -> {
                    colorGray()
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                    icon5RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_forest_24
                }
            }
        }
        iconGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.icon6 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                    icon6RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_sports_esports_24
                }
                R.id.icon7 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                    icon7RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_computer_24
                }
                R.id.icon8 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                    icon8RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_piano_24
                }
                R.id.icon9 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                    icon9RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_ramen_dining_24
                }
                R.id.icon10 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                    icon10RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_local_cafe_24
                }
            }
        }
        iconGroup3.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.icon11 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                    icon11RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_business_center_24
                }
                R.id.icon12 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                    icon12RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_storefront_24
                }
                R.id.icon13 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                    icon13RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_draw_24
                }
                R.id.icon14 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                    icon14RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_savings_24
                }
                R.id.icon15 -> {
                    colorGray()
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                    icon15RadioButton.backgroundTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_query_stats_24
                }
            }
        }

        cancelImageButton = dialog.findViewById(R.id.iconCancelImageButton)
        cancelImageButton.setOnClickListener {
            onClickListener.onClick(false, originIcon)
            dialog.dismiss()
        }

        okImageButton = dialog.findViewById(R.id.iconOkImageButton)
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

    private fun colorGray() {
        icon1RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon2RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon3RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon4RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon5RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon6RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon7RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon8RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon9RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon10RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon11RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon12RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon13RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon14RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon15RadioButton.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
    }

}