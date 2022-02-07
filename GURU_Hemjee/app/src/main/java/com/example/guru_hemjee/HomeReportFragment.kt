package com.example.guru_hemjee

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
    lateinit var dbManager: DBManager
    lateinit var sqlite: SQLiteDatabase

    // 일간
    lateinit var dayTextView: TextView // 날짜
    lateinit var todayTimeTextView: TextView // 총 잠금시간
    lateinit var totalDailyBarChart: HorizontalBarChart // 총 잠금 시간 가로 막대 그래프
    lateinit var dailyGoalListLayout: LinearLayout // 대표목표 리스트가 들어갈 레이아웃

    // 주간
    lateinit var periodWeekTextView: TextView // 날짜(기간)
    lateinit var weekTimeTextView: TextView // 총 잠금시간
    lateinit var weeklyStackBarChart: BarChart  // 차트
    lateinit var weeklyGoalListLayout: LinearLayout // 대표목표 리스트가 들어갈 레이아웃

//    // 월간
//    lateinit var monthDayTextView: TextView // 날짜
//    lateinit var monthTimeTextView: TextView // 총 잠금 시간
//    lateinit var monthBarChart: BarChart // 총 잠금 시간 세로 막대 그래프
//    lateinit var monthGoalListLayout: LinearLayout // 대표목표 리스트가 들어갈 레이아웃

    // 오늘 날짜 & 현재 설정된 날짜
    lateinit var todayDate: LocalDateTime // 오늘 날짜(전체)
    lateinit var nowDate : LocalDateTime  // 현재 설정된 날짜

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
        dayTextView = requireView().findViewById(R.id.homeReport_dayDateTextView)
        todayTimeTextView = requireView().findViewById(R.id.homeReport_todayTimeTextView)
        totalDailyBarChart = requireView().findViewById(R.id.homeReport_totalDailyBarChart)
        dailyGoalListLayout = requireView().findViewById(R.id.homeReport_dailyGoalListLayout)

        // 주간
        periodWeekTextView = requireView().findViewById(R.id.homeReport_periodDayTextView)
        weekTimeTextView = requireView().findViewById(R.id.homeReport_weeklyTimeTextView)
        weeklyStackBarChart = requireView().findViewById(R.id.homeReport_weeklyBarChart)
        weeklyGoalListLayout = requireView().findViewById(R.id.homeReport_weeklyGoalListLayout)

//        // 월간
//        monthDayTextView = requireView().findViewById(R.id.monthDayTextView)
//        monthTimeTextView = requireView().findViewById(R.id.monthTimeTextView)
//        monthBarChart = requireView().findViewById(R.id.monthBarChart)
//        monthGoalListLayout = requireView().findViewById(R.id.monthGoalListLayout)

        // 레이이웃 초기화
        dailyGoalListLayout.removeAllViews()
        weeklyGoalListLayout.removeAllViews()
//        monthGoalListLayout.removeAllViews()

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
            if (!isBigGoalInitialised) { //처음 입력시 초기화
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
//        // 월간 리포트 함수
//        createMonthlyReport(nowDate)
    }

    // 스택바 차트 세팅 함수
    fun setWeeklyStackBarChart(weekList: ArrayList<String>, bigGoalArrayList: ArrayList<MutableMap<String, String>>) {
        weeklyStackBarChart.invalidate()

        val entry = ArrayList<BarEntry>()
        val itemColor = ArrayList<Int>()

        var bigGoalNameList = ArrayList<String>()
        var bigGoalTimeLists = ArrayList<MutableList<Float>>()

        //사용가능한 대표 목표 이름 뽑기
        for(i in 0 until bigGoalArrayList.size){
            for(j in 0 until weekList.size){
                if(bigGoalArrayList[i]["lock_date"] == weekList[j]){
                    if(!bigGoalNameList.contains(bigGoalArrayList[i]["big_goal_name"])){
                        bigGoalNameList.add(bigGoalArrayList[i]["big_goal_name"]!!.toString())
                        itemColor.add(bigGoalArrayList[i]["color"]!!.toInt())
                    }
                }
            }
        }


        for (i in 0 until weekList.size){
            var tempArrayList = MutableList(bigGoalNameList.size, {0.0f})
            for (nameNum in 0 until bigGoalNameList.size){
                for (goalNum in 0 until bigGoalArrayList.size){
                    if (bigGoalArrayList[goalNum]["lock_date"] == weekList[i] && bigGoalArrayList[goalNum]["big_goal_name"] == bigGoalNameList[nameNum]){
                        tempArrayList[nameNum] += bigGoalArrayList[goalNum]["total_lock_time"]!!.toFloat()/(1000*60*60)
                        if (tempArrayList[nameNum] != 0f){
                            tempArrayList[nameNum] = round(tempArrayList[nameNum]*100) /100
                        } else {
                            tempArrayList[nameNum] = round(tempArrayList[nameNum]*10000) /10000
                        }
                    }
                }
            }
            bigGoalTimeLists.add(tempArrayList)
        }

        for(i in 0 until bigGoalTimeLists.size){
            var tempList:Array<Float> = bigGoalTimeLists[i].toTypedArray()
            var isZero = false
            for(i in 0 until tempList.size){
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
        Log.i("Data", "${barDataSet.colors}")

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
        val weekXLables = arrayListOf<String>("월","화","수","목","금","토","일")
        weeklyStackBarChart.xAxis.apply { // 아래 라벨 x축
            isEnabled = true // 라벨 표시
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false) // 격자구조X
            valueFormatter = IndexAxisValueFormatter(weekXLables)
        }
        weeklyStackBarChart.axisLeft.apply { // 왼쪽 y축
            isEnabled = true // 라벨 표시X
            setDrawLabels(true) // 값 세팅X
            textColor = R.color.Black
            textSize = 14f
            axisMinimum = 0.0f
        }
        weeklyStackBarChart.axisRight.apply { // 오른쪽 y축
            isEnabled = false // 라벨 표시X
            textColor = R.color.Black
            textSize = 14f
            // axisMaximum = 0f // 최소값
            // axisMaximum = 18f // 최대값
        }
    }

    // 날짜에 따른 리포트
    fun weeklyReport(moveTime: ZonedDateTime, bigGoalArrayList: ArrayList<MutableMap<String, String>>, isBigGoalInitialised: Boolean) { // 지난 주 값
        weeklyGoalListLayout.removeAllViews() // 초기화

        var weekList: ArrayList<String> = getWeekDate(moveTime)
        var monday = weekList[0].split('-') // 월요일
        var sunday = weekList[6].split('-') // 일요일
        periodWeekTextView.text = monday[1] + "월 " + monday[2] + "일 - " + sunday[1] + "월 " + sunday[2] + "일"

        var totalMilli: BigInteger = BigInteger.ZERO // 총 전체 잠금 시간

        if(isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size) {
                for (j in 0 until weekList.size) {
                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) // 잠금 날짜가 같다면 총 시간 저장
                        totalMilli += bigGoalArrayList[i]["total_lock_time"]!!.toBigInteger()
                }
            }
        }

        var integer_hour: Int = ((totalMilli.toLong() / (1000 * 60 * 60)) % 24).toInt()
        var integer_min: Int = ((totalMilli.toLong() / (1000 * 60)) % 60).toInt()
        if (integer_hour == 0 && integer_min == 0){
            var integer_sec: Int = (totalMilli.toLong() / (1000)%60).toInt()
            weekTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분 " + integer_sec+"초"
        } else {
            weekTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"
        }

        // 스택바 차트 세팅
        var isBarFlag = false
        if(isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size) {
                for (j in 0 until weekList.size) {
                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) { // 같은 잠금 날짜가 1개라도 있다면 차트 띄우기
                        setWeeklyStackBarChart(weekList, bigGoalArrayList)
                        weeklyStackBarChart.visibility = View.VISIBLE
                        isBarFlag = true
                        break
                    }
                }
            }
        }
        if (!isBarFlag) { // 일치하는 날짜 값이 없다면 스택바 차트 숨기기
            weeklyStackBarChart.visibility = View.INVISIBLE
        }

        // 동적 뷰를 활용한 대표목표 및 세부목표 리스트 만들기
        var bigGoalName = ArrayList<String>()
        var bigGoalTime = ArrayList<Long>()
        var bigGoalColor = ArrayList<Int>()

        if(isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size) { //detailGoalArray사용
                for(j in 0 until weekList.size){
                    if(bigGoalArrayList[i]["lock_date"] == weekList[j]){
                        if(!bigGoalName.contains(bigGoalArrayList[i]["big_goal_name"])){
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


        for(i in 0 until bigGoalName.size){
            // 동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.container_big_goal_report_text, weeklyGoalListLayout, false)

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
            if(hour == 0 && min == 0){
                var sec: Int = ((bigGoalTime[i] / (1000)) % 60).toInt()
                biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분 " + sec.toString() + "초"
            } else {
                biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분"
            }

            // 레이아웃에 객체 추가
            weeklyGoalListLayout.addView(view)
        }
    }

    // 입력받은 날짜 근처의 월요일~일요일(7일) 구하기
    fun getWeekDate(moveTime: ZonedDateTime): ArrayList<String> {
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
    fun findDailyReportData(nowDate: LocalDateTime): Triple<ArrayList<String>, ArrayList<BigInteger>, ArrayList<Int>> {

        // 일간 위젯에 오늘 날짜 반영
        var nowDateArray = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E"))
        var nowDateSplit = nowDateArray.split('-') // 년도, 월, 일, 요일
        dayTextView.text = nowDateSplit[1] + "월 " + nowDateSplit[2] + "일 " + nowDateSplit[3] + "요일"

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
    fun createDailyReport(nowDateBigGoalList: ArrayList<String>, nowDateBigGoalTimeList: ArrayList<BigInteger>, nowDateBigGoalColorList: ArrayList<Int>) {

        // 일간 리포트 뷰 클리어
        dailyGoalListLayout.removeAllViews()

        // 총 일간 잠금시간 구하기
        var totalMilli: BigInteger = BigInteger.ZERO
        for (i in 0 until nowDateBigGoalTimeList.size) {
            totalMilli += nowDateBigGoalTimeList[i] }

        var long_hour: Long = (totalMilli.toLong() / (1000 * 60 * 60)) % 24
        var long_min: Long = (totalMilli.toLong() / (1000 * 60)) % 60
        var long_sec: Long = (totalMilli.toLong()) / 1000 % 60

        todayTimeTextView.text = long_hour.toString() + "시간 " + long_min.toString() + "분 " + long_sec.toString() + "초"

        // 리스트 개수만큼 동적 뷰 생성
        for (i in 0 until nowDateBigGoalList.size) {
            var view2: View = layoutInflater.inflate(R.layout.container_daily_home_report, dailyGoalListLayout, false)

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
            var temp2DailyBarChart: HorizontalBarChart = totalBarChartApperance(totalDailyBarChart) // 그래프 기본 레이아웃 설정
            totalBarChartDate(temp2DailyBarChart, nowDateBigGoalTimeList, totalMilli, nowDateBigGoalColorList)

            // 레이아웃에 객체 추가
            dailyGoalListLayout.addView(view2)
        }
    }

    // 일간 리포트의 개별 BarChart의 레이아웃 & 데이터 세팅
    fun oneBarChartApperance(dailyBarChart: HorizontalBarChart): HorizontalBarChart {

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
    fun oneBarChartDate(temp1DailyBarChart: HorizontalBarChart, milliTime: BigInteger, totalMilli: BigInteger, goalColor: Int) {
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
    fun totalBarChartApperance(dailyBarChart: HorizontalBarChart): HorizontalBarChart {

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
    fun totalBarChartDate(temp2DailyBarChart: HorizontalBarChart, nowDateBigGoalTimeList: ArrayList<BigInteger>,
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

//    fun createMonthlyReport(nowDate: LocalDateTime) {
//
//        /** 월간 리포트에 들어갈 데이터를 리스트에 저장하기 **/
//        // 월간 위젯에 년도와 월 반영
//        var nowDateArray = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E"))
//        var nowDateSplit = nowDateArray.split('-') // 년도, 월, 일, 요일
//        monthDayTextView.text = nowDateSplit[0] + "년 " + nowDateSplit[1] + "월"
//
//        // 이번달의 1일부터 마지막날까지의 날짜를 리스트에 저장
//        // (리스트에 저장되는 값의 형태 : yyyy-MM-dd-E)
//        var sdf = SimpleDateFormat("yyyy-MM-dd-E")
//        val calendar = Calendar.getInstance()
//        var date = sdf.parse(nowDateArray) // String -> Date
//
//        calendar.time = date    // 현재 시간으로 캘린더 설정
//
//        calendar.add(Calendar.DATE, -Calendar.DATE) // 현재 날짜로부터 현재날짜를 뺀 값
//
//        var monthlyList = ArrayList<String>() // 이번달의 1일~말일을 저장 ex) 2022-02-06-일
//        var first = calendar.getMinimum(Calendar.DAY_OF_MONTH)  // 1일
//        var last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // 마지막날
//
//        for (i in first..last) {
//            val date = calendar.time
//            val day = sdf.format(date)
//
//            monthlyList.add(day)
//            calendar.add(Calendar.DAY_OF_MONTH, 1)
//        }
//
//        // DB 열기
//        dbManager = DBManager(context, "hamster_db", null, 1)
//        sqlite = dbManager.readableDatabase
//
//        var monthlyDateBigGoalList = ArrayList<String>()            // 이번달에 수행한 대표목표를 저장하는 리스트(같은 날짜내에 중복값X)
//        var monthlyDateBigGoalTimeList = ArrayList<BigInteger>()    // 이번달에 수행한 대표목표의 각 잠금 시간을 저장하는 리스트
//        var monthlyDateBigGoalColorList = ArrayList<Int>()          // 이번달에 수행한 대표목표의 각 색상을 저장하는 리스트
//        var monthlyLockDateBigGoalList = ArrayList<String>()          // 이번달에 수행하 대표목표의 잠금 날짜를 저장하는 리스트
//
//        // 대표 목표 리포트 DB 읽기
//        var cursor: Cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db", null)
//        while (cursor.moveToNext()) {
//
//            var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
//            var str_big_goal: String = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
//            var bigint_lock_time: BigInteger = cursor.getInt(cursor.getColumnIndex("total_lock_time")).toBigInteger()
//            var int_color: Int = cursor.getInt(cursor.getColumnIndex("color"))
//
//            // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
//            var isFlag: Boolean = false
//
//            // 날짜[0], 시간[1] 분리
//            var tempDate: String = temp1.split(' ')[0]
//
//            // 이번달에 수행한 대표목표와 시간을 ArrayList에 추가
//            for (i in 0 until monthlyList.size) {
//                if (tempDate == monthlyList[i]) {
//                    for (j in 0 until monthlyDateBigGoalList.size) {
//
//                        // 중복값이 있다면 시간만 저장
//                        if (monthlyDateBigGoalList[j] == str_big_goal) {
//                            monthlyDateBigGoalTimeList[j] += bigint_lock_time
//                            isFlag = true
//                            break
//                        }
//                    }
//                    if (!isFlag) {// 중복값이 없다면 대표목표, 시간, 색상, 잠금 날짜 저장
//                        monthlyDateBigGoalList.add(str_big_goal)
//                        monthlyDateBigGoalTimeList.add(bigint_lock_time)
//                        monthlyDateBigGoalColorList.add(int_color)
//                        monthlyLockDateBigGoalList.add(temp1)
//                    }
//                }
//            }
//        }
//
//        cursor.close()
//        sqlite.close()
//        dbManager.close()
//
//        /** 월간 리포트에 들어갈 데이터를 활용하여 리스트 만들기 **/
//
//        // 월간 리포트 뷰 클리어
//        monthGoalListLayout.removeAllViews()
//
//        // 총 월간 잠금 시간 구하기
//        var totalMilli: BigInteger = BigInteger.ZERO
//        for (i in 0 until monthlyDateBigGoalList.size) {
//            totalMilli += monthlyDateBigGoalTimeList[i] }
//
//        var long_hour: Long = (totalMilli.toLong() / (1000 * 60 * 60)) % 24
//        var long_min: Long = (totalMilli.toLong() / (1000 * 60)) % 60
//
//        monthTimeTextView.text = long_hour.toString() + "시간 " + long_min.toString() + "분 "
//
//        // 7일 단위로 주간 리스트 만들기
//        var timeWeekList1 = ArrayList<BigInteger>()     // 1주차 시간
//        var colorWeekList1 = ArrayList<Int>()           // 1주차 색상
//        var timeWeekList2 = ArrayList<BigInteger>()     // 2주차 시간
//        var colorWeekList2 = ArrayList<Int>()           // 2주차 색상
//        var timeWeekList3 = ArrayList<BigInteger>()     // 3주차 시간
//        var colorWeekList3 = ArrayList<Int>()           // 3주차 색상
//        var timeWeekList4 = ArrayList<BigInteger>()     // 4주차 시간
//        var colorWeekList4 = ArrayList<Int>()           // 4주차 색상
//        var timeWeekList5 = ArrayList<BigInteger>()     // 5주차 시간
//        var colorWeekList5 = ArrayList<Int>()           // 5주차 색상
//
//        // 1주차
//        // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
//        for (i in 0 until monthlyDateBigGoalList.size) {
//            var isFlag: Boolean = false
//            for (k in 0 until monthlyDateBigGoalList.size) {
//                for (j in 0 until 6) {
//
//                    // 잠금 날짜와 대표목표가 같다면 시간만 저장
//                    if (monthlyLockDateBigGoalList[i] == monthlyList[j]
//                            && monthlyDateBigGoalList[i] == monthlyDateBigGoalList[j]) {
//                        timeWeekList1[i] += monthlyDateBigGoalTimeList[i]
//                        isFlag = true
//                        break
//                    }
//                }
//                if (!isFlag) { // 중복값이 없다면 시간, 색상 저장
//                    timeWeekList1.add(monthlyDateBigGoalTimeList[i])
//                    colorWeekList1.add(monthlyDateBigGoalColorList[i])
//                }
//            }
//        }
//
//        // 2주차
//        // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
//        for (i in 0 until monthlyDateBigGoalList.size) {
//            var isFlag: Boolean = false
//            for (k in 0 until monthlyDateBigGoalList.size) {
//                for (j in 7 until 13) {
//
//                    // 잠금 날짜와 대표목표가 같다면 시간만 저장
//                    if (monthlyLockDateBigGoalList[i] == monthlyList[j]
//                            && monthlyDateBigGoalList[i] == monthlyDateBigGoalList[j]) {
//                        timeWeekList2[i] += monthlyDateBigGoalTimeList[i]
//                        isFlag = true
//                        break
//                    }
//                }
//                if (!isFlag) { // 중복값이 없다면 시간, 색상 저장
//                    timeWeekList2.add(monthlyDateBigGoalTimeList[i])
//                    colorWeekList2.add(monthlyDateBigGoalColorList[i])
//                }
//            }
//        }
//
//        // 3주차
//        // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
//        for (i in 0 until monthlyDateBigGoalList.size) {
//            var isFlag: Boolean = false
//            for (k in 0 until monthlyDateBigGoalList.size) {
//                for (j in 14 until 20) {
//
//                    // 잠금 날짜와 대표목표가 같다면 시간만 저장
//                    if (monthlyLockDateBigGoalList[i] == monthlyList[j]
//                            && monthlyDateBigGoalList[i] == monthlyDateBigGoalList[j]) {
//                        timeWeekList3[i] += monthlyDateBigGoalTimeList[i]
//                        isFlag = true
//                        break
//                    }
//                }
//                if (!isFlag) { // 중복값이 없다면 시간, 색상 저장
//                    timeWeekList3.add(monthlyDateBigGoalTimeList[i])
//                    colorWeekList3.add(monthlyDateBigGoalColorList[i])
//                }
//            }
//        }
//
//        // 4주차
//        // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
//        for (i in 0 until monthlyDateBigGoalList.size) {
//            var isFlag: Boolean = false
//            for (k in 0 until monthlyDateBigGoalList.size) {
//                for (j in 21 until 27) {
//
//                    // 잠금 날짜와 대표목표가 같다면 시간만 저장
//                    if (monthlyLockDateBigGoalList[i] == monthlyList[j]
//                            && monthlyDateBigGoalList[i] == monthlyDateBigGoalList[j]) {
//                        timeWeekList4[i] += monthlyDateBigGoalTimeList[i]
//                        isFlag = true
//                        break
//                    }
//                }
//                if (!isFlag) { // 중복값이 없다면 시간, 색상 저장
//                    timeWeekList4.add(monthlyDateBigGoalTimeList[i])
//                    colorWeekList4.add(monthlyDateBigGoalColorList[i])
//                }
//            }
//        }
//
//        // 5주차
//        // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
//        for (i in 0 until monthlyDateBigGoalList.size) {
//            var isFlag: Boolean = false
//            for (k in 0 until monthlyDateBigGoalList.size) {
//                for (j in 29 until monthlyList.size) {
//
//                    // 잠금 날짜와 대표목표가 같다면 시간만 저장
//                    if (monthlyLockDateBigGoalList[i] == monthlyList[j]
//                            && monthlyDateBigGoalList[i] == monthlyDateBigGoalList[j]) {
//                        timeWeekList5[i] += monthlyDateBigGoalTimeList[i]
//                        isFlag = true
//                        break
//                    }
//                }
//                if (!isFlag) { // 중복값이 없다면 시간, 색상 저장
//                    timeWeekList5.add(monthlyDateBigGoalTimeList[i])
//                    colorWeekList5.add(monthlyDateBigGoalColorList[i])
//                }
//            }
//        }
//
//        /** 리스트 개수만큼 동적 뷰 생성 **/
//        for (i in 0 until monthlyDateBigGoalList.size) {
//            var view2: View = layoutInflater.inflate(R.layout.layout_big_goal_report_text, monthGoalListLayout, false)
//
//            // 동적 객체 생성 (색상 이미지, 대표목표, 막대 그래프)
//            var bigGoalColorImg: ImageView = view2.findViewById(R.id.bigGoalColorImg)
//            var bigGoalTextview: TextView = view2.findViewById(R.id.bigGoalTextview)
//            var biglGoalTimeview: TextView = view2.findViewById(R.id.biglGoalTimeview)
//            var bigGoalPercentview: TextView = view2.findViewById(R.id.bigGoalPercentview)
//
//            // 값 할당하기
//            bigGoalColorImg.setImageResource(R.drawable.ic_colorselectionicon)
//            bigGoalColorImg.setColorFilter(monthlyDateBigGoalColorList[i])
//            bigGoalTextview.text = monthlyDateBigGoalList[i]
//            var long_hour: Long = (monthlyDateBigGoalTimeList[i].toLong() / (1000 * 60 * 60)) % 24
//            var long_min: Long = (monthlyDateBigGoalTimeList[i].toLong() / (1000 * 60)) % 60
//            var long_sec: Long = (monthlyDateBigGoalTimeList[i].toLong()) / 1000 % 60
//            if (long_hour == 0L && long_min == 0L) {
//                biglGoalTimeview.text = long_sec.toString() + "초"
//            } else if (long_hour == 0L) {
//                biglGoalTimeview.text = long_min.toString() + "분"
//            } else {
//                biglGoalTimeview.text = long_hour.toString() + "시간 " + long_min.toString() + "분"
//            }
//
//            // 레이아웃에 객체 추가
//            monthGoalListLayout.addView(view2)
//        }
//
//        /** 월간 차트 **/
//        var monthlyBarChart = monthBarChartApperance(monthBarChart) // 월간 리포트 레이아웃 설정
//        // 총시간에서의 백분율 구하기(밀리초)
//        // 1주차
//        var timeArray1 = ArrayList<Float>()
//        for (i in 0 until timeWeekList1.size) {
//            timeArray1.add((timeWeekList1[i].toDouble() / totalMilli.toDouble() * 100.0).toFloat())
//        }
//        // 2주차
//        var timeArray2 = ArrayList<Float>()
//        for (i in 0 until timeWeekList2.size) {
//            timeArray1.add((timeWeekList2[i].toDouble() / totalMilli.toDouble() * 100.0).toFloat())
//        }
//        // 3주차
//        var timeArray3 = ArrayList<Float>()
//        for (i in 0 until timeWeekList3.size) {
//            timeArray3.add((timeWeekList3[i].toDouble() / totalMilli.toDouble() * 100.0).toFloat())
//        }
//        // 4주차
//        var timeArray4 = ArrayList<Float>()
//        for (i in 0 until timeWeekList4.size) {
//            timeArray4.add((timeWeekList4[i].toDouble() / totalMilli.toDouble() * 100.0).toFloat())
//        }
//        // 5주차
//        var timeArray5 = ArrayList<Float>()
//        for (i in 0 until timeWeekList5.size) {
//            timeArray4.add((timeWeekList5[i].toDouble() / totalMilli.toDouble() * 100.0).toFloat())
//        }
//
//        // BarChart에 표시될 데이터
//        // 1주차
//        val entry = ArrayList<BarEntry>()
//        entry.add(BarEntry(0f, floatArrayOf(timeArray1)))
//        entry.add(BarEntry(1f, floatArrayOf(timeArray2)))
//        entry.add(BarEntry(2f, floatArrayOf(timeArray3)))
//        entry.add(BarEntry(3f, floatArrayOf(timeArray4)))
//        entry.add(BarEntry(4f, floatArrayOf(timeArray5)))
//        /*for (i in 0 until timeArray1.size) {
//            entry.add(BarEntry(0f, timeArray1[i].toFloat()))
//        }
//        // 2주차
//        for (i in 0 until timeArray2.size) {
//            entry.add(BarEntry(1f, timeArray2[i].toFloat()))
//        }
//        // 3주차
//        for (i in 0 until timeArray3.size) {
//            entry.add(BarEntry(2f, timeArray3[i].toFloat()))
//        }
//        // 4주차
//        for (i in 0 until timeArray4.size) {
//            entry.add(BarEntry(3f, timeArray4[i].toFloat()))
//        }
//        // 5주차
//        for (i in 0 until timeArray5.size) {
//            entry.add(BarEntry(4f, timeArray5[i].toFloat()))
//        }*/
//
//        // 아이템 범위별 색상
//        val itemcolor = ArrayList<Int>()
//        for (i in 0 until colorWeekList1.size) {
//            itemcolor.add(colorWeekList1[i])
//        }
//        /*for (i in 0 until colorWeekList2.size) {
//            itemcolor.add(colorWeekList2[i])
//        }
//        for (i in 0 until colorWeekList3.size) {
//            itemcolor.add(colorWeekList3[i])
//        }
//        for (i in 0 until colorWeekList4.size) {
//            itemcolor.add(colorWeekList4[i])
//        }
//        for (i in 0 until colorWeekList5.size) {
//            itemcolor.add(colorWeekList5[i])
//        }*/
//
//        // 데이터를 막대모양으로 표시하기
//        val barDataSet = BarDataSet(entry, "")
//        barDataSet.apply {
//            barDataSet.setDrawIcons(false)
//            colors = itemcolor           // 아이템 색상
//            setDrawValues(false)         // %값 안보이기
//            valueTextColor = R.color.Black
//            valueFormatter
//            valueTextSize = 14f
//        }
//
//        val barData = BarData(barDataSet)
//        barData.barWidth = 0.5f
//        monthlyBarChart.data = barData
//        monthlyBarChart.invalidate() // 차트 갱신
//    }

//    // 월간 리포트의 총잠금시간 BarChart의 레이아웃 & 데이터 세팅
//    fun monthBarChartApperance(monthBarChart: BarChart): BarChart {
//
//        monthBarChart.description.isEnabled = false // 그래프 이름 띄우기X
//        monthBarChart.setTouchEnabled(false)        // 터치X
//        monthBarChart.legend.isEnabled = false      // 차트 범례 표시X
//
//        monthBarChart.xAxis.apply { // 수평막대 기준 왼쪽
//            setDrawAxisLine(false)  // 선X
//            setDrawLabels(false)    // 라벨X
//            setDrawGridLines(false)
//        }
//
//        monthBarChart.axisLeft.apply {  // 수평막대 기준 아래쪽
//            setDrawGridLines(false)     // 선X
//            axisMinimum = 0f            // 최솟값
//            axisMaximum = 50f          // 최댓값
//            setDrawLabels(false)        // 값 세팅X
//            setDrawGridLines(false)
//            setDrawAxisLine(false)
//        }
//
//        monthBarChart.axisRight.apply { // 수평막대 기준 위쪽
//            setDrawAxisLine(false)      // 선X
//            setDrawLabels(false)        // 값 세팅X
//            setDrawGridLines(false)
//        }
//
//        return monthBarChart
//    }


}