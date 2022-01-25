package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContentProviderCompat.requireContext

class LockActivity : AppCompatActivity() {

    lateinit var appListButton: ImageButton
    lateinit var timeMinusImageButton: ImageButton
    lateinit var timePlusImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        appListButton = findViewById(R.id.appListButton)
        timeMinusImageButton = findViewById(R.id.timeMinusImageButton)
        timePlusImageButton = findViewById(R.id.timePlusImageButton)

        appListButton.setOnClickListener {
            showAppListPopup()
        }

        timeMinusImageButton.setOnClickListener {
            showTimeMinusPopUp()
        }

        timePlusImageButton.setOnClickListener {
            showTimePlusPopUp()
        }
    }

    private fun showAppListPopup() {
        val dialog = AvailableAppsDialog(this)
        dialog.availableApps()
    }

    private fun showTimeMinusPopUp(){
        val dialog = AlertDialog(this, "10분 줄이기", "-10      ", true)
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isReduced: Boolean) {
                if(isReduced){
                    finalOK("10분 줄이기", "확인", false)
                }
            }
        })
    }

    private fun showTimePlusPopUp(){
        val dialog = AlertDialog(this, "10분 늘리기", "10분 늘리기", false)
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isReduced: Boolean) {
                if(isReduced){
                    finalOK("10분 늘리기", "확인", false)
                }
            }
        })
    }

    private fun finalOK(title: String, okString: String, isNeedDrawable: Boolean) {
        val dialog = FinalOK(this,title, okString, isNeedDrawable)
        dialog.AlertDialog()
    }

}