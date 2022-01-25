package com.example.guru_hemjee

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


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

    // 사용 가능한 앱
    lateinit var plusPossibleAppBtn : ImageButton // 사용 가능한 앱 추가할 수 있는 버튼

    // 삭제, 확인 버튼
    lateinit var deleteButton : androidx.appcompat.widget.AppCompatButton // 삭제 버튼
    lateinit var completeButton : androidx.appcompat.widget.AppCompatButton // 확인 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_big_goal_setup, container, false);

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

        // 사용 가능한 앱
        plusPossibleAppBtn = view.findViewById(R.id.plusPossibleAppBtn)

        // 삭제, 확인 버튼
        deleteButton = view.findViewById(R.id.deleteButton)
        completeButton = view.findViewById(R.id.completeButton)

        // DB
        dbManager = DBManager(context, "biggoalDB", null, 1)

        // TODO: (코드 수정했는데 디바이스 오류로 인해서 아직 시도X)라디오 그룹간의 전환 시 다른 라디오 그룹에 있는 버튼을 2번 눌러야만 선택되는 문제 수정 필요
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

        // 확인 버튼을 눌렀을 경우
        completeButton.setOnClickListener {
            var str_biggoal : String = bigGoalEditText.text.toString() // 대표 목표
            var str_color : String = "" // 색상
            var str_hour : String = todayLockHourView.text.toString() // 잠금 시간
            var str_min : String = todayLockMinView.text.toString() // 잠금 분
            // TODO : 사용 가능한 앱 리스트 가져오기 구현 필요

            when (colorRadioGroup1.checkedRadioButtonId) {
                R.id.orangeRadioBtn -> str_color = "오렌지색"
                R.id.yellowRadioBtn -> str_color = "노랑색"
                R.id.noteYellowRadioBtn -> str_color = "노트노랑색"
                R.id.apricotRadioBtn -> str_color = "살구색"
                R.id.seedBrownRadioBtn -> str_color = "갈색"
                R.id.darkBrownRadioBtn -> str_color = "고동색"
                R.id.lightGreenRadioBtn -> str_color = "연두색"
                R.id.greenRadioBtn -> str_color = "초록색"
                R.id.lightBlueRadioBtn -> str_color = "하늘색"
                R.id.blueRadioBtn -> str_color = "파랑색"
                R.id.purpleRadioBtn -> str_color = "보라색"
                R.id.pinkRadioBtn -> str_color = "분홍색"
            }

            sqlitedb = dbManager.writableDatabase // 정보를 DB에 저장
            sqlitedb.execSQL("INSERT INTO personnel VALUES ('" + str_biggoal + "', '" + str_color + "', " + str_hour + ", '" + str_min + "')")
            sqlitedb.close()

            val bundle = Bundle() // 번들을 통해서 값 전달

            bundle.putString("bundle_big_goal", str_biggoal) // 번들에 넘길 값(대표 목표) 저장

            val transaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val setupfragment = SetupFragment() // setupfragment 선언

            setupfragment.setArguments(bundle) //번들을 setupfragment로 보낼 준비

            transaction.replace(R.id.bigGoalListView, setupfragment) // 리스트 뷰에 보내기
            transaction.commit()
        }

        // 삭제 버튼을 눌렀을 경우
        deleteButton.setOnClickListener {
            // TODO : 입력되어 있는 정보가 DB에 등록된 정보인지 확인
            // Todo : if) 입력되어 있다면, 정보 삭제
            // Todo : else if) 입력되어 있지 않다면, 토스트 메시지 띄우기 (입력하신 정보는 등록되어 있지 않습니다)
        }

        return view
    }
}