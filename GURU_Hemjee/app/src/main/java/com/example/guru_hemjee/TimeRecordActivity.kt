package com.example.guru_hemjee

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class TimeRecordActivity: AppCompatActivity() {

    // 시간 기록 타이머 관련
    private lateinit var TimeRecord_timeTextView: TextView
    private var timerTask: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_record)

        // 위젯 연결
        TimeRecord_timeTextView = findViewById(R.id.TimeRecord_timeTextView)

        // 타이머 기록 시작
        countTime()
    }

    // 타이머 늘어나게 하고, 변경된 값을 업데이트해서 보여주는 함수
    private fun countTime() {

        // 초기 시간값
        var time: Int = 0

        // 0.01초마다 변수를 증가시킴
        timerTask = timer(period = 1000) {
            val hour = (time/3600) % 24 // 1시간
            val min = (time/60) % 60   // 1분
            val sec = time % 60   // 1초

            // 위젯 값 변경
            runOnUiThread {
                TimeRecord_timeTextView.text = "$hour : $min : $sec"
            }

            time++  // 시간 증가

            // TODO: 타이머 종료 추가
//            // 타이머 종료
//            if (hour <= 0 && min <= 0 && sec <= 0)
//            {
//                timerTask?.cancel()
//            }
        }
    }
}