package com.example.guru_hemjee.Home.Fame

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.databinding.FragmentFameBinding
import java.text.SimpleDateFormat

class FameFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private var dbManager: DBManager = DBManager(requireContext(), "hamster_db", null, 1)
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

    private var mBinding: FragmentFameBinding? = null // binding변수
    private val binding get() = mBinding!!

    override fun onDestroy() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroy()
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
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM complete_big_goal_db", null)
        var isFlag = false
        // DB에 있는 행의 개수 파악하기
        while(cursor.moveToNext()) {
            if (cursor.count > 0) {
                cursor.close()
                isFlag = true // 값이 있다면 true
            } else {
                cursor.close()
            }
        }

        // 명예의 전당 상단 부분 정보 찾기(최장 기록상, 최장기간상, 최다달성상, 최다앨범상)
        if (isFlag) {

            cursor = sqlitedb.rawQuery("SELECT * FROM complete_big_goal_db", null)

            var originRecordTime: String = "00:00:00" // 최장 기록상(시간)
            var originTimeTime: Int = 0   // 최장 기간상(일)
            var originMoreTime: Int = 0   // 최다 달성상(개)
            var originAlbumTime: Int = 0  // 최다 앨범상(장)

            while(cursor.moveToNext()) {
                var tempTitle = cursor.getString(cursor.getColumnIndex("big_goal_name"))
                var tempRecordTime = cursor.getString(cursor.getColumnIndex("big_goal_report_time"))

                // 문자열 자르기
                var originArray = originRecordTime.split(":")
                var tempArray = tempRecordTime.split(":")

                // 만약 최장 기록된 목표라면 대표목표 시간 저장 및 뷰에 반영
                if (tempArray[0].toBigInteger() > originArray[0].toBigInteger()) { // 시간이 많다면
                    binding.fameLongRecordGoalTitleTv.text = tempTitle
                    binding.fameLongRecordGoalTimeTv.text = tempArray[0]
                    originRecordTime = tempRecordTime
                }
                else if (tempArray[0] == originArray[0] &&
                    tempArray[1].toBigInteger() > originArray[1].toBigInteger()) { // 시간은 같지만 분이 많다면
                    binding.fameLongRecordGoalTitleTv.text = tempTitle
                    binding.fameLongRecordGoalTimeTv.text = tempArray[0]
                    originRecordTime = tempRecordTime
                }

                // 만약 최장 기간 달성한 목표라면 대표목표 기간 저장 및 뷰에 반영
                var tempCreatedTime = cursor.getString(cursor.getColumnIndex("big_goal_created_time"))
                var tempCompletedTime = cursor.getString(cursor.getColumnIndex("big_goal_completed_time"))

                var sf = SimpleDateFormat("yyyy-MM-dd H:mm:ss")
                var createdTime = sf.parse(tempCreatedTime)
                var completedTime = sf.parse(tempCompletedTime)
                var calculateDate = (completedTime.time - createdTime.time) / (60 * 60 * 24 * 1000)

                if (calculateDate >= originTimeTime) {
                    binding.fameLongTimeGoalTitleTv.text = tempTitle
                    binding.fameLongTimeGoalTimeTv.text = calculateDate.toString()
                    originTimeTime = calculateDate.toInt()
                }
            }
        }
    }
}