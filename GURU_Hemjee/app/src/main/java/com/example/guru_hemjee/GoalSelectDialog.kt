package com.example.guru_hemjee

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.LayoutDirection
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class GoalSelectDialog(context: Context, bigGoalTitle: String) {
    private var context = context
    private var dialog = Dialog(context)
    private var bigGoalTitle = bigGoalTitle

    //db관련(대표 목표 리스트)
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    //리스트 레이아웃
    private lateinit var setBigGoalRecyclerView: RecyclerView

    //라디오 그룹
    private lateinit var goalRadioGroup: RadioGroup
    private lateinit var totalGoalCountTextView: TextView

    //돌아가기 버튼
    private lateinit var backImageButton: ImageButton

    fun goalSelectPop(){
        dialog.show()
        dialog.setContentView(R.layout.popup_goal_select)

        //리스트 가져오기
        goalRadioGroup = dialog.findViewById(R.id.setGoalRadioGroup)

        dbManager = DBManager(context, "big_goal_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db;", null)

        var num: Int = 0
        while(cursor.moveToNext()){
            val name = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val color = cursor.getInt(cursor.getColumnIndex("color"))

            var radioButton: RadioButton = RadioButton(context)
            radioButton.id = num
            radioButton.width = 700
            radioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.goal_dot_small, 0, 0, 0)
            radioButton.compoundDrawablePadding = 20
            radioButton.compoundDrawableTintList = ColorStateList.valueOf(color)
            radioButton.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.SeedBrown))
            radioButton.setText(name)
            radioButton.layoutDirection = RadioButton.LAYOUT_DIRECTION_RTL
            radioButton.isChecked = name==bigGoalTitle

            goalRadioGroup.addView(radioButton)
            num++
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        totalGoalCountTextView = dialog.findViewById(R.id.totalGoalCountTextView)
        totalGoalCountTextView.text = "총 ${num}개"

        //선택 항목 얻어오기
        goalRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            for(i in 0..num){
                var id = context.resources.getIdentifier("$i", "id", "com.example.guru_hemjee")
                if(checkedId == id){
                    var radioButton = dialog.findViewById<RadioButton>(id)
                    bigGoalTitle = radioButton.text as String
                }
            }
        }

        //선택후 넘기기
        backImageButton = dialog.findViewById(R.id.popBackImageButton)
        backImageButton.setOnClickListener {
            onClickListener.onClicked(bigGoalTitle)
            dialog.dismiss()
        }

    }

    interface ButtonClickListener {
        fun onClicked(changedBigGoalTitle: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}