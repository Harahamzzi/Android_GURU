package com.example.guru_hemjee.Home.Home

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.example.guru_hemjee.TimeConvert
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// 홈 화면의 하단 요약본 페이지
class BottomSummaryDialog(context: Context) {

    private var TAG = "BottomSummaryDialog"

    private var context = context
    private var dialog = BottomSheetDialog(context)    // 부모 액티비티의 context가 들어가도록 함

    // DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlite: SQLiteDatabase

    // 버튼
    private lateinit var arrowDownImageButton: ImageButton

    // 차트 관련
    private lateinit var todayDateTextView: TextView        // 오늘 날짜 View
    private lateinit var todayDate: LocalDateTime           // 오늘 날짜 데이터

    // 데이터 관련
    // 2차원 배열(대표목표)
    private lateinit var bigGoalArrayList: java.util.ArrayList<MutableMap<String, String>>
    private var isBigGoalInitialised = false

    // 2차원 배열(세부목표)
    private lateinit var detailGoalArrayList: java.util.ArrayList<MutableMap<String, String>>
    private var isDetailGoalInitialized = false

    // 1. 일간
    private lateinit var dialog_dailyTimeTextView: TextView       // 총 기록시간
    private lateinit var dialog_dailyPieChart: PieChart           // 차트
    private lateinit var dialog_dailyGoalListLayout: LinearLayout // 목표 리스트가 들어갈 레이아웃

    // 2. 주간
    private lateinit var dialog_weeklyTimeTextView: TextView       // 총 기록시간
    private lateinit var dialog_weeklyBarChart: BarChart           // 차트
    private lateinit var dialog_weeklyGoalListLayout: LinearLayout // 목표 리스트가 들어갈 레이아웃

    // 팝업을 표시할 때 쓰는 함수
    @SuppressLint("Range")
    fun showPopup() {
        dialog.setContentView(R.layout.popup_bottom_summary)

        // 오늘 날짜 불러오기
        todayDate = LocalDateTime.now()

        /** View 연결 **/
        arrowDownImageButton = dialog.findViewById(R.id.arrowDownImageButton)!!

        todayDateTextView = dialog.findViewById(R.id.todayDateTextView)!!

        dialog_dailyTimeTextView = dialog.findViewById(R.id.dailyTimeTextView)!!
        dialog_dailyPieChart = dialog.findViewById(R.id.dailyPieChart)!!
        dialog_dailyGoalListLayout = dialog.findViewById(R.id.dailyGoalListLayout)!!

        dialog_weeklyTimeTextView = dialog.findViewById(R.id.weeklyTimeTextView)!!
        dialog_weeklyBarChart = dialog.findViewById(R.id.weeklyBarChart)!!
        dialog_weeklyGoalListLayout = dialog.findViewById(R.id.weeklyGoalListLayout)!!

        // 레이아웃 초기화
        dialog_dailyGoalListLayout.removeAllViews()
        dialog_weeklyGoalListLayout.removeAllViews()



        /** 데이터 세팅 **/
        // 대표목표 리포트 db에 저장된 값 읽어오기(대표목표 값, 대표목표 총 수행 시간, 잠금 날짜)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db", null)

        // 모든 값들 배열에 저장(같은 날짜 내에 중복값 저장X)
        while (cursor.moveToNext()) {
            val str_big_goal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            val str_date = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

            val str_time = cursor.getString(cursor.getColumnIndex("total_report_time")).toString()  // 00:00:00 형태
            var ms_time: String = TimeConvert.timeToMsConvert(str_time).toString()  // ms 단위로 바꿈

            // 해당 대표목표의 색상값 뽑아오기
            var int_color = 0
            var tempCursor: Cursor = sqlite.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '${str_big_goal}'", null)
            if (tempCursor.moveToNext())
            {
                var tempColor = context.resources.getIdentifier(tempCursor.getString(tempCursor.getColumnIndex("color")), "color", context.packageName)
                int_color = context.resources.getColor(tempColor)
            }
            tempCursor.close()

            // 배열에 읽어온 값 저장
            var isFlag = false // 중복값 확인
            var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
            if (!isBigGoalInitialised) { // 처음 입력시 초기화
                bigGoalArrayList = arrayListOf(
                    mutableMapOf(
                        "big_goal_name" to str_big_goal,
                        "total_lock_time" to ms_time,
                        "lock_date" to date1[0],
                        "color" to int_color.toString()
                    )
                )
                isBigGoalInitialised = true

            } else { // 같은 날 같은 목표를 달성했다면 시간만 추가하고 새로 추가 X
                var i = 0
                while (i < bigGoalArrayList.size) {
                    if (bigGoalArrayList[i]["big_goal_name"] == str_big_goal &&
                        bigGoalArrayList[i]["color"] == int_color.toString() &&
                        bigGoalArrayList[i]["lock_date"] == date1[0]) {

                        bigGoalArrayList[i]["total_lock_time"] =
                            (bigGoalArrayList[i]["total_lock_time"]?.toInt()
                                ?.plus(ms_time.toInt())).toString()
                        isFlag = true
                        break
                    }
                    i++
                }
                if (!isFlag) {
                    bigGoalArrayList.add(
                        mutableMapOf(
                            "big_goal_name" to str_big_goal,
                            "total_lock_time" to ms_time,
                            "lock_date" to date1[0],
                            "color" to int_color.toString()
                        )
                    )
                }
            }
        }
        cursor.close()

        // 세부목표 리포트 db에서 저장된 값 읽어오기(세부목표, 잠금 날짜)
        var cursor2: Cursor
        cursor2 = sqlite.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

        while (cursor2.moveToNext()) {
            var str_detail_goal = cursor2.getString(cursor2.getColumnIndex("detail_goal_name"))
            var str_date = cursor2.getString(cursor2.getColumnIndex("lock_date")).toString()
            var str_big_goal = cursor2.getString(cursor2.getColumnIndex("big_goal_name")).toString()

            var int_icon = 0
            var tempCursor: Cursor = sqlite.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '${str_detail_goal}'", null)
            if (tempCursor.moveToNext())
            {
                int_icon = DBConvert.iconConvert(tempCursor.getString(tempCursor.getColumnIndex("icon")).toString(), context)
            }
            tempCursor.close()

            var int_color = 0
            tempCursor = sqlite.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '${str_big_goal}'", null)
            if (tempCursor.moveToNext())
            {
                var tempColor = context.resources.getIdentifier(tempCursor.getString(tempCursor.getColumnIndex("color")), "color", context.packageName)
                int_color = context.resources.getColor(tempColor)
            }
            tempCursor.close()

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
                var i = 0
                //기존에 값이 없을 때만 새로 추가
                while (i < detailGoalArrayList.size) {
                    if (detailGoalArrayList[i]["detail_goal_name"] == str_detail_goal
                            && detailGoalArrayList[i]["lock_date"] == date1[0]) {
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

        cursor2.close()
        dbManager.close()
        sqlite.close()

        // 주간 & 일간 리포트 생성
        if(!isBigGoalInitialised)
        {
            Toast.makeText(context, "수행한 기록이 없습니다.", Toast.LENGTH_SHORT).show()
            weeklyReport(ZonedDateTime.now(), ArrayList<MutableMap<String, String>>(), isBigGoalInitialised)
        }
        else
        {
            weeklyReport(ZonedDateTime.now(), bigGoalArrayList, isBigGoalInitialised)
        }

        dayReport(ZonedDateTime.now())

        // 닫기 버튼 리스너
        arrowDownImageButton.setOnClickListener {
            dialog.dismiss()
        }

        // 배경색 투명화
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 팝업 띄우기
        dialog.show()
    }


    /******* 일간 리포트 구성 *******/
    // 파이차트 세팅 함수
    private fun dailyPieChart(moveDate: String) {

        dialog_dailyPieChart.setUsePercentValues(true) // 100% 범위로 계산

        // 데이터(시간, 대표목표) 입력
        val entry = java.util.ArrayList<PieEntry>()
        for (i in 0 until bigGoalArrayList.size) {
            if (bigGoalArrayList[i]["lock_date"] == moveDate)
                entry.add(PieEntry(bigGoalArrayList[i]["total_lock_time"]!!.toFloat(), ""))
        }

        // 아이템 범위별 색상
        val itemcolor = java.util.ArrayList<Int>()
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
        dialog_dailyPieChart.apply {
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
    private fun dayReport(moveTime: ZonedDateTime) {
        var moveDate = moveTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E")) // 년도, 월, 일, 요일

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
        dialog_dailyTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"

        // 파이차트 세팅
        var isPieFlag = false
        if(isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size) { // 해당 날짜와 관련한 값이 1개라도 있다면 파이차트 생성
                if (bigGoalArrayList[i]["lock_date"] == moveDate) {
                    dailyPieChart(moveDate)
                    dialog_dailyPieChart.visibility = View.VISIBLE
//                    reportDaily_noGoalTimeTextView.visibility = View.INVISIBLE
                    isPieFlag = true
                    break
                }
            }
        }
        if (!isPieFlag) { // 일치하는 날짜 값이 없다면 파이차트 숨기기
            dialog_dailyPieChart.visibility = View.INVISIBLE
//            reportDaily_noGoalTimeTextView.visibility = View.VISIBLE
        }

        // 동적 뷰를 활용한 목표 리스트 만들기
        if (isBigGoalInitialised){
            for (i in 0 until bigGoalArrayList.size){
                // 동적 뷰 생성
                var view: View = LayoutInflater.from(context).inflate(R.layout.container_goal_daily_big_goal, dialog_dailyGoalListLayout, false)

                // 아이콘과 대표 목표 동적 객체 생성
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
                        bigGoalTimeTextView.text = integer_sec.toString() + "초"
                    } else {
                        bigGoalTimeTextView.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"
                    }

                    // 레이아웃에 객체 추가
                    dialog_dailyGoalListLayout.addView(view)

                    //아래에 세부 목표 붙이기
                    if(isDetailGoalInitialized){
                        for (j in 0 until detailGoalArrayList.size) {
                            // 동적 뷰 생성
                            var view: View = LayoutInflater.from(context).inflate(R.layout.container_goal_daily_detail_goal, dialog_dailyGoalListLayout, false)

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
                                dialog_dailyGoalListLayout.addView(view)
                            }
                        }
                    }
                }
            }
        }
    }

    /******* 주간 리포트 구성 *******/
    // 스택바 차트 세팅 함수(주간)
    private fun setWeeklyStackBarChart(weekList: ArrayList<String>, bigGoalArrayList: ArrayList<MutableMap<String, String>>) {
        dialog_weeklyBarChart.invalidate()

        val entry = ArrayList<BarEntry>()
        val itemColorID = ArrayList<Int>()

        var bigGoalNameList = ArrayList<String>()
        var bigGoalTimeLists = ArrayList<MutableList<Float>>()

        //사용가능한 대표 목표 이름 뽑기
        for (i in 0 until bigGoalArrayList.size) {
            for (j in 0 until weekList.size){
                if (bigGoalArrayList[i]["lock_date"] == weekList[j]) {
                    if (!bigGoalNameList.contains(bigGoalArrayList[i]["big_goal_name"])) {
                        bigGoalNameList.add(bigGoalArrayList[i]["big_goal_name"]!!.toString())
                        itemColorID.add(bigGoalArrayList[i]["color"]!!.toInt())
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
            if(itemColorID.size >= 1)
                colors = itemColorID
            else
                color = itemColorID[0]
            valueTextColor = R.color.Black
            valueTextSize = 16f
        }

        val barData = BarData(barDataSet)
        dialog_weeklyBarChart.apply {
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
        dialog_weeklyBarChart.xAxis.apply { // 아래 라벨 x축
            isEnabled = true // 라벨 표시
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false) // 격자구조X
            valueFormatter = IndexAxisValueFormatter(weekXLables)
        }
        dialog_weeklyBarChart.axisLeft.apply { // 왼쪽 y축
            isEnabled = true // 라벨 표시
            setDrawLabels(true) // 값 세팅
            textColor = R.color.Black
            textSize = 14f
            axisMinimum = 0.0f
        }
        dialog_weeklyBarChart.axisRight.apply { // 오른쪽 y축
            isEnabled = false // 라벨 표시X
            textColor = R.color.Black
            textSize = 14f
        }
    }

    // 날짜에 따른 리포트
    private fun weeklyReport(moveTime: ZonedDateTime, bigGoalArrayList: ArrayList<MutableMap<String, String>>,
                             isBigGoalInitialised: Boolean) { // 지난 주 값
        dialog_weeklyGoalListLayout.removeAllViews() // 초기화

        var weekList: ArrayList<String> = getWeekDate(moveTime)

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
            dialog_weeklyTimeTextView.text = "${integer_sec}초"
        } else {
            dialog_weeklyTimeTextView.text = "${integer_hour}시간 ${integer_min}분"
        }

        // 스택바 차트 세팅
        var isBarFlag = false
        if (isBigGoalInitialised) {
            for (i in 0 until bigGoalArrayList.size) {
                for (j in 0 until weekList.size) {
                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) { // 같은 잠금 날짜가 1개라도 있다면 차트 띄우기
                        setWeeklyStackBarChart(weekList, bigGoalArrayList)
                        dialog_weeklyBarChart.visibility = View.VISIBLE
                        isBarFlag = true
                        break
                    }
                }
            }
        }
        if (!isBarFlag) { // 일치하는 날짜 값이 없다면 스택바 차트 숨기기
            dialog_weeklyBarChart.visibility = View.INVISIBLE
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
            var view: View = LayoutInflater.from(context).inflate(R.layout.container_big_goal_report_text, dialog_weeklyGoalListLayout, false)

            // 대표목표 동적 객체 생성
            var bigGoalColorImg: ImageView = view.findViewById(R.id.bigGoalColorImg)
            var bigGoalTextview: TextView = view.findViewById(R.id.bigGoalTextview)
            var biglGoalTimeview: TextView = view.findViewById(R.id.biglGoalTimeview)

            // 값 할당하기
            bigGoalColorImg.setImageResource(R.drawable.ic_colorselectionicon)
            bigGoalColorImg.setColorFilter(bigGoalColor[i])
            bigGoalTextview.text = bigGoalName[i]
            var hour: Int = ((bigGoalTime[i] / (1000 * 60 * 60)) % 24).toInt()
            var min: Int = ((bigGoalTime[i] / (1000 * 60)) % 60).toInt()
            if (hour == 0 && min == 0) {
                var sec: Int = ((bigGoalTime[i] / (1000)) % 60).toInt()
                biglGoalTimeview.text = "${sec}초"
            } else {
                biglGoalTimeview.text = "${hour}시간 ${min}분"
            }

            // 레이아웃에 객체 추가
            dialog_weeklyGoalListLayout.addView(view)
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
}