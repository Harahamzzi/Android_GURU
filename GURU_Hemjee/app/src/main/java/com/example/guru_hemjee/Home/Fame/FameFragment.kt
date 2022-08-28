package com.example.guru_hemjee.Home.Fame

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentFameBinding
import java.text.SimpleDateFormat

class FameFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

    private var mBinding: FragmentFameBinding? = null // binding변수
    private val binding get() = mBinding!!

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null
        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentFameBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("Range", "SimpleDateFormat")
    override fun onStart() {
        super.onStart()

        // db open
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM complete_big_goal_db", null)
        var isFlag = false
        // DB에 있는 행의 개수 파악하기
        if (cursor.count > 0) {
            isFlag = true // 값이 있다면 true
            Log.d("cursor in", cursor.count.toString())
        } else {
            binding.fameTopGoalHsv.visibility = View.INVISIBLE
            Log.d("cursor out", cursor.count.toString())
        }

        // 명예의 전당 상단 부분 정보 찾기(최장 기록상, 최장기간상, 최다달성상, 최다앨범상)
        var cursor2: Cursor
        cursor2 = sqlitedb.rawQuery("SELECT * FROM complete_detail_goal_db", null)
        var fameGoalList = ArrayList<FameItem>() // 명예의 전당 리스트
        try {
            if (isFlag) {
                binding.fameTopGoalHsv.visibility = View.VISIBLE
                cursor = sqlitedb.rawQuery("SELECT * FROM complete_big_goal_db", null)

                var originLongTime: String = "00:00:00" // 최장 기록상(시간)
                var originTimePeriod: Int = 0   // 최장 기간상(일)
                var originGoalCount: Int = 0   // 최다 달성상(개)
                var originAlbumCount: Int = 0  // 최다 앨범상(장)

                while(cursor.moveToNext()) {
                    val tempTitle = cursor.getString(cursor.getColumnIndex("big_goal_name")) // 대표목표
                    val tempColor = cursor.getString(cursor.getColumnIndex("color")) // 색상
                    val tempRecordTime = cursor.getString(cursor.getColumnIndex("big_goal_report_time")) // 기록시간
                    val tempCompletedTime = cursor.getString(cursor.getColumnIndex("big_goal_completed_time")) // 완료된 날짜+시간
                    val tempCreatedTime = cursor.getString(cursor.getColumnIndex("big_goal_created_time")) // 생성 날짜+시간

                    // 문자열 자르기
                    val originArray = originLongTime.split(":")
                    val tempArray = tempRecordTime.split(":")

                    val originHour = originArray[0].toInt()
                    val originMin = originArray[1].toInt()
                    val originSec = originArray[2].toInt()
                    val tempHour = tempArray[0].toInt()
                    val tempMin = tempArray[1].toInt()
                    val tempSec = tempArray[2].toInt()

                    // 만약 최장 기록된 목표라면 대표목표 시간 저장 및 뷰에 반영
                    // 최장 기록상
                    var isLong = false
                    if (tempHour > originHour) { // 시간이 많다면
                        isLong = true
                    }
                    else if (tempHour == originHour && tempMin > originMin) { // 시간은 같지만 분이 다르다면
                        isLong = true
                    }
                    else if (tempHour == originHour && tempMin == originMin) { // 시분 모두 같다면
                        isLong = true
                    }

                    if (isLong) {
                        binding.fameLongRecordGoalTitleTv.text = tempTitle
                        val hour = tempHour.toString()
                        if (hour.indexOf("0") == 0) {
                            binding.fameLongRecordGoalTimeTv.text = hour.substring(1)
                        }
                        else {
                            binding.fameLongRecordGoalTimeTv.text = hour
                        }
                        originLongTime = tempRecordTime
                    }

                    // 만약 최장 기간 달성한 목표라면 대표목표 기간 저장 및 뷰에 반영
                    val sf = SimpleDateFormat("yyyy-MM-dd-E H:mm:ss")
                    val createdTime = sf.parse(tempCreatedTime)
                    val completedTime = sf.parse(tempCompletedTime)
                    val calculateDate = (completedTime.time - createdTime.time) / (60 * 60 * 24 * 1000)

                    if (calculateDate >= originTimePeriod) {
                        binding.fameLongTimeGoalTitleTv.text = tempTitle
                        binding.fameLongTimeGoalTimeTv.text = calculateDate.toString()
                        originTimePeriod = calculateDate.toInt()
                    }

                    // 최다 달성상 (하나의 세부목표가 가장 많은 횟수를 시행했을 경우)
                    while (cursor2.moveToNext()) {
                        val int_count = cursor2.getInt(cursor2.getColumnIndex("count"))
                        val str_biggoal = cursor2.getString(cursor2.getColumnIndex("big_goal_name"))

                        if (int_count >= originGoalCount) {
                            binding.fameLongCountGoalTitleTv.text = str_biggoal
                            binding.fameLongCountGoalTimeTv.text = int_count.toString()
                            originGoalCount = int_count
                        }
                    }

                    // TODO 최다 앨범상


                    Log.d("famefragment", "시간 : $tempHour")
                    Log.d("famefragment", "분 : $tempMin")
                    Log.d("famefragment", "초 : $tempSec")
                    // 리스트에 정보 추가
                    // 시:분:초 -> 초
                    var resultSec = tempHour * 3600
                    resultSec += tempMin * 60
                    resultSec += tempSec

                    // 초 -> 일 / 시간 / 분 / 초
                    val totalDay = resultSec / (60 * 60 * 24)
                    val totalHour = (resultSec - totalDay * 60 * 60 * 24) / (60 * 60)
                    val totalMinute = (resultSec - totalDay * 60 * 60 * 24 - totalHour * 3600) / 60
                    val totalSecond = resultSec % 60

                    val totalTime =
                        when {
                            totalDay > 0 -> totalDay.toString() + "일 " + totalHour.toString() + "시간"
                            totalDay == 0 && totalHour > 0 -> totalHour.toString() + "시간 " + totalMinute.toString() + "분"
                            totalHour == 0 && totalMinute > 0 -> totalMinute.toString() + "분 " + totalSecond.toString() + "초"
                            else -> totalSecond.toString() + "초"
                        }

                    // 기간 00.00.00 - 00.00.00
                    val createDateArray = tempCreatedTime.split(" ")
                    val createDateArray2 = createDateArray[0].split("-")
                    val createDate = createDateArray2[0].substring(2) + "." + createDateArray2[1] + "." + createDateArray2[2]
                    val completedDateArray = tempCompletedTime.split(" ")
                    val completedDateArray2 = completedDateArray[0].split("-")
                    val completedDate = completedDateArray2[0].substring(2) + "." + completedDateArray2[1] + "." + completedDateArray2[2]
                    val totalDate = "$createDate - $completedDate"

                    fameGoalList.add(FameItem(tempColor, tempTitle, totalTime, totalDate, calculateDate.toString(), tempCreatedTime)) // 정보 추가
                }
            }
        } catch (e: Exception) {
            Log.d("FameFragment", "명예의 전당 상단 부분 오류 " + e.printStackTrace())
            Toast.makeText(requireContext(), "명예의 전당 화면을 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
        }

        // 명예의 전당 리스트 띄우기
        val fameListAdapter = FameListAdapter(fameGoalList, requireContext())
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.fameGoalRv.layoutManager = gridLayoutManager
        binding.fameGoalRv.adapter = fameListAdapter

        // 대표목표 아이템 클릭 이벤트
        // 화면 이동
        fameListAdapter.onFameItemClickListener = { position ->
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_main, FameDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("fameItem", fameGoalList[position])
                    }
                })
                .addToBackStack(null)
                .commit()
        }

        cursor2.close()
        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }
}