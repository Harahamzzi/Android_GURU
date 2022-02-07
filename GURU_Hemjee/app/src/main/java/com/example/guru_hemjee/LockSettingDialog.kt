package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.graphics.PorterDuff
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class LockSettingDialog(context: Context, bigGoalTitle: String, bigGoalColor: Int, time: String) {
    private var context = context
    private var dialog = Dialog(context)

    //기본 취소, 확인 버튼
    private lateinit var lockCancelImageButton: ImageButton
    private lateinit var settingOkImageButton: ImageButton

    //대표 목표 수정 버튼
    private lateinit var goalColorImageView: ImageView
    private lateinit var goalTitleTextView: TextView
    private lateinit var changeGoalButton: TextView

    //시간 관련
    private lateinit var hourEditText: EditText
    private lateinit var minEditText: EditText
    private lateinit var secEditText: EditText

    //기본 정보 관련
    private var bigGoalTitle: String = bigGoalTitle
    private var bigGoalColor = bigGoalColor
    private var time = time
    private var timeArray = time.split(':')

    //달성 가능한 세부 목표 리스트
    private lateinit var detailGoalRecyclerView: RecyclerView

    fun lockSetting(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting)

        //가져온 대표 목표 제목으로 수정, 대표 목표 색상으로 변경
        goalColorImageView = dialog.findViewById(R.id.lockSettingGoalColorImageView)
        goalTitleTextView = dialog.findViewById(R.id.goalTitleTextView)
        goalTitleTextView.text = bigGoalTitle
        goalColorImageView.setColorFilter(bigGoalColor, PorterDuff.Mode.SRC_IN)

        //기본 시간 설정
        hourEditText = dialog.findViewById<EditText>(R.id.hourTimeEditText)
        minEditText = dialog.findViewById<EditText>(R.id.minTimeEditText)
        secEditText = dialog.findViewById<EditText>(R.id.secTimeEditText)
        timeHintSet()

        //상세 목표 리스트
        detailGoalRecyclerView = dialog.findViewById(R.id.lockSettingDetailGoalRecyclerView)
        upDateGoalList(bigGoalTitle, bigGoalColor)

        //대표 목표 수정
        changeGoalButton = dialog.findViewById(R.id.changeGoalButton)
        changeGoalButton.setOnClickListener {
            //대표 목표 수정을 위한 팝업 연결
            val subDialog = GoalSelectDialog(context, bigGoalTitle, "목표 변경", false)
            subDialog.goalSelectPop()

            //해당 팝업에서 받아온 정보로 데이터 갱신
            subDialog.setOnClickedListener(object : GoalSelectDialog.ButtonClickListener{
                override fun onClicked(changedBigGoalTitle: String) {
                    bigGoalTitle = changedBigGoalTitle
                    goalTitleTextView.text = changedBigGoalTitle

                    var dbManager = DBManager(context, "hamster_db", null, 1)
                    var sqlitedb = dbManager.readableDatabase
                    var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '$bigGoalTitle'",null)
                    if (cursor.moveToNext()){
                        bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))
                        time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time"))
                        timeArray = time.split(':')
                        timeHintSet()
                        goalColorImageView.setColorFilter(bigGoalColor)
                    }
                    cursor.close()
                    sqlitedb.close()
                    dbManager.close()

                    upDateGoalList(bigGoalTitle, bigGoalColor)
                }
            })
        }


        //설정 취소 버튼
        lockCancelImageButton = dialog.findViewById(R.id.lockCancelImageButton)
        lockCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, "목표를 생성해주세요", context.resources.getColor(R.color.Gray),"")
            dialog.dismiss()
        }

        //설정 확인 버튼
        settingOkImageButton = dialog.findViewById(R.id.settingOkImageButton)
        settingOkImageButton.setOnClickListener {
            //시간이 올바르게 들어갔는지 확인

            //시간 갱신
            if(hourEditText.text.toString() != "" || minEditText.text.toString() != "" || secEditText.text.toString() != ""){
                if((hourEditText.text.toString() != "" && hourEditText.text.toString().toInt() > 23) || (minEditText.text.toString() != "" && minEditText.text.toString().toInt() > 59)||(secEditText.text.toString() != "" && secEditText.text.toString().toInt() > 59))
                    Toast.makeText(context, "올바른 시간을 입력해주세요!", Toast.LENGTH_SHORT).show()
                else{
                    time = FunTimeConvert.timeConvert(hourEditText.text.toString(), minEditText.text.toString(), secEditText.text.toString())

                    onClickListener.onClicked(true, bigGoalTitle, bigGoalColor, time)
                    dialog.dismiss()
                }
            } else {
                onClickListener.onClicked(true, bigGoalTitle, bigGoalColor, time)
                dialog.dismiss()
            }

        }
    }

    private fun upDateGoalList(bigGoalName: String, bigGoalColor: Int){
        if(bigGoalName != "목표를 생성해주세요"){
            val items = ArrayList<DetailGoalItem>()
            val detailGoalListAdapter = DetailGoalListAdapter(context, items)
            detailGoalRecyclerView.adapter = detailGoalListAdapter

            var dbManager = DBManager(context, "hamster_db", null, 1)
            var sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '$bigGoalName'", null)

            while(cursor.moveToNext()){
                val goalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
                val iconPic = cursor.getInt(cursor.getColumnIndex("icon"))

                items.addAll(listOf(DetailGoalItem(iconPic, bigGoalColor, goalName)))
                detailGoalListAdapter.notifyDataSetChanged()
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()
        }
    }

    private fun timeHintSet(){
        hourEditText.hint = timeArray[0]
        minEditText.hint = timeArray[1]
        secEditText.hint =timeArray[2]
    }

    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, bigGoalTitle: String, bigGoalColor: Int, getTime: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}