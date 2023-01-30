package com.harahamzzi.android.Home.TimeRecord

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.harahamzzi.android.*
import com.harahamzzi.android.databinding.ActivityTimeRecordBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
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
    private lateinit var spf: SharedPreferences     // 백그라운드 돌입 전까지의 누적 시간을 저장하기 위함

    // 시간 기록 타이머 관련
    private var timerTask: Timer? = null
    private var time: BigInteger = BigInteger.ZERO      // 타이머에 들어가는 시간

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

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // 상태바 숨기기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) // API 30이상
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else
        {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN )
        }

        // 햄찌 GIF 이미지 연결
        Glide.with(this).load(R.raw.hamzzi_rolling).into(binding.hamzziImageView)

        /** 리사이클러뷰 어댑터 연결 **/

        // 1. 세부목표 목록 관련 recycler view
        var linearLayoutManager1 = LinearLayoutManager(this@TimeRecordActivity)
        binding.TimeRecordGoalRecyclerView.layoutManager = linearLayoutManager1

        remainGoalAdapter = TimeRecordGoalAdapter(this@TimeRecordActivity)
        binding.TimeRecordGoalRecyclerView.adapter = remainGoalAdapter

        // 2. 완료한 세부목표 관련 recycler view
        var linearLayoutManager2 = LinearLayoutManager(this@TimeRecordActivity)
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

        // 상단 대표목표 세팅
        DBConvert.colorConvert(binding.TimeRecordIconImageView, bigGoalColor, this@TimeRecordActivity)
        binding.TimeRecordBigGoalNameTextView.text = bigGoalName

        // 타이머 설정
        spf = getSharedPreferences("RecordTime", MODE_PRIVATE)

        if (spf.getString("recordedTime", "0").equals("0"))  // 만일 지금이 처음 기록을 시작한 거라면
        {
            // 타이머 초기화
            time = BigInteger.ZERO

            // 타이머 기록 시작
            countTime()

            // 기록 시작 날짜 저장
            var editor: SharedPreferences.Editor = spf.edit()

            editor.putString("recordDate", System.currentTimeMillis().toString())

            editor.apply()

            /** 세부 목표 관련 정보 처리 **/

            // 세부 목표 리포트 DB 데이터 정리
            clearDetailGoal()

            // 세부 목표 기록 DB 데이터 생성
            insertDetailGoalData()
        }
        else    // 이미 기록 시작 후 다른 화면으로 갔다가 다시 돌아온 거라면
        {
            try {
                var recordedTime = spf.getString("recordedTime", "")!!.toLong() // 누적시간 가져오기
                var beforeTime = spf.getString("beforeTime", "")!!.toLong()     // 백그라운드 돌입시 시각 가져오기

                // 누적 시간 설정
                time = recordedTime.toBigInteger()

                if (!(spf.getBoolean("isPause", false)))    // 타이머가 멈춰있지 않았다면
                {
                    // 이후 흐른 시간 더하기
                    var presentTime = System.currentTimeMillis()
                    time += (presentTime - beforeTime).toBigInteger()

                    // 타이머 기록 시작
                    countTime()
                }
                else    // 타이머가 멈춰있었다면
                {
                    // 일시정지 버튼 이미지 변경
                    binding.TimeRecordPauseButton.setImageResource(R.drawable.ic_play)

                    // 플래그 설정
                    isPause = true
                }
            }
            catch (e: Exception) {
                Log.e(TAG, "시간 설정 오류 발생")
                e.printStackTrace()
            }
        }

        // 클릭 리스너
        // 일시정지 버튼
        binding.TimeRecordPauseButton.setOnClickListener {

            // 만일 현재 타이머가 일시정지된 상태였다면
            if (isPause)
            {
                // 1. 버튼 이미지 변경
                binding.TimeRecordPauseButton.setImageResource(R.drawable.ic_pause_black_48dp)

                // 2. 타이머 재작동
                countTime()
            }
            else
            {
                // 1. 버튼 이미지 변경
                binding.TimeRecordPauseButton.setImageResource(R.drawable.ic_play)

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
                var recordDate = spf.getString("recordDate", "0")!!.toLong()
                var resultDate = SimpleDateFormat("yyyy-MM-dd-E HH:mm:ss").format(Date(recordDate))

                // 밀리초 -> hh:mm:ss 형태로 변환
                var resultTime = TimeConvert.msToTimeConvert(time)

                // 데이터 추가
                sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('$bigGoalName', " +
                        "'$resultTime', '$resultDate');")

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

                // DB 데이터(총 시간)의 밀리초 단위 시간 구하기
                var dbTotalMsTime = TimeConvert.timeToMsConvert(dbTempTotalTimeText)

                // 기존의 총 기록한 시간 + 이번에 총 기록한 시간
                var resultTotalMsTime = dbTotalMsTime + time

                // 해당 데이터를 다시 문자열로 분리하기
                var resultTotalStrTime = TimeConvert.msToTimeConvert(resultTotalMsTime)

                // 구한 시간 데이터 업데이트
                dbManager = DBManager(this@TimeRecordActivity, "hamster_db", null, 1)
                sqlitedb = dbManager.writableDatabase

                sqlitedb.execSQL("UPDATE basic_info_db SET total_time = '$resultTotalStrTime'")

                sqlitedb.close()
                dbManager.close()
            }
            catch(e: Exception)
            {
                Log.e(TAG, "DB 데이터 업데이트 실패")
                Log.e(TAG, e.stackTraceToString())
            }

            /** 씨앗 개수 업데이트 **/
            // 3. 씨앗 보상 지급 (1분당 1포인트 지급)
            var rewardPoint: Int = (time / 1000.toBigInteger() / 60.toBigInteger()).toInt() // 보상 씨앗
            var updatePoint = rewardPoint // db에 적용할 씨앗(현재 갖고 있는 씨앗 + 보상 씨앗)

            try {
                // 현재 씨앗 개수 가져오기
                dbManager = DBManager(this@TimeRecordActivity, "hamster_db", null, 1)
                sqlitedb = dbManager.readableDatabase
                cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)

                if (cursor.moveToNext())
                {
                    updatePoint += cursor.getInt(cursor.getColumnIndex("seed"))
                }

                cursor.close()
                sqlitedb.close()
                dbManager.close()

                // 획득한 씨앗 갱신
                dbManager = DBManager(this@TimeRecordActivity, "hamster_db", null, 1)
                sqlitedb = dbManager.writableDatabase

                sqlitedb.execSQL("UPDATE basic_info_db SET seed = $updatePoint")
                sqlitedb.close()
                dbManager.close()
            }
            catch (e: Exception) {
                Log.e(TAG, "씨앗 보상 지급 오류")
                Log.e(TAG, e.stackTraceToString())
            }

            // 4. is_active == 1인 세부목표들 0으로 변경(활성화->비활성화)
            dbManager = DBManager(this@TimeRecordActivity, "hamster_db", null, 1)
            sqlitedb = dbManager.writableDatabase

            sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET is_active = 0 WHERE is_active = 1")

            sqlitedb.close()
            dbManager.close()

            // 5. 팝업 표시 후 홈 화면으로 돌아가기
            finalPopup("기록 종료", "+$rewardPoint", true)

            // 6. 타이머 초기화
            time = 0.toBigInteger()
        }
    }

    override fun onResume() {
        super.onResume()

        // 세부 목표 어댑터 아이템 초기화
        remainGoalAdapter.clearAllItem()
        completeGoalAdapter.clearAllItem()

        // 세부 목표 동적 생성
        addDetailGoalView()
    }

    // 화면이 백그라운드로 들어갈 때 실행
    override fun onPause() {
        super.onPause()

        // 시간 데이터 저장하기
        var editor: SharedPreferences.Editor = spf.edit()

        editor.putString("recordedTime", time.toString())   // 누적 기록 시간

        // 타이머가 돌아가는 중이었다면
        if (!isPause)
        {
            // 타이머 멈춤
            timerTask?.cancel()

            editor.putString("beforeTime", System.currentTimeMillis().toString())   // 화면이 잠시 닫힐 때(백그라운드 돌입)의 시각
        }

        // 플래그 값 저장
        editor.putBoolean("isPause", isPause)

        editor.apply()
    }

    // 백그라운드에서 포그라운드로 전환될 때 호출되는 콜백
    override fun onRestart() {
        super.onRestart()

        // 타이머가 돌아가는 중이었다면
        if (!isPause)
        {
            // 타이머 재작동
            countTime()

            try {
                // 시간 기록 더하기
                var beforeTime = spf.getString("beforeTime", "")!!.toLong()
                var presentTime = System.currentTimeMillis()
                time += (presentTime - beforeTime).toBigInteger()
            }
            catch (e: Exception) {
                Log.e(TAG, "백그라운드 돌입 시 시간 합산 오류")
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        // (폰) 뒤로가기 버튼을 누르면 sliding layout이 내려가게 함
        binding.TimeRecordLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    // 타이머 늘어나게 하고, 변경된 값을 업데이트해서 보여주는 함수
    private fun countTime() {

        // 0.001초마다 변수를 증가시킴
        timerTask = timer(period = 1000) {

            // 자릿수(hh:mm:ss)를 맞추기 위해 TimeConvert 활용
            // 밀리초(time) -> hh:mm:ss 형태의 문자열 변환
            var timeStr: String = TimeConvert.msToTimeConvert(time)

            var hour = timeStr.split(':')[0]
            var min = timeStr.split(':')[1]
            var sec = timeStr.split(':')[2]

            // 위젯 값 변경
            runOnUiThread {
                binding.TimeRecordTimeTextView.text = "$hour : $min : $sec"
            }

            time += 1000.toBigInteger()  // 시간 증가
        }
    }

    // 이전에 생성된 세부 목표들 중, 달성하지 못한 세부 목표들은 삭제하는 함수
    private fun clearDetailGoal() {
        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 파일명이 적혀있지 않고, 현재 활성화 되지 않은 세부목표 삭제
        sqlitedb.execSQL("DELETE FROM detail_goal_time_report_db WHERE photo_name IS NULL")

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
                    item.setGoalName(detailGoalCheckedNameList[i])

                    // 4. 최종 반영

                    // 해당 세부 목표(현재 활성화 된 것) 가져오기
                    var tempCursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE is_active = 1 AND detail_goal_name = '${detailGoalCheckedNameList[i]}'", null)

                    if (tempCursor.moveToNext())
                    {
                        // 현재 완료된 목표라면
                        if (tempCursor.getInt(tempCursor.getColumnIndex("is_complete")) == 1)
                        {
                            // 완료 플래그 올리기
                            item.setIsComplete(true)

                            // 완료된 목표 레이아웃 어댑터에 아이템 추가
                            completeGoalAdapter.addItem(item)
                        }
                        else
                        {
                            // 완료 플래그 내리기
                            item.setIsComplete(false)

                            // 남은 목표 레이아웃 어댑터에 아이템 추가
                            remainGoalAdapter.addItem(item)
                        }
                    }

                    tempCursor.close()
                }

                cursor.close()
            }

            // adapter의 값 변경을 알려줌
            remainGoalAdapter.notifyDataSetChanged()
            completeGoalAdapter.notifyDataSetChanged()

            // 닫기
            sqlitedb.close()
            dbManager.close()
        }
        catch(e: Exception) {
            Log.e(TAG, "세부 목표 가져오기 실패")
            Log.e(TAG, e.stackTraceToString())
        }
    }

    // 마지막 팝업 창(목표 달성!)
    private fun finalPopup(title: String, okString: String, isNeedDrawable: Boolean) {
        val dialog = FinalOKDialog(this, title, okString, isNeedDrawable, R.drawable.complete_hamzzi, null)
        dialog.alertDialog()

        dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener {
            @SuppressLint("Range")
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    /** 기록 종료 **/

                    // 현재 액티비티 닫기
                    finish()
                }
            }
        })
    }
}