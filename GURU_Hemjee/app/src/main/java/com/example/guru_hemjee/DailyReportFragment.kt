package com.example.guru_hemjee

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

class DailyReportFragment : Fragment() {

    // db
    lateinit var dbManager: DBManager
    lateinit var sqlite: SQLiteDatabase

    // 일간, 주간, 월간
    lateinit var dailyBtn: androidx.appcompat.widget.AppCompatButton
    lateinit var weeklyBtn: androidx.appcompat.widget.AppCompatButton
    lateinit var monthlyBtn: androidx.appcompat.widget.AppCompatButton

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
    var nowTime = ZonedDateTime.now((ZoneId.of("Asia/Seoul")))
    var nowDate = nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E")) // 년도, 월, 일, 요일

    // 2차원 배열(대표목표)
    var bigGoalStringArray = Array(10, {Array(2, {""}) }) // 10행 2열, 하나의 행에 (대표목표,날짜) 순으로 저장
    var bigGoalIntArray = Array(10, {Array(2, {BigInteger.ZERO}) }) // 10행 2열, 하나의 행에 (시간, 색상) 순으로 저장
    var num = 0 // bigGoalStringArray와 bigGoalIntArray의 index

    // 2차원 배열(세부목표)
    var detailGoalStringArray = Array(20, {Array(3, {""}) }) // 20행 3열, 하나의 행에 (세부목표,날짜,대표목표) 순으로 저장
    var detailGoalIntArray = Array(20, {Array(2, {BigInteger.ZERO}) }) // 20행 2열, 하나의 행에 (아이콘,색상) 순으로 저장
    var num2 = 0 // detailGoalStringArray와 detailGoalIntArray의 index

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
        monthlyBtn = view.findViewById(R.id.monthlyBtn)
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

            // 배열에 읽어온 값 저장
            var isFlag: Boolean = false // 중복값 확인
            var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
            if (bigGoalStringArray.isNullOrEmpty()) { // 처음 저장하는 거라면
                bigGoalStringArray[num][0] = str_big_goal // 대표목표
                bigGoalIntArray[num][0] = bigint_time // 대표목표 총 수행 시간
                bigGoalStringArray[num][1] = date1[0] // 년도-월-일-요일 형태로 저장
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
                    ++num
                }
            }
        }
        cursor.close()

        // 대표목표 색상값 가져와서 배열에 저장
        var cursor2: Cursor
        for (i in 0 until num) {
            cursor2 = sqlite.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '" + bigGoalStringArray[i][0] + "';", null)

            while (cursor2.moveToNext()) {
                var int_color = cursor2.getInt(cursor2.getColumnIndex("color")).toBigInteger()

                // 배열에 읽어온 값 저장
                bigGoalIntArray[i][1] = int_color // 대표목표 색상
            }
            cursor2.close()
        }

        // 세부목표 리포트 db에서 저장된 값 읽어오기(세부목표, 잠금 날짜)
        var cursor3: Cursor
        cursor3 = sqlite.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

        while (cursor3.moveToNext()) {
            var str_detail_goal = cursor3.getString(cursor3.getColumnIndex("detail_goal_name"))
            var str_date = cursor3.getString(cursor3.getColumnIndex("lock_date")).toString()

            // 배열에 읽어온 값 저장 (같은 날짜 내에 중복값 저장X)
            var isFlag: Boolean = false // 중복값 확인
            var date1 = str_date.split(" ") // 날짜(0)와 시간(1) 분리
            if (detailGoalStringArray.isNullOrEmpty()) { // 처음 저장하는 거라면
                detailGoalStringArray[num2][0] = str_detail_goal // 세부목표
                detailGoalStringArray[num2][1] = date1[0] // 잠금 날짜(// 년도-월-일-요일 형태로 저장)
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
                    ++num2
                }
            }
        }
        cursor3.close()

        // 세부목표 db에서 저장된 값 읽어오기(아이콘, 대표목표)
        var cursor4: Cursor
        for (i in 0 until num2) {
            cursor4 = sqlite.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '" + detailGoalStringArray[i][0] + "';", null)

            while (cursor4.moveToNext()) {
                var int_icon = cursor4.getInt(cursor4.getColumnIndex("icon")).toBigInteger()
                var str_big_goal_name = cursor4.getString(cursor4.getColumnIndex("big_goal_name"))

                // 배열에 읽어온 값 저장
                detailGoalIntArray[i][0] = int_icon // 아이콘
                detailGoalStringArray[i][2] = str_big_goal_name // 대표목표
            }
            cursor4.close()
        }

        // 각 배열에 있는 대표목표 값이 같다면 detailGoalArray 배열에 색상 추가하기
        for (i in 0 until num) {
            for (j in 0 until num2) {
                if (bigGoalStringArray[i][0] == detailGoalStringArray[j][2]) {
                    detailGoalIntArray[j][1] = bigGoalIntArray[i][1]
                }
            }
        }

        dbManager.close()
        sqlite.close()

        // 배열 값 확인 용 Log
        /*for (i in 0 until num) {
            Log.d("bigGoalArray 대표목표 ", i.toString() + "번째 " + bigGoalStringArray[i][0])
            Log.d("bigGoalArray 시간 ", i.toString() + "번째 " + bigGoalIntArray[i][0].toString())
            Log.d("bigGoalArray 날짜 ", i.toString() + "번째 " + bigGoalStringArray[i][1])
            Log.d("bigGoalArray 색상 ", i.toString() + "번째 " + bigGoalIntArray[i][1].toString())
        }
        for (i in 0 until num2) {
            Log.d("detailGoalArray 세부목표 ", i.toString() + "번째 " + detailGoalStringArray[i][0])
            Log.d("detailGoalArray 날짜 ", i.toString() + "번째 " + detailGoalStringArray[i][1])
            Log.d("detailGoalArray 아이콘 ", i.toString() + "번째 " + detailGoalIntArray[i][0].toString())
            Log.d("detailGoalArray 대표목표 ", i.toString() + "번째 " + detailGoalStringArray[i][2])
            Log.d("detailGoalArray 색상 ", i.toString() + "번째 " + detailGoalIntArray[i][1].toString())
        }*/

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
            dayReport(nowTime)
        }

        // 이전 버튼 클릭 이벤트
        prevBtn1.setOnClickListener {
            // 이전 날짜의 리포트 보여주기
            dailyReportListLayout.removeAllViews()
            reportSate += -1
            dayReport(nowTime.minusDays(Math.abs(reportSate).toLong())) // 현재 상태에 맞춰서 날짜 전달
        }

        // 다음 버튼 클릭 이벤트
        nextBtn1.setOnClickListener {
            // 현재 리포트를 보고 있다면
            if (reportSate == 0) {
                Toast.makeText(context, "현재 화면이 가장 최신 리포트 화면입니다.", Toast.LENGTH_SHORT).show()
            } else { // 다음 날짜의 리포트 보여주기
                dailyReportListLayout.removeAllViews()
                reportSate += 1
                dayReport(nowTime.plusDays(Math.abs(reportSate).toLong())) // 하루 더하기
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

        // 일간 버튼 클릭 이벤트
        monthlyBtn.setOnClickListener {
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
            ?.replace(R.id.fragment_main, SetupFragment()) // 프래그먼트 이름 변경하기
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ?.commit()
    }

    // 파이차트 세팅 함수
    fun dailyPieChart(moveDate: String) {

        dailyPieChart.setUsePercentValues(true) // 100%범위로 계산

        // 데이터(시간, 대표목표) 입력
        val entry = ArrayList<PieEntry>()
        for (i in 0 until num) {
            if (bigGoalStringArray[i][1] == moveDate)
                entry.add(PieEntry(bigGoalIntArray[i][0].toFloat(), ""))
        }

        // 아이템 범위별 색상
        val itemcolor = ArrayList<Int>()
        for (i in 0 until num) {
            if (bigGoalStringArray[i][1] == moveDate)
                itemcolor.add(bigGoalIntArray[i][1].toInt())
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
        for (i in 0 until num) {
            if (bigGoalStringArray[i][1] == moveDate) { // 해당 날짜값의 시간만 가져와서 적용
                totalMilli += bigGoalIntArray[i][0]
            }
        }
        var integer_hour: Long = (totalMilli.toLong() / (1000 * 60 * 60)) % 24
        var integer_min: Long = (totalMilli.toLong() / (1000 * 60)) % 60
        dailyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"

        // 파이차트 세팅
        var isPieFlag = false
        for (i in 0 until num) { // 해당 날짜와 관련한 값이 1개라도 있다면 파이차트 생성
            if (bigGoalStringArray[i][1] == moveDate) {
                dailyPieChart(moveDate)
                dailyPieChart.visibility = View.VISIBLE
                noGoalTimeView.visibility = View.INVISIBLE
                isPieFlag = true
                break
            }
        }
        if (!isPieFlag) { // 일치하는 날짜 값이 없다면 파이차트 숨기기
            dailyPieChart.visibility = View.INVISIBLE
            noGoalTimeView.visibility = View.VISIBLE
        }

        // 동적 뷰를 활용한 세부목표 리스트 만들기
        for (i in 0 until num2) { //detailGoalArray사용
            // 동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.layout_daily_report_text, dailyReportListLayout, false)

            // 아이콘과 세부목표 동적 객체 생성
            var dailyIconImg: ImageView = view.findViewById(R.id.dailyIconImg)
            var dailyDetailTextview: TextView = view.findViewById(R.id.dailyDetailTextview)

            // 값 할당하기
            if (detailGoalStringArray[i][0].isNotBlank()) { // 아무값도 없거나 공백이 있는 경우가 아니라면
                if (detailGoalStringArray[i][1] == moveDate) { // 해당 날짜의 값이라면
                    dailyIconImg.setImageResource(detailGoalIntArray[i][0].toInt())
                    dailyIconImg.setColorFilter(detailGoalIntArray[i][1].toInt(), PorterDuff.Mode.SRC_IN)
                    dailyDetailTextview.text = detailGoalStringArray[i][0]

                    // 레이아웃에 객체 추가
                    dailyReportListLayout.addView(view)
                }
            }
        }
    }
}

/** 혹시 몰라서 남겨두는 코드 (추후 삭제 예정) **/
/* val nowTime = System.currentTimeMillis()
           val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR")).format(Date(nowTime))
           var nowDay = SimpleDateFormat("E", Locale("ko", "KR")).format(Date(nowTime))
           var date = Date(nowTime) // 시간관련한 모든 값들 ex) Wed Feb 02 15:44:48 GMT 2022
           val dateFormat = SimpleDateFormat("yyyy-MM-dd") // 년도, 월, 일
           val dayFormat = SimpleDateFormat("E") // 날짜 */

// 배열 정리하기
// 1) 세부목표 배열에서 중복되는 값을 1개만 남겨두고 삭제하기
// 2) 대표목표 배열에서 중복되는 값을 1개만 남겨두고, 남긴 1개 행에다가 시간 더해주기
// 세부목표 배열에서 중복되는 값의 인덱스 찾기
/*var indexArray = ArrayList<Int>()
for (i in 0 until num2) {
    for (j in i+1 until num2) {
        if (detailGoalStringArray[i][0] == detailGoalStringArray[j][0]) { // 세부목표 비교
            if (indexArray.size == 0) {
                indexArray.add(i)
                indexArray.add(j)
            } else {
                for (k in 0 until indexArray.size) { // 인덱스 배열에 같은 값이 없다면
                    if (indexArray[k] != i) {
                        indexArray.add(i) // 중복 위치 인덱스 저장
                    }
                    if (indexArray[k] != j) {
                        indexArray.add(j) // 중복 위치 인덱스 저장
                    }
                }
            }
        }
    }
}*/
/*var indexArray = ArrayList<Int>()
for (i in 0 until num2) {
    for (j in i+1 until num2) {
        if (detailGoalStringArray[i][0] == detailGoalStringArray[j][0]) { // 세부목표 비교
            indexArray.add(i) // 중복 값 위치 인덱스 저장
            indexArray.add(j)
        }
    }
}
// 중복값을 제외하고 리스트에 인덱스 저장
var resultArray = ArrayList<Int>()
for (i in 0 until indexArray.size) {
    var index = indexArray[i]
    if (!resultArray.contains(index)) {
        resultArray.add(index)
    }
}
Log.d("세부목표 resultArray의 size", resultArray.size.toString())
for (i in resultArray.indices) {
    Log.d("세부목표 indexArray값 ", resultArray[i].toString())
}
// 세부목표 배열에서 중복되는 값 1개 남기고 삭제
var i = 1
while (i < resultArray.size) {
    var index = resultArray[i]
    detailGoalStringArray[index][0] = ""
    detailGoalStringArray[index][1] = ""
    detailGoalStringArray[index][2] = ""
    detailGoalIntArray[index][0] = BigInteger.ZERO
    detailGoalIntArray[index][1] = BigInteger.ZERO
    ++i
}
resultArray.clear() // 리스트 초기화
indexArray.clear()

// 대표목표 배열에서 중복되는 값의 인덱스 찾기
for (i in 0 until num) {
    for (j in i+1 until  num) {
        if (bigGoalStringArray[i][0] == bigGoalStringArray[j][0] &&
                bigGoalStringArray[i][1] == bigGoalStringArray[j][1]) {
            indexArray.add(i) // 중복 값 위치 인덱스 저장
            indexArray.add(j)
        }
    }
}
// 중복값을 제외하고 리스트에 인덱스 저장
for (i in 0 until indexArray.size) {
    var index = indexArray[i]
    if (!resultArray.contains(index)) {
        resultArray.add(index)
    }
}
Log.d("대표목표 resultArray의 size", resultArray.size.toString())
for (i in indexArray.indices) {
    Log.d("대표목표 resultArray값 ", resultArray[i].toString())
}

// 대표목표 배열에서 중복되는 값 1개 남기고 삭제, 시간 더해주기
i = 1
while(i < resultArray.size) {
    var index = resultArray[i]
    bigGoalIntArray[resultArray[0]][0] += bigGoalIntArray[index][0] // 중복되는 대표목표의 시간값 더하기
    bigGoalStringArray[index][0] = ""
    bigGoalStringArray[index][1] = ""
    bigGoalIntArray[index][0] = BigInteger.ZERO
    bigGoalIntArray[index][1] = BigInteger.ZERO
    ++i
}
indexArray.clear() // 리스트 초기화
resultArray.clear()*/