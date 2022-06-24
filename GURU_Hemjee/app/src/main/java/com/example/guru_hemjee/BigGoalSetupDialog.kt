package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.button.MaterialButton

// 대표목표 추가 버튼 클릭 시 나타나는 팝업
class BigGoalSetupDialog(context: Context) {
    private val dialog = Dialog(context)

    private lateinit var pop_biggoal_title_edt: EditText // 대표목표
    private lateinit var pop_biggoal_color_rgroup1: RadioGroup // 색상 라디오그룹1
    private lateinit var pop_biggoal_color_rgroup2: RadioGroup // 색상 라디오
    private lateinit var pop_biggoal_cancel_btn: MaterialButton // 취소 버튼
    private lateinit var pop_biggoal_add_btn: MaterialButton // 추가 버튼

    private lateinit var dbManager: DBManager // 내부 db 사용을 위한 변수
    private lateinit var sqlitedb: SQLiteDatabase

    fun bigGoalSetup() {
        // 팝업 열기
        dialog.show()
        dialog.setContentView(R.layout.popup_add_big_goal)

        // 위젯 연결
        pop_biggoal_title_edt = dialog.findViewById(R.id.pop_biggoal_title_edt)
        pop_biggoal_color_rgroup1 = dialog.findViewById(R.id.pop_biggoal_color_rgroup1)
        pop_biggoal_color_rgroup2 = dialog.findViewById(R.id.pop_biggoal_color_rgroup2)
        pop_biggoal_add_btn = dialog.findViewById(R.id.pop_biggoal_add_btn)
        pop_biggoal_cancel_btn = dialog.findViewById(R.id.pop_biggoal_cancel_btn)

        // 라디오 그룹1 클릭 이벤트
        pop_biggoal_color_rgroup1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                else -> pop_biggoal_color_rgroup2.clearCheck() // 그룹2에 클릭된 버튼 초기화
            }
        }

        // 라디오 그룹2 클릭 이벤트
        pop_biggoal_color_rgroup2.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                else -> pop_biggoal_color_rgroup1.clearCheck() // 그룹1에 클릭된 버튼 초기화
            }
        }

        // 취소 버튼 클릭 이벤트
        pop_biggoal_cancel_btn.setOnClickListener{
            onClickListener.onClicked(false)
            dialog.dismiss()
        }

        // 대표목표 추가 버튼 클릭 이벤트
        pop_biggoal_add_btn.setOnClickListener{

            var biggoal_title = pop_biggoal_title_edt.text.toString() // 대표목표 변수
            var biggoal_color = "" // 색상 변수
            var isOverlap: Boolean = false // 중복값을 확인하기 위한 변수

            // 대표 목표를 입력 안 했다면
            if (biggoal_title != null) {
                Toast.makeText(MyApplication.applicationContext(), "대표 목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            // DB에 저장된 대표목표 값을 리스트에 저장하기
            dbManager = DBManager(MyApplication.applicationContext(), "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            var cursor: Cursor
            cursor = sqlitedb.rawQuery("SELECT big_goal_name FROM big_goal_db WHERE big_goal_name = '${biggoal_title}'",null)
            if (cursor.moveToNext()) {
                isOverlap = true
                Toast.makeText(MyApplication.applicationContext(), "이미 같은 목표가 존재합니다.", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
            sqlitedb.close()
            dbManager.close()

            // 선택한 색상 값 저장
            when (pop_biggoal_color_rgroup1.checkedRadioButtonId) {
                R.id.bigGoal_orangeRadioButton -> biggoal_color = "Orange"
                R.id.bigGoal_yellowRadioButton -> biggoal_color = "Yellow"
                R.id.bigGoal_noteYellowRadioButton -> biggoal_color = "NoteYellow"
                R.id.bigGoal_apricotRadioButton -> biggoal_color = "Apricot"
                R.id.bigGoal_seedBrownRadioButton -> biggoal_color = "SeedBrown"
                R.id.bigGoal_darkBrownRadioButton -> biggoal_color = "DarkBrown"
            }
            when (pop_biggoal_color_rgroup2.checkedRadioButtonId) {
                R.id.bigGoal_lightGreenRadioButton -> biggoal_color = "LightGreen"
                R.id.bigGoal_greenRadioButton -> biggoal_color = "Green"
                R.id.bigGoal_lightBlueRadioButton -> biggoal_color = "LightBlue"
                R.id.bigGoal_blueRadioButton -> biggoal_color = "Blue"
                R.id.bigGoal_purpleRadioButton -> biggoal_color = "Purple"
                R.id.bigGoal_pinkRadioButton -> biggoal_color = "Pink"
            }

            // 입력한 값이 기존 db에 없는 값이라면 fragment파일에서 db에 값 저장
            if (!isOverlap) {
                onClickListener.onClicked(true, biggoal_title, biggoal_color)
                dialog.dismiss()
            }
        }

    }

    // 파라미터 값을 넘겨주기 위한 클릭 인터페이스
    interface ButtonClickListener {
        fun onClicked(isAdd: Boolean, biggoalTitle: String?, biggoalColor: String?) // 추가 버튼 클릭 이벤트
        fun onClicked(isAdd: Boolean) // 취소 버튼 클릭 이벤트
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickledListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}
