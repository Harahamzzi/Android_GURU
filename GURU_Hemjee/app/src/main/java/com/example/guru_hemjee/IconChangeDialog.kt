package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat

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
        icon1RadioButton.isChecked = R.drawable.ic_outline_menu_book_24==icon
        icon2RadioButton = dialog.findViewById(R.id.icon2)
        icon2RadioButton.isChecked = R.drawable.ic_outline_play_lesson_24==icon
        icon3RadioButton = dialog.findViewById(R.id.icon3)
        icon3RadioButton.isChecked = R.drawable.ic_outline_school_24==icon
        icon4RadioButton = dialog.findViewById(R.id.icon4)
        icon4RadioButton.isChecked = R.drawable.dumble_icon==icon
        icon5RadioButton = dialog.findViewById(R.id.icon5)
        icon5RadioButton.isChecked = R.drawable.ic_outline_forest_24==icon
        icon6RadioButton = dialog.findViewById(R.id.icon6)
        icon6RadioButton.isChecked = R.drawable.ic_outline_sports_esports_24==icon
        icon7RadioButton = dialog.findViewById(R.id.icon7)
        icon7RadioButton.isChecked = R.drawable.ic_outline_computer_24==icon
        icon8RadioButton = dialog.findViewById(R.id.icon8)
        icon8RadioButton.isChecked = R.drawable.ic_outline_piano_24==icon
        icon9RadioButton = dialog.findViewById(R.id.icon9)
        icon9RadioButton.isChecked = R.drawable.ic_outline_ramen_dining_24==icon
        icon10RadioButton = dialog.findViewById(R.id.icon10)
        icon10RadioButton.isChecked = R.drawable.ic_outline_local_cafe_24==icon
        icon11RadioButton = dialog.findViewById(R.id.icon11)
        icon11RadioButton.isChecked = R.drawable.ic_outline_business_center_24==icon
        icon12RadioButton = dialog.findViewById(R.id.icon12)
        icon12RadioButton.isChecked = R.drawable.ic_outline_storefront_24==icon
        icon13RadioButton = dialog.findViewById(R.id.icon13)
        icon13RadioButton.isChecked = R.drawable.ic_outline_draw_24==icon
        icon14RadioButton = dialog.findViewById(R.id.icon14)
        icon14RadioButton.isChecked = R.drawable.ic_outline_savings_24==icon
        icon15RadioButton = dialog.findViewById(R.id.icon15)
        icon15RadioButton.isChecked = R.drawable.ic_outline_query_stats_24==icon

        iconGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId){
                R.id.icon1 -> {
                    colorGray()
                    icon1RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_menu_book_24
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon2 -> {
                    colorGray()
                    icon2RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_play_lesson_24
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon3 -> {
                    colorGray()
                    icon3RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_school_24
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon4 -> {
                    colorGray()
                    icon4RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.dumble_icon
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon5 -> {
                    colorGray()
                    icon5RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_forest_24
                    iconGroup3.clearCheck()
                    iconGroup2.clearCheck()
                }
            }
            iconGroup2.clearCheck()
            iconGroup3.clearCheck()
        }
        iconGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.icon6 -> {
                    colorGray()
                    icon6RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_sports_esports_24
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                }
                R.id.icon7 -> {
                    colorGray()
                    icon7RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_computer_24
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                }
                R.id.icon8 -> {
                    colorGray()
                    icon8RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_piano_24
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                }
                R.id.icon9 -> {
                    colorGray()
                    icon9RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_ramen_dining_24
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                }
                R.id.icon10 -> {
                    colorGray()
                    icon10RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_local_cafe_24
                    iconGroup1.clearCheck()
                    iconGroup3.clearCheck()
                }
            }
        }
        iconGroup3.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.icon11 -> {
                    colorGray()
                    icon11RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_business_center_24
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon12 -> {
                    colorGray()
                    icon12RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_storefront_24
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon13 -> {
                    colorGray()
                    icon13RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_draw_24
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon14 -> {
                    colorGray()
                    icon14RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_savings_24
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
                }
                R.id.icon15 -> {
                    colorGray()
                    icon15RadioButton.compoundDrawableTintList = ColorStateList.valueOf(goalColor)
                    icon = R.drawable.ic_outline_query_stats_24
                    iconGroup1.clearCheck()
                    iconGroup2.clearCheck()
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

    fun colorGray() {
        icon1RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon2RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon3RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon4RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon5RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon6RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon7RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon8RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon9RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon10RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon11RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon12RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon13RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon14RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
        icon15RadioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
    }

}