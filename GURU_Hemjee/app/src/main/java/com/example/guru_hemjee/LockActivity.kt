package com.example.guru_hemjee

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class LockActivity : AppCompatActivity() {

    //사용가능 한 앱, 시간 조절 버튼
    lateinit var appListButton: ImageButton
    lateinit var timeMinusImageButton: ImageButton
    lateinit var timePlusImageButton: ImageButton

    // 전화 걸기, 메시지 보내기 버튼
    lateinit var phoneButton: ImageButton
    lateinit var messageButton: ImageButton

    //나가기 버튼
    lateinit var lockExitImageButton: ImageButton//첫번째
    lateinit var exitImageButton: ImageButton//두번째

    // 타이머 시간 관련
    lateinit var lockHourTextView: TextView
    lateinit var lockMinTextView: TextView
    lateinit var lockSecTextView: TextView

    private var time = 0
    private var timerTask: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 잠금화면으로 쓰이기 위한 플래그 지정
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)   // 기본 잠금화면보다 우선 출력
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)   // 기본 잠금화면 해제시키기
//        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)     // 화면 켜기..?

        setContentView(R.layout.activity_lock)

        //위젯들 연결
        appListButton = findViewById(R.id.appListButton)
        timeMinusImageButton = findViewById(R.id.timeMinusImageButton)
        timePlusImageButton = findViewById(R.id.timePlusImageButton)

        lockExitImageButton = findViewById(R.id.lockExitImageButton)
        exitImageButton = findViewById(R.id.exitImageButton)
        exitImageButton.visibility = View.GONE

        phoneButton = findViewById(R.id.phoneButton)
        messageButton = findViewById(R.id.messageButton)

        lockHourTextView = findViewById(R.id.lockHourTextView)
        lockMinTextView = findViewById(R.id.lockMinTextView)
        lockSecTextView = findViewById(R.id.lockSecTextView)

        // 타이머 세팅
        var intent = intent
        lockHourTextView.setText(intent.getStringExtra("hour"))
        lockMinTextView.setText(intent.getStringExtra("min"))
        lockSecTextView.setText(intent.getStringExtra("sec"))

//        var timeTemp = intent.getStringExtra("time")
//        var sf = SimpleDateFormat("hh:mm:ss")
//        time = sf.parse(timeTemp).getTime().toInt()

        // 타이머 시작
        time = (lockHourTextView.text.toString().toInt() * 3600) + (lockMinTextView.text.toString().toInt() * 60) + lockSecTextView.text.toString().toInt()
        countTime()

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

        // 전화 걸기 버튼 리스너
        phoneButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            if(intent.resolveActivity(packageManager) != null)
            {
                startActivity(intent)
            }
        }

        // 메시지 보내기 버튼 리스너
        messageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:")
            if(intent.resolveActivity(packageManager) != null)
            {
                startActivity(intent)
            }
        }
    }

    // 타이머 줄어들게 하고, 변경된 값을 업데이트해서 보여주는 함수
    private fun countTime() {
        var tempTime = time * 100

        // 0.01초마다 변수를 감소시킴
        timerTask = timer(period = 10) {
            val hour = (tempTime / 144000) % 24 // 1시간
            val min = (tempTime / 6000) % 60   // 1분
            val sec = (tempTime / 100) % 60   // 1초

            runOnUiThread {
                lockHourTextView.text = "$hour"
                lockMinTextView.text = "$min"
                lockSecTextView.text = "$sec"
            }

            tempTime--

            // 타이머 종료
            if (hour == 0 && min == 0 && sec == 0)
            {
                timerTask?.cancel()
            }
        }
    }

    // 하단 소프트키를 숨겨 잠금 화면을 풀스크린으로 뿌리도록 함
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasFocus) window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
        }
    }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        super.onAttachedToWindow()
    }

    override fun onBackPressed() {
        // (폰) 뒤로가기 버튼이 아무런 동작도 하지 않도록 함
    }

    //사용 가능한 앱
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
                    LockScreenUtil.deActive()   // 잠금 서비스 종료

                    //잠금 종료
                    finish()
                }
            }
        })
    }

}


