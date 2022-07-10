package com.example.guru_hemjee.Home.Goal

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.*
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.R
import com.google.android.material.button.MaterialButton

// 세부목표 클릭 시 나타나는 세부 목표 팝업
class DetailGoalSetupDialog(
    val context: Context,
    val title: String,
    val icon: String,
    val color: String,
    val bigGoal: String
) {
    private val dialog = Dialog(context)

    private lateinit var dbManager: DBManager // 내부 db 사용을 위한 변수
    private lateinit var sqlitedb: SQLiteDatabase

    // 세부목표
    private lateinit var pop_detailgoal_title_edt: EditText
    private lateinit var pop_detailgoal_title_tv: TextView // 팝업 제목

    //확인 취소 버튼
    private lateinit var cancelBtn: MaterialButton
    private lateinit var confirmBtn: MaterialButton

    // 라디오 그룹
    private lateinit var rGroup1: RadioGroup
    private lateinit var rGroup2: RadioGroup
    private lateinit var rGroup3: RadioGroup

    // 라디오 버튼
    private lateinit var bookRBtn: RadioButton
    private lateinit var lessonRBtn: RadioButton
    private lateinit var schoolRBtn: RadioButton
    private lateinit var dumbleRBtn: RadioButton
    private lateinit var forestRBtn: RadioButton
    private lateinit var sportsRBtn: RadioButton
    private lateinit var computerRBtn: RadioButton
    private lateinit var pianoRBtn: RadioButton
    private lateinit var foodRBtn: RadioButton
    private lateinit var cafeRBtn: RadioButton
    private lateinit var businessRBtn: RadioButton
    private lateinit var storeRBtn: RadioButton
    private lateinit var drawingRBtn: RadioButton
    private lateinit var savingsRBtn: RadioButton
    private lateinit var stockRBtn: RadioButton

    // 기존에 저장되어 있는 세부목표, 색상, 아이콘, 대표목표 값
    private val initTitle = title
    private val initIcon = icon
    private val initColor = color
    private val initBigGoal = bigGoal

    // 새롭게 변경된 세부목표, 아이콘 값
    private var newIcon: String = ""

    //아이콘 변경 팝업
    fun detailGoalSetup() {

        // 팝업 열기
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 모서리 둥글게
        dialog.setContentView(R.layout.popup_add_detail_goal)

        // 위젯 연결
        pop_detailgoal_title_tv = dialog.findViewById(R.id.pop_detailgoal_title_tv)
        pop_detailgoal_title_edt = dialog.findViewById(R.id.pop_detailgoal_title_edt)
        cancelBtn = dialog.findViewById(R.id.pop_detailgoal_cancelBtn)
        confirmBtn = dialog.findViewById(R.id.pop_detailgoal_confirmBtn)

        rGroup1 = dialog.findViewById(R.id.pop_detailgoal_icon_rgroup1)
        rGroup2 = dialog.findViewById(R.id.pop_detailgoal_icon_rgroup2)
        rGroup3 = dialog.findViewById(R.id.pop_detailgoal_icon_rgroup3)

        bookRBtn = dialog.findViewById(R.id.pop_detailgoal_book_rbtn)
        lessonRBtn = dialog.findViewById(R.id.pop_detailgoal_lesson_rbtn)
        schoolRBtn = dialog.findViewById(R.id.pop_detailgoal_school_rbtn)
        dumbleRBtn = dialog.findViewById(R.id.pop_detailgoal_dumble_rbtn)
        forestRBtn = dialog.findViewById(R.id.pop_detailgoal_forest_rbtn)
        sportsRBtn = dialog.findViewById(R.id.pop_detailgoal_sports_rbtn)
        computerRBtn = dialog.findViewById(R.id.pop_detailgoal_computer_rbtn)
        pianoRBtn = dialog.findViewById(R.id.pop_detailgoal_piano_rbtn)
        foodRBtn = dialog.findViewById(R.id.pop_detailgoal_food_rbtn)
        cafeRBtn = dialog.findViewById(R.id.pop_detailgoal_cafe_rbtn)
        businessRBtn = dialog.findViewById(R.id.pop_detailgoal_business_rbtn)
        storeRBtn = dialog.findViewById(R.id.pop_detailgoal_store_rbtn)
        drawingRBtn = dialog.findViewById(R.id.pop_detailgoal_drawing_rbtn)
        savingsRBtn = dialog.findViewById(R.id.pop_detailgoal_savings_rbtn)
        stockRBtn = dialog.findViewById(R.id.pop_detailgoal_stock_rbtn)

        // 기존에 저장된 값이 있는 경우 적용하기
        if (initTitle.isNotBlank()) { // 세부목표
            pop_detailgoal_title_tv.text = "세부목표 수정"
            pop_detailgoal_title_edt.setText(initTitle)
        }
        if (initIcon.isNotBlank()) { // 아이콘

            // 기존에 선택되어 있는 bookRBtn 버튼 초기화
            bookRBtn.isChecked = false
            DBConvert.radioColorConvert(bookRBtn, "Gray", context)

            when (initIcon) {
                "ic_book_24" -> {
                    bookRBtn.isChecked = true
                    DBConvert.radioColorConvert(bookRBtn, initColor, context) // 색상 적용
                }
                "ic_lesson_24" -> {
                    lessonRBtn.isChecked = true
                    DBConvert.radioColorConvert(lessonRBtn, initColor, context)
                }
                "ic_school_24" -> {
                    schoolRBtn.isChecked = true
                    DBConvert.radioColorConvert(schoolRBtn, initColor, context)
                }
                "dumble_icon" -> {
                    dumbleRBtn.isChecked = true
                    DBConvert.radioColorConvert(dumbleRBtn, initColor, context)
                }
                "ic_forest_24" -> {
                    forestRBtn.isChecked = true
                    DBConvert.radioColorConvert(forestRBtn, initColor, context)
                }
                "ic_sports_24" -> {
                    sportsRBtn.isChecked = true
                    DBConvert.radioColorConvert(sportsRBtn, initColor, context)
                }
                "ic_computer_24" -> {
                    computerRBtn.isChecked = true
                    DBConvert.radioColorConvert(computerRBtn, initColor, context)
                }
                "ic_piano_24" -> {
                    pianoRBtn.isChecked = true
                    DBConvert.radioColorConvert(pianoRBtn, initColor, context)
                }
                "ic_food_24" -> {
                    foodRBtn.isChecked = true
                    DBConvert.radioColorConvert(foodRBtn, initColor, context)
                }
                "ic_cafe_24" -> {
                    cafeRBtn.isChecked = true
                    DBConvert.radioColorConvert(cafeRBtn, initColor, context)
                }
                "ic_business_24" -> {
                    businessRBtn.isChecked = true
                    DBConvert.radioColorConvert(businessRBtn, initColor, context)
                }
                "ic_store_24" -> {
                    storeRBtn.isChecked = true
                    DBConvert.radioColorConvert(storeRBtn, initColor, context)
                }
                "ic_drawing_24" -> {
                    drawingRBtn.isChecked = true
                    DBConvert.radioColorConvert(drawingRBtn, initColor, context)
                }
                "ic_savings_24" -> {
                    savingsRBtn.isChecked = true
                    DBConvert.radioColorConvert(savingsRBtn, initColor, context)
                }
                "ic_stock_24" -> {
                    stockRBtn.isChecked = true
                    DBConvert.radioColorConvert(stockRBtn, initColor, context)
                }
            }
        }

        // 기존에 저장되어 있는 아이콘 값이 없다면, bookRBtn으로 값 초기화
        bookRBtn.isChecked = true
        DBConvert.radioColorConvert(bookRBtn, initColor, context)
        newIcon = "ic_book_24"

        // 라디오 그룹1 클릭 이벤트
        rGroup1.setOnCheckedChangeListener { group, checkedId ->
            colorGray()
            when(checkedId){
                R.id.pop_detailgoal_book_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(bookRBtn, initColor, context)
                    newIcon = "ic_book_24"
                }
                R.id.pop_detailgoal_lesson_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(lessonRBtn, initColor, context)
                    newIcon = "ic_lesson_24"
                }
                R.id.pop_detailgoal_school_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(schoolRBtn, initColor, context)
                    newIcon = "ic_school_24"
                }
                R.id.pop_detailgoal_dumble_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(dumbleRBtn, initColor, context)
                    newIcon = "dumble_icon"
                }
                R.id.pop_detailgoal_forest_rbtn -> {
                    colorGray()
                    rGroup2.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(forestRBtn, initColor, context)
                    newIcon = "ic_forest_24"
                }
            }
        }

        // 라디오 그룹2 클릭 이벤트
        rGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.pop_detailgoal_sports_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(sportsRBtn, initColor, context)
                    newIcon = "ic_sports_24"
                }
                R.id.pop_detailgoal_computer_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(computerRBtn, initColor, context)
                    newIcon = "ic_computer_24"
                }
                R.id.pop_detailgoal_piano_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(pianoRBtn, initColor, context)
                    newIcon = "ic_piano_24"
                }
                R.id.pop_detailgoal_food_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(foodRBtn, initColor, context)
                    newIcon = "ic_food_24"
                }
                R.id.pop_detailgoal_cafe_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(cafeRBtn, initColor, context)
                    newIcon = "ic_cafe_24"
                }
            }
        }

        // 라디오 그룹3 클릭 이벤트
        rGroup3.setOnCheckedChangeListener { radioGroup, checkedId ->
            colorGray()
            when(checkedId){
                R.id.pop_detailgoal_business_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(businessRBtn, initColor, context)
                    newIcon = "ic_business_24"
                }
                R.id.pop_detailgoal_store_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(storeRBtn, initColor, context)
                    newIcon = "ic_store_24"
                }
                R.id.pop_detailgoal_drawing_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(drawingRBtn, initColor, context)
                    newIcon = "ic_drawing_24"
                }
                R.id.pop_detailgoal_savings_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(savingsRBtn, initColor, context)
                    newIcon = "ic_savings_24"
                }
                R.id.pop_detailgoal_stock_rbtn -> {
                    colorGray()
                    rGroup1.clearCheck()
                    rGroup3.clearCheck()
                    DBConvert.radioColorConvert(stockRBtn, initColor, context)
                    newIcon = "ic_stock_24"
                }
            }
        }

        // 취소 버튼 클릭 이벤트
        cancelBtn.setOnClickListener {
            onClickListener.onClick(false, initTitle, initIcon, initColor, initBigGoal)
            dialog.dismiss()
        }

        // 세부목표 추가&수정 버튼 클릭 이벤트
        confirmBtn.setOnClickListener {

            var str_detailgoal = pop_detailgoal_title_edt.text.toString() // 세부목표 변수
            var isOverlap = false // 중복값을 확인하기 위한 변수

            // 세부목표를 입력 안 했다면
            if (str_detailgoal.isBlank()) {
                Toast.makeText(context, "세부 목표를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else { // 세부목표를 입력했다면
                // DB에 중복 세부목표 값이 있는지 확인
                dbManager = DBManager(context, "hamster_db", null, 1)
                sqlitedb = dbManager.readableDatabase

                var cursor: Cursor
                cursor = sqlitedb.rawQuery("SELECT detail_goal_name FROM detail_goal_db WHERE detail_goal_name = '${str_detailgoal}'",null)
                if (cursor.moveToNext()) {
                    isOverlap = true
                }
                cursor.close()
                sqlitedb.close()

                // 아이콘만 수정했다면 db값 수정
                if (initTitle == str_detailgoal && initIcon != newIcon) {
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE detail_goal_db SET icon = '$newIcon' WHERE detail_goal_name = '$initTitle';")
                    sqlitedb.close()

                    onClickListener.onClick(true, initTitle, newIcon, initColor, initBigGoal)
                    dialog.dismiss()
                    Toast.makeText(context, "목표를 수정했습니다", Toast.LENGTH_SHORT).show()
                }
                // 세부목표만 수정했다면
                else if (initTitle != str_detailgoal && initIcon == newIcon) {
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE detail_goal_db SET detail_goal_name = '$str_detailgoal' WHERE detail_goal_name = '$initTitle';")
                    sqlitedb.close()

                    onClickListener.onClick(true, str_detailgoal, initIcon, initColor, initBigGoal)
                    dialog.dismiss()
                    Toast.makeText(context, "목표를 수정했습니다", Toast.LENGTH_SHORT).show()
                }
                // 세부목표와 아이콘 모두 수정했다면
                else if (isOverlap && initTitle != str_detailgoal && initIcon != newIcon) {
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE detail_goal_db SET detail_goal_name = '$str_detailgoal' WHERE detail_goal_name = '$initTitle';")
                    sqlitedb.execSQL("UPDATE detail_goal_db SET icon = '$newIcon' WHERE detail_goal_name = '$initTitle';")
                    sqlitedb.close()

                    onClickListener.onClick(true, str_detailgoal, newIcon, initColor, initBigGoal)
                    dialog.dismiss()
                    Toast.makeText(context, "목표를 수정했습니다", Toast.LENGTH_SHORT).show()
                }
                // 입력한 값이 기존 db에 없는 값이라면 db에 값 저장
                else if (!isOverlap) {
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('" + str_detailgoal + "', '" + newIcon + "', '" + "${0}" + "', '" + initBigGoal + "', '" + initColor + "');")
                    sqlitedb.close()

                    onClickListener.onClick(true, str_detailgoal, newIcon, initColor, initBigGoal)
                    dialog.dismiss()
                    Toast.makeText(context, "목표를 저장했습니다", Toast.LENGTH_SHORT).show()
                }
                else { // 중복 값이라면 메시지 띄우기
                    Toast.makeText(context, "이미 같은 목표가 존재합니다", Toast.LENGTH_SHORT).show()
                }
                dbManager.close()
            }
        }
    }

    interface ButtonClickListener {
        fun onClick(isChanged: Boolean, title: String, icon: String, color: String, bigGoal: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickListener = listener
    }

    // 버튼 초기화 (버튼색을 회색으로 변경)
    private fun colorGray() {
        DBConvert.radioColorConvert(bookRBtn, "Gray", context)
        DBConvert.radioColorConvert(lessonRBtn, "Gray", context)
        DBConvert.radioColorConvert(schoolRBtn, "Gray", context)
        DBConvert.radioColorConvert(dumbleRBtn, "Gray", context)
        DBConvert.radioColorConvert(forestRBtn, "Gray", context)
        DBConvert.radioColorConvert(sportsRBtn, "Gray", context)
        DBConvert.radioColorConvert(computerRBtn, "Gray", context)
        DBConvert.radioColorConvert(pianoRBtn, "Gray", context)
        DBConvert.radioColorConvert(foodRBtn, "Gray", context)
        DBConvert.radioColorConvert(cafeRBtn, "Gray", context)
        DBConvert.radioColorConvert(businessRBtn, "Gray", context)
        DBConvert.radioColorConvert(storeRBtn, "Gray", context)
        DBConvert.radioColorConvert(drawingRBtn, "Gray", context)
        DBConvert.radioColorConvert(savingsRBtn, "Gray", context)
        DBConvert.radioColorConvert(stockRBtn, "Gray", context)
    }
}