package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import org.w3c.dom.Text
import java.util.*

class LockSettingConfirmDialog(context: Context, time: String) {
    private val dialog = Dialog(context)

    private lateinit var lock: ImageButton
    private lateinit var cancel: ImageButton

    //시간 관련
    private var timeArray = time.split(':')
    private lateinit var hour: TextView
    private lateinit var min: TextView
    private lateinit var sec: TextView

    fun myDig(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting_confirm)

        //시간 연결
        hour = dialog.findViewById(R.id.hourTimeTextView)
        hour.text = timeArray[0]
        min = dialog.findViewById(R.id.minTmeTextView)
        min.text = timeArray[1]
        sec = dialog.findViewById(R.id.secTimeTextView)
        sec.text = timeArray[2]

        lock = dialog.findViewById<ImageButton>(R.id.settingOkImageButton)
        lock.setOnClickListener {
            onClickListener.onClicked(true)
            dialog.dismiss()
        }

        cancel = dialog.findViewById<ImageButton>(R.id.lockCancelImageButton)
        cancel.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }


    }

    interface ButtonClickListener {
        fun onClicked(isLock: Boolean)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}
