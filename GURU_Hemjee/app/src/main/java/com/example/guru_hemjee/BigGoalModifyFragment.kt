package com.example.guru_hemjee

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.guru_hemjee.FunTimeConvert.Companion.time
import java.text.SimpleDateFormat

class BigGoalModifyFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // 대표 목표
    lateinit var modBigGoalEditText : EditText

    // 라디오 그룹
    lateinit var modColorRadioGroup1 : RadioGroup // 1행 라디오 그룹 변수
    lateinit var modColorRadioGroup2 : RadioGroup // 2행 라디오 그룹 변수

    // 라디오 버튼
    lateinit var modOrangeRadioBtn : RadioButton // 오렌지색 라디오 버튼
    lateinit var modYellowRadioBtn : RadioButton // 노랑색 라디오 버튼
    lateinit var modNoteYellowRadioBtn : RadioButton // 노트노랑색 라디오 버튼
    lateinit var modApricotRadioBtn : RadioButton // 살구색 라디오 버튼
    lateinit var modSeedBrownRadioBtn : RadioButton // 갈색 라디오 버튼
    lateinit var modDarkBrownRadioBtn : RadioButton // 고동색 라디오 버튼
    lateinit var modLightGreenRadioBtn : RadioButton // 연두색 라디오 버튼
    lateinit var modGreenRadioBtn : RadioButton // 초록색 라디오 버튼
    lateinit var modLightBlueRadioBtn : RadioButton // 하늘색 라디오 버튼
    lateinit var modBlueRadioBtn : RadioButton // 파랑색 라디오 버튼
    lateinit var modPurpleRadioBtn : RadioButton // 보라색 라디오 버튼
    lateinit var modPinkRadioBtn : RadioButton // 분홍색 라디오 버튼

    // 목표 잠금 시간
    lateinit var modTodayLockHourView : EditText // 시간
    lateinit var modTodayLockMinView : EditText // 분

    // 삭제, 확인 버튼
    lateinit var modDeleteButton : androidx.appcompat.widget.AppCompatButton // 삭제 버튼
    lateinit var modCompleteButton : androidx.appcompat.widget.AppCompatButton // 확인 버튼

    private lateinit var str_biggoal : String // 대표목표
    private var integer_color : Int = 0 // 대표목표 색상
    private var lock_time = time // 시간
    private var lock_timeArray = time.split(':')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_big_goal_modify, container, false)

        // 대표 목표
        modBigGoalEditText = view.findViewById(R.id.modBigGoalEditText)

        // 라디오 그룹
        modColorRadioGroup1 = view.findViewById(R.id.modColorRadioGroup1)
        modColorRadioGroup2 = view.findViewById(R.id.modColorRadioGroup2)

        // 라디오 버튼
        modOrangeRadioBtn = view.findViewById(R.id.modOrangeRadioBtn)
        modYellowRadioBtn = view.findViewById(R.id.modYellowRadioBtn)
        modNoteYellowRadioBtn = view.findViewById(R.id.modNoteYellowRadioBtn)
        modApricotRadioBtn = view.findViewById(R.id.modApricotRadioBtn)
        modSeedBrownRadioBtn = view.findViewById(R.id.modSeedBrownRadioBtn)
        modDarkBrownRadioBtn = view.findViewById(R.id.modDarkBrownRadioBtn)
        modLightGreenRadioBtn = view.findViewById(R.id.modLightGreenRadioBtn)
        modGreenRadioBtn = view.findViewById(R.id.modGreenRadioBtn)
        modLightBlueRadioBtn = view.findViewById(R.id.modLightBlueRadioBtn)
        modBlueRadioBtn = view.findViewById(R.id.modBlueRadioBtn)
        modPurpleRadioBtn = view.findViewById(R.id.modPurpleRadioBtn)
        modPinkRadioBtn = view.findViewById(R.id.modPinkRadioBtn)

        // 목표 잠금 시간
        modTodayLockHourView = view.findViewById(R.id.modTodayLockHourView)
        modTodayLockMinView = view.findViewById(R.id.modTodayLockMinView)

        // 삭제, 확인 버튼
        modDeleteButton = view.findViewById(R.id.modDeleteButton)
        modCompleteButton = view.findViewById(R.id.modCompleteButton)

        // DetailGoalSetupFragment에서 넘어온 값(대표 목표) 받기
        if (arguments != null)
        {
            str_biggoal = requireArguments().getString("bundle_biggoal").toString() // 대표목표

            // 대표목표 DB
            dbManager = DBManager(context, "big_goal_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 대표목표 찾기
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '"+str_biggoal+"';", null)

            // 색상, 시간 값 찾기
            if(cursor.moveToNext()) {
                integer_color = cursor.getInt(cursor.getColumnIndex("color"))
                lock_time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time"))
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            // 위젯에 반영하기
            modBigGoalEditText.setText(str_biggoal)
            when(integer_color) {
                ContextCompat.getColor(requireContext(), R.color.Orange) -> modOrangeRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Yellow) -> modYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.NoteYellow) -> modNoteYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Apricot) -> modNoteYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.SeedBrown) -> modSeedBrownRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.DarkBrown) -> modDarkBrownRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.LightGreen) -> modLightGreenRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Green) -> modGreenRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.LightBlue) -> modLightBlueRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Blue) -> modBlueRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Purple) -> modPurpleRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Pink) -> modPinkRadioBtn.isChecked = true
            }
            modTodayLockHourView.setText(lock_timeArray[0])
            modTodayLockMinView.setText(lock_timeArray[1])
        }
        return view
    }
}