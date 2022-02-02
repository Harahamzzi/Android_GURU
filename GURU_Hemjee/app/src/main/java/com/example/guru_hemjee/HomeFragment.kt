package com.example.guru_hemjee

import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    //씨앗 개수
    private lateinit var seedPointView: TextView

    //시작 버튼
    private lateinit var startButton: Button

    //잠금 수정 버튼
    private lateinit var goalSelectButton: MaterialButton
    private var bigGoalName: String = ""
    private var bigGoalColor: Int = 0

    //잠금 시간 안내
    private var time = "00:00:00"
    private lateinit var goalTime: TextView

    //햄찌 관련
    private lateinit var mainBGFrameLayout: FrameLayout
    private lateinit var mainClothFrameLayout: FrameLayout

    //db관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var hamName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        //씨앗 표시
        seedPointView = requireView().findViewById(R.id.Home_seedPointView)
        dbManager = DBManager(requireContext(), "basic_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
            seedPointView.text = cursor.getString(cursor.getColumnIndex("seed")).toString()
            hamName = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //잠금 버튼 수정
        goalSelectButton = requireView().findViewById(R.id.goalSelectButton)
        dbManager = DBManager(context, "big_goal_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor= sqlitedb.rawQuery("SELECT * FROM big_goal_db",null)
        var isThereBigGoal = false
        if(cursor.moveToNext()){
            bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))
            time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time"))

            goalSelectButton.text = bigGoalName
            goalSelectButton.setTextColor(resources.getColor(R.color.Black))
            goalSelectButton.iconTint = ColorStateList.valueOf(bigGoalColor)

            isThereBigGoal = true
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        if(!isThereBigGoal){
            bigGoalName = "목표를 생성해주세요"
            bigGoalColor = R.color.Gray
            goalSelectButton.text = bigGoalName
            goalSelectButton.setTextColor(bigGoalColor)
            goalSelectButton.setIconTintResource(R.color.Gray)
        }
        goalSelectButton.setOnClickListener {
            showLockSettingPopUp()
        }

        //메인 화면 햄찌 설정
        mainBGFrameLayout = requireView().findViewById(R.id.mainBGFrameLayout)
        mainClothFrameLayout = requireView().findViewById(R.id.mainClothFrameLayout)
        FunUpDateHamzzi.upDate(requireContext(), mainBGFrameLayout, mainClothFrameLayout, false)

        //잠금 시간 안내
        goalTime = requireView().findViewById(R.id.goalTime)
        goalTime.text = time.split(':')[0]+"시 " + time.split(':')[1]+"분 " +time.split(':')[2]+"초"

        //lock 화면 연결
        startButton = requireView().findViewById(R.id.startButton)
        startButton.setOnClickListener {
            showSettingConfirmPopUp()
        }


    }

    //잠금 시작
    private fun showSettingConfirmPopUp() {
        val dialog = LockSettingConfirmDialog(requireContext(),bigGoalName,bigGoalColor,time)
        dialog.myDig()

        dialog.setOnClickedListener(object : LockSettingConfirmDialog.ButtonClickListener{
            override fun onClicked(isLock: Boolean) {
                if(isLock){
                    // 잠금 서비스 실행
                    LockScreenUtil.active()

                    var intent = Intent(requireActivity(), LockActivity::class.java)

                    // 유저 정보 보내기
                    intent.putExtra("seed", seedPointView.text)
                    intent.putExtra("hamsterName", hamName)

                    // 대표 목표 이름 보내기
                    intent.putExtra("bigGoalName", goalSelectButton.text)

                    // 타이머 시간 데이터 보내기
                    intent.putExtra("hour", time.split(':')[0])
                    intent.putExtra("min", time.split(':')[1])
                    intent.putExtra("sec", time.split(':')[2])

                    startActivity(intent)

                }
            }
        })
    }

    //잠금 설정
    private fun showLockSettingPopUp() {
        val dialog = LockSettingDialog(requireContext(), bigGoalName, bigGoalColor, time)
        dialog.lockSetting()

        dialog.setOnClickedListener(object : LockSettingDialog.ButtonClickListener{
            override fun onClicked(isChanged: Boolean, bigGoalTitle: String, changedBigGoalColor: Int, getTime: String) {
                if(isChanged){
                    bigGoalName = bigGoalTitle
                    goalSelectButton.text = bigGoalTitle
                    bigGoalColor = changedBigGoalColor
                    goalSelectButton.iconTint = ColorStateList.valueOf(bigGoalColor)
                    if(getTime != "00:00:00"){
                        time = getTime
                    }
                    var timeArray = time.split(':')
                    goalTime.text = timeArray[0] + "시 " + timeArray[1]+"분 " + timeArray[2]+"초"
                }
            }
        })
    }


}