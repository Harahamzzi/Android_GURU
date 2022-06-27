package com.example.guru_hemjee.Home.Home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBManager

//잠금 설정 팝업
class LockSettingDialog(context: Context, bigGoalTitle: String, bigGoalColor: Int, time: String) {
    private var context = context
    private var dialog = Dialog(context)

    //기본 취소, 확인 버튼
    private lateinit var pop_lockCancelImageButton: ImageButton
    private lateinit var pop_settingOkImageButton: ImageButton

    //대표 목표 수정 버튼
    private lateinit var pop_lockSettingGoalColorImageView: ImageView
    private lateinit var pop_goalTitleTextView: TextView
    private lateinit var pop_changeGoalButton: TextView

    //시간 관련
    private lateinit var pop_hourTimeEditText: EditText
    private lateinit var pop_minTimeEditText: EditText
    private lateinit var pop_secTimeEditText: EditText

    //기본 정보 관련
    private var bigGoalTitle: String = bigGoalTitle
    private var bigGoalColor = bigGoalColor
    private var time = time
    private var timeArray = time.split(':')

    //달성 가능한 세부 목표 리스트
    private lateinit var pop_lockSettingDetailGoalRecyclerView: RecyclerView

    //팝업 표시
    /*fun lockSetting(){
        dialog.show()
        dialog.setContentView(R.layout.popup_lock_setting)

        //가져온 대표 목표 제목으로 수정, 대표 목표 색상으로 변경
        pop_lockSettingGoalColorImageView = dialog.findViewById(R.id.pop_lockSettingGoalColorImageView)
        pop_goalTitleTextView = dialog.findViewById(R.id.pop_goalTitleTextView)
        pop_goalTitleTextView.text = bigGoalTitle
        pop_lockSettingGoalColorImageView.setColorFilter(bigGoalColor, PorterDuff.Mode.SRC_IN)

        //기본 시간 설정
        pop_hourTimeEditText = dialog.findViewById<EditText>(R.id.pop_hourTimeEditText)
        pop_minTimeEditText = dialog.findViewById<EditText>(R.id.pop_minTimeEditText)
        pop_secTimeEditText = dialog.findViewById<EditText>(R.id.pop_secTimeEditText)
        timeHintSet()

        //상세 목표 리스트
        pop_lockSettingDetailGoalRecyclerView = dialog.findViewById(R.id.pop_lockSettingDetailGoalRecyclerView)
        upDateGoalList(bigGoalTitle, bigGoalColor)

        //대표 목표 수정
        pop_changeGoalButton = dialog.findViewById(R.id.pop_changeGoalButton)
        pop_changeGoalButton.setOnClickListener {
            //대표 목표 수정을 위한 팝업 연결
            val subDialog = GoalSelectDialog(context, bigGoalTitle, "목표 변경", false)
            subDialog.goalSelectPop()

            //해당 팝업에서 받아온 정보로 데이터 갱신
            subDialog.setOnClickedListener(object : GoalSelectDialog.ButtonClickListener{
                override fun onClicked(changedBigGoalTitle: String) {
                    bigGoalTitle = changedBigGoalTitle
                    pop_goalTitleTextView.text = changedBigGoalTitle

                    var dbManager = DBManager(context, "hamster_db", null, 1)
                    var sqlitedb = dbManager.readableDatabase
                    var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE " +
                            "big_goal_name = '$bigGoalTitle'",null)
                    if (cursor.moveToNext()){
                        bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))
                        time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time"))
                        timeArray = time.split(':')
                        timeHintSet()
                        pop_lockSettingGoalColorImageView.setColorFilter(bigGoalColor)
                    }
                    cursor.close()
                    sqlitedb.close()
                    dbManager.close()

                    upDateGoalList(bigGoalTitle, bigGoalColor)
                }
            })
        }


        //설정 취소 버튼
        pop_lockCancelImageButton = dialog.findViewById(R.id.pop_lockCancelImageButton)
        pop_lockCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, "목표를 생성해주세요",
                context.resources.getColor(R.color.Gray),"")
            dialog.dismiss()
        }

        //설정 확인 버튼
        pop_settingOkImageButton = dialog.findViewById(R.id.pop_settingOkImageButton)
        pop_settingOkImageButton.setOnClickListener {
            //시간 갱신
            if(pop_hourTimeEditText.text.toString() != "" || pop_minTimeEditText.text.toString() != ""
                || pop_secTimeEditText.text.toString() != ""){
                if((pop_hourTimeEditText.text.toString() != "" && pop_hourTimeEditText.text.toString().toInt() > 23) ||
                    (pop_minTimeEditText.text.toString() != "" && pop_minTimeEditText.text.toString().toInt() > 59)||
                    (pop_secTimeEditText.text.toString() != "" && pop_secTimeEditText.text.toString().toInt() > 59))
                    Toast.makeText(context, "올바른 시간을 입력해주세요!", Toast.LENGTH_SHORT).show()
                else{
                    time = FunTimeConvert.timeConvert(pop_hourTimeEditText.text.toString(),
                        pop_minTimeEditText.text.toString(), pop_secTimeEditText.text.toString())

                    onClickListener.onClicked(true, bigGoalTitle, bigGoalColor, time)
                    dialog.dismiss()
                }
            } else {
                onClickListener.onClicked(true, bigGoalTitle, bigGoalColor, time)
                dialog.dismiss()
            }

        }
    }*/

    //세부 목표 리스트 표시
    @SuppressLint("Range")
    private fun upDateGoalList(bigGoalName: String, bigGoalColor: Int){
        //대표 목표가 있을 경우("목표를 생성해주세요"는 대표 목표가 없을 때 받아옴)
        if(bigGoalName != "목표를 생성해주세요"){
            val items = ArrayList<DetailGoalListItem>()
            val detailGoalListAdapter = DetailGoalListAdapter(context, items)
            pop_lockSettingDetailGoalRecyclerView.adapter = detailGoalListAdapter

            var dbManager = DBManager(context, "hamster_db", null, 1)
            var sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE " +
                    "big_goal_name = '$bigGoalName'", null)

            while(cursor.moveToNext()){
                val goalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
                val iconPic = cursor.getInt(cursor.getColumnIndex("icon"))

                items.addAll(listOf(DetailGoalListItem(iconPic, bigGoalColor, goalName)))
                detailGoalListAdapter.notifyDataSetChanged()
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()
        }
    }

    //시간 힌트 설정
    private fun timeHintSet(){
        pop_hourTimeEditText.hint = timeArray[0]
        pop_minTimeEditText.hint = timeArray[1]
        pop_secTimeEditText.hint =timeArray[2]
    }

    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, bigGoalTitle: String, bigGoalColor: Int, getTime: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}