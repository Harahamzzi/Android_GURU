package com.example.guru_hemjee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar

class LockActivity : AppCompatActivity() {

    lateinit var appListButton: ImageButton
    lateinit var timeMinusImageButton: ImageButton
    lateinit var timePlusImageButton: ImageButton

    lateinit var lockExitImageButton: ImageButton
    lateinit var exitImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 잠금화면으로 쓰이기 위한 플래그 지정
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        setContentView(R.layout.activity_lock)

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        appListButton = findViewById(R.id.appListButton)
        timeMinusImageButton = findViewById(R.id.timeMinusImageButton)
        timePlusImageButton = findViewById(R.id.timePlusImageButton)

        lockExitImageButton = findViewById(R.id.lockExitImageButton)
        exitImageButton = findViewById(R.id.exitImageButton)
        exitImageButton.visibility = View.GONE

        //사용 가능 한 앱
        appListButton.setOnClickListener {
            showAppListPopup()
        }

        //시간 감소 버튼
        timeMinusImageButton.setOnClickListener {
            showTimeMinusPopUp()
        }

        //시간 추가 버튼
        timePlusImageButton.setOnClickListener {
            showTimePlusPopUp()
        }

        //나가기 버튼들
        lockExitImageButton.setOnClickListener {
            exitImageButton.visibility = View.VISIBLE
            lockExitImageButton.visibility = View.GONE

            Handler().postDelayed({
                exitImageButton.visibility = View.GONE;
                lockExitImageButton.visibility = View.VISIBLE;
            }, 3000L)
        }

        exitImageButton.setOnClickListener {
            showExitPop()
        }

    }

    override fun onBackPressed() {
        // (폰) 뒤로가기 버튼이 아무런 동작도 하지 않도록 함
    }

    private fun showAppListPopup() {
        val dialog = AvailableAppsDialog(this)
        dialog.availableApps()
    }

    private fun showTimeMinusPopUp(){
        val dialog = AlertDialog(this, "10분 줄이기", "-10      ", true)
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    finalOK("10분 줄이기", "확인", false,false)
                }
            }
        })
    }

    private fun showTimePlusPopUp(){
        val dialog = AlertDialog(this, "10분 늘리기", "10분 늘리기", false)
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    finalOK("10분 늘리기", "확인", false, false)
                }
            }
        })
    }

    private fun showExitPop(){
        val dialog = AlertDialog(this,"잠금 종료하기", "나가기", false)
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    finalOK("잠금 종료하기", "확인", false, true)
                }
            }
        })
    }

    private fun finalOK(title: String, okString: String, isNeedDrawable: Boolean, isLockFinished: Boolean) {
        val dialog = FinalOK(this,title, okString, isNeedDrawable)
        dialog.alertDialog()

        dialog.setOnClickedListener(object : FinalOK.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm && isLockFinished){
                    //잠금 종료시 필요한 연산

                    //잠금 종료
                    finish()
                }
            }
        })
    }
}


