package com.example.guru_hemjee

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

// MainActivity -> 홈 리포트
// 홈 화면에서 스와이프를 통해 접근할 수 있는 홈 리포트 Fragment 화면
// 해당 날짜를 기준으로 하나의 일간, 주간 분석 리포트를 보여준다.
class HomeReportFragment : Fragment() {

    // db
    private lateinit var dbManager: DBManager
    private lateinit var sqlite: SQLiteDatabase

    // 일간
    private lateinit var homeReport_dayDateTextView: TextView // 날짜
    private lateinit var homeReport_todayTimeTextView: TextView // 총 잠금시간
    private lateinit var homeReport_totalDailyBarChart: HorizontalBarChart // 총 잠금 시간 가로 막대 그래프
    private lateinit var homeReport_dailyGoalListLayout: LinearLayout // 대표목표 리스트가 들어갈 레이아웃

    // 주간
    private lateinit var homeReport_periodDayTextView: TextView // 날짜(기간)
    private lateinit var homeReport_weeklyTimeTextView: TextView // 총 잠금시간
    private lateinit var homeReport_weeklyBarChart: BarChart  // 차트
    private lateinit var homeReport_weeklyGoalListLayout: LinearLayout // 대표목표 리스트가 들어갈 레이아웃

    // 오늘 날짜 & 현재 설정된 날짜
    private lateinit var todayDate: LocalDateTime // 오늘 날짜(전체)
    private lateinit var nowDate: LocalDateTime  // 현재 설정된 날짜

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_home_report, container, false)
    }

    override fun onStart() {
        super.onStart()

        // 오늘 날짜 불러오기
        todayDate = LocalDateTime.now()

        // 일간
        homeReport_dayDateTextView = requireView().findViewById(R.id.homeReport_dayDateTextView)
        homeReport_todayTimeTextView = requireView().findViewById(R.id.homeReport_todayTimeTextView)
        homeReport_totalDailyBarChart = requireView().findViewById(R.id.homeReport_totalDailyBarChart)
        homeReport_dailyGoalListLayout = requireView().findViewById(R.id.homeReport_dailyGoalListLayout)

        // 주간
        homeReport_periodDayTextView = requireView().findViewById(R.id.homeReport_periodDayTextView)
        homeReport_weeklyTimeTextView = requireView().findViewById(R.id.homeReport_weeklyTimeTextView)
        homeReport_weeklyBarChart = requireView().findViewById(R.id.homeReport_weeklyBarChart)
        homeReport_weeklyGoalListLayout = requireView().findViewById(R.id.homeReport_weeklyGoalListLayout)

        // 레이이웃 초기화
        homeReport_dailyGoalListLayout.removeAllViews()
        homeReport_weeklyGoalListLayout.removeAllViews()

        // 현재 날짜를 오늘 날짜로 설정
        nowDate = todayDate

        /** 일간 세팅 **/

        // 일간 리포트에 들어갈 데이터 찾고 리스트로 반환
        var (nowDateBigGoalList, nowDateBigGoalTimeList, nowDateBigGoalColorList) = findDailyReportData(nowDate)

        // 일간 리포트에 들어갈 데이터로 동적 뷰 생성
        createDailyReport(nowDateBigGoalList, nowDateBigGoalTimeList, nowDateBigGoalColorList)

        /** 주간 세팅 **/

        // 대표목표 리포트 db에 저장된 값 읽어오기(대표목표 값, 대표목표 총 수행 시간, 잠금 날짜)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db", null)

        lateinit var bigGoalArrayList: ArrayList<MutableMap<String, String>>    // 2차원 배열(대표목표)
        var isBigGoalInitialised = false
        // 모든 값들 배열에 저장(같은 날짜 내에 중복값 저장X)
        while (cursor.moveToNext()) {
            val str_big_goal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            val bigint_time = cursor.getInt(cursor.getColumnIndex("total_lock_time")).toBigInteger()
            val str_date = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
            val int_color = cursor.getInt(cursor.getColumnIndex("color")).toBigInteger()

            // 배열에 읽어온 값 저장
            var isFlag: Boolean = false // 중복값 확인
            var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
            if (!isBigGoalInitialised) { // 처음 입력시 초기화
                bigGoalArrayList = arrayListOf(
                    mutableMapOf(
                        "big_goal_name" to str_big_goal,
                        "total_lock_time" to bigint_time.toString(),
                        "lock_date" to date1[0],
                        "color" to int_color.toString()
                    )
                )
                isBigGoalInitialised = true

            } else { // 같은 날 같은 목표를 달성했다면 시간만 추가하고 새로 추가 X
                var i = 0
                while (i < bigGoalArrayList.size) {
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
        dbManager.close()
        sqlite.close()

        if(!isBigGoalInitialised){
            Toast.makeText(context, "수행한 기록이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        weeklyReport(ZonedDateTime.now(), bigGoalArrayList, isBigGoalInitialised)
    }

    // 스택바 차트 세팅 함수
    private fun setWeeklyStackBarChart(weekList: ArrayList<String>, bigGoalArrayList: ArrayList<MutableMap<String, String>>) {
        homeReport_weeklyBarChart.invalidate()

        val entry = ArrayList<BarEntry>()
        val itemColor = ArrayList<Int>()

        var bigGoalNameList = ArrayList<String>()
        var bigGoalTimeLists = ArrayList<MutableList<Float>>()

        //사용가능한 대표 목표 이름 뽑기
        for (i in 0 until bigGoalArrayList.size) {
            for (j in 0 until weekList.size){
                if (bigGoalArrayList[i]["lock_date"] == weekList[j]) {
                    if (!bigGoalNameList.contains(bigGoalArrayList[i]["big_goal_name"])) {
                        bigGoalNameList.add(bigGoalArrayList[i]["big_goal_name"]!!.toString())
                        itemColor.add(bigGoalArrayList[i]["color"]!!.toInt())
                    }
                }
            }
        }


        for (i in 0 until weekList.size){
            var tempArrayList = MutableList(bigGoalNameList.size, {0.0f})
            for (nameNum in 0 until bigGoalNameList.size) {
                for (goalNum in 0 until bigGoalArrayList.size) {
                    if (bigGoalArrayList[goalNum]["lock_date"] == weekList[i] && bigGoalArrayList[goalNum]["big_goal_name"]
                            == bigGoalNameList[nameNum]){
                        tempArrayList[nameNum] += bigGoalArrayList[goalNum]["total_lock_time"]!!.toFloat()/(1000*60*60)
                        if (tempArrayList[nameNum] != 0f) {
                            tempArrayList[nameNum] = round(tempArrayList[nameNum]*100) /100
                        } else {
                            tempArrayList[nameNum] = round(tempArrayList[nameNum]*10000) /10000
                        }
                    }
                }
            }
            bigGoalTimeLists.add(tempArrayList)
        }

        for (i in 0 until bigGoalTimeLists.size) {
            var tempList:Array<Float> = bigGoalTimeLists[i].toTypedArray()
            var isZero = false
            for (i in 0 until tempList.size) {
                if(tempList[i]!=0.0f) {
                    isZero = true
                    break
                }
            }
            entry.add(BarEntry(i.toFloat(), floatArrayOf(tempList)))
        }

        val barDataSet = BarDataSet(entry, "")
        barDataSet.apply {
            if(itemColor.size >= 1)
                colors= itemColor
            else
                color = itemColor[0]
                valueTextColor = R.color.Black
                valueTextSize = 16f
        }

        val barData = BarData(barDataSet)
        homeReport_weeklyBarChart.apply {
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
        val weekXLables = arrayListOf<String>("월","화","수","목","금","토","일")
            homeReport_weeklyBarChart.xAxis.apply { // 아래 라벨 x축
            isEnabled = true // 라벨 표시
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false) // 격자구조X
            valueFormatter = IndexAxisValueFormatter(weekXLables)
        }
        homeReport_weeklyBarChart.axisLeft.apply { // 왼쪽 y축
            isEnabled = true // 라벨 표시X
            setDrawLabels(true) // 값 세팅X
            textColor = R.color.Black
            textSize = 14f
            axisMinimum = 0.0f
        }
        homeReport_weeklyBarChart.axisRight.apply { // 오른쪽 y축
            isEnabled = false // 라벨 표시X
            textColor = R.color.Black
            textSize = 14f
        }
    }

    // 날짜에 따른 리포트
    private fun weeklyReport(moveTime: ZonedDateTime, bigGoalArrayList: ArrayList<MutableMap<String, String>>,
                     isBigGoalInitialised: Boolean) { // 지난 주 값
        homeReport_weeklyGoalListLayout.removeAllViews() // 초기화

        var weekList: ArrayList<String> = getWeekDate(moveTime)
        var monday = weekList[0].split('-') // 월요일
        var sunday = weekList[6].split('-') // 일요일
        homeReport_periodDayTextView.text = monday[1] + "월 " + monday[2] + "일 - " + sunday[1] + "월 " + sunday[2] + "일"

        var totalMilli: BigInteger = BigInteger.ZERO // 총 전체 잠금 시간

        if (isBigGoalInitialised) {
            for (i in 0 until bigGoalArrayList.size) {
                for (j in 0 until weekList.size) {
                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) // 잠금 날짜가 같다면 총 시간 저장
                        totalMilli += bigGoalArrayList[i]["total_lock_time"]!!.toBigInteger()
                }
            }
        }

        var integer_hour: Int = ((totalMilli.toLong() / (1000 * 60 * 60)) % 24).toInt()
        var integer_min: Int = ((totalMilli.toLong() / (1000 * 60)) % 60).toInt()
        if (integer_hour == 0 && integer_min == 0) {
            var integer_sec: Int = (totalMilli.toLong() / (1000)%60).toInt()
            homeReport_weeklyTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분 " + integer_sec+"초"
        } else {
            homeReport_weeklyTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"
        }

        // 스택바 차트 세팅
        var isBarFlag = false
        if (isBigGoalInitialised) {
            for (i in 0 until bigGoalArrayList.size) {
                for (j in 0 until weekList.size) {
                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) { // 같은 잠금 날짜가 1개라도 있다면 차트 띄우기
                        setWeeklyStackBarChart(weekList, bigGoalArrayList)
                        homeReport_weeklyBarChart.visibility = View.VISIBLE
                        isBarFlag = true
                        break
                    }
                }
            }
        }
        if (!isBarFlag) { // 일치하는 날짜 값이 없다면 스택바 차트 숨기기
            homeReport_weeklyBarChart.visibility = View.INVISIBLE
        }

        // 동적 뷰를 활용한 대표목표 및 세부목표 리스트 만들기
        var bigGoalName = ArrayList<String>()
        var bigGoalTime = ArrayList<Long>()
        var bigGoalColor = ArrayList<Int>()

        if (isBigGoalInitialised) {
            for (i in 0 until bigGoalArrayList.size) { //detailGoalArray사용
                for (j in 0 until weekList.size) {
                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) {
                        if (!bigGoalName.contains(bigGoalArrayList[i]["big_goal_name"])) {
                            bigGoalName.add(bigGoalArrayList[i]["big_goal_name"].toString())
                            bigGoalColor.add(bigGoalArrayList[i]["color"]!!.toInt())
                            bigGoalTime.add(bigGoalArrayList[i]["total_lock_time"]!!.toLong())
                        } else {
                            var index = bigGoalName.indexOf(bigGoalArrayList[i]["big_goal_name"])
                            bigGoalTime[index] = bigGoalTime[index] + bigGoalArrayList[i]["total_lock_time"]!!.toLong()
                        }
                    }
                }
            }
        }


        for (i in 0 until bigGoalName.size) {
            // 동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.container_big_goal_report_text, homeReport_weeklyGoalListLayout, false)

            // 대표목표 동적 객체 생성
            var bigGoalColorImg: ImageView = view.findViewById(R.id.bigGoalColorImg)
            var bigGoalTextview: TextView = view.findViewById(R.id.bigGoalTextview)
            var biglGoalTimeview: TextView = view.findViewById(R.id.biglGoalTimeview)

            // 값 할당하기
            bigGoalColorImg.setImageResource(R.drawable.ic_colorselectionicon)
            bigGoalColorImg.setColorFilter(bigGoalColor[i], PorterDuff.Mode.SRC_IN)
            bigGoalTextview.text = bigGoalName[i]
            var hour: Int = ((bigGoalTime[i] / (1000 * 60 * 60)) % 24).toInt()
            var min: Int = ((bigGoalTime[i] / (1000 * 60)) % 60).toInt()
            if (hour == 0 && min == 0) {
                var sec: Int = ((bigGoalTime[i] / (1000)) % 60).toInt()
                biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분 " + sec.toString() + "초"
            } else {
                biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분"
            }

            // 레이아웃에 객체 추가
            homeReport_weeklyGoalListLayout.addView(view)
        }
    }

    // 입력받은 날짜 근처의 월요일~일요일(7일) 구하기
    private fun getWeekDate(moveTime: ZonedDateTime): ArrayList<String> {
        val calendar = Calendar.getInstance()
        val nowDate = SimpleDateFormat("yyyy-MM-dd-E") // 현재 년도, 월, 일
        var totalMoveTime = moveTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E"))
        var date = nowDate.parse(totalMoveTime) // ZonedDateTime -> Date

        calendar.time = date // Date

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

    // Array를 FloatArray로 변환하는 함수
    private fun floatArrayOf(elements: Array<Float>): FloatArray {
        var temp: FloatArray = FloatArray(elements.size, {0.0f})
        for(i in 0 until elements.size){
            temp[i] = elements[i]
        }

        return temp
    }

    // 일간 리포트에 들어갈 데이터를 리스트에 저장하고 반환하는 함수
    private fun findDailyReportData(nowDate: LocalDateTime): Triple<ArrayList<String>, ArrayList<BigInteger>, ArrayList<Int>> {

        // 일간 위젯에 오늘 날짜 반영
        var nowDateArray = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E"))
        var nowDateSplit = nowDateArray.split('-') // 년도, 월, 일, 요일
        homeReport_dayDateTextView.text = nowDateSplit[1] + "월 " + nowDateSplit[2] + "일 " + nowDateSplit[3] + "요일"

        // DB 열기
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var nowDateBigGoalList = ArrayList<String>()            // 오늘 수행한 대표목표를 저장하는 리스트(중복값 저장X)
        var nowDateBigGoalTimeList = ArrayList<BigInteger>()    // 오늘 수행한 대표목표의 각 잠금 시간을 저장하는 리스트
        var nowDateBigGoalColorList = ArrayList<Int>()          // 오늘 수행한 대표목표의 각 색상을 저장하는 리스트

        // 대표 목표 리포트 DB 읽기
        var cursor: Cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db", null)
        while (cursor.moveToNext()) {

            var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
            var str_big_goal: String = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            var bigint_lock_time: BigInteger = cursor.getInt(cursor.getColumnIndex("total_lock_time")).toBigInteger()
            var int_color: Int = cursor.getInt(cursor.getColumnIndex("color"))

            // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
            var isFlag: Boolean = false

            // 날짜[0], 시간[1] 분리
            var tempDate: String = temp1.split(' ')[0]

            // 오늘 수행한 대표목표와 시간을 ArrayList에 추가하기
            if (tempDate == nowDateArray) { // 오늘 날짜
                for (i in 0 until nowDateBigGoalList.size) {

                    // 대표 목표와 색상이 중복된다면
                    if (nowDateBigGoalList[i] == str_big_goal && nowDateBigGoalColorList[i] == int_color) {
                        nowDateBigGoalTimeList[i] += bigint_lock_time
                        isFlag = true
                        break
                    }
                }
                if (!isFlag) { // 목표만 중복되거나 중복값이 없다면 대표목표, 시간, 색상 저장
                    nowDateBigGoalList.add(str_big_goal)
                    nowDateBigGoalTimeList.add(bigint_lock_time)
                    nowDateBigGoalColorList.add(int_color)
                }
            }
        }

        cursor.close()
        sqlite.close()
        dbManager.close()

        // 오늘 수행한 대표목표와 시간을 저장한 리스트 반환
        return Triple(nowDateBigGoalList, nowDateBigGoalTimeList, nowDateBigGoalColorList)
    }

    // 일간 리포트에 들어갈 데이터를 활용하여 리스트 만들기
    private fun createDailyReport(nowDateBigGoalList: ArrayList<String>, nowDateBigGoalTimeList: ArrayList<BigInteger>,
                          nowDateBigGoalColorList: ArrayList<Int>) {

        // 일간 리포트 뷰 클리어
        homeReport_dailyGoalListLayout.removeAllViews()

        // 총 일간 잠금시간 구하기
        var totalMilli: BigInteger = BigInteger.ZERO
        for (i in 0 until nowDateBigGoalTimeList.size) {
            totalMilli += nowDateBigGoalTimeList[i] }

        var long_hour: Long = (totalMilli.toLong() / (1000 * 60 * 60)) % 24
        var long_min: Long = (totalMilli.toLong() / (1000 * 60)) % 60
        var long_sec: Long = (totalMilli.toLong()) / 1000 % 60

        homeReport_todayTimeTextView.text = long_hour.toString() + "시간 " + long_min.toString() + "분 " + long_sec.toString() + "초"

        // 리스트 개수만큼 동적 뷰 생성
        for (i in 0 until nowDateBigGoalList.size) {
            var view2: View = layoutInflater.inflate(R.layout.container_daily_home_report, homeReport_dailyGoalListLayout, false)

            // 동적 객체 생성 (색상 이미지, 대표목표, 막대 그래프)
            var bigGoalColorImg: ImageView = view2.findViewById(R.id.bigGoalColorImg)
            var bigGoalTextview: TextView = view2.findViewById(R.id.bigGoalTextview)
            var dailyBarChart: HorizontalBarChart = view2.findViewById(R.id.dailyBarChart)
            var biglGoalTimeview: TextView = view2.findViewById(R.id.biglGoalTimeview)

            // 값 할당하기
            bigGoalColorImg.setImageResource(R.drawable.ic_colorselectionicon)
            bigGoalColorImg.setColorFilter(nowDateBigGoalColorList[i])
            bigGoalTextview.text = nowDateBigGoalList[i]
            var long_hour: Long = (nowDateBigGoalTimeList[i].toLong() / (1000 * 60 * 60)) % 24
            var long_min: Long = (nowDateBigGoalTimeList[i].toLong() / (1000 * 60)) % 60
            var long_sec: Long = (nowDateBigGoalTimeList[i].toLong()) / 1000 % 60
            if (long_hour == 0L && long_min == 0L) {
                biglGoalTimeview.text = long_sec.toString() + "초"
            } else if (long_hour == 0L) {
                biglGoalTimeview.text = long_min.toString() + "분"
            } else {
                biglGoalTimeview.text = long_hour.toString() + "시간 " + long_min.toString() + "분"
            }

            var milliTime: BigInteger = nowDateBigGoalTimeList[i] // 대표목표의 총 잠금시간(=밀리초)
            var goalColor: Int = nowDateBigGoalColorList[i]            // 대표목표의 색상

            var temp1DailyBarChart: HorizontalBarChart = oneBarChartApperance(dailyBarChart) // 그래프 기본 레이아웃 설정
            oneBarChartDate(temp1DailyBarChart, milliTime, totalMilli, goalColor)

            // 일간리포트의 총 잠금시간 가로 막대 그래프
            var temp2DailyBarChart: HorizontalBarChart = totalBarChartApperance(homeReport_totalDailyBarChart) // 그래프 기본 레이아웃 설정
            totalBarChartDate(temp2DailyBarChart, nowDateBigGoalTimeList, totalMilli, nowDateBigGoalColorList)

            // 레이아웃에 객체 추가
            homeReport_dailyGoalListLayout.addView(view2)
        }
    }

    // 일간 리포트의 개별 BarChart의 레이아웃 & 데이터 세팅
    private fun oneBarChartApperance(dailyBarChart: HorizontalBarChart): HorizontalBarChart {

        dailyBarChart.description.isEnabled = false // 그래프 이름 띄우기X
        dailyBarChart.setTouchEnabled(false)        // 터치X
        dailyBarChart.legend.isEnabled = false      // 차트 범례 표시X

        dailyBarChart.xAxis.apply { // 수평막대 기준 왼쪽
            setDrawAxisLine(false)  // 선X
            setDrawLabels(false)    // 라벨X
            gridLineWidth = 5f
            gridColor = Color.parseColor("#80E5E5E5")   // 배경색
        }

        dailyBarChart.axisLeft.apply {  // 수평막대 기준 아래쪽
            setDrawGridLines(false)     // 선X
            setDrawAxisLine(false)
            axisMinimum = 0f            // 최솟값
            axisMaximum = 100f          // 최댓값
            granularity = 1f            // 값만큼 라인선 설정
            setDrawLabels(false)        // 값 세팅X
        }

        dailyBarChart.axisRight.apply { // 수평막대 기준 위쪽
            textSize = 15f              // 텍스트 크기
            setDrawLabels(false)        // 라벨X
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        return dailyBarChart
    }

    // 일간 리포트의 개별 BarChart의 데이터 세팅
    private fun oneBarChartDate(temp1DailyBarChart: HorizontalBarChart, milliTime: BigInteger,
                                totalMilli: BigInteger, goalColor: Int) {
        // 총시간에서의 백분율 구하기(밀리초)
        var timeData = (milliTime.toDouble() / totalMilli.toDouble() * 100.0)

        // BarChart에 표시될 데이터
        val entry = ArrayList<BarEntry>()
        entry.add(BarEntry(0f, timeData.toFloat()))

        // 데이터를 막대모양으로 표시하기
        val barDataSet = BarDataSet(entry, "")
        barDataSet.apply {
            barDataSet.setDrawIcons(false)
            color = goalColor           // 아이템 색상
            setDrawValues(true)         // %값 보이기
            valueTextColor = R.color.Black
            valueFormatter
            valueTextSize = 14f
        }

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        temp1DailyBarChart.data = barData
        temp1DailyBarChart.invalidate() // 차트 갱신
    }

    // 일간 리포트의 총잠금시간 BarChart의 레이아웃 & 데이터 세팅
    private fun totalBarChartApperance(dailyBarChart: HorizontalBarChart): HorizontalBarChart {

        dailyBarChart.description.isEnabled = false // 그래프 이름 띄우기X
        dailyBarChart.setTouchEnabled(false)        // 터치X
        dailyBarChart.legend.isEnabled = false      // 차트 범례 표시X

        dailyBarChart.xAxis.apply { // 수평막대 기준 왼쪽
            setDrawAxisLine(false)  // 선X
            setDrawLabels(false)    // 라벨X
            setDrawGridLines(false)
        }

        dailyBarChart.axisLeft.apply {  // 수평막대 기준 아래쪽
            setDrawGridLines(false)     // 선X
            axisMinimum = 0f            // 최솟값
            axisMaximum = 100f          // 최댓값
            setDrawLabels(false)        // 값 세팅X
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        dailyBarChart.axisRight.apply { // 수평막대 기준 위쪽
            setDrawAxisLine(false)  // 선X
            setDrawLabels(false)        // 값 세팅X
            setDrawGridLines(false)
        }

        return dailyBarChart
    }

    private fun floatArrayOf(elements: ArrayList<Float>): FloatArray {
        var temp: FloatArray = FloatArray(elements.size, {0.0f})
        for(i in 0 until elements.size){
            temp[i] = elements[i]
        }

        return temp
    }

    // 일간리포트의 총 잠금시간 가로 막대 그래프 데이터 세팅
    private fun totalBarChartDate(temp2DailyBarChart: HorizontalBarChart, nowDateBigGoalTimeList: ArrayList<BigInteger>,
                          totalMilli: BigInteger, nowDateBigGoalColorList: ArrayList<Int>) {
        // 총시간에서의 백분율 구하기(밀리초)
        var timeArray = ArrayList<Float>()
        for (i in 0 until nowDateBigGoalTimeList.size) {
            timeArray.add((nowDateBigGoalTimeList[i].toDouble() / totalMilli.toDouble() * 100.0).toFloat())
        }

        // BarChart에 표시될 데이터
        val entry = ArrayList<BarEntry>()
        entry.add(BarEntry(0f, floatArrayOf(timeArray)))

        // 아이템 범위별 색상
        val itemcolor = java.util.ArrayList<Int>()
        for (i in 0 until nowDateBigGoalColorList.size) {
            itemcolor.add(nowDateBigGoalColorList[i])
        }

        // 데이터를 막대모양으로 표시하기
        val barDataSet = BarDataSet(entry, "")
        barDataSet.apply {
            barDataSet.setDrawIcons(false)
            colors = itemcolor           // 아이템 색상
            setDrawValues(false)         // %값 안보이기
            valueTextColor = R.color.Black
            valueFormatter
            valueTextSize = 14f
        }

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        temp2DailyBarChart.data = barData
        temp2DailyBarChart.invalidate() // 차트 갱신
    }
}