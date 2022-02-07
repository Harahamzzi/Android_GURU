package com.example.guru_hemjee

import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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
import com.github.mikephil.charting.formatter.*
import com.google.android.material.button.MaterialButton
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

// 홈(MainActivity) -> 목표 리포트 -> 주간
// 주간 리포트 Fragment 화면
class WeeklyReportFragment : Fragment() {

    // db
    lateinit var dbManager: DBManager
    lateinit var sqlite: SQLiteDatabase

    // 일간, 주간
    lateinit var dailyBtn2: AppCompatButton
    lateinit var weeklyBtn2: AppCompatButton
    //lateinit var monthlyBtn2: AppCompatButton

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
    var nowTime = ZonedDateTime.now()
    var nowViewTime = nowTime

    // 2차원 배열(대표목표)
    lateinit var bigGoalArrayList: ArrayList<MutableMap<String, String>>
    //대표 목표가 초기화 되었는지 확인
    private var isBigGoalInitialised = false

    // 2차원 배열(세부목표)
    lateinit var detailGoalArrayList: ArrayList<MutableMap<String, String>>
    //세부목표가 초기화 되었는지 확인
    private var isDetailGoalInitialized = false

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
        weeklyStackBarChart.invalidate()

        // 대표목표 리포트 db에 저장된 값 읽어오기(대표목표 값, 대표목표 총 수행 시간, 잠금 날짜)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db", null)

        // 모든 값들 배열에 저장(같은 날짜 내에 중복값 저장X)
        while (cursor.moveToNext()) {
            val str_big_goal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            val bigint_time = cursor.getInt(cursor.getColumnIndex("total_lock_time")).toBigInteger()
            val str_date = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
            val int_color = cursor.getInt(cursor.getColumnIndex("color")).toBigInteger()

            // 배열에 읽어온 값 저장
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
                var isFlag: Boolean = false // 중복값 확인
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

        // 세부목표 리포트 db에서 저장된 값 읽어오기(세부목표, 잠금 날짜, 아이콘, 색상)
        var cursor3: Cursor
        cursor3 = sqlite.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

        while (cursor3.moveToNext()) {
            var str_detail_goal = cursor3.getString(cursor3.getColumnIndex("detail_goal_name"))
            var str_date = cursor3.getString(cursor3.getColumnIndex("lock_date")).toString()
            var int_icon = cursor3.getInt(cursor3.getColumnIndex("icon")).toBigInteger()
            var int_color = cursor3.getInt(cursor3.getColumnIndex("color")).toBigInteger()
            var str_big_goal = cursor3.getString(cursor3.getColumnIndex("big_goal_name")).toString()

            // 배열에 읽어온 값 저장 (같은 날짜 내에 중복값 저장X)
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
        if (reportSate == 0) { // 지난주
            weeklyReportListLayout.removeAllViews()
            weeklyReport(nowTime) // 현재 날짜 넣기
        }

        // 달력 버튼 클릭 이벤트
        moveWeeklyButton.setOnClickListener {
            // 최신 리포트를 보여주기
            weeklyReportListLayout.removeAllViews()
            reportSate = 0
            nowViewTime = nowTime
            weeklyReport(nowTime)
        }

        // 이전 버튼 클릭 이벤트
        prevBtn2.setOnClickListener {
            // 이전 주간 리포트 보여주기
            weeklyReportListLayout.removeAllViews()
            reportSate--
            nowViewTime = nowViewTime.minusDays(7)
            weeklyReport(nowViewTime)
        }

        // 다음 버튼 클릭 이벤트
        nextBtn2.setOnClickListener {
            // 현재 리포트를 보고 있다면
            if (reportSate == 0) {
                Toast.makeText(context, "현재 화면이 가장 최신 리포트 화면입니다.", Toast.LENGTH_SHORT).show()
            } else { // 다음 주간의 리포트 보여주기
                weeklyReportListLayout.removeAllViews()
                reportSate++
                nowViewTime = nowViewTime.plusDays(7)
                weeklyReport(nowViewTime)
            }
        }

        // 대표목표 토글 클릭 이벤트
        selectBigGoalBtn.setOnClickListener {
            val dialog = GoalSelectDialog(requireContext(), selectBigGoalBtn.text.toString(),"목표 선택", true)
            dialog.goalSelectPop()

            dialog.setOnClickedListener( object : GoalSelectDialog.ButtonClickListener{
                override fun onClicked(changedBigGoalTitle: String) {
                    selectBigGoalBtn.text = changedBigGoalTitle
                    if(changedBigGoalTitle=="전체"){
                        selectBigGoalBtn.iconTint = ColorStateList.valueOf(resources.getColor(R.color.Black))
                        toggleState = false
                        weeklyReport(nowViewTime)
                    }
                    else {
                        dbManager = DBManager(context, "hamster_db", null, 1)
                        sqlite = dbManager.readableDatabase
                        cursor = sqlite.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '$changedBigGoalTitle'",null)
                        if(cursor.moveToNext()){
                            selectBigGoalBtn.iconTint = ColorStateList.valueOf(cursor.getInt(cursor.getColumnIndex("color")))
                        }
                        cursor.close()
                        sqlite.close()
                        dbManager.close()
                        toggleState = true
                        toggleGoal = changedBigGoalTitle
                        weeklyReport(nowViewTime)
                    }
                }
            })
        }

        // 일간 버튼 클릭 이벤트
        dailyBtn2.setOnClickListener {
            goDailyReport()
        }

        // 주간 버튼 클릭 이벤트
        weeklyBtn2.setOnClickListener {
            goWeeklyReport()
        }

       //월간 버튼 클릭 이벤트
//        monthlyBtn2.setOnClickListener {
//            goMonthlyReport()
//        }

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
//    fun goMonthlyReport() {
//        mainActivity?.supportFragmentManager
//            ?.beginTransaction()
//            ?.replace(R.id.fragment_main, MonthlyReportFragment())
//            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            ?.commit()
//    }

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

    // 스택바 차트 세팅 함수
    fun weeklyStackBarChart(weekList: ArrayList<String>) {
        weeklyStackBarChart.invalidate()

        val entry = ArrayList<BarEntry>()
        val itemColor = ArrayList<Int>()

        val df = DecimalFormat("##.##")

        if(toggleState){
            var isThereRecord = false
            var bigGoalTimeList = mutableListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f)
            for(i in 0 until bigGoalArrayList.size){
                for(j in 0 until weekList.size){
                    if(bigGoalArrayList[i]["lock_date"] == weekList[j] && bigGoalArrayList[i]["big_goal_name"] == toggleGoal){
                        bigGoalTimeList[j] += bigGoalArrayList[i]["total_lock_time"]!!.toFloat()/(1000*60*60)
                        if (bigGoalTimeList[j] != 0f){
                            bigGoalTimeList[j] = round(bigGoalTimeList[j]*100) /100
                        } else {
                            bigGoalTimeList[j] = round(bigGoalTimeList[j]*10000) /10000
                        }
                        itemColor.add(bigGoalArrayList[i]["color"]!!.toInt())
                        isThereRecord = true
                    }
                }
            }
            Log.i("lock", "$bigGoalTimeList")

            //해당 대표 목표의 잠금 기록이 없을 경우
            if(!isThereRecord){
                itemColor.add(R.color.Black)
            }

            for(i in 0 until weekList.size){
                entry.add(BarEntry(i.toFloat(), floatArrayOf(bigGoalTimeList[i])))
            }
        } else {
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

            //사용가능한 대표 목표 목록으로 사용 시간 구하기
            for(i in 0 until weekList.size){
                var tempArrayList = MutableList(bigGoalNameList.size, {0.0f})
                for(nameNum in 0 until bigGoalNameList.size){
                    for(goalNum in 0 until bigGoalArrayList.size){
                        if(bigGoalArrayList[goalNum]["lock_date"] == weekList[i] && bigGoalArrayList[goalNum]["big_goal_name"] == bigGoalNameList[nameNum]){
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

                Log.i("lock", "$tempArrayList")
            }

            for(i in 0 until bigGoalTimeLists.size){
                var tempList:Array<Float> = bigGoalTimeLists[i].toTypedArray()
                entry.add(BarEntry(i.toFloat(), floatArrayOf(tempList)))
            }
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
        //x축 라벨(요일)추가
        val weekXLables = arrayListOf<String>("월","화","수","목","금","토","일")
        weeklyStackBarChart.xAxis.apply { // 아래 라벨 x축
            isEnabled = true // 라벨 표시X
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false) // 격자구조X
            valueFormatter = IndexAxisValueFormatter(weekXLables)
        }
        weeklyStackBarChart.axisLeft.apply { // 왼쪽 y축
            isEnabled = true // 라벨 표시
            setDrawLabels(true) // 값 세팅
            textColor = R.color.Black
            textSize = 14f
            axisMinimum = 0.0f
        }
        weeklyStackBarChart.axisRight.apply { // 오른쪽 y축
            isEnabled = false // 라벨 표시X
        }
    }

    private fun floatArrayOf(elements: Array<Float>): FloatArray {
        var temp: FloatArray = FloatArray(elements.size, {0.0f})
        for(i in 0 until elements.size){
            temp[i] = elements[i]
        }

        return temp
    }

    // 날짜에 따른 리포트(선택지가 전체일 경우)
    fun weeklyReport(moveTime: ZonedDateTime) { // 지난 주 값
        weeklyReportListLayout.removeAllViews() // 초기화

        var weekList: ArrayList<String> = getWeekDate(moveTime)
        var monday = weekList[0].split('-') // 월요일
        var sunday = weekList[6].split('-') // 일요일
        weeklyTextview.text = monday[1] + "월 " + monday[2] + "일 - " + sunday[1] + "월 " + sunday[2] + "일"

        var isBarFlag = false
        //대표 목표 기록이 있는 경우에만
        if(isBigGoalInitialised){
            var totalMilli: BigInteger = BigInteger.ZERO // 총 전체 잠금 시간

            if (toggleState) { // 대표 목표를 1개 클릭
                for (i in 0 until bigGoalArrayList.size) {
                    for (j in 0 until weekList.size) {
                        if (bigGoalArrayList[i]["lock_date"] == weekList[j] && bigGoalArrayList[i]["big_goal_name"] == toggleGoal) // 잠금 날짜와 대표목표가 같다면 총 시간 저장
                            totalMilli += bigGoalArrayList[i]["total_lock_time"]!!.toBigInteger()
                    }
                }
            } else { // 전체 선택
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
                weeklyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분 " + integer_sec+"초"
            } else {
                weeklyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"
            }


            // 스택바 차트 세팅
            for (i in 0 until bigGoalArrayList.size) {
                for (j in 0 until weekList.size) {
                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) { // 같은 잠금 날짜가 1개라도 있다면 차트 띄우기
                        weeklyStackBarChart(weekList)
                        weeklyStackBarChart.visibility = View.VISIBLE
                        noGoalTimeView2.visibility = View.INVISIBLE
                        isBarFlag = true
                        break
                    }
                }
            }
        }

        if (!isBarFlag) { // 일치하는 날짜 값이 없다면 스택바 차트 숨기기
            weeklyStackBarChart.visibility = View.INVISIBLE
            noGoalTimeView2.visibility = View.VISIBLE
        }

        // 동적 뷰를 활용한 대표목표 및 세부목표 리스트 만들기
        if (toggleState) { // 대표목표를 선택했다면
            val dayList = listOf<String>("월 ", "화 ","수 ","목 ","금 ","토 ","일 ")
            var detailGoalName = ArrayList<String>()
            var detailGoalDays = ArrayList<String>()
            var detailGoalIcon = ArrayList<Int>()
            var detailGoalColor = ArrayList<Int>()

            if(isDetailGoalInitialized){
                for(i in 0 until detailGoalArrayList.size) {
                    if (detailGoalArrayList[i]["detail_goal_name"]!!.isNotBlank()) { // 아무값도 없거나 공백이 있는 경우가 아니라면
                        for (j in 0 until weekList.size) {
                            if (detailGoalArrayList[i]["lock_date"] == weekList[j] && detailGoalArrayList[i]["big_goal_name"] == toggleGoal) { // 해당 날짜의 값이고, 해당하는 대표목표라면
                                if (!detailGoalName.contains(detailGoalArrayList[i]["detail_goal_name"])) {
                                    detailGoalName.add(detailGoalArrayList[i]["detail_goal_name"].toString())
                                    detailGoalDays.add(dayList[j])
                                    detailGoalIcon.add(detailGoalArrayList[i]["icon"]!!.toInt())
                                    detailGoalColor.add(detailGoalArrayList[i]["color"]!!.toInt())
                                } else {
                                    detailGoalDays[detailGoalName.indexOf(detailGoalArrayList[i]["detail_goal_name"])] += dayList[j]
                                }

                            }
                        }
                    }
                }
            }


            for (i in 0 until detailGoalName.size) { //detailGoalArray사용
                // 동적 뷰 생성
                var view: View = layoutInflater.inflate(R.layout.container_detail_goal_report_text, weeklyReportListLayout, false)

                // 아이콘과 세부목표 동적 객체 생성
                var detailIconImg: ImageView = view.findViewById(R.id.detailIconImg)
                var detailGoalTextview: TextView = view.findViewById(R.id.detailGoalTextview)
                var detailGoalDayText: TextView = view.findViewById(R.id.detailDayview)

                // 값 할당하기
                detailIconImg.setImageResource(detailGoalIcon[i])
                detailIconImg.setColorFilter(detailGoalColor[i], PorterDuff.Mode.SRC_IN)
                detailGoalTextview.text = detailGoalName[i]
                detailGoalDayText.text = detailGoalDays[i]
                // 레이아웃에 객체 추가
                weeklyReportListLayout.addView(view)
            }

        } else { // 전체를 선택했다면
            var bigGoalName = ArrayList<String>()
            var bigGoalTime = ArrayList<Long>()
            var bigGoalColor = ArrayList<Int>()

            //대표 목표가 있을 경우에만
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
                var view: View = layoutInflater.inflate(R.layout.container_big_goal_report_text, weeklyReportListLayout, false)

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
                weeklyReportListLayout.addView(view)
            }
        }
    }
}