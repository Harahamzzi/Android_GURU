package com.example.guru_hemjee.Home.Report

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.Home.MainActivity
import com.example.guru_hemjee.R
import com.example.guru_hemjee.TimeConvert
import com.example.guru_hemjee.databinding.FragmentWeeklyReportBinding
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

// 홈(MainActivity) -> 목표 리포트 -> 주간
// 주간 리포트 Fragment 화면
class WeeklyReportFragment : Fragment() {

    // 태그
    private var TAG = "WeeklyReportFragment"

    // 뷰 바인딩 변수
    private var mBinding: FragmentWeeklyReportBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // db
    private lateinit var dbManager: DBManager
    private lateinit var sqlite: SQLiteDatabase

    // 뒤로가기 콜백
    private lateinit var callback: OnBackPressedCallback

//    // 날짜 & 시간
//    private lateinit var reportWeekly_weeklyDateTextview: TextView
//    private lateinit var reportWeekly_weeklyTimeTextview: TextView
//
//    // 스택바 차트
//    private lateinit var reportWeekly_weeklyStackBarChart: BarChart
//
//    // 대표목표 선택 버튼
//    private lateinit var reportWeekly_selectBigGoalButton: MaterialButton
//
//    // 대표목표&세부목표 리스트를 저장할 리니어 레이아웃
//    private lateinit var reportWeekly_weeklyReportListLayout: LinearLayout
//
//    // 텍스트뷰
//    private lateinit var reportWeekly_noGoalTimeView: TextView

    // 현재 날짜
    private var nowTime = ZonedDateTime.now()

//    // 2차원 배열(대표목표)
//    private lateinit var bigGoalArrayList: ArrayList<MutableMap<String, String>>
//    //대표 목표가 초기화 되었는지 확인
//    private var isBigGoalInitialised = false
//
//    // 2차원 배열(세부목표)
//    private lateinit var detailGoalArrayList: ArrayList<MutableMap<String, String>>
//    //세부목표가 초기화 되었는지 확인
//    private var isDetailGoalInitialized = false
//
//    // 현재 리포트 화면 상태
//    private var reportSate: Int = 0 // 이번주
//    private var toggleState: Boolean = false // 대표목표 선택 버튼의 클릭 여부(false=전체 선택, true=대표목표 선택)
//    private lateinit var toggleGoal: String // 선택한 대표목표

    // 날짜값 리스트
    private var dateList = ArrayList<String>()

    // 리포트 생성에 필요한 데이터 묶음 리스트
    private var reportDataList = ArrayList<ReportData>()

    data class ReportData(
        var weeklyDateList: ArrayList<String>,      // 일주일 동안의 날짜값: yyyy-MM-dd-E 형태
        var bigGoalDataList: ArrayList<MutableMap<String, String>>,    // 대표목표 관련 데이터
    )

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentWeeklyReportBinding.inflate(inflater, container, false)

        // 달력 버튼 클릭 이벤트
        binding.reportWeeklyCalenderImageButton.setOnClickListener {
            // 최신 리포트를 보여주기(스크롤 최상단으로 올림)
            binding.reportWeeklyReportRecyclerView.smoothScrollToPosition(0)
        }

        // TODO: 대표목표 토글 클릭 이벤트
//        binding.reportWeeklySelectBigGoalButton.setOnClickListener {
//            val dialog = GoalSelectDialog(requireContext(), reportWeekly_selectBigGoalButton.text.toString(),
//                "목표 선택", true)
//            dialog.goalSelectPop()
//
//            dialog.setOnClickedListener( object : GoalSelectDialog.ButtonClickListener {
//                override fun onClicked(changedBigGoalTitle: String) {
//                    reportWeekly_selectBigGoalButton.text = changedBigGoalTitle
//                    if(changedBigGoalTitle=="전체"){
//                        reportWeekly_selectBigGoalButton.iconTint = ColorStateList.valueOf(resources.getColor(
//                            R.color.Black
//                        ))
//                        toggleState = false
//                        weeklyReport(nowTime)
//                    }
//                    else {
//                        dbManager = DBManager(context, "hamster_db", null, 1)
//                        sqlite = dbManager.readableDatabase
//                        var cursor: Cursor
//                        cursor = sqlite.rawQuery("SELECT * FROM big_goal_db WHERE " +
//                                "big_goal_name = '$changedBigGoalTitle'",null)
//                        if(cursor.moveToNext()){
//                            reportWeekly_selectBigGoalButton.iconTint = ColorStateList.valueOf(cursor.getInt(cursor.getColumnIndex("color")))
//                        }
//                        cursor.close()
//                        sqlite.close()
//                        dbManager.close()
//                        toggleState = true
//                        toggleGoal = changedBigGoalTitle
//                        weeklyReport(nowTime)
//                    }
//                }
//            })
//        }

        // 일간 버튼 클릭 이벤트
        binding.reportWeeklyDailyButton.setOnClickListener {
            requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_main, DailyReportFragment())
                    .commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroyView()
    }

    @SuppressLint("Range")
    override fun onStart() {
        super.onStart()

        // 화면에 접속할 때마다 항상 레이아웃 초기화
        binding.reportWeeklyReportRecyclerView.removeAllViews()
//        reportWeekly_weeklyStackBarChart.invalidate()
        for (i in reportDataList.indices)
        {
            reportDataList.removeFirst()
        }
        for (i in dateList.indices)
        {
            dateList.removeFirst()
        }

        /** 데이터 값 세팅 **/
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        // 대표 목표 리포트 DB 읽기(내림차순(최근날짜 먼저), 중복날짜 제거) - 날짜 가져오기 위함
        var cursor: Cursor = sqlite.rawQuery("SELECT DISTINCT substr(lock_date, 1, 12) AS date FROM big_goal_time_report_db ORDER BY lock_date DESC", null)

        while (cursor.moveToNext())
        {
            // dateStr: yyyy-MM-dd-E 형태의 문자열
            var dateStr = cursor.getString(cursor.getColumnIndex("date")).toString()

            // 해당 날짜 데이터가 이미 있으면 넘김
            if (dateList.contains(dateStr))
            {
                continue
            }

            reportDataList.add(findWeeklyReportData(getWeekDate(dateStr)))
        }

        cursor.close()
        sqlite.close()
        dbManager.close()

        // Recycler View에 Layout Manager 및 Adapter 연결
        var layoutManager = LinearLayoutManager(requireContext())
        binding.reportWeeklyReportRecyclerView.layoutManager = layoutManager

        var weeklyReportAdapter = WeeklyReportListAdapter(requireContext(), reportDataList)
        binding.reportWeeklyReportRecyclerView.adapter = weeklyReportAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // MainActivity로 바로 이동
                var tempIntent = Intent(requireContext(), MainActivity::class.java)
                startActivity(tempIntent)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()

        callback.remove()
    }

    // 입력받은 날짜 근처의 월요일~일요일(7일) 구하기
    private fun getWeekDate(inputDateStr: String): ArrayList<String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-E") // 현재 년도, 월, 일
        var date = dateFormat.parse(inputDateStr) // 해당 날짜 문자열 -> Date

        calendar.time = date // Date

        // 입력받은 날짜가 일요일이라면
        if (inputDateStr.split('-')[3] == "일")
        {
            // 날짜를 하루 전으로 맞춤
            // 이번주의 주차를 구하기 위함
            calendar.add(Calendar.DATE, -1)
        }

        // 지난주의 월요일부터 일요일까지의 날짜를 배열에 저장
        var weekList = ArrayList<String>()
        for (i in 2..8) {
            calendar.add(Calendar.DAY_OF_MONTH, (i-calendar.get(Calendar.DAY_OF_WEEK)))
            val lastDate = calendar.time
            val lastDay = dateFormat.format(lastDate)
            weekList.add(lastDay)
            dateList.add(lastDay)   // 전체 날짜 리스트
        }

        return weekList
    }

    // 해당 날짜(7일간)의 리포트 데이터를 가져오는 함수
    // nowDate: yyyy-MM-dd-E 형태의 문자열
    @SuppressLint("Range")
    private fun findWeeklyReportData(weeklyDateList: ArrayList<String>): ReportData {

        var tempBigGoalDataList = ArrayList<MutableMap<String, String>>()    // 대표목표 관련 데이터

        // 대표목표 리포트 db에 저장된 값 읽어오기(대표목표 값, 대표목표 총 수행 시간, 잠금 날짜)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase
        var cursor: Cursor

        // 일주일 동안의 데이터 저장
        for (i in weeklyDateList.indices)
        {
            cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db WHERE lock_date LIKE '${weeklyDateList[i]}%'", null)

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
                    var tempColor = requireActivity().resources.getIdentifier(tempCursor.getString(tempCursor.getColumnIndex("color")), "color", requireActivity().packageName)
                    int_color = requireActivity().resources.getColor(tempColor)
                }
                tempCursor.close()

                // 배열에 읽어온 값 저장
                var isFlag = false // 중복값 확인
                var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
                if (tempBigGoalDataList.isEmpty()) { // 처음 입력시 초기화
                    tempBigGoalDataList = arrayListOf(
                            mutableMapOf(
                                    "big_goal_name" to str_big_goal,
                                    "total_lock_time" to ms_time,
                                    "lock_date" to date1[0],
                                    "color" to int_color.toString()
                            )
                    )

                } else { // 같은 날 같은 목표를 달성했다면 시간만 추가하고 새로 추가 X
                    var i = 0
                    while (i < tempBigGoalDataList.size) {
                        if (tempBigGoalDataList[i]["big_goal_name"] == str_big_goal &&
                                tempBigGoalDataList[i]["color"] == int_color.toString() &&
                                tempBigGoalDataList[i]["lock_date"] == date1[0]) {

                            tempBigGoalDataList[i]["total_lock_time"] =
                                    (tempBigGoalDataList[i]["total_lock_time"]?.toInt()
                                            ?.plus(ms_time.toInt())).toString()
                            isFlag = true
                            break
                        }
                        i++
                    }
                    if (!isFlag) {
                        tempBigGoalDataList.add(
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
        }

        dbManager.close()
        sqlite.close()

        return ReportData(weeklyDateList, tempBigGoalDataList)
    }

//    // 스택바 차트 세팅 함수
//    private fun weeklyStackBarChart(weekList: ArrayList<String>) {
//        reportWeekly_weeklyStackBarChart.invalidate()
//
//        val entry = ArrayList<BarEntry>()
//        val itemColor = ArrayList<Int>()
//
//        val df = DecimalFormat("##.##")
//
//        if(toggleState){
//            var isThereRecord = false
//            var bigGoalTimeList = mutableListOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f)
//            for(i in 0 until bigGoalArrayList.size){
//                for(j in 0 until weekList.size){
//                    if(bigGoalArrayList[i]["lock_date"] == weekList[j] && bigGoalArrayList[i]["big_goal_name"] == toggleGoal){
//                        bigGoalTimeList[j] += bigGoalArrayList[i]["total_lock_time"]!!.toFloat()/(1000*60*60)
//                        if (bigGoalTimeList[j] != 0f){
//                            bigGoalTimeList[j] = round(bigGoalTimeList[j]*100) /100
//                        } else {
//                            bigGoalTimeList[j] = round(bigGoalTimeList[j]*10000) /10000
//                        }
//                        itemColor.add(bigGoalArrayList[i]["color"]!!.toInt())
//                        isThereRecord = true
//                    }
//                }
//            }
//
//            //해당 대표 목표의 잠금 기록이 없을 경우
//            if(!isThereRecord){
//                itemColor.add(R.color.Black)
//            }
//
//            for(i in 0 until weekList.size){
//                entry.add(BarEntry(i.toFloat(), floatArrayOf(bigGoalTimeList[i])))
//            }
//        } else {
//            var bigGoalNameList = ArrayList<String>()
//            var bigGoalTimeLists = ArrayList<MutableList<Float>>()
//
//            //사용가능한 대표 목표 이름 뽑기
//            for(i in 0 until bigGoalArrayList.size){
//                for(j in 0 until weekList.size){
//                    if(bigGoalArrayList[i]["lock_date"] == weekList[j]){
//                        if(!bigGoalNameList.contains(bigGoalArrayList[i]["big_goal_name"])){
//                            bigGoalNameList.add(bigGoalArrayList[i]["big_goal_name"]!!.toString())
//                            itemColor.add(bigGoalArrayList[i]["color"]!!.toInt())
//                        }
//                    }
//                }
//            }
//
//            //사용가능한 대표 목표 목록으로 사용 시간 구하기
//            for(i in 0 until weekList.size){
//                var tempArrayList = MutableList(bigGoalNameList.size, {0.0f})
//                for(nameNum in 0 until bigGoalNameList.size){
//                    for(goalNum in 0 until bigGoalArrayList.size){
//                        if(bigGoalArrayList[goalNum]["lock_date"] == weekList[i]
//                            && bigGoalArrayList[goalNum]["big_goal_name"] == bigGoalNameList[nameNum]){
//                            tempArrayList[nameNum] += bigGoalArrayList[goalNum]["total_lock_time"]!!.toFloat()/(1000*60*60)
//                            if (tempArrayList[nameNum] != 0f){
//                                tempArrayList[nameNum] = round(tempArrayList[nameNum]*100) /100
//                            } else {
//                                tempArrayList[nameNum] = round(tempArrayList[nameNum]*10000) /10000
//                            }
//                        }
//                    }
//                }
//                bigGoalTimeLists.add(tempArrayList)
//            }
//
//            for(i in 0 until bigGoalTimeLists.size){
//                var tempList:Array<Float> = bigGoalTimeLists[i].toTypedArray()
//                entry.add(BarEntry(i.toFloat(), floatArrayOf(tempList)))
//            }
//        }
//
//        val barDataSet = BarDataSet(entry, "")
//        barDataSet.apply {
//            if(itemColor.size >= 1)
//                colors= itemColor
//            else
//                color = itemColor[0]
//            valueTextColor = R.color.Black
//            valueTextSize = 16f
//        }
//        val barData = BarData(barDataSet)
//        reportWeekly_weeklyStackBarChart.apply {
//            data = barData
//            description.isEnabled = false // 그래프 이름 띄우기X
//            legend.isEnabled = false // x-value값 안보이게
//            setTouchEnabled(false) // 차트 클릭X
//            setDrawBarShadow(false) // 그림자X
//            isDoubleTapToZoomEnabled = false
//            setPinchZoom(false) // 줌 설정X
//            setDrawGridBackground(false) // 격자구조X
//            setMaxVisibleValueCount(7) // 그래프 최대 개수
//            setDrawValueAboveBar(false) // 차트 입력값 아래로
//        }
//        //x축 라벨(요일)추가
//        val weekXLables = arrayListOf<String>("월","화","수","목","금","토","일")
//        reportWeekly_weeklyStackBarChart.xAxis.apply { // 아래 라벨 x축
//            isEnabled = true // 라벨 표시X
//            position = XAxis.XAxisPosition.BOTTOM
//            setDrawGridLines(false) // 격자구조X
//            valueFormatter = IndexAxisValueFormatter(weekXLables)
//        }
//        reportWeekly_weeklyStackBarChart.axisLeft.apply { // 왼쪽 y축
//            isEnabled = true // 라벨 표시
//            setDrawLabels(true) // 값 세팅
//            textColor = R.color.Black
//            textSize = 14f
//            axisMinimum = 0.0f
//        }
//        reportWeekly_weeklyStackBarChart.axisRight.apply { // 오른쪽 y축
//            isEnabled = false // 라벨 표시X
//        }
//    }
//
//    //floatArrayOf로 데이터 변경
//    private fun floatArrayOf(elements: Array<Float>): FloatArray {
//        var temp: FloatArray = FloatArray(elements.size, {0.0f})
//        for(i in 0 until elements.size){
//            temp[i] = elements[i]
//        }
//
//        return temp
//    }
//
//    // 날짜에 따른 리포트(선택지가 전체일 경우)
//    private fun weeklyReport(moveTime: ZonedDateTime) { // 지난 주 값
//        reportWeekly_weeklyReportListLayout.removeAllViews() // 초기화
//
//        var weekList: ArrayList<String> = getWeekDate(moveTime)
//        var monday = weekList[0].split('-') // 월요일
//        var sunday = weekList[6].split('-') // 일요일
//        reportWeekly_weeklyDateTextview.text = monday[1] + "월 " + monday[2] + "일 - " + sunday[1] + "월 " + sunday[2] + "일"
//
//        var isBarFlag = false
//        //대표 목표 기록이 있는 경우에만
//        if(isBigGoalInitialised){
//            var totalMilli: BigInteger = BigInteger.ZERO // 총 전체 잠금 시간
//
//            if (toggleState) { // 대표 목표를 1개 클릭
//                for (i in 0 until bigGoalArrayList.size) {
//                    for (j in 0 until weekList.size) {
//                        if (bigGoalArrayList[i]["lock_date"] == weekList[j] && bigGoalArrayList[i]["big_goal_name"] == toggleGoal) // 잠금 날짜와 대표목표가 같다면 총 시간 저장
//                            totalMilli += bigGoalArrayList[i]["total_lock_time"]!!.toBigInteger()
//                    }
//                }
//            } else { // 전체 선택
//                for (i in 0 until bigGoalArrayList.size) {
//                    for (j in 0 until weekList.size) {
//                        if (bigGoalArrayList[i]["lock_date"] == weekList[j]) // 잠금 날짜가 같다면 총 시간 저장
//                            totalMilli += bigGoalArrayList[i]["total_lock_time"]!!.toBigInteger()
//                    }
//                }
//            }
//            var integer_hour: Int = ((totalMilli.toLong() / (1000 * 60 * 60)) % 24).toInt()
//            var integer_min: Int = ((totalMilli.toLong() / (1000 * 60)) % 60).toInt()
//            if (integer_hour == 0 && integer_min == 0){
//                var integer_sec: Int = (totalMilli.toLong() / (1000)%60).toInt()
//                reportWeekly_weeklyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분 " + integer_sec+"초"
//            } else {
//                reportWeekly_weeklyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"
//            }
//
//
//            // 스택바 차트 세팅
//            for (i in 0 until bigGoalArrayList.size) {
//                for (j in 0 until weekList.size) {
//                    if (bigGoalArrayList[i]["lock_date"] == weekList[j]) { // 같은 잠금 날짜가 1개라도 있다면 차트 띄우기
//                        weeklyStackBarChart(weekList)
//                        reportWeekly_weeklyStackBarChart.visibility = View.VISIBLE
//                        reportWeekly_noGoalTimeView.visibility = View.INVISIBLE
//                        isBarFlag = true
//                        break
//                    }
//                }
//            }
//        }
//
//        if (!isBarFlag) { // 일치하는 날짜 값이 없다면 스택바 차트 숨기기
//            reportWeekly_weeklyStackBarChart.visibility = View.INVISIBLE
//            reportWeekly_noGoalTimeView.visibility = View.VISIBLE
//        }
//
//        // 동적 뷰를 활용한 대표목표 및 세부목표 리스트 만들기
//        if (toggleState) { // 대표목표를 선택했다면
//            val dayList = listOf<String>("월 ", "화 ","수 ","목 ","금 ","토 ","일 ")
//            var detailGoalName = ArrayList<String>()
//            var detailGoalDays = ArrayList<String>()
//            var detailGoalIcon = ArrayList<Int>()
//            var detailGoalColor = ArrayList<Int>()
//
//            if(isDetailGoalInitialized){
//                for(i in 0 until detailGoalArrayList.size) {
//                    if (detailGoalArrayList[i]["detail_goal_name"]!!.isNotBlank()) { // 아무값도 없거나 공백이 있는 경우가 아니라면
//                        for (j in 0 until weekList.size) {
//                            if (detailGoalArrayList[i]["lock_date"] == weekList[j] && detailGoalArrayList[i]["big_goal_name"] == toggleGoal) { // 해당 날짜의 값이고, 해당하는 대표목표라면
//                                if (!detailGoalName.contains(detailGoalArrayList[i]["detail_goal_name"])) {
//                                    detailGoalName.add(detailGoalArrayList[i]["detail_goal_name"].toString())
//                                    detailGoalDays.add(dayList[j])
//                                    detailGoalIcon.add(detailGoalArrayList[i]["icon"]!!.toInt())
//                                    detailGoalColor.add(detailGoalArrayList[i]["color"]!!.toInt())
//                                } else {
//                                    detailGoalDays[detailGoalName.indexOf(detailGoalArrayList[i]["detail_goal_name"])] += dayList[j]
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            for (i in 0 until detailGoalName.size) { //detailGoalArray사용
//                // 동적 뷰 생성
//                var view: View = layoutInflater.inflate(R.layout.container_detail_goal_report_text, reportWeekly_weeklyReportListLayout, false)
//
//                // 아이콘과 세부목표 동적 객체 생성
//                var detailIconImg: ImageView = view.findViewById(R.id.detailIconImg)
//                var detailGoalTextview: TextView = view.findViewById(R.id.detailGoalTextview)
//                var detailGoalDayText: TextView = view.findViewById(R.id.detailDayview)
//
//                // 값 할당하기
//                detailIconImg.setImageResource(detailGoalIcon[i])
//                detailIconImg.setColorFilter(detailGoalColor[i], PorterDuff.Mode.SRC_IN)
//                detailGoalTextview.text = detailGoalName[i]
//                detailGoalDayText.text = detailGoalDays[i]
//                // 레이아웃에 객체 추가
//                reportWeekly_weeklyReportListLayout.addView(view)
//            }
//
//        } else { // 전체를 선택했다면
//            var bigGoalName = ArrayList<String>()
//            var bigGoalTime = ArrayList<Long>()
//            var bigGoalColor = ArrayList<Int>()
//
//            //대표 목표가 있을 경우에만
//            if(isBigGoalInitialised){
//                for (i in 0 until bigGoalArrayList.size) { //detailGoalArray사용
//                    for(j in 0 until weekList.size){
//                        if(bigGoalArrayList[i]["lock_date"] == weekList[j]){
//                            if(!bigGoalName.contains(bigGoalArrayList[i]["big_goal_name"])){
//                                bigGoalName.add(bigGoalArrayList[i]["big_goal_name"].toString())
//                                bigGoalColor.add(bigGoalArrayList[i]["color"]!!.toInt())
//                                bigGoalTime.add(bigGoalArrayList[i]["total_lock_time"]!!.toLong())
//                            } else {
//                                var index = bigGoalName.indexOf(bigGoalArrayList[i]["big_goal_name"])
//                                bigGoalTime[index] = bigGoalTime[index] + bigGoalArrayList[i]["total_lock_time"]!!.toLong()
//                            }
//                        }
//                    }
//                }
//            }
//
//            for(i in 0 until bigGoalName.size){
//                // 동적 뷰 생성
//                var view: View = layoutInflater.inflate(R.layout.container_big_goal_report_text, reportWeekly_weeklyReportListLayout, false)
//
//                // 대표목표 동적 객체 생성
//                var bigGoalColorImg: ImageView = view.findViewById(R.id.bigGoalColorImg)
//                var bigGoalTextview: TextView = view.findViewById(R.id.bigGoalTextview)
//                var biglGoalTimeview: TextView = view.findViewById(R.id.biglGoalTimeview)
//
//                // 값 할당하기
//                bigGoalColorImg.setImageResource(R.drawable.ic_colorselectionicon)
//                bigGoalColorImg.setColorFilter(bigGoalColor[i], PorterDuff.Mode.SRC_IN)
//                bigGoalTextview.text = bigGoalName[i]
//                var hour: Int = ((bigGoalTime[i] / (1000 * 60 * 60)) % 24).toInt()
//                var min: Int = ((bigGoalTime[i] / (1000 * 60)) % 60).toInt()
//                if(hour == 0 && min == 0){
//                    var sec: Int = ((bigGoalTime[i] / (1000)) % 60).toInt()
//                    biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분 " + sec.toString() + "초"
//                } else {
//                    biglGoalTimeview.text = hour.toString() + "시간 " + min.toString() + "분"
//                }
//
//
//                // 레이아웃에 객체 추가
//                reportWeekly_weeklyReportListLayout.addView(view)
//            }
//        }
//    }
}