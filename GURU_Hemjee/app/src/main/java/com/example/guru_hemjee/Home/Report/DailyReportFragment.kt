package com.example.guru_hemjee.Home.Report

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
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
import com.example.guru_hemjee.databinding.FragmentDailyReportBinding
import java.math.BigInteger
import kotlin.collections.ArrayList

// 홈(MainActivity) -> 목표 리포트
// 일간별 분석 리포트를 보여주는 Fragment 화면
// 해당 날짜에서의 목표별 총 잠금시간/총 잠금시간 비율로 통계를 보여준다.
class DailyReportFragment : Fragment() {

    // 태그
    private var TAG = "DailyReportFragment"

    // 뷰 바인딩 변수
    private var mBinding: FragmentDailyReportBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // db
    private lateinit var dbManager: DBManager
    private lateinit var sqlite: SQLiteDatabase

    // 뒤로가기 콜백
    private lateinit var callback: OnBackPressedCallback

    // 리포트 데이터
    // 대표목표 이름 목록, 누적 기록 시간 목록, 색상 아이디 값, 세부목표 목록
    data class ReportData(
            var date: String,                           // 날짜값: yyyy-MM-dd-E 형태
            var bigGoalNameList: ArrayList<String>,     // 대표목표 이름 목록
            var bigGoalTimeList: ArrayList<BigInteger>, // 각 대표목표의 누적 기록 시간 목록
            var colorIDList: ArrayList<Int>,            // 색상 아이디 값 목록

            // 세부목표 목록
            // 세부목표 이름, 해당하는 대표목표 이름, 아이콘 값 저장
            var detailGoalDataList: ArrayList<MutableMap<String, String>>
            )

    private var reportDataList = ArrayList<ReportData>()
    private var isDetailGoalInitialized = false

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mBinding = FragmentDailyReportBinding.inflate(inflater, container, false)

        // 달력 버튼 클릭 이벤트
        binding.reportDailyCalenderImageButton.setOnClickListener {
            // 최상단으로 이동하게 함
            binding.reportDailyReportRecyclerView.smoothScrollToPosition(0)
        }

        // 주간 버튼 클릭 이벤트
        binding.reportDailyWeeklyButton.setOnClickListener {
            // 주간 리포트로 이동
            requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_main, WeeklyReportFragment())
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

        // 화면에 접속할 때마다 레이아웃 및 데이터 초기화
        binding.reportDailyReportRecyclerView.removeAllViews()
        for (i in reportDataList.indices)
        {
            reportDataList.removeFirst()
        }

        /** 데이터 값 세팅 **/

        // DB 열기
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        // 대표 목표 리포트 DB 읽기(내림차순(최근날짜 먼저), 중복날짜 제거) - 날짜 가져오기 위함
        var cursor: Cursor = sqlite.rawQuery("SELECT DISTINCT substr(lock_date, 1, 12) AS date FROM big_goal_time_report_db ORDER BY lock_date DESC", null)

        while (cursor.moveToNext())
        {
            // dateStr: yyyy-MM-dd-E 형태의 문자열
            var dateStr = cursor.getString(cursor.getColumnIndex("date")).toString()
            Log.d(TAG, dateStr)

            reportDataList.add(findDailyReportData(dateStr))
        }

        cursor.close()
        sqlite.close()
        dbManager.close()

        // Layout Manager 및 어댑터 연결
        var layoutManager = LinearLayoutManager(requireContext())
        binding.reportDailyReportRecyclerView.layoutManager = layoutManager

        var dailyReportAdapter = DailyReportListAdapter(requireContext(), reportDataList)
        binding.reportDailyReportRecyclerView.adapter = dailyReportAdapter

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

    // 일간 리포트에 들어갈 데이터를 리스트에 저장하고 반환하는 함수
    // nowDate: yyyy-MM-dd-E 형태의 문자열
    @SuppressLint("Range")
    private fun findDailyReportData(nowDate: String): ReportData {

        // DB 열기
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlite = dbManager.readableDatabase

        var nowDateBigGoalList = ArrayList<String>()            // 오늘 수행한 대표목표를 저장하는 리스트(중복값 저장X)
        var nowDateBigGoalTimeList = ArrayList<BigInteger>()    // 오늘 수행한 대표목표의 각 잠금 시간을 저장하는 리스트
        var nowDateBigGoalColorList = ArrayList<Int>()          // 오늘 수행한 대표목표의 각 색상 아이디값을 저장하는 리스트

        // 대표 목표 리포트 DB 읽기 - 해당 날짜의 데이터만 가져오도록 함
        var cursor: Cursor = sqlite.rawQuery("SELECT * FROM big_goal_time_report_db WHERE lock_date LIKE '${nowDate}%'", null)
        while (cursor.moveToNext()) {

            var str_big_goal: String = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()

            var str_reportTime: String = cursor.getString(cursor.getColumnIndex("total_report_time")).toString()
            var bigint_report_time: BigInteger = TimeConvert.timeToMsConvert(str_reportTime)

            // 해당 대표목표의 색상값 뽑아오기
            var int_color = 0
            var tempCursor: Cursor = sqlite.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '${str_big_goal}'", null)
            if (tempCursor.moveToNext())
            {
                var tempColor = requireActivity().resources.getIdentifier(tempCursor.getString(tempCursor.getColumnIndex("color")), "color", requireActivity().packageName)
                int_color = requireActivity().resources.getColor(tempColor)
            }
            tempCursor.close()

            // 중복값을 찾기 위한 flag변수 (중복값O: true, 중복값X: false)
            var isFlag: Boolean = false

            // 오늘 수행한 대표목표와 시간을 ArrayList에 추가하기
            for (i in 0 until nowDateBigGoalList.size) {

                // 대표 목표와 색상이 중복된다면
                if (nowDateBigGoalList[i] == str_big_goal && nowDateBigGoalColorList[i] == int_color) {
                    nowDateBigGoalTimeList[i] += bigint_report_time
                    isFlag = true
                    break
                }
            }
            if (!isFlag) { // 목표만 중복되거나 중복값이 없다면 대표목표, 시간, 색상 저장
                nowDateBigGoalList.add(str_big_goal)
                nowDateBigGoalTimeList.add(bigint_report_time)
                nowDateBigGoalColorList.add(int_color)
            }
        }

        cursor.close()

        // 세부목표 리포트 db에서 저장된 값 읽어오기(세부목표, 잠금 날짜)
        var detailGoalDataList = ArrayList<MutableMap<String, String>>()

        var cursor2: Cursor
        cursor2 = sqlite.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE lock_date LIKE '${nowDate}%'", null)

        while (cursor2.moveToNext()) {
            var str_detail_goal = cursor2.getString(cursor2.getColumnIndex("detail_goal_name"))
            var str_big_goal = cursor2.getString(cursor2.getColumnIndex("big_goal_name")).toString()

            var str_iconName = ""
            var tempCursor: Cursor = sqlite.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '${str_detail_goal}'", null)
            if (tempCursor.moveToNext())
            {
                str_iconName = tempCursor.getString(tempCursor.getColumnIndex("icon")).toString()
            }
            tempCursor.close()

            // 배열에 읽어온 값 저장 (같은 날짜 내에 중복값 저장X)
            var isFlag: Boolean = false // 중복값 확인
            if (!isDetailGoalInitialized) {
                detailGoalDataList = arrayListOf(
                        mutableMapOf(
                                "detail_goal_name" to str_detail_goal,
                                "icon" to str_iconName,
                                "big_goal_name" to str_big_goal
                        )
                )
                isDetailGoalInitialized = true

            } else {
                var i = 0
                //기존에 값이 없을 때만 새로 추가
                while (i < detailGoalDataList.size) {
                    if (detailGoalDataList[i]["detail_goal_name"] == str_detail_goal) {
                        isFlag = true
                        break
                    }
                    i++
                }
                if (!isFlag) {
                    detailGoalDataList.add(
                            mutableMapOf(
                                    "detail_goal_name" to str_detail_goal,
                                    "icon" to str_iconName,
                                    "big_goal_name" to str_big_goal
                            )
                    )
                }
            }
        }

        cursor2.close()
        sqlite.close()
        dbManager.close()

        // 오늘 날짜와 오늘 수행한 대표목표, 시간, 색상값을 저장한 리스트 반환
        return ReportData(nowDate, nowDateBigGoalList, nowDateBigGoalTimeList, nowDateBigGoalColorList, detailGoalDataList)
    }
}