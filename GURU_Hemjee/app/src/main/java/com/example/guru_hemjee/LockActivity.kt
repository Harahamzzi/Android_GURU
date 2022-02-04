package com.example.guru_hemjee

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dinuscxj.progressbar.CircleProgressBar
import java.lang.Exception
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

@Suppress("DEPRECATION")    // 사용하지 말아야 할 메소드 관련 경고 억제
class LockActivity : AppCompatActivity() {

    //씨앗 관련
    private lateinit var seedPointView: TextView

    //햄스터 장식 관련
    private lateinit var clothFrameLayout: FrameLayout
    private lateinit var bgFrameLayout: FrameLayout

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

    private var totalTime = 0   // 전체 잠금 시간
    private var time = 0        // 현재 남은 잠금 시간
    private var timerTask: Timer? = null

    // 대표 목표 리포트에 들어갈 시간들
    private var bigGoallockDate: Long = 0L
    private lateinit var bigGoalTotalTime: BigInteger

    // progress bar
    lateinit var progressBar: CircleProgressBar

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var dbManager2: DBManager
    private lateinit var sqlitedb:SQLiteDatabase
    private lateinit var sqlitedb2:SQLiteDatabase
    private lateinit var hamsterName: String

    // 세부 목표 리스트 관련
    lateinit var detailGoalListContainer: LinearLayout  // 세부 목표들 전체가 담길 레이아웃(기존 레이아웃)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상단 알림 표시 삭제
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel("channel_1")

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

        detailGoalListContainer = findViewById(R.id.Lock_detailGoalLinearLayout)

        //씨앗 세팅
        seedPointView.text = intent.getStringExtra("seed")
        hamsterName = intent.getStringExtra("hamsterName")

        //햄스터 세팅
        bgFrameLayout = findViewById(R.id.lockBGFrameLayout)
        clothFrameLayout= findViewById(R.id.lockHamsterClothFrameLayout)
        FunUpDateHamzzi.upDate(this, bgFrameLayout, clothFrameLayout, false, false)

        // 타이머 세팅
        lockHourTextView.setText(intent.getStringExtra("hour"))
        lockMinTextView.setText(intent.getStringExtra("min"))
        lockSecTextView.setText(intent.getStringExtra("sec"))

        totalTime = (lockHourTextView.text.toString().toInt() * 3600) + (lockMinTextView.text.toString().toInt() * 60) + lockSecTextView.text.toString().toInt()
        time = totalTime

//        var timeTemp = intent.getStringExtra("time")
//        var sf = SimpleDateFormat("hh:mm:ss")
//        time = sf.parse(timeTemp).getTime().toInt()

        // progressBar 세팅
        progressBar.progress = 0
        progressBar.max = totalTime

        // 세부 목표 동적 생성 및 세팅
        addDetailGoal()

        // 타이머 시작
        countTime()

        // 현재 시각 받아오기(타이머 시작 시각)
        bigGoallockDate = System.currentTimeMillis() // 현재 시간 가져오기

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

    // 액티비티가 화면에 보였을 때 호출되는 함수
    override fun onResume() {
        super.onResume()

        // 뷰에 보이는 씨앗 갱신
        dbManager = DBManager(baseContext, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
            seedPointView.text = cursor.getString(cursor.getColumnIndex("seed")).toString()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 세부 목표 달성 체크 - 세부 목표 리포트 확인하기
        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        // 현재 활성화된(is_active = 1) 값만 가져오기
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE is_active = 1", null)

        // view의 아이디 숫자로 활용
        var i = 0

        while(cursor.moveToNext())
        {
            // 목표를 달성했다면
            if(cursor.getString(cursor.getColumnIndex("photo_name")) != null)
            {
                // FixMe: 왜..안 되는 거지..?
                var id = resources.getIdentifier("detailGoalView_${i}", "id", packageName)
                var view: View = findViewById(id)

                // 버튼 리스너 제거
                var button: ImageButton = view.findViewById(R.id.lockDetialmageButton)
                button.setOnClickListener {
                    // 아무것도 하지 않음
                }
                // 버튼 배경 변경
                button.setBackgroundResource(R.drawable.lock_detail_goal_check)

                // 카테고리 아이콘 색상 변경
                var iconImage: ImageView = view.findViewById(R.id.detailGoalIconImageView)
                iconImage.setColorFilter(null)
                iconImage.setColorFilter(ContextCompat.getColor(applicationContext, R.color.NoteYellow))

                // 텍스트 색상 변경
                var textView: TextView = view.findViewById(R.id.detailGoalTextView)
                textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.White))

                // 위치 조정
                button.bringToFront()
                iconImage.bringToFront()
                textView.bringToFront()
            }

            // 아이디 숫자 증가
            i++
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
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
            if (hour <= 0 && min <= 0 && sec <= 0)
            {
                runOnUiThread {
                    try {
                        // 나갈 수 있는 팝업창 띄우기
                        finalOK("잠금 종료!", "확인", false, false, true, "목표 달성이다 햄찌!!\n역시 믿고 있었다고 집사!")
                    }
                    catch (e: WindowManager.BadTokenException) {
                        Log.e("lockExitException", "잠금 종료 팝업창 오류..")
                    }

                }

                timerTask?.cancel()
            }
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
    private fun addDetailGoal() {

        // 잠금 전 확인 팝업창에 있던 대표 목표 이름(위젯) 가져오기
        var bigGoalName = intent.getStringExtra("bigGoalName")  // 대표 목표 이름
        var bigGoalColor: Int = 0   // 대표 목표의 색상

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
            var i = 0

            // 위젯 생성 및 적용
            while(cursor.moveToNext())
            {
                // detailGoalListContainer에 세부 목표 뷰(container_defail_goal.xml) inflate 하기
                var view: View = layoutInflater.inflate(R.layout.container_detail_goal, detailGoalListContainer, false)

                // 각 view에 id 지정
                view.id = resources.getIdentifier("detailGoalView_${i}", "id", packageName)

                // icon 변경
                var icon: ImageView = view.findViewById(R.id.detailGoalIconImageView)
                icon.setImageResource(cursor.getInt(cursor.getColumnIndex("icon")))

                // icon의 색을 대표 목표의 색으로 변경
                icon.setColorFilter(bigGoalColor, PorterDuff.Mode.SRC_IN)

                // 세부 목표 이름 변경
                var textView: TextView = view.findViewById(R.id.detailGoalTextView)
                textView.setText(cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString())

                // 버튼에 리스너 달기
                var button: ImageButton = view.findViewById(R.id.lockDetialmageButton)
                button.setOnClickListener {
                    // Camera Activity로 이동
                    var intent = Intent(this, CameraActivity::class.java)
                    intent.putExtra("detailGoalName", textView.text)    // 세부 목표 이름 보내기
                    intent.putExtra("detailGoalCount", detailGoalCount) // 총 세부 목표 개수 보내기
                    intent.putExtra("totalLockTime", totalTime)         // 총 잠금 시간 보내기
                    intent.putExtra("seedPoint", seedPointView.text.toString().toInt()) // 현재 씨앗 개수 보내기
//                    intent.putExtra("viewId", view.id)
                    startActivity(intent)
                }

                // 위젯 추가
                detailGoalListContainer.addView(view)

                // 아이디 구분 숫자 증가
                i++

                // 세부 목표 리포트 데이터 추가(세부 목표 이름) - is_active: 활성화 표시
                sqlitedb2.execSQL("INSERT INTO detail_goal_time_report_db (detail_goal_name, is_active) VALUES ('${textView.text}', 1);")
            }

            // 닫기
            cursor.close()
            sqlitedb.close()
            dbManager.close()

            sqlitedb2.close()
        }
        catch(e: Exception) {
            Log.e("DBException", "세부 목표 가져오기 실패")
        }
    }

    // 시간 감소 팝업
    private fun showTimeMinusPopUp() {
        val dialog = AlertDialog(this, "10분 줄이기", "-40      ", true,"고작 10분 줄이려고\n씨앗 40개나 쓰냐 햄찌...?")
        dialog.AlertDialog()

        // 만일 현재 보유 씨앗이 구매 비용(=40)보다 많다면 -> 정상 구매
        if(seedPointView.text.toString().toInt() > 40)
        {
            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
                override fun onClicked(isConfirm: Boolean) {
                    if(isConfirm){
                        // 현재 잔여 시간이 10분 이상일 때만 확인 팝업 뜨도록 함
                        // 잠금 종료 팝업과의 중복을 방지하기 위함
                        if(time >= 600)
                        {
                            finalOK("10분 줄이기", "확인", false, false,false, "인생은 한방이 아니라\n서서히 망한다 햄찌...")
                            time -= 600     // 잔여 시간 10분 감소
                        }
                        // 현재 잔여 시간이 10분 이하일 경우
                        else
                        {
                            time = 0        // 잔여 시간 0으로 세팅
                        }
                        progressBar.progress += 600 // 10분만큼 진행도 증가
                        seedChange(-40)     // 씨앗 차감
                    }
                }
            })
        }
        // 현재 보유한 씨앗이 구매 비용보다 적을 경우 -> 구매 실패
        else
        {
            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
                override fun onClicked(isConfirm: Boolean) {
                    if(isConfirm){
                        // 시간 감소를 구매할 수 없다는..뜻의 팝업 띄우기
                        finalOK("구매 불가", "확인", false, false,false, "씨앗이 없다 햄찌!\n일해라 햄찌!")
                    }
                }
            })
        }
    }

    // 시간 추가 팝업
    private fun showTimePlusPopUp() {
        val dialog = AlertDialog(this, "10분 늘리기", "10분 늘리기", false, "좋아 좋아 더 열심히!!!\n더 씨앗을 버는 거다 햄찌!!")
        dialog.AlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    finalOK("10분 늘리기", "확인", false, false,false, "좋아! 끝까지 가보는 거다 햄찌!\n해바라기 씨를 위해!")

                    time += 600                         // 잔여시간 10분 늘리기
                    progressBar.max = totalTime + 600   // 전체 진행도(max)값 10분만큼 확장
                }
            }
        })
    }

    // (나가기 구매를 통한)나가기 팝업
    private fun showExitPop() {
        val dialog = AlertDialog(this,"잠금 종료하기", "-180      ", true, "진짜 갈꺼냐 햄찌...?\n여기서 진짜 포기냐 햄찌??")
        dialog.AlertDialog()

        // 만일 현재 보유 씨앗이 구매 비용(=180)보다 많다면 -> 정상 구매
        if(seedPointView.text.toString().toInt() > 180)
        {
            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener{
                override fun onClicked(isConfirm: Boolean) {
                    if(isConfirm){
                        finalOK("잠금 종료하기", "확인", false, true, true, "나보다 나약하다 햄찌..!\n열심히해라 햄찌!")
                    }
                }
            })
        }
        // 만일 현재 보유 씨앗이 구매 비용(=180)보다 적다면 -> 구매 실패(나가기 실패)
        else
        {
            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener{
                override fun onClicked(isConfirm: Boolean) {
                    if(isConfirm){
                        // 시간 감소를 구매할 수 없다는..뜻의 팝업 띄우기
                        finalOK("구매 불가", "확인", false, false, false, "씨앗이 없다 햄찌!\n일해라 햄찌!")
                    }
                }
            })
        }
    }

    // 마지막 확인 팝업 창
    private fun finalOK(title: String, okString: String, isNeedDrawable: Boolean, isExitBuy: Boolean, isLockFinished: Boolean, talkText: String) {
        val dialog = FinalOKDialog(this,title, okString, isNeedDrawable, null, talkText)
        dialog.alertDialog()

        dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {

                // 나갈려고 하는 상황
                if(isConfirm && isLockFinished){
                    // -- 잠금 종료시 필요한 연산 --

                    // 나가기를 구매해서 나가는 경우
                    if(isExitBuy)
                    {
                        seedChange(-180)    // 나가기 사용으로 인한 씨앗 소모
                        timerTask?.cancel()         // 타이머 종료
                    }

                    /** 잠금화면에 띄워졌던 세부 목표들 비활성화 설정 **/
                    dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET is_active = 0 WHERE is_active = 1")

                    sqlitedb.close()
                    dbManager.close()

                    /** 대표 목표 리포트 DB 기록 (생성) **/
                    dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    // 대표 목표 이름
                    var bigGoalName: String = intent.getStringExtra("bigGoalName")

                    // 총 잠금한 시간 구하기
                    var tempTime: BigInteger = System.currentTimeMillis().toBigInteger()
                    bigGoalTotalTime = tempTime - bigGoallockDate.toBigInteger()

                    // SimpleDateFormat 이용, 해당 형식으로 날짜 저장
                    var resultDate = SimpleDateFormat("yyyy-MM-dd-E HH:mm:ss").format(Date(bigGoallockDate + 32400000))

                    // 데이터 추가
                    sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('$bigGoalName', $bigGoalTotalTime, '$resultDate');")

                    sqlitedb.close()
                    dbManager.close()

                    // 이번에 생성한 세부 목표들 중, 달성하지 못한 세부 목표들은 삭제
                    clearDetailGoal()

                    LockScreenUtil.deActive()   // 잠금 서비스 종료

                    //잠금 종료
                    finish()
                }
            }
        })
    }

    // 씨앗 변화
    private fun seedChange(change: Int) {
        var changedSeed = seedPointView.text.toString().toInt() + change

        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.writableDatabase
        sqlitedb.execSQL("UPDATE basic_info_db SET seed = '"+changedSeed.toString()+
                "' WHERE hamster_name = '"+hamsterName+"'")
        sqlitedb.close()
        dbManager.close()

        seedPointView.text = changedSeed.toString()
    }

}