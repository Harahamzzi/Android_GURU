package com.example.guru_hemjee

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    //씨앗 개수
    private lateinit var seedPointView: TextView

    //시작 버튼
    private lateinit var startButton: Button

    //잠금 수정 버튼
    private lateinit var goalSelectButton: MaterialButton

    //잠금 시간 안내
    private var time = "00:00:00"
    private lateinit var goalTime: TextView

    //db관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var userName: String

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
            userName = cursor.getString(cursor.getColumnIndex("user_name")).toString()
        }

        //잠금 시간 안내
        goalTime = requireView().findViewById(R.id.goalTime)
        goalTime.text = time.split(':')[0]+"시 " + time.split(':')[1]+"분 " +time.split(':')[2]+"초"

        //lock 화면 연결
        startButton = requireView().findViewById(R.id.startButton)
        startButton.setOnClickListener {
            showSettingConfirmPopUp()
        }

        //잠금 수정
        goalSelectButton = requireView().findViewById(R.id.goalSelectButton)
        goalSelectButton.setOnClickListener {
            showLockSettingPopUp()
        }
    }

    //잠금 시작
    private fun showSettingConfirmPopUp() {
        val dialog = LockSettingConfirmDialog(requireContext(),time)
        dialog.myDig()

        dialog.setOnClickedListener(object : LockSettingConfirmDialog.ButtonClickListener{
            override fun onClicked(isLock: Boolean) {
                if(isLock){
                    // 잠금 서비스 실행
                    LockScreenUtil.active()

                    var intent = Intent(requireActivity(), LockActivity::class.java)

                    // 유저 정보 보내기
                    intent.putExtra("seed", seedPointView.text)
                    intent.putExtra("userName", userName)

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
        val dialog = LockSettingDialog(requireContext(), goalSelectButton.text.toString(), time)
        dialog.lockSetting()

        dialog.setOnClickedListener(object : LockSettingDialog.ButtonClickListener{
            override fun onClicked(isChanged: Boolean, bigGoalTitle: String?, getTime: String) {
                if(isChanged){
                    goalSelectButton.text = bigGoalTitle
                    time = getTime
                    var timeArray = time.split(':')
                    goalTime.text = timeArray[0] + "시 " + timeArray[1]+"분 " + timeArray[2]+"초"
                }
            }
        })
    }


}