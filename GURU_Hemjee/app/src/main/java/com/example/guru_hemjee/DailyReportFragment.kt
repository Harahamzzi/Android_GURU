package com.example.guru_hemjee

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DailyReportFragment : Fragment() {

    // db
    lateinit var dbManager: DBManager
    lateinit var sqlite: SQLiteDatabase

    // 라디오 그룹 & 라디오 버튼
    lateinit var reportRadioGroup: RadioGroup
    lateinit var dailyRadioBtn: RadioButton
    lateinit var weeklyRadioBtn: RadioButton
    lateinit var monthlyRadioBtn: RadioButton

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
    var nowTime = LocalDateTime.now()
    var nowDate = nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E")) // 년도, 월, 일, 요일

    // 2차원 배열(대표목표, 대표목표 수행시간을 저장)
    var bigGoalArray = Array(10, {Array(4, {""}) }) // 10행 4열, 하나의 행에 (대표목표,시간,날짜,색상) 순으로 저장
    var num = 0 // bigGoalArray index

    // 2차원 배열(세부목표, 세부목표 아이콘을 저장)
    var detailGoalArray = Array(20, {Array(5, {""}) }) // 20행 3열, 하나의 행에 (세부목표,날짜,아이콘,대표목표,색상) 순으로 저장
    var num2 = 0 // detailGoalArray index

    // SimpleDateFormat 형태
    val dateFormat = SimpleDateFormat("yyyy-MM-dd") // 년도, 월, 일
    val dayFormat = SimpleDateFormat("E") // 날짜

    var mainActivity : SubMainActivity? = null // 서브 메인 액티비티 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_daily_report, container, false)

        reportRadioGroup = view.findViewById(R.id.reportRadioGroup)
        dailyRadioBtn = view.findViewById(R.id.dailyRadioBtn)
        weeklyRadioBtn = view.findViewById(R.id.weeklyRadioBtn)
        monthlyRadioBtn = view.findViewById(R.id.monthlyRadioBtn)
        moveTodayButton = view.findViewById(R.id.moveTodayButton)
        dailyTextview = view.findViewById(R.id.dailyTextview)
        dailyTimeTextview = view.findViewById(R.id.dailyTimeTextview)
        prevBtn1 = view.findViewById(R.id.prevBtn1)
        nextBtn1 = view.findViewById(R.id.nextBtn1)
        dailyPieChart = view.findViewById(R.id.dailyPieChart)
        dailyReportListLayout = view.findViewById(R.id.dailyReportListLayout)
        noGoalTimeView = view.findViewById(R.id.noGoalTimeView)

        // 대표목표 리포트 db에 저장된 값 읽어오기(대표목표 값, 대표목표 총 수행 시간, 잠금 날짜)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db", null)

        // 모든 값들 배열에 저장
        while (cursor.moveToNext()) {
            var str_big_goal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            var bigint_time = cursor.getInt(cursor.getColumnIndex("total_lock_time")).toBigInteger()
            var str_date = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

            // 배열에 읽어온 값 저장
            bigGoalArray[num][0] = str_big_goal // 대표목표
            bigGoalArray[num][1] = bigint_time.toString() // 대표목표 총 수행 시간
            bigGoalArray[num][2] = str_date // Date값
            ++num
        }
        cursor.close()
        sqlite.close()
        dbManager.close()

        // 대표목표 색상값 가져와서 배열에 저장
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor2: Cursor
        for (i in 0..num) {
            cursor2 = sqlite.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '" + bigGoalArray[i][0] + "';", null)

            while (cursor2.moveToNext()) {
                var int_color = cursor2.getInt(cursor2.getColumnIndex("color"))

                // 배열에 읽어온 값 저장
                bigGoalArray[i][3] = int_color.toString() // 대표목표 색상
            }
            cursor2.close()
        }
        sqlite.close()
        dbManager.close()

        // 세부목표 리포트 db에서 저장된 값 읽어오기(세부목표, 잠금 날짜)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor3: Cursor

        for (i in 0..num) {
            cursor3 = sqlite.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

            while (cursor3.moveToNext()) {
                var str_detail_goal = cursor3.getString(cursor3.getColumnIndex("detail_goal_name"))
                var str_date = cursor3.getString(cursor3.getColumnIndex("lock_date"))

                // 배열에 읽어온 값 저장
                detailGoalArray[i][0] = str_detail_goal // 세부목표
                detailGoalArray[i][1] = str_date // 잠금 날짜
                ++num2
            }
            cursor3.close()
        }
        dbManager.close()
        sqlite.close()

        // 세부목표 db에서 저장된 값 읽어오기(아이콘, 대표목표)
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var cursor4: Cursor
        for (i in 0..num2) {
            cursor4 = sqlite.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '" + detailGoalArray[i][0] + "';", null)

            while (cursor4.moveToNext()) {
                var int_icon = cursor4.getInt(cursor4.getColumnIndex("icon"))
                var str_big_goal_name = cursor4.getString(cursor4.getColumnIndex("big_goal_name"))

                // 배열에 읽어온 값 저장
                detailGoalArray[i][2] = int_icon.toString() // 아이콘
                detailGoalArray[i][3] = str_big_goal_name // 대표목표
            }
            cursor4.close()
        }
        dbManager.close()
        sqlite.close()

        // 각 배열에 있는 대표목표 값이 같다면 detailGoalArray 배열에 색상 추가하기
        for (i in 0..num) {
            for (j in 0..num2) {
                if (bigGoalArray[i][0] == detailGoalArray[j][3]) {
                    detailGoalArray[j][4] = bigGoalArray[i][3]
                }
            }
        }

        /* val nowTime = System.currentTimeMillis()
           val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR")).format(Date(nowTime))
           var nowDay = SimpleDateFormat("E", Locale("ko", "KR")).format(Date(nowTime))
           var date = Date(nowTime) // 시간관련한 모든 값들 ex) Wed Feb 02 15:44:48 GMT 2022
           val dateFormat = SimpleDateFormat("yyyy-MM-dd") // 년도, 월, 일
           val dayFormat = SimpleDateFormat("E") // 날짜 */

        // 위젯에 값 적용하기(날짜, 총 수행 시간)
        val splitDate = nowDate.split('-') // 년도, 월, 일, 요일
        dailyTextview.text = splitDate[1] + "월 " + splitDate[2] + "일 " + splitDate[3] + "요일"

        for (i in 0..num) {
            Log.d("bigGoalArray 대표목표 ", bigGoalArray[i][0])
            Log.d("bigGoalArray 시간 ", bigGoalArray[i][1])
            Log.d("bigGoalArray 날짜 ", bigGoalArray[i][2])
            Log.d("bigGoalArray 색상 ", bigGoalArray[i][3])
        }
        for (i in 0..num2) {
            Log.d("detailGoalArray 세부목표 ", detailGoalArray[i][0])
            Log.d("detailGoalArray 날짜 ", detailGoalArray[i][1])
            Log.d("detailGoalArray 아이콘 ", detailGoalArray[i][2])
            Log.d("detailGoalArray 대표목표 ", detailGoalArray[i][3])
            Log.d("detailGoalArray 색상 ", detailGoalArray[i][4])
        }

        lateinit var totalMilli: BigInteger
        for (i in 0..num) {
            try {
                totalMilli += bigGoalArray[i][1].toBigInteger()
            } catch (e: NumberFormatException) {
                Log.i("NumberFormatException", "totalMilli로 바꿀 때 오류")
                totalMilli = BigInteger.ZERO
            } catch (e: Exception) {
                Log.i("Exception", "totalMilli 오류!")
                totalMilli = BigInteger.ZERO
            }
        }
        var integer_hour: Long = (totalMilli.toLong() / (1000 * 60 * 60)) % 24
        var integer_min: Long = (totalMilli.toLong() / (1000 * 60)) % 60
        dailyTimeTextview.text = integer_hour.toString() + "시간 " + integer_min.toString() + "분"

        // 파이차트 세팅
        if (bigGoalArray.isNotEmpty() && detailGoalArray.isNotEmpty()) {
            dailyPieChart()
            noGoalTimeView.visibility = View.INVISIBLE
        } else {
            dailyPieChart.visibility = View.INVISIBLE
            noGoalTimeView.visibility = View.VISIBLE
        }

        // 동적 뷰를 활용한 세부목표 리스트 만들기
        for (i in 0..num2) { //detailGoalArray사용
            // 동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.layout_daily_report_text, dailyReportListLayout, false)

            // 아이콘과 세부목표 동적 객체 생성
            var dailyIconImg: ImageView = view.findViewById(R.id.dailyIconImg)
            var dailyDetailTextview: TextView = view.findViewById(R.id.dailyDetailTextview)

            // 값 할당하기
            try {
                dailyIconImg.setImageResource(detailGoalArray[i][2].toInt())
                dailyIconImg.setColorFilter(detailGoalArray[i][4].toInt(), PorterDuff.Mode.SRC_IN)
                dailyDetailTextview.text = detailGoalArray[i][0]
            } catch (e: NumberFormatException) {
                Log.i("NumberFormatException", "동적뷰 값 할당 오류")
            } catch (e: Exception) {
                Log.i("Exception", "동적뷰 오류!")
            }
        }

        // 달력 버튼 클릭 이벤트
        moveTodayButton.setOnClickListener {
            // 현재 날짜의 리포트를 보여주기


        }

        // 이전 버튼 클릭 이벤트
        prevBtn1.setOnClickListener {
            // 이전 날짜의 리포트 보여주기

        }

        // 다음 버튼 클릭 이벤트
        nextBtn1.setOnClickListener {
            // 다음 날짜의 리포트 보여주기

        }

        // 라디오 버튼 클릭 이벤트
        reportRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // id값에 따라 화면 이동(일간, 주간, 월간)
                weeklyRadioBtn.id -> goMonthlyReport()
                monthlyRadioBtn.id -> goMonthlyReport()
            }
        }

        return view
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
    fun dailyPieChart() {

        dailyPieChart.setUsePercentValues(true) // 100%범위로 계산

        // 데이터 입력
        val entry = ArrayList<PieEntry>()
        for (i in 0..num) {
            try {
                entry.add(PieEntry(bigGoalArray[i][1].toFloat(), bigGoalArray[i][0]))
            } catch (e: NumberFormatException) {
                Log.i("NumberFormatException", "파이차트 데이터 오류")
            } catch (e: Exception) {
                Log.i("Exception", "파이차트 오류!")
            }
        }

        // 아이템 범위별 색상
        val itemcolor = ArrayList<Int>()
        for (i in 0..num) {
            try {
                itemcolor.add(bigGoalArray[i][3].toInt())
            } catch (e: NumberFormatException) {
                Log.i("NumberFormatException", "파이차트 색상 오류")
            } catch (e: Exception) {
                Log.i("Exception", "파이차트 오류!")
            }
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
            description.isEnabled = false // 그래프이름
            isRotationEnabled = false // 애니메이션 효과
            setEntryLabelColor(R.color.Black)
            animateY(1400, Easing.EaseInOutQuad) // 최초 애니메이션
            animate()
        }
    }

    // 동적으로 세부목표 리스트 추가
    fun addDetailGoalList() {

    }
}