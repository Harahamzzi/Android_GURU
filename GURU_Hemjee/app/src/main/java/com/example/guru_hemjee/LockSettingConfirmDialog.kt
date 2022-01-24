package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.ImageButton
import androidx.core.content.ContextCompat.startActivity
import java.util.*

class LockSettingConfirmDialog(context: Context) {
    private val dialog = Dialog(context)

    fun myDig(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting_confirm)

        val lock = dialog.findViewById<ImageButton>(R.id.settingOkImageButton)
        lock.setOnClickListener {
//            var intent = Intent(Intent.,LockActivity::class.java)
//            startActivity(intent)
            dialog.dismiss()
        }
//
//        val cancel = dialog.findViewById<ImageButton>(R.id.lockCancelImageButton)
//        cancel.setOnClickListener {
//            isConfrim = false
//            dialog.dismiss()
//        }
//
//        return isConfrim
    }

}