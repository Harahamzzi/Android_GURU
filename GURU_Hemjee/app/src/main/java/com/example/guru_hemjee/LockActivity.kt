package com.example.guru_hemjee
import android.util.Log

import android.app.NotificationManager
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import com.dinuscxj.progressbar.CircleProgressBar
import java.util.*
import kotlin.concurrent.timer

class LockActivity : AppCompatActivity() {

    //씨앗 관련
    private lateinit var seedPointView: TextView

    //시간 조절 버튼
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

    // progress bar
    lateinit var progressBar: CircleProgressBar

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb:SQLiteDatabase
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FIXME: 알림 여부 확인 코드 수정 필요
//        if(해당 알림 껐는지 체크..)
//        {
//            // 알림 설정 창으로 사용자 보내기
//            var tempIntent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
//            tempIntent.putExtra(Settings.EXTRA_CHANNEL_ID, "channel_1")
//            tempIntent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//            startActivity(tempIntent)
//        }


        // 잠금화면으로 쓰이기 위한 플래그 지정
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)   // 기본 잠금화면보다 우선 출력
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)   // 기본 잠금화면 해제시키기
//        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)     // 화면 켜기..?

        setContentView(R.layout.activity_lock)

        //위젯들 연결
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

        seedPointView = findViewById(R.id.Lock_seedPointView)

        progressBar = findViewById(R.id.timeLeftCircleProgressBar)

        //씨앗 세팅
        seedPointView.text = intent.getStringExtra("seed")
        userName = intent.getStringExtra("userName")

        // 타이머 세팅
        var intent = intent
        lockHourTextView.setText(intent.getStringExtra("hour"))
        lockMinTextView.setText(intent.getStringExtra("min"))
        lockSecTextView.setText(intent.getStringExtra("sec"))

        time = (lockHourTextView.text.toString().toInt() * 3600) + (lockMinTextView.text.toString().toInt() * 60) + lockSecTextView.text.toString().toInt()

//        var timeTemp = intent.getStringExtra("time")
//        var sf = SimpleDateFormat("hh:mm:ss")
//        time = sf.parse(timeTemp).getTime().toInt()

        // progressBar 세팅
        progressBar.progress = 0
        progressBar.max = time

        // 타이머 시작

        countTime()

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
        //var tempTime = time * 100

        // 0.01초마다 변수를 감소시킴
        timerTask = timer(period = 1000) {
            val hour = (time/3600) % 24 // 1시간
            val min = (time/60) % 60   // 1분
            val sec = time % 60   // 1초

            // 위젯 값 변경
            runOnUiThread {
                lockHourTextView.text = "$hour"
                lockMinTextView.text = "$min"
                lockSecTextView.text = "$sec"
            }

            time--  // 시간 감소
            progressBar.progress++  // progress 수치 증가

            // 타이머 종료
            if (hour == 0 && min == 0 && sec == 0)
            {
                runOnUiThread {
                    try {
                        // 나갈 수 있는 팝업창 띄우기
                        finalOK("잠금 종료!", "확인", false, false, true)
                    }
                    catch (e: WindowManager.BadTokenException) {
                        Log.e("오류태그", "잠금 종료 팝업창 오류..")
                    }

                }

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

    //시간 감소 팝업
    private fun showTimeMinusPopUp(){
        val dialog = AlertDialog(this, "10분 줄이기", "-40      ", true)
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    finalOK("10분 줄이기", "확인", false, false,false)

                    time -= 600
                    seedChange(-40)
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
                    finalOK("10분 늘리기", "확인", false, false,false)

                    time += 600
                }
            }
        })
    }

    //나가기 팝업
    private fun showExitPop(){
        val dialog = AlertDialog(this,"잠금 종료하기", "-180      ", true)
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    finalOK("잠금 종료하기", "확인", false, true, true)
                }
            }
        })
    }

    //마지막 확인 팝업 창
    private fun finalOK(title: String, okString: String, isNeedDrawable: Boolean, isExitBuy: Boolean, isLockFinished: Boolean) {
        val dialog = FinalOK(this,title, okString, isNeedDrawable)
        dialog.alertDialog()

        dialog.setOnClickedListener(object : FinalOK.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {

                // 나갈려고 하는 상황
                if(isConfirm && isLockFinished){
                    // -- 잠금 종료시 필요한 연산 --

                    // 나가기를 구매해서 나가는 경우
                    if(isExitBuy)
                    {
                        seedChange(-180)    // 나가기 사용으로 인한 씨앗 소모
                    }

                    LockScreenUtil.deActive()   // 잠금 서비스 종료

                    //잠금 종료
                    finish()
                }
            }
        })
    }

    //씨앗 변화
    private fun seedChange(change: Int) {
        var changedSeed = seedPointView.text.toString().toInt() + change

        dbManager = DBManager(this, "basic_info_db", null, 1)
        sqlitedb = dbManager.writableDatabase
        sqlitedb.execSQL("UPDATE basic_info_db SET seed = '"+changedSeed.toString()+
                "' WHERE user_name = '"+userName+"'")
        sqlitedb.close()
        dbManager.close()

        seedPointView.text = changedSeed.toString()
    }

}


