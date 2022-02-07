package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

// 홈(MainActivity) -> SubMainActivity -> 목표/잠금 시간 설정
// 목표/잠금 시간 설정 Fragment 화면
// 대표 목표를 확인할 수 있다.
// 버튼을 통해 수정 및 추가하는 화면으로 갈 수 있다.
class SetupFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    private lateinit var goalBig_goalBigListView: ListView // fragment_setup.xml파일에 있는 리스트뷰의 아이디를 저장할 변수
    private lateinit var goalBig_plusGoalButton: ImageButton // fragment_setup.xml파일에 있는 +버튼의 아이디를 저장할 변수

    private var mainActivity: SubMainActivity? = null // 메인 액티비티 변수

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
        // LayoutInflater를 사용해서 Resource Layout을 View로 변환시켜서 findViewById()를 호출
        var view : View = inflater.inflate(R.layout.fragment_setup, container, false);

        goalBig_goalBigListView = view.findViewById(R.id.goalBig_goalBigListView) // 리스트뷰의 아이디 할당
        goalBig_plusGoalButton = view.findViewById(R.id.goalBig_plusGoalButton) // +버튼의 아이디 할당

        // DB
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        /** DB 데이터 -> 어댑터 -> 반환 **/
        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)

        var items = mutableListOf<BigGoalListViewItem>() // 목표를 저장할 배열
        val goalList_adapter = BigGoalListViewAdapter(items) // 커스텀 어댑터
        goalBig_goalBigListView.adapter = goalList_adapter // 어댑터 연결
        goalBig_goalBigListView.choiceMode = ListView.CHOICE_MODE_SINGLE // 단일 선택모드

        var num : Int = 0 // 리스트 아이템 개수
        while (cursor.moveToNext()) { // 어댑터에 있는 list배열에 데이터 개수만큼 추가

            // 현재 커서에 있는 값 가져오기
            var str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            var integer_color = cursor.getInt(cursor.getColumnIndex("color"))

            // 색상 아이콘
            var imgColor : ImageView = ImageView(context)
            imgColor.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_colorselectionicon))
            imgColor.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN) // 아이콘 위에 색깔 입히기

            // 대표 목표
            var textGoal : TextView = TextView(context)
            textGoal.text = str_biggoal

            // 오른쪽 화살표 이미지
            var imgRightIcon : ImageView = ImageView(context)
            imgRightIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.next_button))
            imgRightIcon.setColorFilter(R.color.Gray, PorterDuff.Mode.SRC_IN)

            // 리스트에 데이터 추가
            items.add(BigGoalListViewItem(imgColor.drawable, textGoal.text as String, imgRightIcon.drawable))

            goalList_adapter.notifyDataSetChanged() // 리스트 갱신

            // 대표 목표 중 하나를 선택했다면
            goalBig_goalBigListView.setOnItemClickListener { adapterView, view, position, id ->
                val checkedItem = goalBig_goalBigListView.checkedItemPosition // 클릭한 대표 목표의 인덱스 가져오기

                if (checkedItem != -1) { // 클릭한 대표 목표가 있다면
                    val transaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    val detailGoalSetupFragment = DetailGoalSetupFragment() // 세부목표 프래그먼트 변수

                    var item_biggoal : String = goalList_adapter.getItem(checkedItem).bigGoalText

                    val bundle = Bundle() // 프래그먼트에 값을 넘기기 위한 번들
                    bundle.putString("bundle_biggoal", item_biggoal) // 번들에 넘길 값의 id 저장

                    detailGoalSetupFragment.setArguments(bundle) // 번들을 통해서 다른 프래그먼트에 값을 보낼 준비

                    transaction.replace(R.id.fragment_main, detailGoalSetupFragment) // 해당 레이아웃을 프래그먼트로 변경
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction.addToBackStack("DetailGoal")
                    transaction.commit() // 저장
                }
            }
        }

        // +버튼을 눌렀다면
        goalBig_plusGoalButton.setOnClickListener {
            // 대표 목록 추가 화면으로 이동
            goBigGoalSetup()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        return view
    }

    // BigGoalSetupFragment로 화면을 전환하는 함수
    private fun goBigGoalSetup() {
        mainActivity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_main, BigGoalSetupFragment())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.addToBackStack("BigGoal")
                ?.commit()
    }
}