package com.example.guru_hemjee.Home.Goal

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.guru_hemjee.*
import com.example.guru_hemjee.Home.MainActivity
import java.lang.Exception

// 홈(MainActivity) -> SubMainActivity -> 목표/잠금 시간 설정(Setup) -> DetailGoalSetupFragment에서 연필 아이콘 클릭시
//      -> BigGoalModigyFragment
// 현재 존재하는 대표 목표를 수정하는 Fragment 화면
class BigGoalModifyFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 대표 목표
    private lateinit var goalBigModify_goalEditText: EditText

    // 라디오 그룹
    private lateinit var goalBig_ModifyColorRadioGroup1: RadioGroup // 1행 라디오 그룹 변수
    private lateinit var goalBig_ModifyColorRadioGroup2: RadioGroup // 2행 라디오 그룹 변수

    // 라디오 버튼
    private lateinit var goalBigModify_orangeRadioButton: RadioButton // 오렌지색 라디오 버튼
    private lateinit var goalBigModify_yellowRadioBtn: RadioButton // 노랑색 라디오 버튼
    private lateinit var goalBigModify_noteYellowRadioBtn: RadioButton // 노트노랑색 라디오 버튼
    private lateinit var goalBigModify_apricotRadioBtn: RadioButton // 살구색 라디오 버튼
    private lateinit var goalBigModify_seedBrownRadioBtn: RadioButton // 갈색 라디오 버튼
    private lateinit var goalBigModify_darkBrownRadioBtn: RadioButton // 고동색 라디오 버튼
    private lateinit var goalBigModify_lightGreenRadioBtn: RadioButton // 연두색 라디오 버튼
    private lateinit var goalBigModify_greenRadioBtn: RadioButton // 초록색 라디오 버튼
    private lateinit var goalBigModify_lightBlueRadioBtn: RadioButton // 하늘색 라디오 버튼
    private lateinit var goalBigModify_blueRadioBtn: RadioButton // 파랑색 라디오 버튼
    private lateinit var goalBigModify_purpleRadioBtn: RadioButton // 보라색 라디오 버튼
    private lateinit var goalBigModify_pinkRadioBtn: RadioButton // 분홍색 라디오 버튼

    // 목표 잠금 시간
    private lateinit var goalBigModify_todayLockHourEditText: EditText // 시간
    private lateinit var bigGoalModify_todayLockMinEditText: EditText // 분

    // 삭제, 확인 버튼
    private lateinit var bigGoalModify_cancelButton: AppCompatButton // 삭제 버튼
    private lateinit var bigGoalModify_storeButton: AppCompatButton // 확인 버튼

    private lateinit var str_big_goal: String // 대표목표
    private var integer_color: Int = 0 // 대표목표 색상
    private lateinit var total_time: String

    private var mainActivity: MainActivity? = null // 메인 액티비티 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()

        mainActivity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_big_goal_modify, container, false)

        // 대표 목표
        goalBigModify_goalEditText = view.findViewById(R.id.goalBigModify_goalEditText)

        // 라디오 그룹
        goalBig_ModifyColorRadioGroup1 = view.findViewById(R.id.goalBig_ModifyColorRadioGroup1)
        goalBig_ModifyColorRadioGroup2 = view.findViewById(R.id.goalBig_ModifyColorRadioGroup2)

        // 라디오 버튼
        goalBigModify_orangeRadioButton = view.findViewById(R.id.goalBigModify_orangeRadioButton)
        goalBigModify_yellowRadioBtn = view.findViewById(R.id.goalBigModify_yellowRadioButton)
        goalBigModify_noteYellowRadioBtn = view.findViewById(R.id.goalBigModify_noteYellowRadioButton)
        goalBigModify_apricotRadioBtn = view.findViewById(R.id.goalBigModify_apricotRadioButton)
        goalBigModify_seedBrownRadioBtn = view.findViewById(R.id.goalBigModify_seedBrownRadioButton)
        goalBigModify_darkBrownRadioBtn = view.findViewById(R.id.goalBigModify_darkBrownRadioButton)
        goalBigModify_lightGreenRadioBtn = view.findViewById(R.id.goalBigModify_lightGreenRadioButton)
        goalBigModify_greenRadioBtn = view.findViewById(R.id.goalBigModify_greenRadioButton)
        goalBigModify_lightBlueRadioBtn = view.findViewById(R.id.goalBigModify_lightBlueRadioButton)
        goalBigModify_blueRadioBtn = view.findViewById(R.id.goalBigModify_blueRadioButton)
        goalBigModify_purpleRadioBtn = view.findViewById(R.id.goalBigModify_purpleRadioButton)
        goalBigModify_pinkRadioBtn = view.findViewById(R.id.goalBigModify_pinkRadioButton)

        // 목표 잠금 시간
        goalBigModify_todayLockHourEditText = view.findViewById(R.id.goalBigModify_todayLockHourEditText)
        bigGoalModify_todayLockMinEditText = view.findViewById(R.id.bigGoalModify_todayLockMinEditText)

        // 삭제, 확인 버튼
        bigGoalModify_cancelButton = view.findViewById(R.id.bigGoalModify_cancelButton)
        bigGoalModify_storeButton = view.findViewById(R.id.bigGoalModify_storeButton)

        // DetailGoalSetupFragment에서 넘어온 값(대표 목표) 받기
        if (arguments != null) {
            str_big_goal = requireArguments().getString("bundle_biggoal_2").toString() // 대표목표

            // 대표목표 DB
            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 대표목표 찾기
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';", null)

            // 색상, 시간 값 찾기
            if (cursor.moveToNext()) {
                integer_color = cursor.getInt(cursor.getColumnIndex("color"))
                total_time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time")).toString()
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            // 위젯에 반영하기
            goalBigModify_goalEditText.setText(str_big_goal)
            when (integer_color) {
                ContextCompat.getColor(requireContext(), R.color.Orange) -> goalBigModify_orangeRadioButton.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Yellow) -> goalBigModify_yellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.NoteYellow) -> goalBigModify_noteYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Apricot) -> goalBigModify_noteYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.SeedBrown) -> goalBigModify_seedBrownRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.DarkBrown) -> goalBigModify_darkBrownRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.LightGreen) -> goalBigModify_lightGreenRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Green) -> goalBigModify_greenRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.LightBlue) -> goalBigModify_lightBlueRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Blue) -> goalBigModify_blueRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Purple) -> goalBigModify_purpleRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Pink) -> goalBigModify_pinkRadioBtn.isChecked = true
            }

            var lock_timeArray = total_time.split(':')
            goalBigModify_todayLockHourEditText.setHint(lock_timeArray[0])
            bigGoalModify_todayLockMinEditText.setHint(lock_timeArray[1])
        }

        // 색깔 라디오 버튼 클릭시 이벤트 연결
        goalBig_ModifyColorRadioGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹1에서 버튼을 눌렀다면
                R.id.goalBigModify_orangeRadioButton,
                R.id.goalBigModify_yellowRadioButton,
                R.id.goalBigModify_noteYellowRadioButton,
                R.id.goalBigModify_apricotRadioButton,
                R.id.goalBigModify_seedBrownRadioButton,
                R.id.goalBigModify_darkBrownRadioButton -> {
                    goalBig_ModifyColorRadioGroup2.clearCheck() // 라디오 그룹2에서 선택되어 있는 버튼 초기화
                }
            }
        }
        goalBig_ModifyColorRadioGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹2에서 버튼을 눌렀다면
                R.id.goalBigModify_lightGreenRadioButton,
                R.id.goalBigModify_greenRadioButton,
                R.id.goalBigModify_lightBlueRadioButton,
                R.id.goalBigModify_blueRadioButton,
                R.id.goalBigModify_purpleRadioButton,
                R.id.goalBigModify_pinkRadioButton -> {
                    goalBig_ModifyColorRadioGroup1.clearCheck() // 라디오 그룹1에서 선택되어 있는 버튼 초기화
                }
            }
        }

        dbManager = DBManager(context, "hamster_db", null, 1)

        // 저장버튼을 눌렀을 경우
        bigGoalModify_storeButton.setOnClickListener {
            var big_goal = goalBigModify_goalEditText.text.toString() // 대표 목표
            var color: Int = integer_color // 색상
            var str_hour: String = goalBigModify_todayLockHourEditText.text.toString() // 잠금 시간
            var str_min: String = bigGoalModify_todayLockMinEditText.text.toString()// 잠금 분

            if (big_goal.isNullOrBlank()) { // EditText가 비어있다면
                Toast.makeText(context, "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (str_hour.isNullOrBlank() && str_min.isNullOrBlank()) {
                Toast.makeText(context, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                when (goalBig_ModifyColorRadioGroup1.checkedRadioButtonId) {
                    R.id.goalBigModify_orangeRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.Orange
                    )
                    R.id.goalBigModify_yellowRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.Yellow
                    )
                    R.id.goalBigModify_noteYellowRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.NoteYellow
                    )
                    R.id.goalBigModify_apricotRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.Apricot
                    )
                    R.id.goalBigModify_seedBrownRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.SeedBrown
                    )
                    R.id.goalBigModify_darkBrownRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.DarkBrown
                    )
                }
                when (goalBig_ModifyColorRadioGroup2.checkedRadioButtonId) {
                    R.id.goalBigModify_lightGreenRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.LightGreen
                    )
                    R.id.goalBigModify_greenRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.Green
                    )
                    R.id.goalBigModify_lightBlueRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.LightBlue
                    )
                    R.id.goalBigModify_blueRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.Blue
                    )
                    R.id.goalBigModify_purpleRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.Purple
                    )
                    R.id.goalBigModify_pinkRadioButton -> color = ContextCompat.getColor(requireContext(),
                        R.color.Pink
                    )
                }
            }

            lateinit var total_time: String
            if (str_hour == "" && str_min == "") {
                Toast.makeText(context, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if((str_hour != "" && str_hour.toInt() > 23 || (str_min != "" && str_min.toInt() > 59))) {
                Toast.makeText(context, "올바른 시간을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                if(str_hour == "")
                    str_hour = "0"
                else if(str_min == "")
                    str_min = "0"

                total_time = FunTimeConvert.timeConvert(str_hour, str_min, null)

                /** <데이터를 수정하는 방법>
                 * 1. 데이터 조회 후 기존db에 저장되어 있는 대표목표 값과 변경하려는 대표목표 값이 같다면, 토스트메시지 띄우기
                 * 2. 새로운 테이블 생성
                 * 2. 기존 테이블에 있던 특정 데이터 값들만 복사해서 새로운 테이블에 붙여넣기
                 * 3. 기존 테이블에 있는 복사된 데이터 삭제
                 * 4. 복사한 테이블의 데이터 수정
                 * 5. 기존 테이블에 복사한 테이블의 데이터 추가하기
                 * 6. 새로 만든 테이블 삭제
                 **/

                sqlitedb = dbManager.writableDatabase
                try {
                    sqlitedb.execSQL("CREATE TABLE IF NOT EXISTS copy_goal_db (big_goal_name text, color INT, big_goal_lock_time text)")
                } catch (e : Exception) {
                    e.printStackTrace()
                }
                sqlitedb.execSQL("INSERT INTO copy_goal_db SELECT * FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
                sqlitedb.execSQL("DELETE FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
                sqlitedb.execSQL("UPDATE copy_goal_db SET color = " + color + " WHERE big_goal_name = '" + str_big_goal + "';")
                sqlitedb.execSQL("UPDATE copy_goal_db SET big_goal_lock_time = '" + total_time + "' WHERE big_goal_name = '" + str_big_goal + "';")
                if (big_goal == str_big_goal) { // 대표목표의 값이 변경되지 않았다면
                    sqlitedb.execSQL("INSERT INTO big_goal_db SELECT * FROM copy_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
                    sqlitedb.execSQL("DROP TABLE copy_goal_db")
                    sqlitedb.close()
                    sqlitedb.close()
                    goDetailGoalSetup(big_goal) // 세부목표 화면으로 이동
                } else { // 대표목표의 값이 변경되었다면
                    var cursor: Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)

                    var isFlag : Boolean = false
                    while (cursor.moveToNext()) { // 기존에 저장된 값과 같다면
                        var goal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
                        if (goal == big_goal) {
                            isFlag = true
                            // Log.d("while문 안의 flag값  ", isFlag.toString())
                            break
                        }
                    }
                    // Log.d("while문 아래 flag값  ", isFlag.toString())
                    if (!isFlag) {
                        // Log.d("if문 flag값  ", isFlag.toString())
                        sqlitedb.execSQL("UPDATE copy_goal_db SET big_goal_name = '" + big_goal + "' WHERE big_goal_name = '" + str_big_goal + "';")
                        sqlitedb.execSQL("INSERT INTO big_goal_db SELECT * FROM copy_goal_db WHERE big_goal_name = '" + big_goal + "';")

                        cursor.close()
                        sqlitedb.execSQL("DROP TABLE copy_goal_db")
                        sqlitedb.close()

                        sqlitedb = dbManager.writableDatabase
                        sqlitedb.execSQL("UPDATE detail_goal_db SET big_goal_name = '" + big_goal + "' WHERE big_goal_name = '" + str_big_goal + "';")
                        sqlitedb.close()

                        goDetailGoalSetup(big_goal) // 세부목표 화면으로 이동
                    }
                    else {
                        // Log.d("else문 flag값  ", isFlag.toString())
                        Toast.makeText(context, "다른 내용의 대표목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            sqlitedb.close()
            dbManager.close()
        }

        // 삭제 버튼을 눌렀을 경우
        bigGoalModify_cancelButton.setOnClickListener {
            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.writableDatabase

            sqlitedb.execSQL("DELETE FROM detail_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.execSQL("DELETE FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.close()
            dbManager.close()

            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val setupFragment = SetupFragment()

            transaction.replace(R.id.fragment_main, setupFragment)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction.commit() // 저장
            requireActivity().supportFragmentManager.popBackStack("Modify", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            requireActivity().supportFragmentManager.popBackStack("BigGoal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            requireActivity().supportFragmentManager.popBackStack("DetailGoal", FragmentManager.POP_BACK_STACK_INCLUSIVE)

            Toast.makeText(context, "목표가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    // 세부목표 화면으로 이동
    private fun goDetailGoalSetup(big_goal: String) {
        Toast.makeText(context, "목표 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()

        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val detailGoalSetupFragment = DetailGoalSetupFragment()
        val bundle = Bundle()

        bundle.putString("bundle_biggoal", big_goal)
        detailGoalSetupFragment.arguments = bundle
        transaction.remove(this)
        transaction.commit() // 저장
        requireActivity().supportFragmentManager.popBackStack("ModifyGoal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        requireActivity().supportFragmentManager.popBackStack("BigGoal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        requireActivity().supportFragmentManager.popBackStack("DetailGoal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}