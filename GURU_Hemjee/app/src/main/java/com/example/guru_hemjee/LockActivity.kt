package com.example.guru_hemjee

import android.annotation.SuppressLint
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
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.dinuscxj.progressbar.CircleProgressBar
import kotlinx.android.synthetic.main.activity_tutorial.*
import java.lang.Exception
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

//// 홈 화면의 시작 버튼(HomeFragment) -> 잠금 화면
//// 잠금을 실행시켜 타이머와 세부 목표 목록이 있는 잠금 화면을 보여주기 위한 fragment 화면
//@Suppress("DEPRECATION")    // 사용하지 말아야 할 메소드 관련 경고 억제
//class LockActivity : AppCompatActivity() {
//
//    //씨앗 관련
//    private lateinit var Lock_seedPointView: TextView
//
//    //햄스터 장식 관련
//    private lateinit var lockHamsterClothFrameLayout: FrameLayout
//    private lateinit var lockBGFrameLayout: FrameLayout
//
//    //시간 조절 버튼
//    private lateinit var timeMinusImageButton: ImageButton
//    private lateinit var timePlusImageButton: ImageButton
//
//    // 전화 걸기, 메시지 보내기 버튼
//    private lateinit var phoneButton: ImageButton
//    private lateinit var messageButton: ImageButton
//
//    //나가기 버튼
//    private lateinit var lockExitImageButton: ImageButton//첫번째
//    private lateinit var exitImageButton: ImageButton//두번째
//    private lateinit var lockExitTextView: TextView
//
//    // 타이머 시간 관련
//    private lateinit var lockHourTextView: TextView
//    private lateinit var lockMinTextView: TextView
//    private lateinit var lockSecTextView: TextView
//
//    private var isTimerCansel = false   // 타이머 종료 플래그 변수
//
//    private var totalTime = 0   // 전체 잠금 시간
//    private var time = 0        // 현재 남은 잠금 시간
//    private var timerTask: Timer? = null
//
//    // 대표 목표 리포트에 들어갈 시간들
//    private var bigGoallockDate: Long = 0L
//    private lateinit var bigGoalTotalTime: BigInteger
//
//    // progress bar
//    private lateinit var timeLeftCircleProgressBar: CircleProgressBar
//
//    //DB 관련
//    private lateinit var dbManager: DBManager
//    private lateinit var dbManager2: DBManager
//    private lateinit var sqlitedb:SQLiteDatabase
//    private lateinit var sqlitedb2:SQLiteDatabase
//    private lateinit var hamsterName: String
//    private var bigGoalColor: Int = 0   // 대표 목표의 색상
//
//    //보상 관련
//    private var rewardSeed = 0
//
//    // 세부 목표 리스트 관련
//    private lateinit var Lock_detailGoalLinearLayout: LinearLayout  // 세부 목표들 전체가 담길 레이아웃(기존 레이아웃)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        // API 29레벨 이하일 때만 상단 알림 표시를 삭제함(안전을 위해 주석 처리)
////        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
////        {
////            // 상단 알림 표시 삭제
////            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////            notificationManager.deleteNotificationChannel("channel_1")
////        }
//
//        // 액션바 숨기기
//        var actionBar: ActionBar? = supportActionBar
//        actionBar?.hide()
//
//        // 잠금화면으로 쓰이기 위한 플래그 지정
//        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)   // 기본 잠금화면보다 우선 출력
//        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)   // 기본 잠금화면 해제시키기
//
//        setContentView(R.layout.activity_lock)
//
//        //위젯들 연결
//        timeMinusImageButton = findViewById(R.id.timeMinusImageButton)
//        timePlusImageButton = findViewById(R.id.timePlusImageButton)
//
//        //나가기 관련
//        lockExitImageButton = findViewById(R.id.lockExitImageButton)
//        exitImageButton = findViewById(R.id.exitImageButton)
//        lockExitTextView = findViewById(R.id.lockExitTextView)
//        exitImageButton.visibility = View.GONE
//        lockExitTextView.visibility = View.GONE
//
//        phoneButton = findViewById(R.id.phoneButton)
//        messageButton = findViewById(R.id.messageButton)
//
//        lockHourTextView = findViewById(R.id.lockHourTextView)
//        lockMinTextView = findViewById(R.id.lockMinTextView)
//        lockSecTextView = findViewById(R.id.lockSecTextView)
//
//        Lock_seedPointView = findViewById(R.id.Lock_seedPointView)
//
//        timeLeftCircleProgressBar = findViewById(R.id.timeLeftCircleProgressBar)
//
//        Lock_detailGoalLinearLayout = findViewById(R.id.Lock_detailGoalLinearLayout)
//
//        //씨앗 세팅
//        Lock_seedPointView.text = intent.getStringExtra("seed")
//        hamsterName = intent.getStringExtra("hamsterName")
//
//        //햄스터 세팅
//        lockBGFrameLayout = findViewById(R.id.lockBGFrameLayout)
//        lockHamsterClothFrameLayout= findViewById(R.id.lockHamsterClothFrameLayout)
//        FunUpDateHamzzi.upDate(this, lockBGFrameLayout, lockHamsterClothFrameLayout, false, false)
//
//        // 타이머 세팅
//        lockHourTextView.setText(intent.getStringExtra("hour"))
//        lockMinTextView.setText(intent.getStringExtra("min"))
//        lockSecTextView.setText(intent.getStringExtra("sec"))
//
//        totalTime = ((lockHourTextView.text.toString().toInt() * 3600)
//                + (lockMinTextView.text.toString().toInt() * 60) + lockSecTextView.text.toString().toInt())
//        time = totalTime
//
//        // timeLeftCircleProgressBar 세팅
//        timeLeftCircleProgressBar.progress = 0
//        timeLeftCircleProgressBar.max = totalTime
//
//        // 세부 목표 동적 생성 및 세팅
//        addDetailGoal()
//
//        // 타이머 시작
//        countTime()
//
//        // 현재 시각 받아오기(타이머 시작 시각)
//        bigGoallockDate = System.currentTimeMillis() // 현재 시간 가져오기
//
//        //시간 감소 버튼
//        timeMinusImageButton.setOnClickListener {
//            showTimeMinusPopUp()
//        }
//
//        //시간 추가 버튼
//        timePlusImageButton.setOnClickListener {
//            showTimePlusPopUp()
//        }
//
//        //나가기 버튼들
//        lockExitImageButton.setOnClickListener {//첫번째 나가기 버튼
//            exitImageButton.visibility = View.VISIBLE
//            lockExitTextView.visibility = View.VISIBLE
//            lockExitImageButton.visibility = View.GONE
//
//            Handler().postDelayed({
//                exitImageButton.visibility = View.GONE;
//                lockExitTextView.visibility = View.GONE
//                lockExitImageButton.visibility = View.VISIBLE;
//            }, 2500L)
//        }
//
//        exitImageButton.setOnClickListener {//마지막 나가기 버튼
//            showExitPop()
//        }
//
//        // 전화 걸기 버튼 리스너
//        phoneButton.setOnClickListener {
//            val intent = Intent(Intent.ACTION_DIAL)
//            if(intent.resolveActivity(packageManager) != null)
//            {
//                startActivity(intent)
//            }
//        }
//
//        // 메시지 보내기 버튼 리스너
//        messageButton.setOnClickListener {
//            val intent = Intent(Intent.ACTION_SENDTO)
//            intent.data = Uri.parse("smsto:")
//            if(intent.resolveActivity(packageManager) != null)
//            {
//                startActivity(intent)
//            }
//        }
//    }
//
//    // 액티비티가 화면에 보였을 때 호출되는 함수
//    @SuppressLint("Range")
//    override fun onRestart() {
//        super.onRestart()
//
//        /** 뷰에 보이는 씨앗 갱신 **/
//        dbManager = DBManager(baseContext, "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
//        if(cursor.moveToNext()){
//            Lock_seedPointView.text = cursor.getString(cursor.getColumnIndex("seed")).toString()
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 세부 목표 달성 체크 **/
//        // 세부 목표 리포트 확인하기
//        dbManager = DBManager(this, "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//        // 현재 활성화된(is_active = 1) 값만 가져오기
//        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE is_active = 1", null)
//
//        // view의 카운트 숫자로 활용
//        var i = 0
//
//        while(cursor.moveToNext())
//        {
//            // 목표를 달성했다면
//            if(cursor.getString(cursor.getColumnIndex("photo_name")) != null)
//            {
//                var view: View = Lock_detailGoalLinearLayout.get(i)
//
//                // 버튼 리스너 제거
//                var button: ImageButton = view.findViewById(R.id.lockDetialmageButton)
//                button.setOnClickListener {
//                    // 아무것도 하지 않음
//                }
//                // 버튼 배경 변경
//                button.setBackgroundResource(R.drawable.lock_detail_goal_check)
//
//                // 카테고리 아이콘 색상 변경
//                var iconImage: ImageView = view.findViewById(R.id.detailGoalIconImageView)
//                iconImage.setColorFilter(null)
//                iconImage.setColorFilter(ContextCompat.getColor(applicationContext, R.color.NoteYellow))
//
//                // 텍스트 색상 변경
//                var textView: TextView = view.findViewById(R.id.detailGoalTextView)
//                textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.White))
//                textView.width = 500
//                textView.setSingleLine(true)
//                textView.ellipsize = TextUtils.TruncateAt.END
//
//                // 위치 조정
//                button.bringToFront()
//                iconImage.bringToFront()
//                textView.bringToFront()
//            }
//
//            // 카운트 숫자 증가
//            i++
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 잠금 타이머 확인 후 특정 경우에서 팝업 띄우기 **/
//        // 현재 타이머가 끝난 상태라면
//        if (isTimerCansel)
//        {
//            // 나갈 수 있는 팝업창 띄우기
//            finalOK("잠금 종료!", "+${rewardSeed}", true,
//                    true, "목표 달성이다 햄찌!!\n역시 믿고 있었다고 집사!")
//        }
//    }
//
//    // 하단 소프트키를 숨겨 잠금 화면을 풀스크린으로 뿌리도록 함
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (hasFocus) window.decorView.systemUiVisibility = (
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    )
//        }
//    }
//    override fun onAttachedToWindow() {
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//        )
//
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        super.onAttachedToWindow()
//    }
//
//    override fun onBackPressed() {
//        // (폰) 뒤로가기 버튼이 아무런 동작도 하지 않도록 함
//    }
//
//    // 타이머 줄어들게 하고, 변경된 값을 업데이트해서 보여주는 함수
//    private fun countTime() {
//        //var tempTime = time * 100
//
//        // 0.01초마다 변수를 감소시킴
//        timerTask = timer(period = 1000) {
//            val hour = (time/3600) % 24 // 1시간
//            val min = (time/60) % 60   // 1분
//            val sec = time % 60   // 1초
//
//            // 위젯 값 변경
//            runOnUiThread {
//                lockHourTextView.text = "$hour"
//                lockMinTextView.text = "$min"
//                lockSecTextView.text = "$sec"
//            }
//
//            time--  // 시간 감소
//            timeLeftCircleProgressBar.progress++  // progress 수치 증가
//
//            // 타이머 종료
//            if (hour <= 0 && min <= 0 && sec <= 0)
//            {
//                runOnUiThread {
//                    try {
//                        /** 대표 목표 리포트 DB 기록 (생성) **/
//                        dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
//                        sqlitedb = dbManager.writableDatabase
//
//                        // 대표 목표 이름
//                        var bigGoalName: String = intent.getStringExtra("bigGoalName")
//
//                        // 총 잠금한 시간 구하기
//                        var tempTime: BigInteger = System.currentTimeMillis().toBigInteger()
//                        bigGoalTotalTime = tempTime - bigGoallockDate.toBigInteger()
//
//                        // SimpleDateFormat 이용, 해당 형식으로 날짜 저장
//                        var resultDate = SimpleDateFormat("yyyy-MM-dd-E HH:mm:ss").format(Date(bigGoallockDate))
//
//                        // 데이터 추가
//                        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('$bigGoalName', " +
//                                "$bigGoalTotalTime, $bigGoalColor, '$resultDate');")
//
//                        sqlitedb.close()
//                        dbManager.close()
//
//                        //잠금 시간 보상 계산
//                        rewardSeed += bigGoalTotalTime.toInt()/60000
//                        seedChange(rewardSeed)
//
//                        // 나갈 수 있는 팝업창 띄우기
//                        finalOK("잠금 종료!", "+${rewardSeed}", true,
//                                true, "목표 달성이다 햄찌!!\n역시 믿고 있었다고 집사!")
//                    }
//                    catch (e: WindowManager.BadTokenException) {
//                        Log.e("lockExitException", "잠금 종료 팝업창 오류..")
//                    }
//
//                    /** 총 함께한 시간 데이터 업데이트 **/
//                    dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
//                    sqlitedb = dbManager.readableDatabase
//
//                    // 기존의 총 함께한 시간 데이터 가져오기
//                    var dbTempTotalTimeText: String = ""
//                    var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
//
//                    if (cursor.moveToNext())
//                        dbTempTotalTimeText = cursor.getString(cursor.getColumnIndex("total_time"))
//
//                    cursor.close()
//                    sqlitedb.close()
//                    dbManager.close()
//
//                    try {
//                        // bigInteger 형의 숫자 변수
//                        var bigInteger1000: BigInteger = BigInteger("1000")
//                        var bigInteger60: BigInteger = BigInteger("60")
//
//                        // 밀리초 단위의 시간 구하기
//                        var dbHour: BigInteger = dbTempTotalTimeText.split(":")[0].toBigInteger()
//                        var dbMin: BigInteger = dbTempTotalTimeText.split(":")[1].toBigInteger()
//                        var dbSec: BigInteger = dbTempTotalTimeText.split(":")[2].toBigInteger()
//
//                        // DB 데이터의 총 시간(밀리초 단위)
//                        var dbTotalTime: BigInteger = (dbHour * bigInteger60 * bigInteger60 * bigInteger1000
//                                + dbMin * bigInteger60 * bigInteger1000 + dbSec * bigInteger1000)
//
//                        // 기존의 총 잠금한 시간 + 이번에 총 잠금한 시간
//                        var resultTotalTime: BigInteger = dbTotalTime + bigGoalTotalTime
//
//                        // 해당 데이터를 다시 문자열로 분리하기
//                        var resultHour = resultTotalTime / bigInteger1000 / bigInteger60 / bigInteger60  // 시간
//                        var resultMin = resultTotalTime / bigInteger1000 / bigInteger60 % bigInteger60   // 분
//                        var resultSec = resultTotalTime / bigInteger1000 % bigInteger60                  // 초
//
//                        // 구한 시간 데이터 업데이트
//                        dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
//                        sqlitedb = dbManager.writableDatabase
//
//                        sqlitedb.execSQL("UPDATE basic_info_db SET total_time = '${resultHour}:${resultMin}:${resultSec}'")
//
//                        sqlitedb.close()
//                        dbManager.close()
//                    }
//                    catch(e: Exception)
//                    {
//                        Log.e("오류태그", "LockActivity: DB 데이터 업데이트 실패")
//                        Log.e("오류태그", "${e.printStackTrace()}")
//                    }
//                }
//
//                // 타이머 종료 플래그
//                isTimerCansel = true
//
//                timerTask?.cancel()
//            }
//        }
//    }
//
//    // 이전에 생성된 세부 목표들 중, 달성하지 못한 세부 목표들은 삭제하는 함수
//    private fun clearDetailGoal() {
//        dbManager = DBManager(this, "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 파일명이 적혀있지 않은 세부목표들 삭제
//        sqlitedb.execSQL("DELETE FROM detail_goal_time_report_db WHERE photo_name IS NULL")
//
//        sqlitedb.close()
//        dbManager.close()
//    }
//
//    // 세부 목표 동적 생성
//    private fun addDetailGoal() {
//
//        // 잠금 전 확인 팝업창에 있던 대표 목표 이름(위젯) 가져오기
//        var bigGoalName = intent.getStringExtra("bigGoalName")  // 대표 목표 이름
//
//        var cursor: Cursor
//
//        try {
//            // 대표 목표의 색상 뽑아오기
//            dbManager = DBManager(this, "hamster_db", null, 1)
//            sqlitedb = dbManager.readableDatabase
//            cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '${bigGoalName}'", null)
//
//
//            if(cursor.moveToNext())
//                bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))
//
//            cursor.close()
//            sqlitedb.close()
//            dbManager.close()
//        }
//        catch(e: Exception) {
//            Log.e("DBException", "대표 목표 색상 뽑아오기 실패")
//        }
//
//        try {
//            // DB 데이터 가져오기(세부 목표)
//            dbManager = DBManager(this, "hamster_db", null, 1)
//            sqlitedb = dbManager.readableDatabase
//
//            // DB 데이터 쓰기 열기(세부 목표 리포트)
//            dbManager2 = DBManager(this, "hamster_db", null, 1)
//            sqlitedb2 = dbManager2.writableDatabase
//
//
//            // 해당 대표 목표의 세부 목표들 가져오기
//            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '${bigGoalName}'", null)
//
//            var detailGoalCount: Int = cursor.count // 세부 목표의 개수
//
//            // 위젯 생성 및 적용
//            while(cursor.moveToNext())
//            {
//                // detailGoalListContainer에 세부 목표 뷰(container_defail_goal.xml) inflate 하기
//                var view: View = layoutInflater.inflate(R.layout.container_lock_detail_goal, Lock_detailGoalLinearLayout, false)
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
//                    intent.putExtra("detailGoalCount", detailGoalCount) // 총 세부 목표 개수 보내기
//                    intent.putExtra("totalLockTime", totalTime)         // 총 잠금 시간 보내기
//                    intent.putExtra("seedPoint", Lock_seedPointView.text.toString().toInt()) // 현재 씨앗 개수 보내기
//                    startActivity(intent)
//                }
//
//                // 위젯 추가
//                Lock_detailGoalLinearLayout.addView(view)
//
//                // 세부 목표 리포트 데이터 추가(세부 목표 이름) - is_active: 활성화 표시
//                sqlitedb2.execSQL("INSERT INTO detail_goal_time_report_db (detail_goal_name, color, icon, big_goal_name, is_active)"
//                        + " VALUES ('${textView.text}', $bigGoalColor, $iconResource, '$bigGoalName', 1);")
//            }
//
//            // 닫기
//            cursor.close()
//            sqlitedb.close()
//            dbManager.close()
//
//            sqlitedb2.close()
//            dbManager2.close()
//        }
//        catch(e: Exception) {
//            Log.e("DBException", "세부 목표 가져오기 실패")
//        }
//    }
//
//    // 시간 감소 팝업
//    private fun showTimeMinusPopUp() {
//        val dialog = AlertDialog(this, "10분 줄이기", "-40      ",
//                true,"고작 10분 줄이려고\n씨앗 40개나 쓰냐 햄찌...?")
//        dialog.AlertDialog()
//
//        // 만일 현재 보유 씨앗이 구매 비용(=40)보다 많다면 -> 정상 구매
//        if(Lock_seedPointView.text.toString().toInt() > 40)
//        {
//            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
//                override fun onClicked(isConfirm: Boolean) {
//                    if(isConfirm){
//                        // 현재 잔여 시간이 10분 이상일 때만 확인 팝업 뜨도록 함
//                        // 잠금 종료 팝업과의 중복을 방지하기 위함
//                        if(time >= 600)
//                        {
//                            finalOK("10분 줄이기", "확인", false,
//                                    false, "인생은 한방이 아니라\n서서히 망한다 햄찌...")
//                            time -= 600     // 잔여 시간 10분 감소
//                        }
//                        // 현재 잔여 시간이 10분 이하일 경우
//                        else
//                        {
//                            time = 0        // 잔여 시간 0으로 세팅
//                        }
//                        timeLeftCircleProgressBar.progress += 600 // 10분만큼 진행도 증가
//                        seedChange(-40)     // 씨앗 차감
//                    }
//                }
//            })
//        }
//        // 현재 보유한 씨앗이 구매 비용보다 적을 경우 -> 구매 실패
//        else
//        {
//            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
//                override fun onClicked(isConfirm: Boolean) {
//                    if(isConfirm){
//                        // 시간 감소를 구매할 수 없다는..뜻의 팝업 띄우기
//                        finalOK("구매 불가", "확인", false,
//                                false, "씨앗이 없다 햄찌!\n일해라 햄찌!")
//                    }
//                }
//            })
//        }
//    }
//
//    // 시간 추가 팝업
//    private fun showTimePlusPopUp() {
//        val dialog = AlertDialog(this, "10분 늘리기", "10분 늘리기",
//                false, "좋아 좋아 더 열심히!!!\n더 씨앗을 버는 거다 햄찌!!")
//        dialog.AlertDialog()
//
//        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
//            override fun onClicked(isConfirm: Boolean) {
//                if(isConfirm){
//                    finalOK("10분 늘리기", "확인", false,
//                            false, "좋아! 끝까지 가보는 거다 햄찌!\n해바라기 씨를 위해!")
//
//                    time += 600                         // 잔여시간 10분 늘리기
//                    timeLeftCircleProgressBar.max = totalTime + 600   // 전체 진행도(max)값 10분만큼 확장
//                }
//            }
//        })
//    }
//
//    // (나가기 구매를 통한)나가기 팝업
//    private fun showExitPop() {
//        val dialog = AlertDialog(this,"잠금 종료하기", "-180      ",
//                true, "진짜 갈꺼냐 햄찌...?\n여기서 진짜 포기냐 햄찌??")
//        dialog.AlertDialog()
//
//        // 만일 현재 보유 씨앗이 구매 비용(=180)보다 많다면 -> 정상 구매
//        if(Lock_seedPointView.text.toString().toInt() > 180)
//        {
//            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener{
//                override fun onClicked(isConfirm: Boolean) {
//                    if(isConfirm){
//                        seedChange(-180)    // 나가기 사용으로 인한 씨앗 소모
//                        timerTask?.cancel()         // 타이머 종료
//
//                        /** 대표 목표 리포트 DB 기록 (생성) **/
//                        dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
//                        sqlitedb = dbManager.writableDatabase
//
//                        // 대표 목표 이름
//                        var bigGoalName: String = intent.getStringExtra("bigGoalName")
//
//                        // 총 잠금한 시간 구하기
//                        var tempTime: BigInteger = System.currentTimeMillis().toBigInteger()
//                        bigGoalTotalTime = tempTime - bigGoallockDate.toBigInteger()
//
//                        // SimpleDateFormat 이용, 해당 형식으로 날짜 저장
//                        var resultDate =
//                            SimpleDateFormat("yyyy-MM-dd-E HH:mm:ss").format(Date(bigGoallockDate))
//
//                        // 대표 목표 리포트 데이터 추가
//                        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('$bigGoalName', " +
//                                "$bigGoalTotalTime, $bigGoalColor, '$resultDate');")
//
//                        sqlitedb.close()
//                        dbManager.close()
//
//                        /** 총 함께한 시간 데이터 업데이트 **/
//                        dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
//                        sqlitedb = dbManager.readableDatabase
//
//                        // 기존의 총 함께한 시간 데이터 가져오기
//                        var dbTempTotalTimeText: String = ""
//                        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
//
//                        if (cursor.moveToNext())
//                            dbTempTotalTimeText = cursor.getString(cursor.getColumnIndex("total_time"))
//
//                        cursor.close()
//                        sqlitedb.close()
//                        dbManager.close()
//
//                        try {
//                            // bigInteger 형의 숫자 변수
//                            var bigInteger1000: BigInteger = BigInteger("1000")
//                            var bigInteger60: BigInteger = BigInteger("60")
//
//                            // 밀리초 단위의 시간 구하기
//                            var dbHour: BigInteger = dbTempTotalTimeText.split(":")[0].toBigInteger()
//                            var dbMin: BigInteger = dbTempTotalTimeText.split(":")[1].toBigInteger()
//                            var dbSec: BigInteger = dbTempTotalTimeText.split(":")[2].toBigInteger()
//
//                            // DB 데이터의 총 시간(밀리초 단위)
//                            var dbTotalTime: BigInteger = (dbHour * bigInteger60 * bigInteger60 * bigInteger1000
//                                    + dbMin * bigInteger60 * bigInteger1000 + dbSec * bigInteger1000)
//
//                            // 기존의 총 잠금한 시간 + 이번에 총 잠금한 시간
//                            var resultTotalTime: BigInteger = dbTotalTime + bigGoalTotalTime
//
//                            // 해당 데이터를 다시 문자열로 분리하기
//                            var resultHour = resultTotalTime / bigInteger1000 / bigInteger60 / bigInteger60  // 시간
//                            var resultMin = resultTotalTime / bigInteger1000 / bigInteger60 % bigInteger60   // 분
//                            var resultSec = resultTotalTime / bigInteger1000 % bigInteger60                  // 초
//
//                            // 구한 시간 데이터 업데이트
//                            dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
//                            sqlitedb = dbManager.writableDatabase
//
//                            sqlitedb.execSQL("UPDATE basic_info_db SET total_time = '${resultHour}:${resultMin}:${resultSec}'")
//
//                            sqlitedb.close()
//                            dbManager.close()
//                        }
//                        catch(e: Exception)
//                        {
//                            Log.e("오류태그", "LockActivity: DB 데이터 업데이트 실패")
//                            Log.e("오류태그", "${e.printStackTrace()}")
//                        }
//
//                        //잠금 시간 보상 계산
//                        // 1분당 1 씨앗
//                        rewardSeed = bigGoalTotalTime.toInt()/60000
//                        seedChange(rewardSeed)
//
//                        finalOK("잠금 종료하기", "${rewardSeed-180}", true,
//                                true, "나보다 나약하다 햄찌..!\n열심히해라 햄찌!")
//                    }
//                }
//            })
//        }
//        // 만일 현재 보유 씨앗이 구매 비용(=180)보다 적다면 -> 구매 실패(나가기 실패)
//        else
//        {
//            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener{
//                override fun onClicked(isConfirm: Boolean) {
//                    if(isConfirm){
//                        // 시간 감소를 구매할 수 없다는..뜻의 팝업 띄우기
//                        finalOK("구매 불가", "확인", false,
//                                false, "씨앗이 없다 햄찌!\n일해라 햄찌!")
//                    }
//                }
//            })
//        }
//    }
//
//    // 마지막 확인 팝업 창
//    private fun finalOK(title: String, okString: String, isNeedDrawable: Boolean, isLockFinished: Boolean, talkText: String) {
//        val dialog = FinalOKDialog(this,title, okString, isNeedDrawable, null, talkText)
//        dialog.alertDialog()
//
//        dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener {
//            override fun onClicked(isConfirm: Boolean) {
//
//                // 나갈려고 하는 상황
//                if (isConfirm && isLockFinished) {
//                    // -- 잠금 종료시 필요한 연산 --
//
//                    // DB 열기
//                    dbManager = DBManager(this@LockActivity, "hamster_db", null, 1)
//                    sqlitedb = dbManager.writableDatabase
//
//                    /** 잠금화면에 띄워졌던 세부 목표들 비활성화 설정 **/
//                    sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET is_active = 0 WHERE is_active = 1")
//
//                    sqlitedb.close()
//                    dbManager.close()
//
//                    // 이번에 생성한 세부 목표들 중, 달성하지 못한 세부 목표들은 삭제
//                    clearDetailGoal()
//
//                    LockScreenUtil.deActive()   // 잠금 서비스 종료
//
//                    //잠금 종료
//                    finish()
//                }
//            }
//        })
//    }
//
//    // 씨앗 변화
//    private fun seedChange(change: Int) {
//        var changedSeed = Lock_seedPointView.text.toString().toInt() + change
//
//        dbManager = DBManager(this, "hamster_db", null, 1)
//        sqlitedb = dbManager.writableDatabase
//        sqlitedb.execSQL("UPDATE basic_info_db SET seed = '"+changedSeed.toString()+
//                "' WHERE hamster_name = '"+hamsterName+"'")
//        sqlitedb.close()
//        dbManager.close()
//
//        Lock_seedPointView.text = changedSeed.toString()
//    }
//
//}