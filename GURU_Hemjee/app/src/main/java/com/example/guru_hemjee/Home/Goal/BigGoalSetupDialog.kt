package com.example.guru_hemjee.Home.Goal

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.*
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.google.android.material.button.MaterialButton

// 대표목표 추가 버튼 클릭 시 나타나는 팝업
// code 0 = 추가, code 1 = 수정
class BigGoalSetupDialog(
    val context: Context,
    val title: String?,
    val color: String?,
    val code: Int
) {
    private val dialog = Dialog(context)

    private lateinit var dbManager: DBManager // 내부 db 사용을 위한 변수
    private lateinit var sqlitedb: SQLiteDatabase

    private lateinit var titleEdt: EditText // 대표목표
    private lateinit var titleTv: TextView // 팝업 제목

    private lateinit var rGroup1: RadioGroup // 색상 라디오그룹1
    private lateinit var rGroup2: RadioGroup // 색상 라디오그룹2

    private lateinit var orangeRBtn: RadioButton // 색상 라디오 버튼
    private lateinit var yellowRBtn: RadioButton
    private lateinit var noteYellowRBtn: RadioButton
    private lateinit var apricotRBtn: RadioButton
    private lateinit var seedBrownRBtn: RadioButton
    private lateinit var darkBrownRBtn: RadioButton
    private lateinit var lightGreenRBtn: RadioButton
    private lateinit var greenRBtn: RadioButton
    private lateinit var lightBlueRBtn: RadioButton
    private lateinit var blueRBtn: RadioButton
    private lateinit var purpleRBtn: RadioButton
    private lateinit var pinkRBtn: RadioButton

    private lateinit var cancelBtn: MaterialButton // 취소 버튼
    private lateinit var confirmBtn: MaterialButton // 추가 버튼

    // 기존에 저장되어 있는 대표목표, 색상 값
    private val initTitle = title
    private val initColor = color

    // 새롭게 저장한 대표목표, 색상 값
    private var newColor: String? = null

    fun bigGoalSetup() {

        // 팝업 열기
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 모서리 둥글게
        dialog.setContentView(R.layout.popup_add_big_goal)

        // 위젯 연결
        titleTv = dialog.findViewById(R.id.pop_biggoal_title_tv)
        titleEdt = dialog.findViewById(R.id.pop_biggoal_title_edt)

        rGroup1 = dialog.findViewById(R.id.pop_biggoal_color_rgroup1)
        rGroup2 = dialog.findViewById(R.id.pop_biggoal_color_rgroup2)

        orangeRBtn = dialog.findViewById(R.id.pop_biggoal_color_orange_rbtn)
        yellowRBtn = dialog.findViewById(R.id.pop_biggoal_color_yellow_rbtn)
        noteYellowRBtn = dialog.findViewById(R.id.pop_biggoal_color_noteyellow_rbtn)
        apricotRBtn = dialog.findViewById(R.id.pop_biggoal_color_apricot_rbtn)
        seedBrownRBtn = dialog.findViewById(R.id.pop_biggoal_color_seedbrown_rbtn)
        darkBrownRBtn = dialog.findViewById(R.id.pop_biggoal_color_darkbrown_rbtn)
        lightGreenRBtn = dialog.findViewById(R.id.pop_biggoal_color_lightgreen_rbtn)
        greenRBtn = dialog.findViewById(R.id.pop_biggoal_color_green_rbtn)
        lightBlueRBtn = dialog.findViewById(R.id.pop_biggoal_color_lightblue_rbtn)
        blueRBtn = dialog.findViewById(R.id.pop_biggoal_color_blue_rbtn)
        purpleRBtn = dialog.findViewById(R.id.pop_biggoal_color_purple_rbtn)
        pinkRBtn = dialog.findViewById(R.id.pop_biggoal_color_pink_rbtn)

        confirmBtn = dialog.findViewById(R.id.pop_biggoal_confirm_btn)
        cancelBtn = dialog.findViewById(R.id.pop_biggoal_cancel_btn)

        // 기존에 저장된 값이 없는 경우, 기본값 설정
        orangeRBtn.isChecked = true
        newColor = "Orange"

        // 기존에 저장된 값이 있는 경우(값 수정일 경우) 대표목표와 색상 적용하기
        if (code == 1) {
            titleTv.text = "대표목표 수정"
            titleEdt.setText(initTitle)

            // 기존에 선택되어 있는 오랜지색 라디오버튼 초기화
            rGroup1.clearCheck()
            orangeRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)

            when (initColor) {
                "Orange" -> {
                    orangeRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Orange"
                }
                "Yellow" -> {
                    yellowRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Yellow"
                }
                "NoteYellow" -> {
                    noteYellowRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "NoteYellow"
                }
                "Apricot" -> {
                    apricotRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Apricot"
                }
                "SeedBrown" -> {
                    seedBrownRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "SeedBrown"
                }
                "DarkBrown" -> {
                    darkBrownRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "DarkBrown"
                }
                "LightGreen"  -> {
                    lightGreenRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "LightGreen"
                }
                "Green" -> {
                    greenRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Green"
                }
                "LightBlue" -> {
                    lightBlueRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "LightBlue"
                }
                "Blue" -> {
                    blueRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Blue"
                }
                "Purple" -> {
                    purpleRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Purple"
                }
                "Pink" -> {
                    pinkRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Pink"
                }
            }
        }

        // 라디오 그룹1 클릭 이벤트
        rGroup1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                // 그룹에 클릭된 버튼 초기화 및 션택한 라디오버튼 클릭 표시
                R.id.pop_biggoal_color_orange_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    orangeRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Orange"
                }
                R.id.pop_biggoal_color_yellow_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    yellowRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Yellow"
                }
                R.id.pop_biggoal_color_noteyellow_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    noteYellowRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "NoteYellow"
                }
                R.id.pop_biggoal_color_apricot_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    apricotRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Apricot"
                }
                R.id.pop_biggoal_color_seedbrown_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    seedBrownRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "SeedBrown"
                }
                R.id.pop_biggoal_color_darkbrown_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    darkBrownRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "DarkBrown"
                }
            }
        }

        // 라디오 그룹2 클릭 이벤트
        rGroup2.setOnCheckedChangeListener { group, checkedId ->
            // 그룹에 클릭된 버튼 초기화 및 션택한 라디오버튼 클릭 표시
            when (checkedId) {
                R.id.pop_biggoal_color_lightgreen_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    lightGreenRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "LightGreen"
                }
                R.id.pop_biggoal_color_green_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    greenRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Green"
                }
                R.id.pop_biggoal_color_lightblue_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    lightBlueRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "LightBlue"
                }
                R.id.pop_biggoal_color_blue_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    blueRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Blue"
                }
                R.id.pop_biggoal_color_purple_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    purpleRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Purple"
                }
                R.id.pop_biggoal_color_pink_rbtn -> {
                    rGroup1.clearCheck()
                    rGroup2.clearCheck()
                    unCheckedRadioBtn()
                    pinkRBtn.setBackgroundResource(R.drawable.shape_radio_checked)
                    newColor = "Pink"
                }
            }
        }

        // 취소 버튼 클릭 이벤트
        cancelBtn.setOnClickListener{
            onClickListener.onClicked(false, 2, null, null, null)
            dialog.dismiss()
        }

        // 대표목표 추가&수정 버튼 클릭 이벤트
        confirmBtn.setOnClickListener{

            var newBigGoal = titleEdt.text.toString() // 대표목표
            var isOverlap = false // 중복값을 확인하기 위한 변수

            // 입력한 값이 기존에 저장되어 있는 값과 같은지 확인
            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            var cursor = sqlitedb.rawQuery("SELECT big_goal_name FROM big_goal_db WHERE big_goal_name = '${newBigGoal}' AND color = '${newColor}'",null)
            if (cursor.moveToNext()) {
                isOverlap = true
            }
            cursor.close()
            sqlitedb.close()

            // 중복값이라면
            if (isOverlap) {
                Toast.makeText(context, "이미 같은 목표가 존재합니다", Toast.LENGTH_SHORT).show()
            }
            // 대표 목표를 입력 안 했다면
            if (newBigGoal.isBlank()) {
                Toast.makeText(context, "대표 목표를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            // 대표 목표를 입력했다면
            else {
                // 대표목표를 추가하는 경우
                if (code == 0) {
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("INSERT INTO big_goal_db VALUES ('" + newBigGoal + "', '" + newColor + "', '" + "${0}:${0}:${0}" + "');")
                    sqlitedb.close()

                    onClickListener.onClicked(true, 0, newBigGoal, newColor, initTitle)
                    dialog.dismiss()
                    Toast.makeText(context, "목표를 저장했습니다", Toast.LENGTH_SHORT).show()
                }
                // 대표목표를 수정하는 경우
                else if (code == 1) {
                    // 색상만 수정했다면
                    if (initTitle == newBigGoal && initColor != newColor) {
                        sqlitedb = dbManager.writableDatabase
                        sqlitedb.execSQL("UPDATE big_goal_db SET color = '$newColor' WHERE big_goal_name = '$initTitle';")
                        sqlitedb.close()

                        onClickListener.onClicked(true, 1, initTitle, newColor, initTitle)
                        dialog.dismiss()
                        Toast.makeText(context, "목표를 수정했습니다", Toast.LENGTH_SHORT).show()
                    }
                    // 대표목표만 수정했다면
                    else if (initTitle != newBigGoal && initColor == newColor) {
                        sqlitedb = dbManager.writableDatabase
                        sqlitedb.execSQL("UPDATE big_goal_db SET big_goal_name = '$newBigGoal' WHERE big_goal_name = '$initTitle';")
                        sqlitedb.close()

                        onClickListener.onClicked(true, 1, newBigGoal, initColor, initTitle)
                        dialog.dismiss()
                        Toast.makeText(context, "목표를 수정했습니다", Toast.LENGTH_SHORT).show()
                    }
                    // 대표목표와 색상 모두 수정했다면
                    else if (initTitle != newBigGoal && initColor != newColor) {
                        sqlitedb = dbManager.writableDatabase
                        sqlitedb.execSQL("UPDATE big_goal_db SET color = '$newColor' WHERE big_goal_name = '$initTitle';")
                        sqlitedb.execSQL("UPDATE big_goal_db SET big_goal_name = '$newBigGoal' WHERE big_goal_name = '$initTitle';")
                        sqlitedb.close()

                        onClickListener.onClicked(true, 1, newBigGoal, newColor, initTitle)
                        dialog.dismiss()
                        Toast.makeText(context, "목표를 수정했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                dbManager.close()
            }
        }
    }

    // 파라미터 값을 넘겨주기 위한 클릭 인터페이스
    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, code: Int, title: String?, color: String?, initTitle: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

    // 라디오버튼 리소스 초기화
    private fun unCheckedRadioBtn() {
        orangeRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        yellowRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        noteYellowRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        apricotRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        seedBrownRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        darkBrownRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        lightGreenRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        greenRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        lightBlueRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        blueRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        purpleRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
        pinkRBtn.setBackgroundResource(R.drawable.shape_radio_unchecked)
    }
}
