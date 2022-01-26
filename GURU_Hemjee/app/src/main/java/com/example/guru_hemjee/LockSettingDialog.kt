package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat

class LockSettingDialog(context: Context, bigGoalTitle: String?, time: String?) {
    private var dialog = Dialog(context)

    //기본 취소, 확인 버튼
    private lateinit var lockCancelImageButton: ImageButton
    private lateinit var settingOkImageButton: ImageButton

    //대표 목표 수정 버튼
    private lateinit var changeGoalButton: TextView

    //시간 관련
    private lateinit var hourEditText: EditText
    private lateinit var minEditText: EditText
    private lateinit var secEditText: EditText

    //기본 정보 관련
    private var bigGoalTitle = bigGoalTitle
    private var time = time

    fun lockSetting(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting)

        hourEditText = dialog.findViewById(R.id.hourTimeEditText)
        minEditText = dialog.findViewById(R.id.minTimeEditText)
        secEditText = dialog.findViewById(R.id.secTimeEditText)

        lockCancelImageButton = dialog.findViewById(R.id.lockCancelImageButton)
        lockCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null, null)
            dialog.dismiss()
        }

        settingOkImageButton = dialog.findViewById(R.id.settingOkImageButton)
        settingOkImageButton.setOnClickListener {

            //onClickListener.onClicked(true, bigGoalTitle, )
        }
    }

    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, bigGoalTitle: String?, time: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}