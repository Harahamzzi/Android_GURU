package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.PorterDuff
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.util.*

class LockSettingConfirmDialog(val context: Context, goalName: String, goalColor: Int, time: String) {
    private val dialog = Dialog(context)

    private lateinit var lock: ImageButton
    private lateinit var cancel: ImageButton

    //시간 관련
    private var timeArray = time.split(':')
    private lateinit var hour: TextView
    private lateinit var min: TextView
    private lateinit var sec: TextView

    //대표 목표 관련
    private lateinit var goalTitleTextView: TextView
    private lateinit var goalColorImageView: ImageView
    private var goalName = goalName
    private var goalColor = goalColor

    //달성 가능한 세부 목표
    private lateinit var detailGoalItemRecyclerView: RecyclerView

    fun myDig(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting_confirm)

        //대표 목표 수정하기
        goalTitleTextView = dialog.findViewById(R.id.goalTitleTextView)
        goalColorImageView = dialog.findViewById(R.id.lockSetConfirmGoalColorImageView)
        goalTitleTextView.text = goalName
        goalColorImageView.setColorFilter(goalColor, PorterDuff.Mode.SRC_IN)


        //시간 연결
        hour = dialog.findViewById(R.id.hourTimeTextView)
        hour.text = timeArray[0]
        min = dialog.findViewById(R.id.minTmeTextView)
        min.text = timeArray[1]
        sec = dialog.findViewById(R.id.secTimeTextView)
        sec.text = timeArray[2]

        lock = dialog.findViewById<ImageButton>(R.id.settingOkImageButton)
        lock.setOnClickListener {
            onClickListener.onClicked(true)
            dialog.dismiss()
        }

        cancel = dialog.findViewById<ImageButton>(R.id.lockCancelImageButton)
        cancel.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }

        //목표 리스트 연결
        detailGoalItemRecyclerView = dialog.findViewById(R.id.lockSettingConfirmDetailGoalRecyclerView)
        if(goalName != "목표를 생성해주세요"){
            val items = ArrayList<DetailGoalItem>()
            val detailGoalListAdapter = DetailGoalListAdapter(context, items)
            detailGoalItemRecyclerView.adapter = detailGoalListAdapter

            var dbManager = DBManager(context, "detail_goal_db", null, 1)
            var sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '$goalName'", null)

            while(cursor.moveToNext()){
                val goalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
                val iconPic = cursor.getInt(cursor.getColumnIndex("icon"))

                items.addAll(listOf(DetailGoalItem(iconPic, goalColor, goalName)))
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
