package com.example.guru_hemjee

import android.drm.DrmStore
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar

class LockActivity : AppCompatActivity() {

    //사용가능 한 앱, 시간 조절 버튼
    lateinit var appListButton: ImageButton
    lateinit var timeMinusImageButton: ImageButton
    lateinit var timePlusImageButton: ImageButton

    //나가기 버튼
    lateinit var lockExitImageButton: ImageButton//첫번째
    lateinit var exitImageButton: ImageButton//두번째

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        //위젯들 연결
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
        lockExitImageButton.setOnClickListener {//첫번째 나가기 버튼
            exitImageButton.visibility = View.VISIBLE
            lockExitImageButton.visibility = View.GONE

            Handler().postDelayed({
                exitImageButton.visibility = View.GONE;
                lockExitImageButton.visibility = View.VISIBLE;
            }, 3000L)
        }

        exitImageButton.setOnClickListener {//마지막 나가기 버튼
            showExitPop()
        }

    }

    //사용가능 한 앱 팝업
    private fun showAppListPopup() {
        val dialog = AvailableAppsDialog(this)
        dialog.availableApps()
    }

    //시간 감소 팝업
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

    //시간 추가 팝업
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

    //나가기 팝업
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

    //마지막 확인 팝업 창
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


