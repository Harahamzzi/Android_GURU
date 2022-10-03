package com.harahamzzi.android.Home.Report

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.DBConvert
import com.harahamzzi.android.R
import com.harahamzzi.android.TimeConvert
import com.harahamzzi.android.databinding.ContainerDailyReportBinding
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.math.BigInteger

// 일간 리포트의 리포트 Recycler view 뷰 홀더
class DailyReportViewHolder(private val context: Context, binding: ContainerDailyReportBinding): RecyclerView.ViewHolder(binding.root) {

    val dateTextView: TextView = binding.containerReportDailyDateTextView
    val timeTextView: TextView = binding.containerReportDailyTimeTextView
    val chart: HorizontalBarChart = binding.containerReportDailyTotalDailyBarChart
    val goalListLayout: LinearLayout = binding.containerReportDailyInfoLinearLayout

    fun bind(item: DailyReportFragment.ReportData) {

        // 레이아웃 초기화
        goalListLayout.removeAllViews()

        // padding 없애기
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)

        /** 날짜 TextView 설정 **/
        var splitDate = item.date.split('-')
        dateTextView.text = "${splitDate[1]}월 ${splitDate[2]}일 ${splitDate[3]}요일"

        /** 시간 TextView 설정 **/
        var totalMs: BigInteger = BigInteger.ZERO
        for (i in item.bigGoalTimeList.indices)
        {
            totalMs += item.bigGoalTimeList[i]
        }

        var strTotalTime: String = TimeConvert.msToTimeConvert(totalMs)
        var splitTime = strTotalTime.split(':')

        // 시간, 분이 0이라면
        if (splitTime[0] == "00" && splitTime[1] == "00")
        {
                timeTextView.text = "${splitTime[2]}초"
        }
        else
        {
            timeTextView.text = "${splitTime[0]}시간 ${splitTime[1]}분"
        }

        /** 목표 목록 생성 **/
        createGoalList(item, totalMs)
    }

    // 리포트 하단의 목표들을 동적 생성하는 함수
    private fun createGoalList(item: DailyReportFragment.ReportData, totalMsTime: BigInteger)
    {
        // 리스트 개수만큼 동적 뷰 생성
        for (i in item.bigGoalNameList.indices) {
            var view: View = LayoutInflater.from(context).inflate(R.layout.container_daily_home_report, goalListLayout, false)

            // 동적 객체 생성 (색상 이미지, 대표목표, 막대 그래프)
            var bigGoalColorImg: ImageView = view.findViewById(R.id.bigGoalColorImg)
            var bigGoalTextview: TextView = view.findViewById(R.id.bigGoalTextview)
            var dailyBarChart: HorizontalBarChart = view.findViewById(R.id.dailyBarChart)
            var biglGoalTimeview: TextView = view.findViewById(R.id.biglGoalTimeview)

            // 값 할당하기
            bigGoalColorImg.setImageResource(R.drawable.ic_colorselectionicon)
            bigGoalColorImg.setColorFilter(item.colorIDList[i])
            bigGoalTextview.text = item.bigGoalNameList[i]

            var strTotalTime: String = TimeConvert.msToTimeConvert(item.bigGoalTimeList[i])
            var splitTime = strTotalTime.split(':')

            var (hour: String, min: String, sec: String) = splitTime

            if (hour == "00" && min == "00") {
                biglGoalTimeview.text = "${sec}초"
            } else if (hour == "00") {
                biglGoalTimeview.text = "${min}분"
            } else {
                biglGoalTimeview.text = "${hour}시간 ${min}분"
            }

            var milliTime: BigInteger = item.bigGoalTimeList[i] // 대표목표의 총 잠금시간(=밀리초)
            var goalColor: Int = item.colorIDList[i]            // 대표목표의 색상

            var temp1DailyBarChart: HorizontalBarChart = oneBarChartApperance(dailyBarChart) // 그래프 기본 레이아웃 설정
            oneBarChartDate(temp1DailyBarChart, milliTime, totalMsTime, goalColor)

            // 일간리포트의 총 잠금시간 가로 막대 그래프
            var temp2DailyBarChart: HorizontalBarChart = totalBarChartApperance(chart) // 그래프 기본 레이아웃 설정
            totalBarChartDate(temp2DailyBarChart, item.bigGoalTimeList, totalMsTime, item.colorIDList)

            // 레이아웃에 객체 추가
            goalListLayout.addView(view)

            //아래에 세부 목표 붙이기
            if(item.detailGoalDataList.isNotEmpty()){
                for (j in item.detailGoalDataList.indices) {
                    // 동적 뷰 생성
                    var view: View = LayoutInflater.from(context).inflate(R.layout.container_goal_daily_detail_goal, goalListLayout, false)

                    // 아이콘과 세부목표 동적 객체 생성
                    var dailyIconImg: ImageView = view.findViewById(R.id.container_reportDaily_detailGoalIconImageView)
                    var dailyDetailTextview: TextView = view.findViewById(R.id.container_reportDaily_detailGoalTextView)

                    // 값 할당하기
                    if (item.detailGoalDataList[j]["big_goal_name"] == item.bigGoalNameList[i]) { // 해당 대표 목표 산하 세부 목표라면
                        dailyIconImg.setImageResource(DBConvert.iconConvert(item.detailGoalDataList[j]["icon"], context))
                        dailyIconImg.setColorFilter(item.colorIDList[i])
                        dailyDetailTextview.text = item.detailGoalDataList[j]["detail_goal_name"]

                        // 레이아웃에 객체 추가
                        goalListLayout.addView(view)
                    }
                }
            }
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

        // 데이터를 막대모양으로 표시하기
        val barDataSet = BarDataSet(entry, "")
        barDataSet.apply {
            barDataSet.setDrawIcons(false)
            colors = nowDateBigGoalColorList   // 아이템 색상
            setDrawValues(false)               // %값 안보이기
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