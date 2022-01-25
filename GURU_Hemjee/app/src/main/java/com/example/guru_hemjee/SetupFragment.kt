package com.example.guru_hemjee

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction

class SetupFragment : Fragment() {

    private val bigGoalList = ArrayList<String>() // 대표 목표를 저장할 배열
    private lateinit var bigGoalListView : ListView // fragment_setup.xml파일에 있는 리스트뷰의 아이디를 저장할 변수
    lateinit var plusGoalButton : ImageButton // fragment_setup.xml파일에 있는 +버튼의 아이디를 저장할 변수

    var mainActivity : MainActivity? = null // 메인 액티비티 변수

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
        // LayoutInflater를 사용해서 Resource Layout을 View로 변환시켜서 findViewById()를 호출
        var view : View = inflater.inflate(R.layout.fragment_setup, container, false);

        bigGoalListView = view.findViewById(R.id.bigGoalListView) // 리스트뷰의 아이디 할당
        plusGoalButton = view.findViewById(R.id.plusGoalButton) // +버튼의 아이디 할당

        // 데이터를 입력받을 어댑터 생성
        // 액티비티 참조를 하기 위해서 getActivity()함수 사용
        val goalList_Adapter = getActivity()?.let { ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, bigGoalList) }
        bigGoalListView.adapter = goalList_Adapter // 어댑터 연결
        bigGoalListView.choiceMode = ListView.CHOICE_MODE_SINGLE // 단일 선택모드

        // 대표 목표 중 하나를 선택했다면
        bigGoalListView.setOnItemClickListener { adapterView, view, position, id ->
            val checkedItem = bigGoalListView.checkedItemPosition // 클릭한 대표 목표의 인덱스 가져오기

            if (checkedItem != -1) { // 클릭한 대표 목표가 있다면
                // 대표 목표
            }
        }

        // +버튼을 눌렀다면
        plusGoalButton.setOnClickListener {
            // 대표 목록 추가 화면으로 이동
            goBigGoalSetup()
        }

        // BigGoalSetupFragment에서 넘어온 값(대표 목표) 받기
        if (arguments != null) {
         // TODO : 꼭 여기부터 이어서 완성하기
            /*
            name = arguments!!.getString("name") // 프래그먼트1에서 받아온 값 넣기
            tv_name.setText(name)
             */
        }

        return view
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode) {
            Activity.RESULT_OK -> { // 메모리 저장이 완료되었다면
                if (data != null) { // 데이터가 비어있는지 확인한 후
                    bigGoalList.add(0, data.getStringExtra("newBigGoalList")) // 0번지에 목표 저장
                }
            }

            Activity.RESULT_CANCELED -> { // 뒤로가기를 눌러서 이전화면으로 돌아왔다면
                Toast.makeText(context, "작성이 취소되었습니다.", Toast.LENGTH_SHORT).show() // 메시지 출력
            }
        }
    }*/

    // BigGoalSetupFragment로 화면을 전환하는 함수
    fun goBigGoalSetup() {
        mainActivity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_main, BigGoalSetupFragment())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.commit()
    }

}