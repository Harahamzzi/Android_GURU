package com.harahamzzi.android.Home.Fame

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
import com.harahamzzi.android.AlertDialog
import com.harahamzzi.android.DBManager
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.FragmentFameBinding
import java.text.SimpleDateFormat

class FameFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

    private var mBinding: FragmentFameBinding? = null // binding변수
    private val binding get() = mBinding!!

    private var fameGoalList = ArrayList<FameItem>() // 명예의 전당 리스트
    private lateinit var fameListAdapter: FameListAdapter // 어댑터

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

        // 리스트 초기화
        fameGoalList.clear()

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
        var cursor2: Cursor? = null
        var cursor3: Cursor? = null
        cursor2 = sqlitedb.rawQuery("SELECT * FROM complete_detail_goal_db", null)
        try {
            if (isFlag) {
                binding.fameTopGoalHsv.visibility = View.VISIBLE
                cursor = sqlitedb.rawQuery("SELECT * FROM complete_big_goal_db", null)

                var originLongTime: String = "00:00:00" // 최장 기록상(시간)
                var originTimePeriod: Int = 0   // 최장 기간상(일)
                var originGoalCount: Int = 0   // 최다 달성상(개)
                var albumCountList = ArrayList<MutableMap<String, String>>() // 최다 앨범상(장)

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
                        if (hour.indexOf("0") == 0 && hour.length != 1) {
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

                    // 하나의 대표목표 중 가장 많은 사진을 보유한 목표에게 주는 상
                    // 최다 앨범상
                    cursor3 = sqlitedb.rawQuery("SELECT count FROM complete_detail_goal_db WHERE big_goal_name = '$tempTitle' AND big_goal_created_time = '$tempCreatedTime';", null)
                    var tempNum = 0
                    while (cursor3.moveToNext()) {
                        // 횟수 전부 더하기
                        val int_count = cursor3.getInt(cursor3.getColumnIndex("count"))

                        tempNum += int_count
                    }
                    albumCountList.add(mutableMapOf(
                        "big_goal_name" to tempTitle,
                        "count" to tempNum.toString(),
                        "created_time" to tempCreatedTime
                    ))

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

                // 최다 앨범상 뷰에 적용하기
                try {
                    var originCount = 0
                    var originTitle: String? = ""
                    var albumCount = 0
                    var albumTitle: String? = ""

                    Log.d("최다 앨범상 최종 리스트", albumCountList.toString())

                    for (i in 0 until albumCountList.size) {
                        albumCount = albumCountList[i]["count"]?.toInt() ?: 0
                        albumTitle = albumCountList[i]["big_goal_name"]

                        if (albumCount >= originCount) {
                            originCount = albumCount
                            originTitle = albumTitle
                        }
                    }

                    binding.fameLongAlbumGoalTitleTv.text = originTitle
                    binding.fameLongAlbumGoalNumTv.text = originCount.toString()
                } catch (e: Exception) {
                    Log.d("FameFragment", "리스트에 값이 없음 " + e.printStackTrace())
                }
            }
        } catch (e: Exception) {
            Log.d("FameFragment", "명예의 전당 상단 부분 오류 " + e.printStackTrace())
            Toast.makeText(requireContext(), "화면을 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
        }

        // 명예의 전당 리스트 띄우기
        fameListAdapter = FameListAdapter(requireContext())
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.fameGoalRv.layoutManager = gridLayoutManager
        binding.fameGoalRv.adapter = fameListAdapter

        for (i in 0 until fameGoalList.size) {
            fameListAdapter.addGoalItem(fameGoalList[i])
        }

        cursor3?.close()
        cursor2.close()
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 클릭 이벤트
        initClickEvent()
    }

    // 클릭 이벤트 함수
    private fun initClickEvent() {
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

        // 대표목표 아이템 롱 클릭 이벤트
        fameListAdapter.onFameItemLongClickListener = { position ->
            // 삭제 팝업 띄우기
            val dialog = AlertDialog(requireContext(), "해당 목표를 삭제하시겠습니까?", "해당 목표의 모든 기록이 삭제되며\n" +
                    "복구는 불가능합니다.", "삭제", 0)
            dialog.showAlertDialog()

            dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
                override fun onClicked(isConfirm: Boolean) {
                    // 삭제 버튼을 눌렀을 경우 db 및 리사이클러뷰에서 아이템 삭제
                    if (isConfirm) {
                        val title = fameGoalList[position].title
                        val time = fameGoalList[position].time
                        val color = fameGoalList[position].color
                        val totalDate = fameGoalList[position].totalDate
                        val totalPeriod = fameGoalList[position].totalPeriod
                        val createdDate = fameGoalList[position].createdDate

                        val sqlitedb2 = dbManager.writableDatabase

                        sqlitedb2.execSQL("DELETE FROM complete_big_goal_db WHERE big_goal_name = '${title}' AND big_goal_created_time = '${createdDate}';")
                        val fameItem = FameItem(color, title, time, totalDate, totalPeriod, createdDate)
                        fameListAdapter.removeGoalItem(fameItem, position)
                        Toast.makeText(requireContext(), "목표를 삭제했습니다", Toast.LENGTH_SHORT).show()

                        sqlitedb2.close()
                        dbManager.close()
                    }
                }

                override fun onDismiss() {

                }
            })
        }
    }
}