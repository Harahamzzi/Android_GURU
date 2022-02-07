package com.example.guru_hemjee
import android.util.Log

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.marginStart
import androidx.fragment.app.FragmentTransaction
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.math.BigInteger
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// 일간별 분석 리포트를 보여주는 fragment 화면
// 해당 날짜에서의 목표별 총 잠금시간/총 잠금시간 비율로 통계를 보여준다.
class DailyReportFragment : Fragment() {

    // db
    lateinit var dbManager: DBManager
    lateinit var sqlite: SQLiteDatabase

    // 일간, 주간, 월간
    lateinit var dailyBtn: AppCompatButton
    lateinit var weeklyBtn: AppCompatButton
    lateinit var monthlyBtn: AppCompatButton

    // 오늘 리포트 화면으로 이동하는 달력 버튼
    lateinit var moveTodayButton: ImageButton

    // 날짜 & 시간
    lateinit var dailyTextview: TextView
    lateinit var dailyTimeTextview: TextView

    // 이전 & 다음 버튼
    lateinit var prevBtn1: ImageButton
    lateinit var nextBtn1: ImageButton

    // 파이 차트
    lateinit var dailyPieChart: PieChart

    // 세부목표 리스트를 저장할 리니어 레이아웃
    lateinit var dailyReportListLayout: LinearLayout

    // 텍스트뷰
    lateinit var noGoalTimeView: TextView

    // 현재 날짜
    var nowTime = ZonedDateTime.now()

    // 현재 페이지에 보여지는 날짜
    var nowViewTime = nowTime

    // 2차원 배열(대표목표)
    private lateinit var bigGoalArrayList: ArrayList<MutableMap<String, String>>
    private var isBigGoalInitialised = false

    // 2차원 배열(세부목표)
    private lateinit var detailGoalArrayList: ArrayList<MutableMap<String, String>>
    private var isDetailGoalInitialized = false

    // 현재 리포트 화면 상태
    var reportSate: Int = 0 // 오늘

    var mainActivity : SubMainActivity? = null // 서브 메인 액티비티 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as SubMainActivity
    }

    override fun onDetach() {
        super.onDetach()

        mainActivity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_daily_report, container, false)

        dailyBtn = view.findViewById(R.id.dailyBtn)
        weeklyBtn = view.findViewById(R.id.weeklyBtn)
        moveTodayButton = view.findViewById(R.id.moveTodayButton)
        dailyTextview = view.findViewById(R.id.dailyTextview)
        dailyTimeTextview = view.findViewById(R.id.dailyTimeTextview)
        prevBtn1 = view.findViewById(R.id.prevBtn1)
        nextBtn1 = view.findViewById(R.id.nextBtn1)
        dailyPieChart = view.findViewById(R.id.dailyPieChart)
        dailyReportListLayout = view.findViewById(R.id.dailyReportListLayout)
        noGoalTimeView = view.findViewById(R.id.noGoalTimeView)

        // 화면에 접속할 때마다 항상 레이아웃 초기화
        dailyReportListLayout.removeAllViews()
        Log.i ("정보태그", "$nowTime")
        // 대표목표 리포트 db에 저장된 값 읽어오기(대표목표 값, 대표목표 총 수행 시간, 잠금 날짜)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db", null)

        // 모든 값들 배열에 저장(같은 날짜 내에 중복값 저장X)
        while (cursor.moveToNext()) {
            var str_big_goal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            var bigint_time = cursor.getInt(cursor.getColumnIndex("total_lock_time")).toBigInteger()
            var str_date = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
            var int_color = cursor.getInt(cursor.getColumnIndex("color")).toBigInteger()

            // 배열에 읽어온 값 저장
            var isFlag: Boolean = false // 중복값 확인
            var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
            if(!isBigGoalInitialised){ //처음 입력시에는 초기화
                bigGoalArrayList = arrayListOf(
                    mutableMapOf(
                        "big_goal_name" to str_big_goal,
                        "total_lock_time" to bigint_time.toString(),
                        "lock_date" to date1[0],
                        "color" to int_color.toString()
                    )
                )
                isBigGoalInitialised = true
            } else {//같은 날 같은 목표를 달성했다면 시간만 추가하고 새로 추가 X
                var isFlag: Boolean = false // 중복값 확인
                var i = 0
                while (i < bigGoalArrayList.size) {
                    //기존에 있는 목표인지 확인(이름과 색상 모두 고려)
                    if (bigGoalArrayList[i]["big_goal_name"] == str_big_goal &&
                        bigGoalArrayList[i]["color"]!!.toBigInteger() == int_color &&
                        bigGoalArrayList[i]["lock_date"] == date1[0]) {

                        bigGoalArrayList[i]["total_lock_time"] =
                            (bigGoalArrayList[i]["total_lock_time"]?.toInt()
                                ?.plus(bigint_time.toInt())).toString()
                        isFlag = true
                        break
                    }
                    i++
                }
                if (!isFlag) {
                    bigGoalArrayList.add(
                        mutableMapOf(
                            "big_goal_name" to str_big_goal,
                            "total_lock_time" to bigint_time.toString(),
                            "lock_date" to date1[0],
                            "color" to int_color.toString()
                        )
                    )
                }
            }
        }
        cursor.close()

        // 세부목표 리포트 db에서 저장된 값 읽어오기(세부목표, 잠금 날짜)
        var cursor3: Cursor
        cursor3 = sqlite.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

        while (cursor3.moveToNext()) {
            var str_detail_goal = cursor3.getString(cursor3.getColumnIndex("detail_goal_name"))
            var str_date = cursor3.getString(cursor3.getColumnIndex("lock_date")).toString()
            var int_icon = cursor3.getInt(cursor3.getColumnIndex("icon")).toBigInteger()
            var int_color = cursor3.getInt(cursor3.getColumnIndex("color")).toBigInteger()
            var str_big_goal = cursor3.getString(cursor3.getColumnIndex("big_goal_name")).toString()

            // 배열에 읽어온 값 저장 (같은 날짜 내에 중복값 저장X)
            var isFlag: Boolean = false // 중복값 확인
            var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
            if (!isDetailGoalInitialized) {
                detailGoalArrayList = arrayListOf(
                    mutableMapOf(
                        "detail_goal_name" to str_detail_goal,
                        "lock_date" to date1[0],
                        "icon" to int_icon.toString(),
                        "color" to int_color.toString(),
                        "big_goal_name" to str_big_goal
                    )
                )
                isDetailGoalInitialized = true

            } else {
                var isFlag: Boolean = false // 중복값 확인
                var i = 0
                //기존에 값이 없을 때만 새로 추가
                while (i < detailGoalArrayList.size) {
                    if (detailGoalArrayList[i]["detail_goal_name"] == str_detail_goal && detailGoalArrayList[i]["lock_date"] == date1[0]) {
                        isFlag = true
                        break
                    }
                    i++
                }
                if (!isFlag) {
                    detailGoalArrayList.add(
                        mutableMapOf(
                            "detail_goal_name" to str_detail_goal,
                            "lock_date" to date1[0],
                            "icon" to int_icon.toString(),
                            "color" to int_color.toString(),
                            "big_goal_name" to str_big_goal
                        )
                    )
                }
            }
        }
        cursor3.close()
        dbManager.close()
        sqlite.close()

        // 위젯에 값 적용하기(날짜, 총 수행 시간) - 오늘기준
        if (reportSate == 0) { // 오늘
            dailyReportListLayout.removeAllViews()
            dayReport(nowTime)
        }

        // 달력 버튼 클릭 이벤트
        moveTodayButton.setOnClickListener {
            // 현재 날짜의 리포트를 보여주기
            dailyReportListLayout.removeAllViews()
            reportSate = 0
            nowViewTime = nowTime   // 오늘 날짜로 재설정
            dayReport(nowViewTime)
        }

        // 이전 버튼 클릭 이벤트
        prevBtn1.setOnClickListener {
            // 이전 날짜의 리포트 보여주기
            dailyReportListLayout.removeAllViews()
            reportSate--    // 페이지 상태 감소(이전 페이지로 이동함)
            nowViewTime = nowViewTime.minusDays(1)  // 하루 빼기
            dayReport(nowViewTime) // 현재 상태에 맞춰서 날짜 전달
        }

        // 다음 버튼 클릭 이벤트
        nextBtn1.setOnClickListener {
            // 현재 리포트를 보고 있다면
            if (reportSate == 0) {
                Toast.makeText(context, "현재 화면이 가장 최신 리포트 화면입니다.", Toast.LENGTH_SHORT).show()
            } else { // 다음 날짜의 리포트 보여주기
                dailyReportListLayout.removeAllViews()
                reportSate++    // 페이지 상태 증가(다음 페이지로 이동함)
                nowViewTime = nowViewTime.plusDays(1) // 하루 더하기
                dayReport(nowViewTime)
            }
        }

        // 일간 버튼 클릭 이벤트
        dailyBtn.setOnClickListener {
            goDailyReport()
        }

        // 주간 버튼 클릭 이벤트
        weeklyBtn.setOnClickListener {
            goWeeklyReport()
        }

        return view
    }

    // DailyReportFragment로 화면 전환
    fun goDailyReport() {
        mainActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_main, DailyReportFragment())
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.commit()
    }

    // WeeklyReportFragmnet로 화면 전환
    fun goWeeklyReport() {
        mainActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_main, WeeklyReportFragment())
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.commit()
    }

    // MonthlyReportfragment로 화면을 전환하는 함수
    fun goMonthlyReport() {
        mainActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_main, MonthlyReportFragment())
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.commit()
    }

    // 파이차트 세팅 함수
    fun dailyPieChart(moveDate: String) {

        dailyPieChart.setUsePercentValues(true) // 100%범위로 계산

        // 데이터(시간, 대표목표) 입력
        val entry = ArrayList<PieEntry>()
        for (i in 0 until bigGoalArrayList.size) {
            if (bigGoalArrayList[i]["lock_date"] == moveDate)
                entry.add(PieEntry(bigGoalArrayList[i]["total_lock_time"]!!.toFloat(), ""))
        }

        // 아이템 범위별 색상
        val itemcolor = ArrayList<Int>()
        for (i in 0 until bigGoalArrayList.size) {
            if (bigGoalArrayList[i]["lock_date"] == moveDate)
                itemcolor.add(bigGoalArrayList[i]["color"]!!.toInt())
        }

        val pieDataSet = PieDataSet(entry, "")
        pieDataSet.apply {
            colors = itemcolor
            valueTextColor = R.color.Black
            valueTextSize = 16f
        }

        val pieData = PieData(pieDataSet)
        dailyPieChart.apply {
            data = pieData
            description.isEnabled = false // 그래프이름 띄우기X
            isRotationEnabled = false // 애니메이션 효과X
            legend.isEnabled = false // x-value값 안보이게
            setTouchEnabled(false) // 차트 클릭X
            setEntryLabelColor(R.color.Black) // 차트 내 글씨 색깔
            animateY(1400, Easing.EaseInOutQuad) // 최초 애니메이션O
            animate()
        }
    }

    // 날짜에 따른 리포트
    fun dayReport(moveTime: ZonedDateTime) {
        var moveDate = moveTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E")) // 년도, 월, 일, 요일
        val splitDate = moveDate.split('-') // 년도, 월, 일, 요일
        dailyTextview.text = splitDate[1] + "월 " + splitDate[2] + "일 " + splitDate[3] + "요일"

        var totalMilli: BigInteger = BigInteger.ZERO
        if(isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size) {
                if (bigGoalArrayList[i]["lock_date"] == moveDate) { // 해당 날짜값의 시간만 가져와서 적용
                    totalMilli += bigGoalArrayList[i]["total_lock_time"]!!.toBigInteger()
                }
            }
        }
        var integer_hour: Long = (totalMilli.toLong() / (1000 * 60 * 60)) % 24
        var integer_min: Long = (totalMilli.toLong() / (1000 * 60)) % 60
        dailyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"

        // 파이차트 세팅
        var isPieFlag = false
        if(isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size) { // 해당 날짜와 관련한 값이 1개라도 있다면 파이차트 생성
                if (bigGoalArrayList[i]["lock_date"] == moveDate) {
                    dailyPieChart(moveDate)
                    dailyPieChart.visibility = View.VISIBLE
                    noGoalTimeView.visibility = View.INVISIBLE
                    isPieFlag = true
                    break
                }
            }
        }
        if (!isPieFlag) { // 일치하는 날짜 값이 없다면 파이차트 숨기기
            dailyPieChart.visibility = View.INVISIBLE
            noGoalTimeView.visibility = View.VISIBLE
        }

        // 동적 뷰를 활용한 세부목표 리스트 만들기
        if (isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size){
                // 동적 뷰 생성
                var view: View = layoutInflater.inflate(R.layout.container_goal_daily_big_goal, dailyReportListLayout, false)

                // 아이콘과 세부목표 동적 객체 생성
                var bigGoalColor: ImageView = view.findViewById(R.id.container_bigGoalColor)
                var bigGoalTitleTextView: TextView = view.findViewById(R.id.container_bigGaolTextView)
                var bigGoalTimeTextView: TextView = view.findViewById(R.id.container_bigGoalTime)


                // 값 할당하기
                if (bigGoalArrayList[i]["lock_date"] == moveDate) { // 해당 날짜의 값이라면
                    bigGoalColor.setColorFilter(bigGoalArrayList[i]["color"]!!.toInt(), PorterDuff.Mode.SRC_IN)
                    bigGoalTitleTextView.text = bigGoalArrayList[i]["big_goal_name"]

                    var totalMilli = bigGoalArrayList[i]["total_lock_time"]
                    var integer_hour: Int = ((totalMilli!!.toLong() / (1000 * 60 * 60)) % 24).toInt()
                    var integer_min: Int = ((totalMilli!!.toLong() / (1000 * 60)) % 60).toInt()
                    if (integer_hour == 0 && integer_min == 0){
                        var integer_sec: Int = (totalMilli!!.toLong() / (1000)%60).toInt()
                        bigGoalTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분 " + integer_sec+"초"
                    } else {
                        bigGoalTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"
                    }

                    // 레이아웃에 객체 추가
                    dailyReportListLayout.addView(view)

                    //아래에 세부 목표 붙이기
                    if(isDetailGoalInitialized){
                        for (j in 0 until detailGoalArrayList.size) {
                            // 동적 뷰 생성
                            var view: View = layoutInflater.inflate(R.layout.container_goal_daily_detail_goal, dailyReportListLayout, false)

                            // 아이콘과 세부목표 동적 객체 생성
                            var dailyIconImg: ImageView = view.findViewById(R.id.container_reportDaily_detailGoalIconImageView)
                            var dailyDetailTextview: TextView = view.findViewById(R.id.container_reportDaily_detailGoalTextView)

                            // 값 할당하기
                            if (detailGoalArrayList[j]["big_goal_name"] == bigGoalArrayList[i]["big_goal_name"]
                                && detailGoalArrayList[j]["lock_date"] == moveDate) { // 해당 날짜의 값, 해당 대표 목표 산하 세부 목표라면
                                dailyIconImg.setImageResource(detailGoalArrayList[j]["icon"]!!.toInt())
                                dailyIconImg.setColorFilter(detailGoalArrayList[j]["color"]!!.toInt(), PorterDuff.Mode.SRC_IN)
                                dailyDetailTextview.text = detailGoalArrayList[j]["detail_goal_name"]

                                // 레이아웃에 객체 추가
                                dailyReportListLayout.addView(view)
                            }
                        }
                    }
                }
            }
        }



    }
}