package com.example.guru_hemjee.Home.Goal

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.Home.MainActivity
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentSetupBinding

// 홈(MainActivity) -> SubMainActivity -> 목표/잠금 시간 설정
// 목표/잠금 시간 설정 Fragment 화면
// 대표 목표를 확인할 수 있다.
// 버튼을 통해 수정 및 추가하는 화면으로 갈 수 있다.
class SetupFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // binding 변수
    private lateinit var binding: FragmentSetupBinding

    private var mainActivity: MainActivity? = null // 메인 액티비티 변수

    // '대표목표' 리스트
    private var bigGoalItem = mutableListOf<BigGoalRecyclerViewItem>()

    // '세부목표' 리스트
    private var detailGoalItem = mutableListOf<DetailGoalRecyclerViewItem>()

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
        binding = FragmentSetupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // DB
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        // 세부목표 db에 있는 대표목표와 아이콘을 리스트에 저장하기(key, value)
        lateinit var detailList: ArrayList<MutableMap<String, String>>

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db", null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndexOrThrow("big_goal_name")).toString()
            val int_icon = cursor.getInt(cursor.getColumnIndexOrThrow("icon"))

            // 값 넣기
            detailList = arrayListOf(
                mutableMapOf(
                    "big_goal_name" to str_biggoal,
                    "icon" to int_icon.toString()
                )
            )
        }

        cursor.close()

        // 대표목표 db에 있는 대표목표와 색상을 찾아서 어댑터 아이템에 저장
        // detailList에 저장되어 있는 아이콘을 어댑터 아이템에 저장
        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)

        // 어댑터 연결
        //val bigGoalAdapter = BigGoalRecyclerViewAdapter(bigGoalItem, detailGoalItem)
        //binding.goalBigRecyclerView.adapter = bigGoalAdapter
        //binding.goalBigRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        var num : Int = 0 // 리스트 아이템 개수
        while (cursor.moveToNext()) {

            // 현재 커서에 있는 값 가져오기
            var str_biggoal = cursor.getString(cursor.getColumnIndexOrThrow("big_goal_name")).toString()
            var integer_color = cursor.getInt(cursor.getColumnIndexOrThrow("color"))

            // 리스트에 대표목표에 맞는 아이콘을 리스트에 추가
            var list = ArrayList<Int>()
            for (i in 0 until detailList.size) {
                // 대표목표가 같다면, 아이콘 저장
                if (detailList[i]["big_goal_name"] == str_biggoal) {
                    list.add(detailList[i]["icon"]!!.toInt())
                }
            }

            // 리스트에 데이터 추가(색상, 목표, 아이콘 리스트)
            //bigGoalAdapter.addBigGoalItem(BigGoalRecyclerViewItem(integer_color, str_biggoal, list))

            /*bigGoalItem.add(BigGoalRecyclerViewItem(integer_color, str_biggoal, list))
            bigGoalAdapter.notifyDataSetChanged() // 리스트 갱신*/

            // 대표 목표 중 하나를 선택했다면
            /*bigGoalAdapter.setOnItemClickListener { adapterView, view, position, id ->
                val checkedItem = goalBig_RecyclerView.checkedItemPosition // 클릭한 대표 목표의 인덱스 가져오기

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
            }*/
        }


        // 대표목표 클릭 이벤트
        /*bigGoalAdapter.setBigGoalRecyclerViewItemClickListener(object: BigGoalRecyclerViewAdapter.BigGoalRecyclerViewItemClickListener{
            override fun onItemClick(bigGoal: BigGoalRecyclerViewItem, position: Int) {
                if (selected.get(position)) {
                    // 펼쳐진 item 클릭 시
                    selected.delete(position)
                } else {
                    // 직전에 클릭됐던 item의 클릭상태 초기화
                    selected.delete(prePos)
                    // 클릭한 item의 position 저장
                    selected.put(position, true)
                }

                // 해당 포지션의 변화를 알리기
                if (prePos != -1)
                    bigGoalAdapter.notifyItemChanged(prePos)
                bigGoalAdapter.notifyItemChanged(position)
                // 클릭된 position 저장
                prePos = position
            }

            override fun onRemoveBigGoal(position: Int) {

            }
        })*/

        // 대표 목표 추가 버튼을 눌렀다면
        binding.goalBigAddBigGoalButtton.setOnClickListener {

            // 팝업 띄우기
            // bigGoalAddPopUp()

            // 임시 테스트용 -> 나중에 삭제할 예정
            var iconList = arrayListOf(
                R.drawable.ic_outline_computer_24,
                R.drawable.ic_outline_menu_book_24
            )
            //bigGoalAdapter.addBigGoalItem(BigGoalRecyclerViewItem(R.color.Orange, "임시테스트", iconList))
            //bigGoalAdapter.notifyDataSetChanged()
            Toast.makeText(context, "추가 완료!", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 대표목표 추가 팝업
    private fun bigGoalAddPopUp() {
        // 대표 목록 추가 팝업 띄우기
        val dialog = BigGoalSetupDialog(requireContext())

        // 팝업 클릭 이벤트 연결
        dialog.bigGoalSetup()

        dialog.setOnClickledListener(object: BigGoalSetupDialog.ButtonClickListener{
            override fun onClicked(isAdd: Boolean, biggoalTitle: String?, biggoalColor: String?) {
                if (isAdd) {
                    // db에 값 저장
                    // 리사이클러뷰 연동

                }
            }

            override fun onClicked(isAdd: Boolean) {
                // 아무런 작업도 안함
            }

        })

    }
}