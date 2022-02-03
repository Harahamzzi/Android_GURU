package com.example.guru_hemjee

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.util.*
import java.text.SimpleDateFormat


class BigGoalSetupFragment : Fragment() { // 대표 목표 추가 프래그먼트

    // 내부DB 사용을 위한 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // 대표 목표
    lateinit var bigGoalEditText : EditText // 대표 목표 입력할 수 있는 EditText 변수

    // 라디오 그룹
    lateinit var colorRadioGroup1 : RadioGroup // 1행 라디오 그룹 변수
    lateinit var colorRadioGroup2 : RadioGroup // 2행 라디오 그룹 변수

    // 라디오 버튼
    lateinit var orangeRadioBtn : RadioButton // 오렌지색 라디오 버튼
    lateinit var yellowRadioBtn : RadioButton // 노랑색 라디오 버튼
    lateinit var noteYellowRadioBtn : RadioButton // 노트노랑색 라디오 버튼
    lateinit var apricotRadioBtn : RadioButton // 살구색 라디오 버튼
    lateinit var seedBrownRadioBtn : RadioButton // 갈색 라디오 버튼
    lateinit var darkBrownRadioBtn : RadioButton // 고동색 라디오 버튼
    lateinit var lightGreenRadioBtn : RadioButton // 연두색 라디오 버튼
    lateinit var greenRadioBtn : RadioButton // 초록색 라디오 버튼
    lateinit var lightBlueRadioBtn : RadioButton // 하늘색 라디오 버튼
    lateinit var blueRadioBtn : RadioButton // 파랑색 라디오 버튼
    lateinit var purpleRadioBtn : RadioButton // 보라색 라디오 버튼
    lateinit var pinkRadioBtn : RadioButton // 분홍색 라디오 버튼

    // 목표 잠금 시간
    lateinit var todayLockHourView : EditText // 시간
    lateinit var todayLockMinView : EditText // 분

    // 삭제, 확인 버튼
    lateinit var deleteButton : androidx.appcompat.widget.AppCompatButton // 삭제 버튼
    lateinit var completeButton : androidx.appcompat.widget.AppCompatButton // 확인 버튼

    var mainActivity : SubMainActivity? = null // 메인 액티비티 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as SubMainActivity
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
        bigGoalEditText = view.findViewById(R.id.bigGoalEditText)

        // 라디오 그룹
        colorRadioGroup1 = view.findViewById(R.id.colorRadioGroup1)
        colorRadioGroup2 = view.findViewById(R.id.colorRadioGroup2)

        // 라디오 버튼
        orangeRadioBtn = view.findViewById(R.id.orangeRadioBtn)
        yellowRadioBtn = view.findViewById(R.id.yellowRadioBtn)
        noteYellowRadioBtn = view.findViewById(R.id.noteYellowRadioBtn)
        apricotRadioBtn = view.findViewById(R.id.apricotRadioBtn)
        seedBrownRadioBtn = view.findViewById(R.id.seedBrownRadioBtn)
        darkBrownRadioBtn = view.findViewById(R.id.darkBrownRadioBtn)
        lightGreenRadioBtn = view.findViewById(R.id.lightGreenRadioBtn)
        greenRadioBtn = view.findViewById(R.id.greenRadioBtn)
        lightBlueRadioBtn = view.findViewById(R.id.lightBlueRadioBtn)
        blueRadioBtn = view.findViewById(R.id.blueRadioBtn)
        purpleRadioBtn = view.findViewById(R.id.purpleRadioBtn)
        pinkRadioBtn = view.findViewById(R.id.pinkRadioBtn)

        // 목표 잠금 시간
        todayLockHourView = view.findViewById(R.id.todayLockHourView)
        todayLockMinView = view.findViewById(R.id.todayLockMinView)

        // 삭제, 확인 버튼
        deleteButton = view.findViewById(R.id.deleteButton)
        completeButton = view.findViewById(R.id.completeButton)

        // DB
        dbManager = DBManager(context, "hamster_db", null, 1)

        // TODO: 라디오 그룹간의 전환 시 다른 라디오 그룹에 있는 버튼을 2번 눌러야만 선택되는 문제 수정 필요
        // 색깔 라디오 버튼 클릭시 이벤트 연결
        colorRadioGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹1에서 버튼을 눌렀다면
                R.id.orangeRadioBtn,
                R.id.yellowRadioBtn,
                R.id.noteYellowRadioBtn,
                R.id.apricotRadioBtn,
                R.id.seedBrownRadioBtn,
                R.id.darkBrownRadioBtn -> {
                    colorRadioGroup2.clearCheck() // 라디오 그룹2에서 선택되어 있는 버튼 초기화
                }
            }
        }

        colorRadioGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹2에서 버튼을 눌렀다면
                R.id.lightGreenRadioBtn,
                R.id.greenRadioBtn,
                R.id.lightBlueRadioBtn,
                R.id.blueRadioBtn,
                R.id.purpleRadioBtn,
                R.id.pinkRadioBtn -> {
                    colorRadioGroup1.clearCheck() // 라디오 그룹1에서 선택되어 있는 버튼 초기화
                }
            }
        }

        // 확인 버튼을 눌렀을 경우 대표 목표 DB에 등록
        completeButton.setOnClickListener {
            var str_biggoal : String = bigGoalEditText.text.toString() // 대표 목표
            var integer_color : Int = ContextCompat.getColor(requireContext(), R.color.Orange) // 기본 색상 = 오렌지색
            var integer_hour : String = todayLockHourView.text.toString() // 잠금 시간
            var integer_min : String = todayLockMinView.text.toString()// 잠금 분

            if (str_biggoal == "") { // EditText가 비어있다면
                Toast.makeText(context, "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if (integer_hour == "" && integer_min == "") {
                Toast.makeText(context, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                when (colorRadioGroup1.checkedRadioButtonId) {
                    R.id.orangeRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.Orange)
                    R.id.yellowRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.Yellow)
                    R.id.noteYellowRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.NoteYellow)
                    R.id.apricotRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.Apricot)
                    R.id.seedBrownRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.SeedBrown)
                    R.id.darkBrownRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.DarkBrown)
                }

                when (colorRadioGroup2.checkedRadioButtonId) {
                    R.id.lightGreenRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.LightGreen)
                    R.id.greenRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.Green)
                    R.id.lightBlueRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.LightBlue)
                    R.id.blueRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.Blue)
                    R.id.purpleRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.Purple)
                    R.id.pinkRadioBtn -> integer_color = ContextCompat.getColor(requireContext(), R.color.Pink)
                }

                // TODO : 더 깔끔하게 코드를 바꿀 수 있도록 고민하기
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

                /*val bundle : Bundle = Bundle() // 번들을 통해서 값 전달
                bundle.putString("bundle_big_goal", str_biggoal) // 번들에 넘길 값(키 값 - 대표 목표) 저장
                setupfragment.setArguments(bundle) //번들을 setupfragment로 보낼 준비 */

                /*val transaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                val setupfragment = SetupFragment() // setupfragment 선언

                transaction.replace(R.id.bigGoalListView, setupfragment) // 리스트 뷰에 보내기*/
                Toast.makeText(context, "목표가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                goSetUp()
            }
        }

        // 취소 버튼을 눌렀을 경우
        deleteButton.setOnClickListener {
            goSetUp()
        }

        return view
    }

    // setupfragment로 화면을 전환하는 함수
    fun goSetUp() {
        mainActivity?.supportFragmentManager
                ?.beginTransaction()
                //?.remove(this)
                ?.replace(R.id.fragment_main, SetupFragment())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.commit()
    }
}