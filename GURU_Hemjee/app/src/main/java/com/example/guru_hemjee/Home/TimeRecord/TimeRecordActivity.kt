package com.example.guru_hemjee.Home.TimeRecord

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ActivityTimeRecordBinding
import java.lang.Exception
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class TimeRecordActivity: AppCompatActivity() {

    // 뷰바인딩
    private var mBinding: ActivityTimeRecordBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // Log 태그
    private var TAG = "TimeRecordActivity"

    // recycler view adapter
    private lateinit var remainGoalAdapter: TimeRecordGoalAdapter    // 남은 세부목표 리사이클러 뷰 어댑터
    private lateinit var completeGoalAdapter: TimeRecordGoalAdapter  // 완료한 세부목표 리사이클러 뷰 어댑터

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 시간 기록 타이머 관련
    private var timerTask: Timer? = null
    private var time: Int = 0
    private var recordDate: Long = 0L

    // 일시정지 상태 플래그
    private var isPause: Boolean = false

    // 목표 관련
    private lateinit var bigGoalName: String    // 대표 목표 이름
    private lateinit var bigGoalColor: String   // 대표 목표 색상
    private var detailGoalNameList = ArrayList<String>()    // 세부 목표 이름 목록
    private var detailGoalCheckedList = ArrayList<Int>()    // 세부 목표 활성화 여부가 담긴 리스트(1: true, 0: false)
    private var detailGoalCheckedNameList = ArrayList<String>() // 활성화된 세부 목표 이름 목록

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTimeRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** 리사이클러뷰 어댑터 연결 **/

        // 1. 세부목표 목록 관련 recycler view
        var linearLayoutManager1 = LinearLayoutManager(this)
        binding.TimeRecordGoalRecyclerView.layoutManager = linearLayoutManager1

        remainGoalAdapter = TimeRecordGoalAdapter(this@TimeRecordActivity)
        binding.TimeRecordGoalRecyclerView.adapter = remainGoalAdapter

        // 2. 완료한 세부목표 관련 recycler view
        var linearLayoutManager2 = LinearLayoutManager(this)
        binding.TimeRecordCompleteRecyclerView.layoutManager = linearLayoutManager2

        completeGoalAdapter = TimeRecordGoalAdapter(this@TimeRecordActivity)
        binding.TimeRecordCompleteRecyclerView.adapter = completeGoalAdapter

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


            if (cursor.moveToNext())
                bigGoalColor = cursor.getString(cursor.getColumnIndex("color")).toString()

            cursor.close()
            sqlitedb.close()
            dbManager.close()
        }
        catch(e: Exception) {
            Log.e(TAG, "대표 목표 색상 뽑아오기 실패")
            Log.e(TAG, e.stackTraceToString())
        }

        // 타이머 초기화
        time = 0

        // 타이머 기록 시작
        countTime()

        // 기록 시작 날짜 저장
        recordDate = System.currentTimeMillis()

        // 클릭 리스너
        // 일시정지 버튼
        binding.TimeRecordPauseButton.setOnClickListener {

            // 만일 현재 타이머가 일시정지된 상태였다면
            if (isPause)
            {
                // 1. 버튼 이미지 변경
                binding.TimeRecordPauseButton.setImageResource(R.drawable.ic_play)

                // 2. 타이머 재작동
                countTime()
            }
            else
            {
                // 1. 버튼 이미지 변경
                binding.TimeRecordPauseButton.setImageResource(R.drawable.ic_pause_black_48dp)

                // 2. 타이머 멈춤
                timerTask?.cancel()
            }

            // 플래그 값 변경
            isPause = !isPause
        }

        // 정지(끝내기 버튼)
        binding.TimeRecordStopButton.setOnClickListener {

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
                        "$time, '$resultDate');")

                sqlitedb.close()
                dbManager.close()
            }
            catch (e: WindowManager.BadTokenException) {
                Log.e(TAG, "기록 종료 오류..")
                Log.e(TAG, e.stackTraceToString())
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
                Log.e(TAG, "DB 데이터 업데이트 실패")
                Log.e(TAG, e.stackTraceToString())
            }

            // TODO: 3. 팝업 표시(?)

            // 4. 타이머 초기화
            time = 0
        }

        /** 세부 목표 관련 정보 처리 **/

        // 세부 목표 리포트 DB 데이터 정리
        clearDetailGoal()

        // 세부 목표 기록 DB 데이터 생성
        insertDetailGoalData()
    }

    override fun onResume() {
        super.onResume()

        // 세부 목표 동적 생성
        addDetailGoalView()
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
                binding.TimeRecordTimeTextView.text = "$hour : $min : $sec"
            }

            time++  // 시간 증가
        }
    }

    // 이전에 생성된 세부 목표들 중, 달성하지 못한 세부 목표들은 삭제하는 함수
    private fun clearDetailGoal() {
        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 파일명이 적혀있지 않고, 현재 활성화 되지 않은 세부목표 삭제
        sqlitedb.execSQL("DELETE FROM detail_goal_time_report_db WHERE photo_name IS NULL AND is_active = 0")

        sqlitedb.close()
        dbManager.close()
    }

    // 세부 목표 기록 DB 데이터를 생성하는 함수(초기 1회 수행)
    private fun insertDetailGoalData() {

        // 세부 목표 관련 데이터 가져오기
        detailGoalNameList = intent.getStringArrayListExtra("detailGoalNameList") as ArrayList<String>
        detailGoalCheckedList = intent.getIntegerArrayListExtra("detailGoalCheckedList") as ArrayList<Int>

        for (i in detailGoalNameList.indices)
        {
            // 체크된 세부목표라면
            if (detailGoalCheckedList[i] == 1)
            {
                // DB 데이터 쓰기 열기(세부 목표 리포트)
                dbManager = DBManager(this, "hamster_db", null, 1)
                sqlitedb = dbManager.writableDatabase

                // 세부 목표 리포트 데이터 생성(세부 목표 이름) - is_active: 활성화 표시
                sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db (detail_goal_name, big_goal_name, is_active)"
                        + " VALUES ('${detailGoalNameList[i]}', '$bigGoalName', 1);")

                // 활성화된 세부 목표 이름 목록에 해당 세부 목표 추가
                detailGoalCheckedNameList.add(detailGoalNameList[i])

                sqlitedb.close()
                dbManager.close()
            }
        }
    }

    // 세부 목표(남은 목표) 동적 생성
    @SuppressLint("Range")
    private fun addDetailGoalView() {

        var cursor: Cursor

        try {
            // DB 데이터 가져오기(세부 목표)
            dbManager = DBManager(this, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 위젯 생성 및 적용
            for (i in detailGoalCheckedNameList.indices)
            {
                // 해당 세부 목표(현재 활성화 된 것) 가져오기
                cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '${detailGoalCheckedNameList[i]}'", null)

                if (cursor.moveToNext())
                {
                    // item 객체 생성
                    var item = TimeRecordGoalItem()

                    // item에 데이터 담기
                    // 1. 아이콘 모양
                    var iconName: String = cursor.getString(cursor.getColumnIndex("icon")).toString()
                    item.setIconName(iconName)

                    // 2. 아이콘 색상
                    item.setIconColor(bigGoalColor)

                    // 3. 세부목표 이름
                    var goalName: String = cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString()
                    item.setGoalName(goalName)

                    // 4. 최종 반영

                    // 해당 세부 목표(현재 활성화 된 것) 가져오기
                    var tempCursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE is_active = 1 AND detail_goal_name = '${detailGoalCheckedNameList[i]}'", null)

                    if (tempCursor.moveToNext())
                    {
                        // 현재 완료된 목표라면
                        if (tempCursor.getInt(tempCursor.getColumnIndex("is_complete")) == 1)
                        {
                            // 완료된 목표 레이아웃 어댑터에 아이템 추가
                            completeGoalAdapter.addItem(item)
                        }
                        else
                        {
                            // 남은 목표 레이아웃 어댑터에 아이템 추가
                            remainGoalAdapter.addItem(item)
                        }
                    }

                    tempCursor.close()

//                // detailGoalListContainer에 세부 목표 뷰(container_defail_goal.xml) inflate 하기
//                var view: View = layoutInflater.inflate(R.layout.container_record_detail_goal, Lock_detailGoalLinearLayout, false)
//
//                // icon 변경
//                var icon: ImageView = view.findViewById(R.id.detailGoalIconImageView)
//                var iconResource: Int = cursor.getInt(cursor.getColumnIndex("icon"))
//                icon.setImageResource(iconResource)
//
//                // icon의 색을 대표 목표의 색으로 변경
//                icon.setColorFilter(bigGoalColor, PorterDuff.Mode.SRC_IN)
//
//                // 세부 목표 이름 변경
//                var textView: TextView = view.findViewById(R.id.detailGoalTextView)
//                textView.width = 500
//                textView.setSingleLine()
//                textView.ellipsize = TextUtils.TruncateAt.END
//                textView.setText(cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString())
//
//                // 버튼에 리스너 달기
//                var button: ImageButton = view.findViewById(R.id.lockDetialmageButton)
//                button.setOnClickListener {
//                    // Camera Activity로 이동
//                    var intent = Intent(this, CameraActivity::class.java)
//                    intent.putExtra("detailGoalName", textView.text)    // 세부 목표 이름 보내기
//                    startActivity(intent)
//                }
//
//                // 위젯 추가
//                Lock_detailGoalLinearLayout.addView(view)
                }

                cursor.close()
            }

            // adapter의 값 변경을 알려줌
            remainGoalAdapter.notifyDataSetChanged()

            // 닫기
            sqlitedb.close()
            dbManager.close()
        }
        catch(e: Exception) {
            Log.e(TAG, "세부 목표 가져오기 실패")
            Log.e(TAG, e.stackTraceToString())
        }
    }
}