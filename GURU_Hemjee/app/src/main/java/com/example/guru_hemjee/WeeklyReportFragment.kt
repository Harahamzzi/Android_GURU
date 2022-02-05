package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentTransaction
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.LabelFormatter
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WeeklyReportFragment : Fragment() {

    // db
    lateinit var dbManager: DBManager
    lateinit var sqlite: SQLiteDatabase

    // 일간, 주간, 월간
    lateinit var dailyBtn2: AppCompatButton
    lateinit var weeklyBtn2: AppCompatButton
    lateinit var monthlyBtn2: AppCompatButton

    // 최신 주간 리포트 화면으로 이동하는 달력 버튼
    lateinit var moveWeeklyButton: ImageButton

    // 날짜 & 시간
    lateinit var weeklyTextview: TextView
    lateinit var weeklyTimeTextview: TextView

    // 이전 & 다음 버튼
    lateinit var prevBtn2: ImageButton
    lateinit var nextBtn2: ImageButton

    // 스택바 차트
    lateinit var weeklyStackBarChart: BarChart

    // 대표목표 선택 버튼
    lateinit var selectBigGoalBtn: MaterialButton

    // 대표목표&세부목표 리스트를 저장할 리니어 레이아웃
    lateinit var weeklyReportListLayout: LinearLayout

    // 텍스트뷰
    lateinit var noGoalTimeView2: TextView

    // 현재 날짜
    var nowTime = ZonedDateTime.now((ZoneId.of("Asia/Seoul")))

    // 2차원 배열(대표목표)
    var bigGoalStringArray = Array(20, {Array(2, {""}) }) // 10행 2열, 하나의 행에 (대표목표,날짜) 순으로 저장
    var bigGoalIntArray = Array(20, {Array(2, { BigInteger.ZERO}) }) // 10행 2열, 하나의 행에 (시간, 색상) 순으로 저장
    var num = 0 // bigGoalStringArray와 bigGoalIntArray의 index

    // 2차원 배열(세부목표)
    var detailGoalStringArray = Array(30, {Array(3, {""}) }) // 20행 3열, 하나의 행에 (세부목표,날짜,대표목표) 순으로 저장
    var detailGoalIntArray = Array(30, {Array(2, { BigInteger.ZERO}) }) // 20행 2열, 하나의 행에 (아이콘,색상) 순으로 저장
    var num2 = 0 // detailGoalStringArray와 detailGoalIntArray의 index

    // 현재 리포트 화면 상태
    var reportSate: Int = 0 // 이번주
    var toggleState: Boolean = false // 대표목표 선택 버튼의 클릭 여부(false=전체 선택, true=대표목표 선택)
    lateinit var toggleGoal: String // 선택한 대표목표

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as SubMainActivity
    }

    override fun onDetach() {
        super.onDetach()

        mainActivity = null
    }

    var mainActivity : SubMainActivity? = null // 서브 메인 액티비티 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_weekly_report, container, false)

        dailyBtn2 = view.findViewById(R.id.dailyBtn2)
        weeklyBtn2 = view.findViewById(R.id.weeklyBtn2)
        monthlyBtn2 = view.findViewById(R.id.monthlyBtn2)
        moveWeeklyButton = view.findViewById(R.id.moveWeeklyButton)
        weeklyTextview = view.findViewById(R.id.weeklyTextview)
        weeklyTimeTextview = view.findViewById(R.id.weeklyTimeTextview)
        prevBtn2 = view.findViewById(R.id.prevBtn2)
        nextBtn2 = view.findViewById(R.id.nextBtn2)
        weeklyStackBarChart = view.findViewById(R.id.weeklyStackBarChart)
        selectBigGoalBtn = view.findViewById(R.id.selectBigGoalBtn)
        weeklyReportListLayout = view.findViewById(R.id.weeklyReportListLayout)
        noGoalTimeView2 = view.findViewById(R.id.noGoalTimeView2)

        // 화면에 접속할 때마다 항상 레이아웃 초기화
        weeklyReportListLayout.removeAllViews()

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
            if (bigGoalStringArray.isNullOrEmpty()) { // 처음 저장하는 거라면
                bigGoalStringArray[num][0] = str_big_goal // 대표목표
                bigGoalIntArray[num][0] = bigint_time // 대표목표 총 수행 시간
                bigGoalStringArray[num][1] = date1[0] // 년도-월-일-요일 형태로 저장
                bigGoalIntArray[num][1] = int_color // 대표목표 색상
                ++num
            } else { // 기존에 데이터가 있다면
                for (i in 0 until num) {
                    if (bigGoalStringArray[i][0] == str_big_goal && bigGoalStringArray[i][1] == date1[0]) { // 중복되는 값은 시간만 저장(같은 날짜의 대표목표)
                        bigGoalIntArray[i][0] += bigint_time
                        isFlag = true
                        break
                    }
                }
                if (!isFlag) { // 중복값이 없었다면, 새로운 값 저장
                    bigGoalStringArray[num][0] = str_big_goal // 대표목표
                    bigGoalIntArray[num][0] = bigint_time // 대표목표 총 수행 시간
                    bigGoalStringArray[num][1] = date1[0] // 년도-월-일-요일 형태로 저장
                    bigGoalIntArray[num][1] = int_color // 대표목표 색상
                    ++num
                }
            }
        }
        cursor.close()

        // 세부목표 리포트 db에서 저장된 값 읽어오기(세부목표, 잠금 날짜, 아이콘, 색상)
        var cursor3: Cursor
        cursor3 = sqlite.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

        while (cursor3.moveToNext()) {
            var str_detail_goal = cursor3.getString(cursor3.getColumnIndex("detail_goal_name"))
            var str_date = cursor3.getString(cursor3.getColumnIndex("lock_date")).toString()
            var int_icon = cursor3.getInt(cursor3.getColumnIndex("icon")).toBigInteger()
            var int_color = cursor3.getInt(cursor3.getColumnIndex("color")).toBigInteger()

            // 배열에 읽어온 값 저장 (같은 날짜 내에 중복값 저장X)
            var isFlag: Boolean = false // 중복값 확인
            var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
            if (detailGoalStringArray.isNullOrEmpty()) { // 처음 저장하는 거라면
                detailGoalStringArray[num2][0] = str_detail_goal // 세부목표
                detailGoalStringArray[num2][1] = date1[0] // 잠금 날짜(// 년도-월-일-요일 형태로 저장)
                detailGoalIntArray[num2][0] = int_icon // 아이콘
                detailGoalIntArray[num][1] = int_color // 색상
                ++num2
            } else { // 기존에 데이터가 있다면
                for (i in 0 until num2) {
                    if (detailGoalStringArray[i][0] == str_detail_goal && detailGoalStringArray[i][1] == date1[0]) {
                        isFlag = true
                        break
                    }
                }
                if (!isFlag) { // 중복값이 없었다면, 새로운 값 저장
                    detailGoalStringArray[num2][0] = str_detail_goal // 세부목표
                    detailGoalStringArray[num2][1] = date1[0] // 잠금 날짜(// 년도-월-일-요일 형태로 저장)
                    detailGoalIntArray[num2][0] = int_icon // 아이콘
                    detailGoalIntArray[num][1] = int_color // 색상
                    ++num2
                }
            }
        }
        cursor3.close()

        // 세부목표 db에서 저장된 값 읽어오기(대표목표)
        var cursor4: Cursor
        for (i in 0 until num2) {
            cursor4 = sqlite.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '" + detailGoalStringArray[i][0] + "';", null)

            while (cursor4.moveToNext()) {
                var str_big_goal_name = cursor4.getString(cursor4.getColumnIndex("big_goal_name"))

                // 배열에 읽어온 값 저장
                detailGoalStringArray[i][2] = str_big_goal_name // 대표목표
            }
            cursor4.close()
        }

        dbManager.close()
        sqlite.close()

        // 위젯에 값 적용하기(날짜, 총 수행 시간) - 오늘기준
        if (reportSate == 0) { // 지난주
            weeklyReportListLayout.removeAllViews()
            weeklyReport(nowTime) // 현재 날짜 넣기
        }

        // 달력 버튼 클릭 이벤트
        moveWeeklyButton.setOnClickListener {
            // 최신 리포트(지난주 리포트)를 보여주기
            weeklyReportListLayout.removeAllViews()
            reportSate = 0
            weeklyReport(nowTime)
        }

        // 이전 버튼 클릭 이벤트
        prevBtn2.setOnClickListener {
            // 이전 주간 리포트 보여주기
            weeklyReportListLayout.removeAllViews()
            reportSate += -7
            weeklyReport(nowTime.minusDays(Math.abs(reportSate).toLong())) // 일주일 뺀 값 전달
        }

        // 다음 버튼 클릭 이벤트
        nextBtn2.setOnClickListener {
            // 현재 리포트를 보고 있다면
            if (reportSate == 0) {
                Toast.makeText(context, "현재 화면이 가장 최신 리포트 화면입니다.", Toast.LENGTH_SHORT).show()
            } else { // 다음 주간의 리포트 보여주기
                weeklyReportListLayout.removeAllViews()
                reportSate += 7
                weeklyReport(nowTime.plusDays(Math.abs(reportSate).toLong())) // 일주일 더한 값 전달
            }
        }

        // 대표목표 토글 클릭 이벤트
        selectBigGoalBtn.setOnClickListener {
            reportSate = 0 // 가장 최신의 주간 리포트 띄우기

            // todo: 요약
            //     if) 전체 클릭
            //      -> toggleState = false
            //      -> 버튼의 색상이랑 글씨도 바꾸기
            //     else if) 대표목표를 1개라도 클릭
            //      -> toggleState = true
            //      -> toggleGoal = 선택한 대표목표를 할당
            //      -> 나중에 toggleGoal을 통해서 대표목표 값을 비교할 예정
            //      -> 버튼의 색상이랑 글씨도 바꾸기
            // if () {
            //     toggleState = false
            //     weeklyReport(nowTime)
            // } else if () {
            //     toggleState = true
            //     toggleStateIndex = 0
            //     weeklyReport(nowTime)
            //     toggleGoal = 선택한 대표목표 값
            //      ... 버튼의 색상, 글씨 바꾸기
            // } else if () {
            //
            // }
        }

        // 일간 버튼 클릭 이벤트
        dailyBtn2.setOnClickListener {
            goDailyReport()
        }

        // 주간 버튼 클릭 이벤트
        weeklyBtn2.setOnClickListener {
            goWeeklyReport()
        }

        // 일간 버튼 클릭 이벤트
        monthlyBtn2.setOnClickListener {
            goMonthlyReport()
        }

        return view
    }

    // DailyReportFragment로 화면 전환
    fun goDailyReport() {
        mainActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_main, DailyReportFragment())
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.addToBackStack(null)
            ?.commit()
    }

    // WeeklyReportFragmnet로 화면 전환
    fun goWeeklyReport() {
        mainActivity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_main, WeeklyReportFragment())
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.addToBackStack(null)
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

    // 입력받은 날짜 근처의 월요일~일요일(7일) 구하기
    fun getWeekDate(moveTime: ZonedDateTime): ArrayList<String> {
        val calendar = Calendar.getInstance()
        val nowDate = SimpleDateFormat("yyyy-MM-dd-E") // 현재 년도, 월, 일
        var totalMoveTime = moveTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E"))
        var date = nowDate.parse(totalMoveTime) // ZonedDateTime -> Date

        calendar.time = date // Date

        calendar.add(Calendar.DATE, -7) // moveTime으로부터 일주일전 날짜로 세팅

        // 지난주의 월요일부터 일요일까지의 날짜를 배열에 저장
        var weekList = ArrayList<String>()
        for (i in 2..8) {
            calendar.add(Calendar.DAY_OF_MONTH, (i-calendar.get(Calendar.DAY_OF_WEEK)))
            val lastDate = calendar.time
            val lastDay = nowDate.format(lastDate)
            weekList.add(lastDay)
        }
        return weekList
    }

    // 스택바 차트 세팅 함수
    fun weeklyStackBarChart(weekList: ArrayList<String>) {

        val entry = ArrayList<BarEntry>()
        val itemcolor = ArrayList<Int>()
        var floatArray = Array(30, {Array(7, {0f}) })

        if (toggleState) { // 대표 목표 선택 중이라면
            // 데이터(시간, 대표목표) 입력
            for (i in 0 until num) {
                for (j in 0 until weekList.size) {
                    if (bigGoalStringArray[i][1] == weekList[j] && bigGoalStringArray[i][0] == toggleGoal) {
                        var integer_hour : Long = (bigGoalIntArray[i][0].toLong() / (1000 * 60 * 60)) % 24
                        var integer_min : Long = (bigGoalIntArray[i][0].toLong() / (1000 * 60)) % 60
                        if (integer_hour.toInt() == 0) {
                            floatArray[i][j] = integer_min.toFloat() // 분 저장
                            Log.d("분 값 = ", floatArray[i][j].toString())
                        } else {
                            floatArray[i][j] = integer_hour.toFloat() // 시간 저장
                            Log.d("시간 값 = ", floatArray[i][j].toString())
                        }
                        itemcolor.add(bigGoalIntArray[i][1].toInt())
                    }
                }

                for (i in 0 until num) {
                    entry.add(BarEntry(0f, floatArray[i][0])) // 월요일
                    entry.add(BarEntry(1f, floatArray[i][1])) // 화요일
                    entry.add(BarEntry(2f, floatArray[i][2])) // 수요일
                    entry.add(BarEntry(3f, floatArray[i][3])) // 목요일
                    entry.add(BarEntry(4f, floatArray[i][4])) // 금요일
                    entry.add(BarEntry(5f, floatArray[i][5])) // 토요일
                    entry.add(BarEntry(6f, floatArray[i][6])) // 일요일
                }
            }
        } else { // 전체를 선택 중이라면
            // 데이터(시간, 대표목표) 입력, 아이템 범위별 색상
            for (i in 0 until num) {
                for (j in 0 until weekList.size) {
                    if (bigGoalStringArray[i][1] == weekList[j]) { // 해당 날짜에 잠금 기록이 있다면
                        var integer_hour : Int = (bigGoalIntArray[i][0].toInt() / (1000 * 60 * 60)) % 24
                        var integer_min : Int = (bigGoalIntArray[i][0].toInt() / (1000 * 60)) % 60
                        if (integer_hour.toInt() == 0) {
                            floatArray[i][j] = integer_min.toFloat() // 분 저장
                            Log.d("분 값 = ", floatArray[i][j].toString())
                        } else {
                            floatArray[i][j] = integer_hour.toFloat() // 시간 저장
                            Log.d("시간 값 = ", floatArray[i][j].toString())
                        }
                        itemcolor.add(bigGoalIntArray[i][1].toInt())
                    }
                }
            }

            for (i in 0 until num) {
                entry.add(BarEntry(0f, floatArray[i][0])) // 월요일
                entry.add(BarEntry(1f, floatArray[i][1])) // 화요일
                entry.add(BarEntry(2f, floatArray[i][2])) // 수요일
                entry.add(BarEntry(3f, floatArray[i][3])) // 목요일
                entry.add(BarEntry(4f, floatArray[i][4])) // 금요일
                entry.add(BarEntry(5f, floatArray[i][5])) // 토요일
                entry.add(BarEntry(6f, floatArray[i][6])) // 일요일
            }
        }

        val barDataSet = BarDataSet(entry, "")
        barDataSet.apply {
            colors = itemcolor
            valueTextColor = R.color.Black
            valueTextSize = 16f
        }

        val barData = BarData(barDataSet)
        weeklyStackBarChart.apply {
            data = barData
            description.isEnabled = false // 그래프 이름 띄우기X
            legend.isEnabled = false // x-value값 안보이게
            setTouchEnabled(false) // 차트 클릭X
            setDrawBarShadow(false) // 그림자X
            isDoubleTapToZoomEnabled = false
            setPinchZoom(false) // 줌 설정X
            setDrawGridBackground(false) // 격자구조X
            setMaxVisibleValueCount(7) // 그래프 최대 개수
            setDrawValueAboveBar(false) // 차트 입력값 아래로
        }
        weeklyStackBarChart.xAxis.apply { // 아래 라벨 x축
            isEnabled = false // 라벨 표시X
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(true) // 격자구조X
        }
        weeklyStackBarChart.axisLeft.apply { // 왼쪽 y축
            isEnabled = false // 라벨 표시X
            setDrawLabels(false) // 값 세팅X
        }
        weeklyStackBarChart.axisRight.apply { // 오른쪽 y축
            isEnabled = false // 라벨 표시X
            textColor = R.color.Black
            textSize = 14f
            // axisMaximum = 0f // 최소값
            // axisMaximum = 18f // 최대값
        }
    }

    // 날짜에 따른 리포트(선택지가 전체일 경우)
    fun weeklyReport(moveTime: ZonedDateTime) { // 지난 주 값

        weeklyReportListLayout.removeAllViews() // 초기화

        var weekList: ArrayList<String> = getWeekDate(moveTime)
        var monday = weekList[0].split('-') // 월요일
        var sunday = weekList[6].split('-') // 일요일
        weeklyTextview.text = monday[1] + "월 " + monday[2] + "일 - " + sunday[1] + "월 " + sunday[2] + "일"

        var totalMilli: BigInteger = BigInteger.ZERO // 총 전체 잠금 시간

        if (toggleState) { // 대표 목표를 1개 클릭
            for (i in 0 until num) {
                for (j in 0 until weekList.size) {
                    if (bigGoalStringArray[i][1] == weekList[j] && bigGoalStringArray[i][0] == toggleGoal) // 잠금 날짜와 대표목표가 같다면 총 시간 저장
                       totalMilli += bigGoalIntArray[i][0]
                }
            }
        } else { // 전체 선택
            for (i in 0 until num) {
                for (j in 0 until weekList.size) {
                    if (bigGoalStringArray[i][1] == weekList[j]) // 잠금 날짜가 같다면 총 시간 저장
                        totalMilli += bigGoalIntArray[i][0]
                }
            }
        }
        var integer_hour: Long = (totalMilli.toLong() / (1000 * 60 * 60)) % 24
        var integer_min: Long = (totalMilli.toLong() / (1000 * 60)) % 60
        weeklyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"

        // 스택바 차트 세팅
        var isBarFlag = false
        for (i in 0 until num) {
            for (j in 0 until weekList.size) {
                if (bigGoalStringArray[i][1] == weekList[j]) { // 같은 잠금 날짜가 1개라도 있다면 차트 띄우기
                    weeklyStackBarChart(weekList)
                    weeklyStackBarChart.visibility = View.VISIBLE
                    noGoalTimeView2.visibility = View.INVISIBLE
                    isBarFlag = true
                    break
                }
            }
        }
        if (!isBarFlag) { // 일치하는 날짜 값이 없다면 스택바 차트 숨기기
            weeklyStackBarChart.visibility = View.INVISIBLE
            noGoalTimeView2.visibility = View.VISIBLE
        }

        // 동적 뷰를 활용한 대표목표 및 세부목표 리스트 만들기
        if (toggleState) { // 대표목표를 선택했다면
            for (i in 0 until num2) { //detailGoalArray사용
                // 동적 뷰 생성
                var view: View = layoutInflater.inflate(R.layout.layout_detail_goal_report_text, weeklyReportListLayout, false)

                // 아이콘과 세부목표 동적 객체 생성
                var detailIconImg: ImageView = view.findViewById(R.id.detailIconImg)
                var detailGoalTextview: TextView = view.findViewById(R.id.detailGoalTextview)

                // 값 할당하기
                if (detailGoalStringArray[i][0].isNotBlank()) { // 아무값도 없거나 공백이 있는 경우가 아니라면
                    for (j in 0 until weekList.size) {
                        if (detailGoalStringArray[i][1] == weekList[j] && detailGoalStringArray[i][2] == toggleGoal) { // 해당 날짜의 값이고, 해당하는 대표목표라면
                            detailIconImg.setImageResource(detailGoalIntArray[i][0].toInt())
                            detailIconImg.setColorFilter(detailGoalIntArray[i][1].toInt(), PorterDuff.Mode.SRC_IN)
                            detailGoalTextview.text = detailGoalStringArray[i][0]

                            // 레이아웃에 객체 추가
                            weeklyReportListLayout.addView(view)
                        }
                    }
                }
            }
        } else { // 전체를 선택했다면
            for (i in 0 until num2) { //detailGoalArray사용
                // 동적 뷰 생성
                var view: View = layoutInflater.inflate(R.layout.layout_big_goal_report_text, weeklyReportListLayout, false)

                // 대표목표 동적 객체 생성
                var bigGoalColorImg: ImageView = view.findViewById(R.id.bigGoalColorImg)
                var bigGoalTextview: TextView = view.findViewById(R.id.bigGoalTextview)
                var biglGoalTimeview: TextView = view.findViewById(R.id.biglGoalTimeview)

                // 값 할당하기
                if (bigGoalStringArray[i][0].isNotBlank()) { // 아무값도 없거나 공백이 있는 경우가 아니라면
                    for (j in 0 until weekList.size) {
                        if (bigGoalStringArray[i][1] == weekList[j]) { // 해당 날짜의 값이라면
                            bigGoalColorImg.setImageResource(R.drawable.ic_colorselectionicon)
                            bigGoalColorImg.setColorFilter(bigGoalIntArray[i][1].toInt(), PorterDuff.Mode.SRC_IN)
                            bigGoalTextview.text = bigGoalStringArray[i][0]
                            var hour: Long = (bigGoalIntArray[i][0].toLong() / (1000 * 60 * 60)) % 24
                            var min: Long = (bigGoalIntArray[i][0].toLong() / (1000 * 60)) % 60
                            biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분"

                            // 레이아웃에 객체 추가
                            weeklyReportListLayout.addView(view)
                        }
                    }
                }
            }
        }
    }
}