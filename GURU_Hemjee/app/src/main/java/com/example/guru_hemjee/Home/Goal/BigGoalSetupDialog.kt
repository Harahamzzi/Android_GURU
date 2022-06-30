package com.example.guru_hemjee.Home.Goal

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.R
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
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 모서리 둥글게
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
                R.id.bigGoal_orangeRadioButton,
                R.id.bigGoal_yellowRadioButton,
                R.id.bigGoal_noteYellowRadioButton,
                R.id.bigGoal_apricotRadioButton,
                R.id.bigGoal_seedBrownRadioButton,
                R.id.bigGoal_darkBrownRadioButton -> {
                    pop_biggoal_color_rgroup2.clearCheck() // 그룹2에 클릭된 버튼 초기화
                }
            }
        }

        // 라디오 그룹2 클릭 이벤트
        pop_biggoal_color_rgroup2.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.bigGoal_lightGreenRadioButton,
                R.id.bigGoal_greenRadioButton,
                R.id.bigGoal_lightBlueRadioButton,
                R.id.bigGoal_blueRadioButton,
                R.id.bigGoal_purpleRadioButton,
                R.id.bigGoal_pinkRadioButton -> {
                    pop_biggoal_color_rgroup1.clearCheck() // 그룹1에 클릭된 버튼 초기화
                }
            }
        }

        // 취소 버튼 클릭 이벤트
        pop_biggoal_cancel_btn.setOnClickListener{
            onClickListener.onClicked(false)
            dialog.dismiss()
        }

        // 대표목표 추가 버튼 클릭 이벤트
        pop_biggoal_add_btn.setOnClickListener{

            var str_biggoal = pop_biggoal_title_edt.text.toString() // 대표목표 변수
            var str_color = "" // 색상 변수
            var isOverlap = false // 중복값을 확인하기 위한 변수

            // 대표 목표를 입력 안 했다면
            if (str_biggoal == null) {
                Toast.makeText(MyApplication.applicationContext(), "대표 목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else { // 대표 목표를 입력했다면
                // DB에 중복 대표목표 값이 있는지 확인
                dbManager = DBManager(MyApplication.applicationContext(), "hamster_db", null, 1)
                sqlitedb = dbManager.readableDatabase

                var cursor: Cursor
                cursor = sqlitedb.rawQuery("SELECT big_goal_name FROM big_goal_db WHERE big_goal_name = '${str_biggoal}'",null)
                if (cursor.moveToNext()) {
                    isOverlap = true
                }

                cursor.close()
                sqlitedb.close()
                dbManager.close()

                // 선택한 색상 값 저장
                when (pop_biggoal_color_rgroup1.checkedRadioButtonId) {
                    R.id.pop_biggoal_color_orange_rbtn -> str_color = "Orange"
                    R.id.pop_biggoal_color_yellow_rbtn -> str_color = "Yellow"
                    R.id.pop_biggoal_color_noteyellow_rbtn -> str_color = "NoteYellow"
                    R.id.pop_biggoal_color_apricot_rbtn -> str_color = "Apricot"
                    R.id.pop_biggoal_color_seedbrown_rbtn -> str_color = "SeedBrown"
                    R.id.pop_biggoal_color_darkbrown_rbtn -> str_color = "DarkBrown"
                }
                when (pop_biggoal_color_rgroup2.checkedRadioButtonId) {
                    R.id.pop_biggoal_color_lightgreen_rbtn -> str_color = "LightGreen"
                    R.id.pop_biggoal_color_green_rbtn -> str_color = "Green"
                    R.id.pop_biggoal_color_lightblue_rbtn -> str_color = "LightBlue"
                    R.id.pop_biggoal_color_blue_rbtn -> str_color = "Blue"
                    R.id.pop_biggoal_color_purple_rbtn -> str_color = "Purple"
                    R.id.pop_biggoal_color_pink_rbtn -> str_color = "Pink"
                }

                Log.i("색상 값 ", str_color)

                // 입력한 값이 기존 db에 없는 값이라면 db에 값 저장
                if (!isOverlap) {
                    dbManager = DBManager(MyApplication.applicationContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    sqlitedb.execSQL("INSERT INTO big_goal_db VALUES ('" + str_biggoal + "', '" + str_color + "', '" + "${0}:${0}:${0}" + "');")
                    sqlitedb.close()

                    onClickListener.onClicked(true, str_biggoal, str_color)
                    dialog.dismiss()
                }
                else { // 중복 값이라면 toast 메시지 띄우기
                    Toast.makeText(MyApplication.applicationContext(), "이미 같은 목표가 존재합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // 파라미터 값을 넘겨주기 위한 클릭 인터페이스
    interface ButtonClickListener {
        fun onClicked(isAdd: Boolean, title: String?, color: String?) // 추가 버튼 클릭 이벤트
        fun onClicked(isAdd: Boolean) // 취소 버튼 클릭 이벤트
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickledListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}
