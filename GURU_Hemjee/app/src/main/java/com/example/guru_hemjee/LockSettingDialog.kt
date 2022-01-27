package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

class LockSettingDialog(context: Context, bigGoalTitle: String?, time: String) {
    private var context = context
    private var dialog = Dialog(context)

    //기본 취소, 확인 버튼
    private lateinit var lockCancelImageButton: ImageButton
    private lateinit var settingOkImageButton: ImageButton

    //대표 목표 수정 버튼
    private lateinit var goalTitleTextView: TextView
    private lateinit var changeGoalButton: TextView

    //시간 관련
    private lateinit var hourEditText: EditText
    private lateinit var minEditText: EditText
    private lateinit var secEditText: EditText

    //기본 정보 관련
    private var bigGoalTitle: String? = bigGoalTitle
    private var time = time
    private var timeArray = time.split(':')

    fun lockSetting(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting)

        //가져온 대표 목표 제목으로 수정
        goalTitleTextView = dialog.findViewById(R.id.goalTitleTextView)
        goalTitleTextView.text = bigGoalTitle

        //기본 시간 설정
        hourEditText = dialog.findViewById<EditText>(R.id.hourTimeEditText)
        hourEditText.hint = timeArray[0]
        minEditText = dialog.findViewById<EditText>(R.id.minTimeEditText)
        minEditText.hint = timeArray[1]
        secEditText = dialog.findViewById<EditText>(R.id.secTimeEditText)
        secEditText.hint =timeArray[2]

        //대표 목표 수정
        changeGoalButton = dialog.findViewById(R.id.changeGoalButton)
        changeGoalButton.setOnClickListener {
            val subDialog = GoalSelectDialog(context, bigGoalTitle)
            subDialog.goalSelectPop()

            subDialog.setOnClickedListener(object : GoalSelectDialog.ButtonClickListener{
                override fun onClicked(changedBigGoalTitle: String?) {
                    bigGoalTitle = changedBigGoalTitle
                    goalTitleTextView.text = changedBigGoalTitle
                }
            })
        }

        lockCancelImageButton = dialog.findViewById(R.id.lockCancelImageButton)
        lockCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null, "")
            dialog.dismiss()
        }

        settingOkImageButton = dialog.findViewById(R.id.settingOkImageButton)
        settingOkImageButton.setOnClickListener {

            if(hourEditText.text.toString().isEmpty()){
                time = timeArray[0]+":"
            } else {
                if(hourEditText.text.toString().toInt()/10 >= 1)
                    time = hourEditText.text.toString()+":"
                else
                    time = "0" + hourEditText.text.toString()+":"
            }

            if(minEditText.text.toString().isEmpty()){
                time += timeArray[1]+":"
            } else {
                if(minEditText.text.toString().toInt()/10 >= 1)
                    time += minEditText.text.toString()+":"
                else
                    time += "0"+minEditText.text.toString()+":"

            }

            if(secEditText.text.toString().isEmpty()){
                time += timeArray[2]
            } else {
                if(secEditText.text.toString().toInt()/10 >= 1)
                    time += secEditText.text.toString()
                else
                    time += "0"+secEditText.text.toString()+":"

            }

            onClickListener.onClicked(true, bigGoalTitle, time)
            dialog.dismiss()
        }
    }

    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, bigGoalTitle: String?, getTime: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}