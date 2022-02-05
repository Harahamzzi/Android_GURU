package com.example.guru_hemjee

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeReportFragment : Fragment() {

    // db
    lateinit var dbManager: DBManager
    lateinit var sqlite: SQLiteDatabase

    // 일간
    lateinit var dayTextView: TextView // 날짜
    lateinit var todayTimeTextView: TextView // 총 잠금시간
    lateinit var totalDailyBarChart: BarChart // 총 잠금 시간 가로 막대 그래프
    lateinit var dailyGoalListLayout: LinearLayout // 대표목표 리스트가 들어갈 레이아웃

    // 주간

    // 월간

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
        todayDate = todayDate.plusHours(9) // 한국 시간

        // 일간
        dayTextView = requireView().findViewById(R.id.dayTextView)
        todayTimeTextView = requireView().findViewById(R.id.todayTimeTextView)
        totalDailyBarChart = requireView().findViewById(R.id.totalDailyBarChart)
        dailyGoalListLayout = requireView().findViewById(R.id.dailyGoalListLayout)

        // 레이이웃 초기화
        dailyGoalListLayout.removeAllViews()

        // 현재 날짜를 오늘 날짜로 설정
        nowDate = todayDate

        // 일간 리포트에 들어갈 데이터 찾고 리스트로 반환
        var (nowDateBigGoalList, nowDateBigGoalTimeList, nowDateBigGoalColorList) = findDailyReportData(nowDate)

        // 일간 리포트에 들어갈 데이터로 동적 뷰 생성
        createDailyReport(nowDateBigGoalList, nowDateBigGoalTimeList, nowDateBigGoalColorList)
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
        var nowDateBigGoalColorList = ArrayList<Int>()          // 오늘 수행하 대표목표의 각 색상을 저장하는 리스트

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

                    // 중복값이 있다면 시간만 저장
                    if (nowDateBigGoalList[i] == str_big_goal) {
                        nowDateBigGoalTimeList[i] += bigint_lock_time
                        isFlag = true
                        break
                    }
                }
                if (!isFlag) { // 중복값이 없다면 대표목표, 시간 저장
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
            var view2: View = layoutInflater.inflate(R.layout.layout_daily_home_report, dailyGoalListLayout, false)

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


            oneBarChartApperance(dailyBarChart, milliTime, totalMilli, goalColor) // 그래프 기본 레이아웃 설정
            // var temp1DailyBarChart: HorizontalBarChart = oneBarChartApperance(dailyBarChart) // 그래프 기본 레이아웃 설정
            // oneBarChartDate(temp1DailyBarChart, milliTime, totalMilli, goalColor)

            // 레이아웃에 객체 추가
            dailyGoalListLayout.addView(view2)
        }
    }

    // 일간 리포트의 개별 BarChart의 레이아웃 & 데이터 세팅
    fun oneBarChartApperance(dailyBarChart: HorizontalBarChart, milliTime: BigInteger, totalMilli: BigInteger, goalColor: Int) {

        dailyBarChart.description.isEnabled = false // 그래프 이름 띄우기X
        dailyBarChart.setTouchEnabled(false)        // 터치X
        dailyBarChart.legend.isEnabled = false      // 차트 범례 표시X

        dailyBarChart.xAxis.apply { // 수평막대 기준 왼쪽
            setDrawAxisLine(false)  // 선X
            setDrawLabels(false)    // 라벨X
            gridLineWidth = 25f
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
        dailyBarChart.data = barData
        dailyBarChart.invalidate() // 차트 갱신
    }

    //// 일간 리포트의 개별 BarChart의 데이터 세팅
    //fun oneBarChartDate(temp1DailyBarChart: HorizontalBarChart, milliTime: BigInteger, totalMilli: BigInteger, goalColor: Int) {
    //
    //}
}