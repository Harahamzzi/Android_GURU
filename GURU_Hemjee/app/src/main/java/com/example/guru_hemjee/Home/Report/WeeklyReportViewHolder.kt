package com.example.guru_hemjee.Home.Report

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ContainerWeeklyReportBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.math.BigInteger

// 주간 리포트의 리포트 Recycler view 뷰 홀더
class WeeklyReportViewHolder(private val context: Context, binding: ContainerWeeklyReportBinding): RecyclerView.ViewHolder(binding.root) {

    val dateTextView: TextView = binding.containerReportWeeklyDateTextView
    val timeTextView: TextView = binding.containerReportWeeklyTimeTextView
    val chart: BarChart = binding.containerReportWeeklyBarChart
    val goalListLayout: LinearLayout = binding.containerReportWeeklyInfoLinearLayout

    fun bind(item: WeeklyReportFragment.ReportData) {

        weeklyReport(item)
    }

    // 스택바 차트 세팅 함수
    private fun weeklyStackBarChart(item: WeeklyReportFragment.ReportData) {
        chart.invalidate()

        val entry = ArrayList<BarEntry>()
        val itemColorID = ArrayList<Int>()

        var bigGoalNameList = ArrayList<String>()
        var bigGoalTimeLists = ArrayList<MutableList<Float>>()

        //사용가능한 대표 목표 이름 뽑기
        for (i in item.bigGoalDataList.indices) {
            for (j in item.weeklyDateList.indices){
                if (item.bigGoalDataList[i]["lock_date"] == item.weeklyDateList[j]) {
                    if (!bigGoalNameList.contains(item.bigGoalDataList[i]["big_goal_name"])) {
                        bigGoalNameList.add(item.bigGoalDataList[i]["big_goal_name"]!!.toString())
                        itemColorID.add(item.bigGoalDataList[i]["color"]!!.toInt())
                    }
                }
            }
        }

        //사용가능한 대표 목표 목록으로 사용 시간 구하기
        for (i in item.weeklyDateList.indices) {
            var tempArrayList = MutableList(bigGoalNameList.size, {0.0f})
            for (nameNum in bigGoalNameList.indices) {
                for (goalNum in item.bigGoalDataList.indices) {
                    if (item.bigGoalDataList[goalNum]["lock_date"] == item.weeklyDateList[i] &&
                            item.bigGoalDataList[goalNum]["big_goal_name"] == bigGoalNameList[nameNum]) {
                        tempArrayList[nameNum] += item.bigGoalDataList[goalNum]["total_lock_time"]!!.toFloat()/(1000*60*60)
//                        if (tempArrayList[nameNum] != 0f) {
//                            tempArrayList[nameNum] = round(tempArrayList[nameNum]*100) /100
//                        } else {
//                            tempArrayList[nameNum] = round(tempArrayList[nameNum]*10000) /10000
//                        }
                    }
                }
            }
            bigGoalTimeLists.add(tempArrayList)
        }

        for(i in bigGoalTimeLists.indices){
            var tempList:Array<Float> = bigGoalTimeLists[i].toTypedArray()
            entry.add(BarEntry(i.toFloat(), floatArrayOf(tempList)))
        }

        val barDataSet = BarDataSet(entry, "")
        barDataSet.apply {
            if(itemColorID.size >= 1)
                colors= itemColorID
            else
                color = itemColorID[0]
            valueTextColor = R.color.Black
            valueTextSize = 16f
        }

        val barData = BarData(barDataSet)
        chart.apply {
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
        //x축 라벨(요일)추가
        val weekXLables = arrayListOf<String>("월","화","수","목","금","토","일")
        chart.xAxis.apply { // 아래 라벨 x축
            isEnabled = true // 라벨 표시X
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false) // 격자구조X
            valueFormatter = IndexAxisValueFormatter(weekXLables)
        }
        chart.axisLeft.apply { // 왼쪽 y축
            isEnabled = true // 라벨 표시
            setDrawLabels(true) // 값 세팅
            textColor = R.color.Black
            textSize = 14f
            axisMinimum = 0.0f
        }
        chart.axisRight.apply { // 오른쪽 y축
            isEnabled = false // 라벨 표시X
        }
    }

    //floatArrayOf로 데이터 변경
    private fun floatArrayOf(elements: Array<Float>): FloatArray {
        var temp: FloatArray = FloatArray(elements.size, {0.0f})
        for(i in 0 until elements.size){
            temp[i] = elements[i]
        }

        return temp
    }

    // 날짜에 따른 리포트(선택지가 전체일 경우)
    private fun weeklyReport(item: WeeklyReportFragment.ReportData) { // 지난 주 값

        goalListLayout.removeAllViews() // 초기화

        var monday = item.weeklyDateList[0].split('-') // 월요일
        var sunday = item.weeklyDateList[6].split('-') // 일요일
        dateTextView.text = monday[1] + "월 " + monday[2] + "일 - " + sunday[1] + "월 " + sunday[2] + "일"

        var totalMilli: BigInteger = BigInteger.ZERO // 총 전체 잠금 시간

        for (i in item.bigGoalDataList.indices) {
            for (j in item.weeklyDateList.indices) {
                if (item.bigGoalDataList[i]["lock_date"] == item.weeklyDateList[j]) // 잠금 날짜가 같다면 총 시간 저장
                    totalMilli += item.bigGoalDataList[i]["total_lock_time"]!!.toBigInteger()
            }
        }

        var integer_hour: Int = ((totalMilli.toLong() / (1000 * 60 * 60)) % 24).toInt()
        var integer_min: Int = ((totalMilli.toLong() / (1000 * 60)) % 60).toInt()
        if (integer_hour == 0 && integer_min == 0){
            var integer_sec: Int = (totalMilli.toLong() / (1000)%60).toInt()
            timeTextView.text = "${integer_sec}초"
        } else {
            timeTextView.text = "${integer_hour}시간 ${integer_min}분"
        }


        // 스택바 차트 세팅
        weeklyStackBarChart(item)

        // 동적 뷰를 활용한 대표목표 및 세부목표 리스트 만들기
        var bigGoalName = ArrayList<String>()
        var bigGoalTime = ArrayList<Long>()
        var bigGoalColor = ArrayList<Int>()

        for (i in item.bigGoalDataList.indices) {
            for(j in 0 until item.weeklyDateList.size) {
                if(item.bigGoalDataList[i]["lock_date"] == item.weeklyDateList[j]) {
                    if(!bigGoalName.contains(item.bigGoalDataList[i]["big_goal_name"])){
                        bigGoalName.add(item.bigGoalDataList[i]["big_goal_name"].toString())
                        bigGoalColor.add(item.bigGoalDataList[i]["color"]!!.toInt())
                        bigGoalTime.add(item.bigGoalDataList[i]["total_lock_time"]!!.toLong())
                    } else {
                        var index = bigGoalName.indexOf(item.bigGoalDataList[i]["big_goal_name"])
                        bigGoalTime[index] = bigGoalTime[index] + item.bigGoalDataList[i]["total_lock_time"]!!.toLong()
                    }
                }
            }
        }

        for(i in bigGoalName.indices){
            // 동적 뷰 생성
            var view: View = LayoutInflater.from(context).inflate(R.layout.container_big_goal_report_text, goalListLayout, false)

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
                biglGoalTimeview.text = sec.toString() + "초"
            } else {
                biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분"
            }

            // 레이아웃에 객체 추가
            goalListLayout.addView(view)
        }
    }
}