package com.example.guru_hemjee.Home.Home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R

// TODO: view binding 확인 필요
// 기록 시작 버튼 클릭시 뜨는 팝업(세부 목표 선택)
class RecordSettingDialog(context: Context, bigGoalName: String) {

    private var context = context
    private var dialog = Dialog(context)    // 부모 액티비티의 context가 들어가도록 함

//    //기본 취소, 확인 버튼
//    private lateinit var pop_lockCancelImageButton: ImageButton
//    private lateinit var pop_settingOkImageButton: ImageButton
//
//    //대표 목표 수정 버튼
//    private lateinit var pop_lockSettingGoalColorImageView: ImageView
//    private lateinit var pop_goalTitleTextView: TextView
//    private lateinit var pop_changeGoalButton: TextView
//
//    //시간 관련
//    private lateinit var pop_hourTimeEditText: EditText
//    private lateinit var pop_minTimeEditText: EditText
//    private lateinit var pop_secTimeEditText: EditText

    //기본 정보 관련
    private var bigGoalName = bigGoalName

    //db관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

//    //달성 가능한 세부 목표 리스트
//    private lateinit var pop_lockSettingDetailGoalRecyclerView: RecyclerView

    // 팝업을 표시할 때 쓰는 함수
    @SuppressLint("Range")
    fun showPopup() {
        dialog.setContentView(R.layout.popup_record_setting)

        /** 세부 목표 목록 갱신 **/
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '$bigGoalName'", null)

        while(cursor.moveToNext())
        {
            // 1. 아이콘
            var iconID = DBConvert.iconConvert(cursor.getString(cursor.getColumnIndex("icon")), context)    // 아이디 값 convert

            var icon = ImageView(context)   // 아이콘 이미지 뷰(temp)
            icon.setImageResource(iconID)
            // TODO: icon view 추가

            // 2. 세부 목표 이름
            var nameTextView = TextView(context)    // 세부 목표 이름 텍스트 뷰(temp)

            nameTextView.text = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
            // TODO: text size dp 단위로 적용 & font 적용
//            nameTextView.textSize = 14f

            // TODO: text view 추가

            // 3. 버튼
            var checkButton = ImageButton(context)  // 체크 버튼(temp)
            checkButton.setImageResource(R.drawable.detail_goal_check_box)

            // TODO: check button view 추가
        }

        /** 취소 & 시작 버튼 리스너 **/


//        //가져온 대표 목표 제목으로 수정, 대표 목표 색상으로 변경
//        pop_lockSettingGoalColorImageView = dialog.findViewById(R.id.pop_lockSettingGoalColorImageView)
//        pop_goalTitleTextView = dialog.findViewById(R.id.pop_goalTitleTextView)
//        pop_goalTitleTextView.text = bigGoalTitle
//        pop_lockSettingGoalColorImageView.setColorFilter(bigGoalColor, PorterDuff.Mode.SRC_IN)
//
//        //기본 시간 설정
//        pop_hourTimeEditText = dialog.findViewById<EditText>(R.id.pop_hourTimeEditText)
//        pop_minTimeEditText = dialog.findViewById<EditText>(R.id.pop_minTimeEditText)
//        pop_secTimeEditText = dialog.findViewById<EditText>(R.id.pop_secTimeEditText)
//        timeHintSet()
//
//        //상세 목표 리스트
//        pop_lockSettingDetailGoalRecyclerView = dialog.findViewById(R.id.pop_lockSettingDetailGoalRecyclerView)
//        upDateGoalList(bigGoalTitle, bigGoalColor)
//
//        //대표 목표 수정
//        pop_changeGoalButton = dialog.findViewById(R.id.pop_changeGoalButton)
//        pop_changeGoalButton.setOnClickListener {
//            //대표 목표 수정을 위한 팝업 연결
//            val subDialog = GoalSelectDialog(context, bigGoalTitle, "목표 변경", false)
//            subDialog.goalSelectPop()
//
//            //해당 팝업에서 받아온 정보로 데이터 갱신
//            subDialog.setOnClickedListener(object : GoalSelectDialog.ButtonClickListener{
//                override fun onClicked(changedBigGoalTitle: String) {
//                    bigGoalTitle = changedBigGoalTitle
//                    pop_goalTitleTextView.text = changedBigGoalTitle
//
//                    var dbManager = DBManager(context, "hamster_db", null, 1)
//                    var sqlitedb = dbManager.readableDatabase
//                    var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE " +
//                            "big_goal_name = '$bigGoalTitle'",null)
//                    if (cursor.moveToNext()){
//                        bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))
//                        time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time"))
//                        timeArray = time.split(':')
//                        timeHintSet()
//                        pop_lockSettingGoalColorImageView.setColorFilter(bigGoalColor)
//                    }
//                    cursor.close()
//                    sqlitedb.close()
//                    dbManager.close()
//
//                    upDateGoalList(bigGoalTitle, bigGoalColor)
//                }
//            })
//        }


//        //설정 취소 버튼
//        pop_lockCancelImageButton = dialog.findViewById(R.id.pop_lockCancelImageButton)
//        pop_lockCancelImageButton.setOnClickListener {
//            onClickListener.onClicked(false, "목표를 생성해주세요",
//                context.resources.getColor(R.color.Gray),"")
//            dialog.dismiss()
//        }
//
//        //설정 확인 버튼
//        pop_settingOkImageButton = dialog.findViewById(R.id.pop_settingOkImageButton)
//        pop_settingOkImageButton.setOnClickListener {
//            //시간 갱신
//            if(pop_hourTimeEditText.text.toString() != "" || pop_minTimeEditText.text.toString() != ""
//                || pop_secTimeEditText.text.toString() != ""){
//                if((pop_hourTimeEditText.text.toString() != "" && pop_hourTimeEditText.text.toString().toInt() > 23) ||
//                    (pop_minTimeEditText.text.toString() != "" && pop_minTimeEditText.text.toString().toInt() > 59)||
//                    (pop_secTimeEditText.text.toString() != "" && pop_secTimeEditText.text.toString().toInt() > 59))
//                    Toast.makeText(context, "올바른 시간을 입력해주세요!", Toast.LENGTH_SHORT).show()
//                else{
//                    time = FunTimeConvert.timeConvert(pop_hourTimeEditText.text.toString(),
//                        pop_minTimeEditText.text.toString(), pop_secTimeEditText.text.toString())
//
//                    onClickListener.onClicked(true, bigGoalTitle, bigGoalColor, time)
//                    dialog.dismiss()
//                }
//            } else {
//                onClickListener.onClicked(true, bigGoalTitle, bigGoalColor, time)
//                dialog.dismiss()
//            }
//
//        }

        // 팝업 띄우기
        dialog.show()
    }

//    //세부 목표 리스트 표시
//    @SuppressLint("Range")
//    private fun upDateGoalList(bigGoalName: String, bigGoalColor: Int){
//        //대표 목표가 있을 경우("목표를 생성해주세요"는 대표 목표가 없을 때 받아옴)
//        if(bigGoalName != "목표를 생성해주세요"){
//            val items = ArrayList<DetailGoalListItem>()
//            val detailGoalListAdapter = DetailGoalListAdapter(context, items)
//            pop_lockSettingDetailGoalRecyclerView.adapter = detailGoalListAdapter
//
//            var dbManager = DBManager(context, "hamster_db", null, 1)
//            var sqlitedb = dbManager.readableDatabase
//            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE " +
//                    "big_goal_name = '$bigGoalName'", null)
//
//            while(cursor.moveToNext()){
//                val goalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
//                val iconPic = cursor.getInt(cursor.getColumnIndex("icon"))
//
//                items.addAll(listOf(DetailGoalListItem(iconPic, bigGoalColor, goalName)))
//                detailGoalListAdapter.notifyDataSetChanged()
//            }
//
//            cursor.close()
//            sqlitedb.close()
//            dbManager.close()
//        }
//    }

    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, bigGoalTitle: String, bigGoalColor: Int, getTime: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}