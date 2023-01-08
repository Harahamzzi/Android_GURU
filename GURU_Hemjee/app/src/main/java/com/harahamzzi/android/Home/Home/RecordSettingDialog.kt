package com.harahamzzi.android.Home.Home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.harahamzzi.android.DBConvert
import com.harahamzzi.android.DBManager
import com.harahamzzi.android.Home.Goal.DetailGoalSetupDialog
import com.harahamzzi.android.Home.TimeRecord.TimeRecordActivity
import com.harahamzzi.android.R

// 기록 시작 버튼 클릭시 뜨는 팝업(세부 목표 선택)
class RecordSettingDialog(context: Context, bigGoalName: String, colorName: String) {

    // 태그
    private var TAG = "RecordSettingDialog"

    private var context = context
    private var dialog = Dialog(context)    // 부모 액티비티의 context가 들어가도록 함

    // 취소, 기록 시작 버튼
    private lateinit var pop_recordCancelButton: Button
    private lateinit var pop_recordStartButton: Button

    // 세부 목표 목록 레이아웃
    private lateinit var pop_detailGoalLayout: LinearLayout

    //기본 정보 관련
    private var bigGoalName = bigGoalName
    private var bigGoalColorName = colorName
    private var detailGoalNameList = ArrayList<String>()
    private var detailGoalCheckedList = ArrayList<Int>()  // 해당 세부 목표가 체크되었는지 아닌지를 저장함 (1: 체크, 0: 체크X)
    private var isChecked = false   // 세부목표가 한 번이라도 체크되었다면 true

    //db관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

    // 팝업을 표시할 때 쓰는 함수
    @SuppressLint("Range", "ResourceAsColor")
    fun showPopup() {
        dialog.setContentView(R.layout.popup_record_setting)

        /** View 연결 **/
        pop_recordCancelButton = dialog.findViewById(R.id.pop_record_cancelButton) // 취소 버튼
        pop_recordStartButton = dialog.findViewById(R.id.pop_record_startButton)   // 시작 버튼
        pop_detailGoalLayout = dialog.findViewById(R.id.detailGoalLayout)               // 세부 목표 목록 레이아웃

        /** 팝업 내용물(세부목표&추가버튼) 동적 생성 **/
        createContent()

        /** 취소 & 시작 버튼 리스너 **/
        // 취소 버튼
        pop_recordCancelButton.setOnClickListener {

            dialog.dismiss()    // 팝업 창 닫기
        }

        // 시작 버튼
        pop_recordStartButton.setOnClickListener {

            // 선택한 세부목표가 있는지 체크
            for (i in detailGoalCheckedList.indices)
            {
                if (detailGoalCheckedList[i] == 1)
                {
                    isChecked = true
                    break
                }
            }

            // 선택한 세부목표가 있을시
            if (isChecked)
            {
                dialog.dismiss()    // 팝업 창 닫기

                // 기록 화면으로 이동할 intent 생성
                var intent = Intent(context, TimeRecordActivity::class.java)

                // 보낼 데이터 넣기
                intent.putExtra("bigGoalName", bigGoalName)
                intent.putExtra("detailGoalCheckedList", detailGoalCheckedList)
                intent.putExtra("detailGoalNameList", detailGoalNameList)

                context.startActivity(intent)
            }
            else
            {
                Toast.makeText(context, "세부목표를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 배경색 투명화
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 팝업 띄우기
        dialog.show()
    }

    // 세부목표 목록 & 목표 추가 버튼을 동적 생성하는 함수
    @SuppressLint("Range")
    private fun createContent() {

        /** 세부 목표 리스트 View 동적 생성 **/
        // 세부 목표 리스트 View 동적 생성을 위한 DB 변수
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '$bigGoalName'", null)

        while(cursor.moveToNext())
        {
            // 0. 세부 목표 목록 레이아웃에 해당 디자인 부풀리기
            var view: View = dialog.layoutInflater.inflate(R.layout.container_detail_goal_select, pop_detailGoalLayout, false)

            // 1. 아이콘
            var iconName = cursor.getString(cursor.getColumnIndex("icon"))
            var iconID = DBConvert.iconConvert(iconName, context)    // 아이디 값 convert

            var icon: ImageView = view.findViewById(R.id.iconImageView)
            icon.setImageResource(iconID)   // 아이콘 이미지 변경
            DBConvert.colorConvert(icon, bigGoalColorName, context) // 아이콘 색상 적용

            // 2. 세부 목표 이름
            var nameTextView: TextView = view.findViewById(R.id.detailGoalNameTextView)
            nameTextView.text = cursor.getString(cursor.getColumnIndex("detail_goal_name"))

            // 2-1. 세부 목표 체크 목록 추가(기본값: 0)
            detailGoalCheckedList.add(0)
            // 2-2. 세부 목표 이름 목록 추가
            detailGoalNameList.add(nameTextView.text.toString())

            // 3. 버튼
            var checkButton: CheckBox = view.findViewById(R.id.checkboxButton)
            checkButton.setOnClickListener {

                // 해당 세부 목표의 위치값을 특정하기 위한 변수
                var index = -1

                for(i in detailGoalNameList.indices)
                {
                    if(nameTextView.text == detailGoalNameList[i])
                    {
                        index = i
                        break
                    }
                }

                // 해당 세부 목표의 체크값 변경
                if(checkButton.isChecked)
                {
                    detailGoalCheckedList[index] = 1
                }
                else if(!checkButton.isChecked)
                {
                    detailGoalCheckedList[index] = 0
                }
            }

            // 4. view 추가
            pop_detailGoalLayout.addView(view)

            // 5. 구분선 추가
            var lineParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3)   // 파라미터 설정
            lineParams.setMargins(0, 20, 0, 20)

            var lineView = View(context)
            lineView.layoutParams = lineParams
            lineView.alpha = 0.1F
            lineView.setBackgroundColor(context.resources.getColor(R.color.black))

            pop_detailGoalLayout.addView(lineView)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 6. 세부목표 추가 아이콘 추가
        var addButton = ImageView(context)
        addButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp)
        addButton.setColorFilter(R.color.Gray, PorterDuff.Mode.SRC_IN)
        addButton.setOnClickListener {
            // 세부 목표 추가 팝업 띄우기
            val dialog = DetailGoalSetupDialog(context, 0, bigGoalName, bigGoalColorName)
            dialog.detailGoalSetup()

            dialog.setOnClickedListener(object : DetailGoalSetupDialog.ButtonClickListener {
                override fun onClick(isChanged: Boolean, code: Int, title: String?, icon: String?, color: String?, initTitle: String?, initBigGoal: String?) {

                    // 확인 버튼 클릭시
                    if (isChanged && code == 0)
                    {
                        // 레이아웃 재구성
                        pop_detailGoalLayout.removeAllViews()
                        createContent()
                    }
                }
            })
        }
        // 파라미터 생성 및 초기화
        var addBtnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        addBtnParams.setMargins(0, 24, 0, 0)
        addBtnParams.gravity = Gravity.CENTER
        addButton.layoutParams = addBtnParams

        pop_detailGoalLayout.addView(addButton)
    }
}