package com.example.guru_hemjee

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_hemjee.databinding.ActivityTimeRecordBinding
import java.lang.Exception
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class TimeRecordActivity: AppCompatActivity() {

    // 뷰바인딩
    private var binding: ActivityTimeRecordBinding? = null

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var dbManager2: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var sqlitedb2: SQLiteDatabase

    // 시간 기록 타이머 관련
    private var timerTask: Timer? = null
    private var time: Int = 0
    private var recordDate: Long = 0L

    // 일시정지 상태 플래그
    private var isPause: Boolean = false

    // 목표 관련
    private lateinit var bigGoalName: String    // 대표 목표 이름
    private var bigGoalColor: Int = 0           // 대표 목표 색상

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeRecordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /** 대표 목표 관련 정보 가져오기 **/

        // 대표 목표 이름 가져오기
        bigGoalName = intent.getStringExtra("bigGoalName")!!

        // 대표 목표 색상 가져오기
        var cursor: Cursor

        try {
            // 대표 목표의 색상 뽑아오기
            dbManager = DBManager(this, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '${bigGoalName}'", null)


            if(cursor.moveToNext())
                bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))

            cursor.close()
            sqlitedb.close()
            dbManager.close()
        }
        catch(e: Exception) {
            Log.e("DBException", "대표 목표 색상 뽑아오기 실패")
        }

        // 세부 목표 동적 생성
        addDetailGoal()

        // 타이머 초기화
        time = 0

        // 타이머 기록 시작
        countTime()

        // 기록 시작 날짜 저장
        recordDate = System.currentTimeMillis()

        // 클릭 리스너
        // 일시정지 버튼
        binding?.TimeRecordPauseButton?.setOnClickListener {

            // 만일 현재 타이머가 일시정지된 상태였다면
            if (isPause)
            {
                // 1. 버튼 이미지 변경
                binding?.TimeRecordPauseButton?.setImageResource(R.drawable.ic_play)

                // 2. 타이머 재작동
                countTime()
            }
            else
            {
                // 1. 버튼 이미지 변경
                binding?.TimeRecordPauseButton?.setImageResource(R.drawable.ic_pause_black_48dp)

                // 2. 타이머 멈춤
                timerTask?.cancel()
            }

            // 플래그 값 변경
            isPause = !isPause
        }

        // 정지(끝내기 버튼)
        binding?.TimeRecordStopButton?.setOnClickListener {

            // 1. 타이머 종료
            timerTask?.cancel()

            // 2. 총 기록시간 DB에 저장
            try {
                /** 대표 목표 리포트 DB 기록 (생성) **/
                dbManager = DBManager(this@TimeRecordActivity, "hamster_db", null, 1)
                sqlitedb = dbManager.writableDatabase

                // SimpleDateFormat 이용, 해당 형식으로 기록한 날짜 저장
                var resultDate = SimpleDateFormat("yyyy-MM-dd-E HH:mm:ss").format(Date(recordDate))

                // 데이터 추가
                sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('$bigGoalName', " +
                        "$time, $bigGoalColor, '$resultDate');")

                sqlitedb.close()
                dbManager.close()
            }
            catch (e: WindowManager.BadTokenException) {
                Log.e("recordExitException", "기록 종료 오류..")
            }

            /** 총 함께한 시간 데이터 업데이트 **/
            dbManager = DBManager(this@TimeRecordActivity, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 기존의 총 함께한 시간 데이터 가져오기
            var dbTempTotalTimeText: String = ""
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)

            if (cursor.moveToNext())
                dbTempTotalTimeText = cursor.getString(cursor.getColumnIndex("total_time"))

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            try {
                // bigInteger 형의 숫자 변수
                var bigInteger1000: BigInteger = BigInteger("1000")
                var bigInteger60: BigInteger = BigInteger("60")

                // 밀리초 단위의 시간 구하기
                var dbHour: BigInteger = dbTempTotalTimeText.split(":")[0].toBigInteger()
                var dbMin: BigInteger = dbTempTotalTimeText.split(":")[1].toBigInteger()
                var dbSec: BigInteger = dbTempTotalTimeText.split(":")[2].toBigInteger()

                // DB 데이터의 총 시간(밀리초 단위)
                var dbTotalTime: BigInteger = (dbHour * bigInteger60 * bigInteger60 * bigInteger1000
                        + dbMin * bigInteger60 * bigInteger1000 + dbSec * bigInteger1000)

                // 기존의 총 기록한 시간 + 이번에 총 기록한 시간
                var resultTotalTime: BigInteger = dbTotalTime + time.toBigInteger()

                // 해당 데이터를 다시 문자열로 분리하기
                var resultHour = resultTotalTime / bigInteger1000 / bigInteger60 / bigInteger60  // 시간
                var resultMin = resultTotalTime / bigInteger1000 / bigInteger60 % bigInteger60   // 분
                var resultSec = resultTotalTime / bigInteger1000 % bigInteger60                  // 초

                // 구한 시간 데이터 업데이트
                dbManager = DBManager(this@TimeRecordActivity, "hamster_db", null, 1)
                sqlitedb = dbManager.writableDatabase

                sqlitedb.execSQL("UPDATE basic_info_db SET total_time = '${resultHour}:${resultMin}:${resultSec}'")

                sqlitedb.close()
                dbManager.close()
            }
            catch(e: Exception)
            {
                Log.e("오류태그", "TimeRecordActivity: DB 데이터 업데이트 실패")
                Log.e("오류태그", "${e.printStackTrace()}")
            }

            // TODO: 3. 팝업 표시(?)

            // 4. 타이머 초기화
            time = 0
        }
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
                binding?.TimeRecordTimeTextView?.text = "$hour : $min : $sec"
            }

            time++  // 시간 증가
        }
    }

    // 이전에 생성된 세부 목표들 중, 달성하지 못한 세부 목표들은 삭제하는 함수
    private fun clearDetailGoal() {
        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 파일명이 적혀있지 않은 세부목표들 삭제
        sqlitedb.execSQL("DELETE FROM detail_goal_time_report_db WHERE photo_name IS NULL")

        sqlitedb.close()
        dbManager.close()
    }

    // 세부 목표 동적 생성
    @SuppressLint("Range")
    private fun addDetailGoal() {

        var cursor: Cursor

        try {
            // DB 데이터 가져오기(세부 목표)
            dbManager = DBManager(this, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // DB 데이터 쓰기 열기(세부 목표 리포트)
            dbManager2 = DBManager(this, "hamster_db", null, 1)
            sqlitedb2 = dbManager2.writableDatabase

            // 해당 대표 목표의 세부 목표들 가져오기
            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '${bigGoalName}'", null)

            var detailGoalCount: Int = cursor.count // 세부 목표의 개수

            // 위젯 생성 및 적용
            while(cursor.moveToNext())
            {
                // detailGoalListContainer에 세부 목표 뷰(container_defail_goal.xml) inflate 하기
                var view: View = layoutInflater.inflate(R.layout.container_record_detail_goal, Lock_detailGoalLinearLayout, false)

                // icon 변경
                var icon: ImageView = view.findViewById(R.id.detailGoalIconImageView)
                var iconResource: Int = cursor.getInt(cursor.getColumnIndex("icon"))
                icon.setImageResource(iconResource)

                // icon의 색을 대표 목표의 색으로 변경
                icon.setColorFilter(bigGoalColor, PorterDuff.Mode.SRC_IN)

                // 세부 목표 이름 변경
                var textView: TextView = view.findViewById(R.id.detailGoalTextView)
                textView.width = 500
                textView.setSingleLine()
                textView.ellipsize = TextUtils.TruncateAt.END
                textView.setText(cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString())

                // 버튼에 리스너 달기
                var button: ImageButton = view.findViewById(R.id.lockDetialmageButton)
                button.setOnClickListener {
                    // Camera Activity로 이동
                    var intent = Intent(this, CameraActivity::class.java)
                    intent.putExtra("detailGoalName", textView.text)    // 세부 목표 이름 보내기
                    startActivity(intent)
                }

                // 위젯 추가
                Lock_detailGoalLinearLayout.addView(view)

                // 세부 목표 리포트 데이터 추가(세부 목표 이름) - is_active: 활성화 표시
                sqlitedb2.execSQL("INSERT INTO detail_goal_time_report_db (detail_goal_name, color, icon, big_goal_name, is_active)"
                        + " VALUES ('${textView.text}', $bigGoalColor, $iconResource, '$bigGoalName', 1);")
            }

            // 닫기
            cursor.close()
            sqlitedb.close()
            dbManager.close()

            sqlitedb2.close()
            dbManager2.close()
        }
        catch(e: Exception) {
            Log.e("DBException", "세부 목표 가져오기 실패")
        }
    }
}