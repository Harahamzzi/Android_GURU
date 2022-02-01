package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class SetupFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

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

        // DB
        dbManager = DBManager(context, "big_goal_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        // TODO : DB 데이터 -> 어댑터 -> 반환
        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)

        var items = mutableListOf<BigGoalListViewItem>() // 목표를 저장할 배열
        val goalList_adapter = BigGoalListViewAdapter(items) // 커스텀 어댑터
        bigGoalListView.adapter = goalList_adapter // 어댑터 연결
        bigGoalListView.choiceMode = ListView.CHOICE_MODE_SINGLE // 단일 선택모드

        var num : Int = 0 // 리스트 아이템 개수
        while (cursor.moveToNext()) { // 어댑터에 있는 list배열에 데이터 개수만큼 추가

            /* var sf = SimpleDateFormat("hh:mm:ss")
            var totalMilli = Date(cursor.getLong(cursor.getColumnIndex("big_goal_lock_time")))
            var result : String = sf.format(totalMilli)
            var returnToMilli = sf.parse(result)
            var milli = returnToMilli.getTime() // 밀리초 받기 */

            /* 리스트 뷰에 있는 데이터를 입력받을 어댑터 생성
            val goalList_adapter = BigGoalListViewAdapter(context, items) // 커스텀 어댑터
            val items = mutableListOf<BigGoalListViewItem>() // 목표를 저장할 배열 */

            // 현재 커서에 있는 값 가져오기
            var str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            var integer_color = cursor.getInt(cursor.getColumnIndex("color"))

            // var integer_hour : Long = (milli / (1000 * 60 * 60)) % 24
            // var integer_min : Long = (milli / (1000 * 60)) % 60

            /*var listview_item : ListView = ListView(context)
            listview_item.id = num // 개수 부여
            bigGoalListView.id = num
            bigGoalListView.setTag(str_biggoal) */

            // 색상 아이콘
            var imgColor : ImageView = ImageView(context)
            imgColor.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_colorselectionicon))
            imgColor.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN) // 아이콘 위에 색깔 입히기
            // imgColor.setColorFilter(Color.parseColor(str_color), PorterDuff.Mode.SRC_IN) // 아이콘 위에 색깔 입히기

            // 대표 목표
            var textGoal : TextView = TextView(context)
            textGoal.text = str_biggoal

            // 오른쪽 화살표 이미지
            var imgRightIcon : ImageView = ImageView(context)
            imgRightIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.next_button))
            imgRightIcon.setColorFilter(R.color.Gray, PorterDuff.Mode.SRC_IN)

            // 리스트에 데이터 추가
            items.add(BigGoalListViewItem(imgColor.drawable, textGoal.text as String, imgRightIcon.drawable))

            // goalList_adapter.addItem(imgColor, textGoal, imgRightIcon)
            // val items = mutableListOf<BigGoalListViewItem>() // 목표를 저장할 배열
            // items.add(BigGoalListViewItem()) // items에 값 넣기

            goalList_adapter.notifyDataSetChanged() // 리스트 갱신

            // 대표 목표 중 하나를 선택했다면
            bigGoalListView.setOnItemClickListener { adapterView, view, position, id ->
                val checkedItem = bigGoalListView.checkedItemPosition // 클릭한 대표 목표의 인덱스 가져오기

                if (checkedItem != -1) { // 클릭한 대표 목표가 있다면
                    Toast.makeText(context, (checkedItem + 1).toString() + "번째를 클릭했습니다.", Toast.LENGTH_SHORT).show() // 클릭 확인용 토스트 메시지

                    // DetailGoalSetupFragment로 데이터를 전송하고 화면을 전환
                    val transaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    val detailGoalSetupFragment = DetailGoalSetupFragment() // 세부목표 프래그먼트 변수

                    //var cursor_item : Cursor = goalList_adapter.getItem(checkedItem) as Cursor
                    //var item_biggoal : String = cursor_item.getString(cursor_item.getColumnIndex("big_goal_name")).toString()
                    //var item_color : Int = cursor_item.getInt(cursor_item.getColumnIndex("color"))

                    var item_biggoal : String = goalList_adapter.getItem(checkedItem).bigGoalText

                    val bundle = Bundle() // 프래그먼트에 값을 넘기기 위한 번들
                    bundle.putString("bundle_biggoal", item_biggoal) // 번들에 넘길 값의 id 저장

                    detailGoalSetupFragment.setArguments(bundle) // 번들을 통해서 다른 프래그먼트에 값을 보낼 준비

                    transaction.replace(R.id.fragment_main, detailGoalSetupFragment) // 해당 레이아웃을 프래그먼트로 변경
                    transaction.commit() // 저장
                }
            }
        }

        // Log.d("현재 num 값", num.toString())
        num++;
        // Log.d("목표를 추가한 후의 num 값", num.toString())

        // +버튼을 눌렀다면
        plusGoalButton.setOnClickListener {
            // 대표 목록 추가 화면으로 이동
            goBigGoalSetup()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        return view
    }

    // BigGoalSetupFragment로 화면을 전환하는 함수
    fun goBigGoalSetup() {
        mainActivity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_main, BigGoalSetupFragment())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.addToBackStack(null)
                ?.commit()
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
}