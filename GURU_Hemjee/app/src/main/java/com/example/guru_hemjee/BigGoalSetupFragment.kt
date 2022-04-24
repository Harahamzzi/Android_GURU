package com.example.guru_hemjee

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

// 홈(MainActivity) -> SubMainActivity -> 목표/잠금 시간 설정(Setup) -> BigGoalSetupFragment
// 대표 목표 추가 Fragment 화면
class BigGoalSetupFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 대표 목표
    private lateinit var bigGoal_TextView: EditText // 대표 목표 입력할 수 있는 EditText 변수

    // 라디오 그룹
    private lateinit var bigGoal_ColorRadioGroup1: RadioGroup // 1행 라디오 그룹 변수
    private lateinit var bigGoal_ColorRadioGroup2: RadioGroup // 2행 라디오 그룹 변수

    // 라디오 버튼
    private lateinit var bigGoal_orangeRadioButton: RadioButton // 오렌지색 라디오 버튼
    private lateinit var bigGoal_yellowRadioBtn: RadioButton // 노랑색 라디오 버튼
    private lateinit var bigGoal_noteYellowRadioBtn: RadioButton // 노트노랑색 라디오 버튼
    private lateinit var bigGoal_apricotRadioBtn: RadioButton // 살구색 라디오 버튼
    private lateinit var bigGoal_seedBrownRadioBtn: RadioButton // 갈색 라디오 버튼
    private lateinit var bigGoal_darkBrownRadioBtn: RadioButton // 고동색 라디오 버튼
    private lateinit var bigGoal_lightGreenRadioBtn: RadioButton // 연두색 라디오 버튼
    private lateinit var bigGoal_greenRadioBtn: RadioButton // 초록색 라디오 버튼
    private lateinit var bigGoal_lightBlueRadioBtn: RadioButton // 하늘색 라디오 버튼
    private lateinit var bigGoal_blueRadioBtn: RadioButton // 파랑색 라디오 버튼
    private lateinit var bigGoal_purpleRadioBtn: RadioButton // 보라색 라디오 버튼
    private lateinit var bigGoal_pinkRadioBtn: RadioButton // 분홍색 라디오 버튼

    // 목표 잠금 시간
    private lateinit var bigGoal_todayLockHourEditText: EditText // 시간
    private lateinit var bigGoal_todayLockMinEditText: EditText // 분

    // 삭제, 확인 버튼
    private lateinit var bigGoal_cancelButton: AppCompatButton // 취소 버튼
    private lateinit var bigGoal_storeButton: AppCompatButton // 확인 버튼

    private var mainActivity : MainActivity? = null // 메인 액티비티 변수

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_big_goal_setup, container, false)

        // 대표 목표
        bigGoal_TextView = view.findViewById(R.id.bigGoal_TextView)

        // 라디오 그룹
        bigGoal_ColorRadioGroup1 = view.findViewById(R.id.bigGoal_ColorRadioGroup1)
        bigGoal_ColorRadioGroup2 = view.findViewById(R.id.bigGoal_ColorRadioGroup2)

        // 라디오 버튼
        bigGoal_orangeRadioButton = view.findViewById(R.id.bigGoal_orangeRadioButton)
        bigGoal_yellowRadioBtn = view.findViewById(R.id.bigGoal_yellowRadioButton)
        bigGoal_noteYellowRadioBtn = view.findViewById(R.id.bigGoal_noteYellowRadioButton)
        bigGoal_apricotRadioBtn = view.findViewById(R.id.bigGoal_apricotRadioButton)
        bigGoal_seedBrownRadioBtn = view.findViewById(R.id.bigGoal_seedBrownRadioButton)
        bigGoal_darkBrownRadioBtn = view.findViewById(R.id.bigGoal_darkBrownRadioButton)
        bigGoal_lightGreenRadioBtn = view.findViewById(R.id.bigGoal_lightGreenRadioButton)
        bigGoal_greenRadioBtn = view.findViewById(R.id.bigGoal_greenRadioButton)
        bigGoal_lightBlueRadioBtn = view.findViewById(R.id.bigGoal_lightBlueRadioButton)
        bigGoal_blueRadioBtn = view.findViewById(R.id.bigGoal_blueRadioButton)
        bigGoal_purpleRadioBtn = view.findViewById(R.id.bigGoal_purpleRadioButton)
        bigGoal_pinkRadioBtn = view.findViewById(R.id.bigGoal_pinkRadioButton)

        // 목표 잠금 시간
        bigGoal_todayLockHourEditText = view.findViewById(R.id.bigGoal_todayLockHourEditText)
        bigGoal_todayLockMinEditText = view.findViewById(R.id.bigGoal_todayLockMinEditText)

        // 취소, 확인 버튼
        bigGoal_cancelButton = view.findViewById(R.id.bigGoal_cancelButton)
        bigGoal_storeButton = view.findViewById(R.id.bigGoal_storeButton)

        // DB
        dbManager = DBManager(context, "hamster_db", null, 1)

        // 색깔 라디오 버튼 클릭시 이벤트 연결
        bigGoal_ColorRadioGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹1에서 버튼을 눌렀다면
                R.id.bigGoal_orangeRadioButton,
                R.id.bigGoal_yellowRadioButton,
                R.id.bigGoal_noteYellowRadioButton,
                R.id.bigGoal_apricotRadioButton,
                R.id.bigGoal_seedBrownRadioButton,
                R.id.bigGoal_darkBrownRadioButton -> {
                    bigGoal_ColorRadioGroup2.clearCheck() // 라디오 그룹2에서 선택되어 있는 버튼 초기화
                }
            }
        }

        bigGoal_ColorRadioGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹2에서 버튼을 눌렀다면
                R.id.bigGoal_lightGreenRadioButton,
                R.id.bigGoal_greenRadioButton,
                R.id.bigGoal_lightBlueRadioButton,
                R.id.bigGoal_blueRadioButton,
                R.id.bigGoal_purpleRadioButton,
                R.id.bigGoal_pinkRadioButton -> {
                    bigGoal_ColorRadioGroup1.clearCheck() // 라디오 그룹1에서 선택되어 있는 버튼 초기화
                }
            }
        }

        // 확인 버튼을 눌렀을 경우 대표 목표 DB에 등록
        bigGoal_storeButton.setOnClickListener {
            var str_biggoal: String = bigGoal_TextView.text.toString() // 대표 목표
            var integer_color: Int = ContextCompat.getColor(requireContext(), R.color.Orange) // 기본 색상 = 오렌지색
            var integer_hour: String = bigGoal_todayLockHourEditText.text.toString() // 잠금 시간
            var integer_min: String = bigGoal_todayLockMinEditText.text.toString() // 잠금 분

            if (str_biggoal == "") { // 대표목표 입력을 안했다면
                Toast.makeText(context, "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if (integer_hour == "" && integer_min == "") { // 시, 분 입력을 안했다면
                Toast.makeText(context, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if((integer_hour != "" && integer_hour.toInt() > 23 || (integer_min != "" && integer_min.toInt() > 59))){
                // 범위 외의 값을 입력헀다면
                Toast.makeText(context, "올바른 시간을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else { // 선택한 색상값 저장
                when (bigGoal_ColorRadioGroup1.checkedRadioButtonId) {
                    R.id.bigGoal_orangeRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.Orange)
                    R.id.bigGoal_yellowRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.Yellow)
                    R.id.bigGoal_noteYellowRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.NoteYellow)
                    R.id.bigGoal_apricotRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.Apricot)
                    R.id.bigGoal_seedBrownRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.SeedBrown)
                    R.id.bigGoal_darkBrownRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.DarkBrown)
                }

                when (bigGoal_ColorRadioGroup2.checkedRadioButtonId) {
                    R.id.bigGoal_lightGreenRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.LightGreen)
                    R.id.bigGoal_greenRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.Green)
                    R.id.bigGoal_lightBlueRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.LightBlue)
                    R.id.bigGoal_blueRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.Blue)
                    R.id.bigGoal_purpleRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.Purple)
                    R.id.bigGoal_pinkRadioButton -> integer_color = ContextCompat.getColor(requireContext(), R.color.Pink)
                }

                lateinit var total_time : String
                if (integer_hour.isNullOrBlank()) { // 시간이 공란인 경우
                    if (integer_min.toInt() < 0 || integer_min.toInt() >= 60) {
                        Toast.makeText(context, "분을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        total_time = FunTimeConvert.timeConvert(null, integer_min.toInt().toString(), null)
                    }
                } else if (integer_min.isNullOrBlank()) { // 분이 공란인 경우
                    if (integer_hour.toInt() < 0 || integer_hour.toInt() > 24) {
                        Toast.makeText(context, "시간을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        total_time = FunTimeConvert.timeConvert(integer_hour.toInt().toString(), null, null)
                    }
                } else if (integer_hour.toInt() < 0 || integer_hour.toInt() > 24) { // 시간 범위
                    Toast.makeText(context, "시간을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (integer_min.toInt() < 0 || integer_min.toInt() >= 60) { // 분 범위
                    Toast.makeText(context, "분을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    total_time = FunTimeConvert.timeConvert(integer_hour.toInt().toString(), integer_min.toInt().toString(), null)
                }

                sqlitedb = dbManager.writableDatabase // 정보를 DB에 저장
                // 중복 데이터 제외하고 저장
                sqlitedb.execSQL("INSERT OR IGNORE INTO big_goal_db VALUES ('" + str_biggoal + "', '" + integer_color + "', '" + total_time + "');")
                sqlitedb.close()

                Toast.makeText(context, "목표가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                goSetUp()
            }
        }

        // 취소 버튼을 눌렀을 경우
        bigGoal_cancelButton.setOnClickListener {
            goSetUp()
        }

        return view
    }

    // Setupfragment로 화면을 전환하는 함수
    private fun goSetUp() {
        mainActivity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_main, SetupFragment())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.commit()
        requireActivity().supportFragmentManager.popBackStack("BigGoal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}