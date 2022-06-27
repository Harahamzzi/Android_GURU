package com.example.guru_hemjee.Home.Home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.graphics.PorterDuff
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import java.util.*

//잠금 확인 팝업
class LockSettingConfirmDialog(val context: Context, goalName: String, goalColor: Int, time: String) {
    private val dialog = Dialog(context)

    //잠금 시작, 취소 버튼
    private lateinit var pop_settingOkImageButton2: ImageButton
    private lateinit var pop_lockCancelImageButton2: ImageButton

    //시간 관련
    private var timeArray = time.split(':')
    private lateinit var hour: TextView
    private lateinit var min: TextView
    private lateinit var sec: TextView

    //대표 목표 관련
    private lateinit var pop_goalTitleTextView2: TextView
    private lateinit var pop_lockSetConfirmGoalColorImageView2: ImageView
    private var goalName = goalName
    private var goalColor = goalColor

    //달성 가능한 세부 목표
    private lateinit var pop_lockSettingConfirmDetailGoalRecyclerView: RecyclerView

    //팝업 표시
    @SuppressLint("Range")
    fun myDig(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting_confirm)

        //선택된 대표 목표로 수정(색도 표시)
        pop_goalTitleTextView2 = dialog.findViewById(R.id.pop_goalTitleTextView2)
        pop_lockSetConfirmGoalColorImageView2 = dialog.findViewById(R.id.pop_lockSetConfirmGoalColorImageView2)
        pop_goalTitleTextView2.text = goalName
        pop_lockSetConfirmGoalColorImageView2.setColorFilter(goalColor, PorterDuff.Mode.SRC_IN)

        //시간 연결
        hour = dialog.findViewById(R.id.pop_hourTimeTextView)
        hour.text = timeArray[0]
        min = dialog.findViewById(R.id.pop_minTmeTextView)
        min.text = timeArray[1]
        sec = dialog.findViewById(R.id.pop_secTimeTextView)
        sec.text = timeArray[2]

        //잠금 버튼
        pop_settingOkImageButton2 = dialog.findViewById<ImageButton>(R.id.pop_settingOkImageButton2)
        pop_settingOkImageButton2.setOnClickListener {
            onClickListener.onClicked(true)
            dialog.dismiss()
        }

        //취소 버튼
        pop_lockCancelImageButton2 = dialog.findViewById<ImageButton>(R.id.pop_lockCancelImageButton2)
        pop_lockCancelImageButton2.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }

        //세부 목표 리스트 연결
        pop_lockSettingConfirmDetailGoalRecyclerView = dialog.findViewById(R.id.pop_lockSettingConfirmDetailGoalRecyclerView)
        if(goalName != "목표를 생성해주세요"){
            val items = ArrayList<DetailGoalListItem>()
            val detailGoalListAdapter = DetailGoalListAdapter(context, items)
            pop_lockSettingConfirmDetailGoalRecyclerView.adapter = detailGoalListAdapter

            var dbManager = DBManager(context, "hamster_db", null, 1)
            var sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE " +
                    "big_goal_name = '$goalName'", null)

            while(cursor.moveToNext()){
                val goalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
                val iconPic = cursor.getInt(cursor.getColumnIndex("icon"))

                items.addAll(listOf(DetailGoalListItem(iconPic, goalColor, goalName)))
                detailGoalListAdapter.notifyDataSetChanged()
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()
        }
    }

    interface ButtonClickListener {
        fun onClicked(isLock: Boolean)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}
