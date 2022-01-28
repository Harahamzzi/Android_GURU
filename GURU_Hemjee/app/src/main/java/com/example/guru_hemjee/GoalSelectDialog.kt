package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.widget.*

class GoalSelectDialog(context: Context, bigGoalTitle: String?) {
    private var context = context
    private var dialog = Dialog(context)
    private var bigGoalTitle = bigGoalTitle

    //db관련(대표 목표 리스트)
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    //리스트 레이아웃
    private lateinit var goalListLinearLayout: LinearLayout

    //라디오 그룹
    private lateinit var goalRadioGroup: RadioGroup

    //돌아가기 버튼
    private lateinit var backImageButton: ImageButton

    fun goalSelectPop(){
        dialog.show()
        dialog.setContentView(R.layout.popup_goal_select)

        //리스트 가져오기
        dbManager = DBManager(context, "big_goal_db", null, 1)
        sqlitedb = dbManager.readableDatabase

       // goalListLinearLayout = dialog.findViewById(R.id.goalListLinearLayout)
        goalRadioGroup = dialog.findViewById(R.id.goalRadioGroup)

        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db;", null)

        var num: Int = 0
        while(cursor.moveToNext()){
            var goalName = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            var color = cursor.getInt(cursor.getColumnIndex("color"))

//            Toast.makeText(context, "$goalName", Toast.LENGTH_SHORT).show()
//
//            var layoutItemLinearLayout: LinearLayout = LinearLayout(context)
//            layoutItemLinearLayout.orientation = LinearLayout.HORIZONTAL
//            layoutItemLinearLayout.id = num
//            layoutItemLinearLayout.setTag(goalName)
//
//            var colorImageView: ImageView = ImageView(context)
//            colorImageView.maxWidth = 27
//            colorImageView.maxHeight = 27
//            colorImageView.setImageResource(R.drawable.goal_dot_small)
//            colorImageView.setColorFilter(Color.parseColor("#${color}"))
//            colorImageView.setPadding(10, 10, 10, 10)
//            layoutItemLinearLayout.addView(colorImageView)


            var radioButton: RadioButton = RadioButton(context)
            radioButton.width = 712
            radioButton.height = 51
            radioButton.id = num
            radioButton.text = goalName
            radioButton.setBackgroundColor(color)
            radioButton.gravity=RadioButton.TEXT_ALIGNMENT_CENTER
            radioButton.layoutDirection=RadioButton.LAYOUT_DIRECTION_RTL
            radioButton.textSize = 12F
//            layoutItemLinearLayout.addView(radioButton)

            if(goalName == bigGoalTitle)
                radioButton.isChecked = true

            goalRadioGroup.addView(radioButton)
            num++
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //선택후 넘기기
        backImageButton = dialog.findViewById(R.id.popBackImageButton)
        backImageButton.setOnClickListener {
            onClickListener.onClicked("제목제목")
            dialog.dismiss()
        }

    }

    interface ButtonClickListener {
        fun onClicked(changedBigGoalTitle: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}