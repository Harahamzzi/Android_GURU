package com.harahamzzi.android

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.widget.*

//목표 선택 팝업
class GoalSelectDialog(context: Context, bigGoalTitle: String, val dialogTitle: String, val isReport: Boolean) {
    private var context = context
    private var dialog = Dialog(context)

    //미리 선택된 대표 목표 제목
    private var bigGoalTitle = bigGoalTitle

    //팝업 제목
    private lateinit var pop_moveDetailGoalTextView: TextView

    //db관련(대표 목표 리스트)
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    //대표 목표 선택 라디오 그룹
    private lateinit var pop_setGoalRadioGroup: RadioGroup
    private lateinit var pop_totalGoalCountTextView: TextView

    //돌아가기 버튼
    private lateinit var pop_backImageButton: ImageButton

    //팝업 표시
    fun goalSelectPop(){
        dialog.show()
        dialog.setContentView(R.layout.popup_goal_select)

        //팝업 제목
        pop_moveDetailGoalTextView = dialog.findViewById<TextView>(R.id.pop_moveDetailGoalTextView)
        pop_moveDetailGoalTextView.text = dialogTitle

        //라디오 그룹 가져오기
        pop_setGoalRadioGroup = dialog.findViewById(R.id.pop_setGoalRadioGroup)

        //라디오 그룹에 추가된 버튼 갯수
        var num: Int = 0

        //리포트 화면에서는 전체 선택 라디오 버튼 추가
        if(isReport){
            var radioButton: RadioButton = RadioButton(context)
            radioButton.id = num
            radioButton.width = 700
            //대표 목표 색상 표시를 위한 원 표시
            radioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.goal_dot_small, 0, 0, 0)
            radioButton.compoundDrawablePadding = 20
            radioButton.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Black))
            //라디오 버튼 색 변경
            radioButton.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.SeedBrown))
            radioButton.setText("전체")
            //라디오 버튼을 왼쪽에서 오른쪽으로 변경
            radioButton.layoutDirection = RadioButton.LAYOUT_DIRECTION_RTL
            radioButton.isChecked = true
            //대표 목표 제목이 긴 경우 처리(뒤에 ...)
            radioButton.setSingleLine()
            radioButton.ellipsize = TextUtils.TruncateAt.END
            radioButton.setPadding(0, 0, 0, 10)

            pop_setGoalRadioGroup.addView(radioButton)
            num++
        }

        //라디오 그룹에 동적으로 연결
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db;", null)
        //라디오 버튼
        while(cursor.moveToNext()){
            val name = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val color = cursor.getInt(cursor.getColumnIndex("color"))

            var radioButton: RadioButton = RadioButton(context)
            radioButton.id = num
            radioButton.width = 800
            radioButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.goal_dot_small, 0, 0, 0)
            radioButton.compoundDrawablePadding = 20
            radioButton.compoundDrawableTintList = ColorStateList.valueOf(color)
            radioButton.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.SeedBrown))
            radioButton.setText(name)
            radioButton.layoutDirection = RadioButton.LAYOUT_DIRECTION_RTL
            radioButton.isChecked = name==bigGoalTitle
            radioButton.setSingleLine()
            radioButton.ellipsize = TextUtils.TruncateAt.END
            radioButton.setPadding(0, 0, 0, 10)

            pop_setGoalRadioGroup.addView(radioButton)
            num++
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //대표 목표 갯수 표시
        pop_totalGoalCountTextView = dialog.findViewById(R.id.pop_totalGoalCountTextView)
        pop_totalGoalCountTextView.text = "총 ${num}개"

        //선택 항목 얻어오기
        pop_setGoalRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            for(i in 0..num){
                var id = context.resources.getIdentifier("$i", "id", "com.harahamzzi.android")
                if(checkedId == id){
                    var radioButton = dialog.findViewById<RadioButton>(id)
                    bigGoalTitle = radioButton.text as String
                }
            }
        }

        //선택후 넘기기
        pop_backImageButton = dialog.findViewById(R.id.pop_backImageButton)
        pop_backImageButton.setOnClickListener {
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