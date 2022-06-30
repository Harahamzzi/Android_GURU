package com.example.guru_hemjee.Home.Goal

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.Home.MainActivity
import com.example.guru_hemjee.databinding.FragmentSetupBinding

// 홈(MainActivity) -> SubMainActivity -> 목표/잠금 시간 설정
// 목표/잠금 시간 설정 Fragment 화면
// 대표 목표를 확인할 수 있다.
// 버튼을 통해 수정 및 추가하는 화면으로 갈 수 있다.
class SetupFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    private var mBinding: FragmentSetupBinding? = null // binding변수
    private val binding get() = mBinding!!

    private var bigGoalList = ArrayList<BigGoalItem>() // 대표목표 리스트
    private var detailGoalList = ArrayList<DetailGoalItem>() // 세부목표 리스트
    private lateinit var bigGoalAdapter: BigGoalItemAdapter // 대표목표 어댑터
    private lateinit var goalBig_RecyclerView: RecyclerView // 아코디언 메뉴 리사이클러뷰

    private var mainActivity: MainActivity? = null // 메인 액티비티 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()

        mainActivity = null
    }

    override fun onDestroy() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentSetupBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("Range")
    override fun onStart() {
        super.onStart()

        // DB
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        // 세부목표 db에 있는 대표목표, 세부목표, 아이콘, 색상을 리스트에 저장하기(key, value)
        var newGoalList = ArrayList<MutableMap<String, String>>()

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db", null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
            val str_detailgoal = cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString()
            val str_icon = cursor.getString(cursor.getColumnIndex("icon")).toString()
            val str_color = cursor.getString(cursor.getColumnIndex("color")).toString()

            // 대표목표, 세부목표, 아이콘, 색상 값 넣기
            newGoalList = arrayListOf(
                mutableMapOf(
                    "big_goal_name" to str_biggoal,
                    "detail_goal_name" to str_detailgoal,
                    "icon" to str_icon,
                    "color" to str_color
                )
            )
        }
        cursor.close()

        // 대표 리사이클러뷰 연결
        goalBig_RecyclerView = binding.goalBigRecyclerView

        // 대표목표 데이터 저장
        bigGoalList = loadBigGoalItems(newGoalList)

        // 대표목표 어댑터 연결
        goalBig_RecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        bigGoalAdapter = BigGoalItemAdapter(bigGoalList)
        goalBig_RecyclerView.adapter = bigGoalAdapter

        // 대표 목표 추가 버튼을 눌렀다면
        binding.goalBigAddBigGoalButtton.setOnClickListener {

            // 팝업 띄우기
            bigGoalAddPopUp()

            /* 임시 테스트용 -> 나중에 삭제할 예정
            var iconList = arrayListOf(
                R.drawable.ic_outline_computer_24,
                R.drawable.ic_outline_menu_book_24
            )
            //bigGoalAdapter.addBigGoalItem(BigGoalItem(R.color.Orange, "임시테스트", iconList))
            //bigGoalAdapter.notifyDataSetChanged()
            Toast.makeText(context, "추가 완료!", Toast.LENGTH_SHORT).show() */
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
            // 추가 버튼을 눌렀을 경우
            override fun onClicked(isAdd: Boolean, title: String?, color: String?) {
                if (isAdd) {
                    // 리사이클러뷰에 대표목표 값 저장
                    bigGoalAdapter.addBigGoalItem(BigGoalItem(color!!, title!!, null, false, null))
                }
            }

            // 취소버튼을 눌렀을 경우
            override fun onClicked(isAdd: Boolean) {
                // 아무런 작업도 안함
            }

        })

    }

    // 대표 목표&세부목표 데이터 저장
    private fun loadBigGoalItems(
        newGoalList: ArrayList<MutableMap<String, String>>
    ): ArrayList<BigGoalItem> {

        lateinit var saveTitleList: ArrayList<String> // 리스트에 저장된 대표목표 목록

        var i = 0
        while (i < newGoalList.size) {

            var newColor = newGoalList[i]["color"].toString() // 색상
            var newTitle = newGoalList[i]["big_goal_name"].toString() // 대표목표
            lateinit var iconList: ArrayList<String> // 아이콘 리스트
            var isSame = false // 중복값 여부 확인

            // 대표목표 중복값 확인
            for (j in 0 until saveTitleList.size) {
                if (newGoalList[i]["big_goal_name"] == saveTitleList[j]) {
                    isSame = true
                }
            }

            // 대표목표 중복값이 없고, i값 != size값 이라면
            if (!isSame && i != newGoalList.size) {
                iconList.add(newGoalList[i]["icon"].toString()) // 아이콘 저장
                saveTitleList.add(newGoalList[i]["big_goal_name"].toString()) // 저장된 대표목표 리스트에 대표목표 저장

                for (j in i + 1 until newGoalList.size) {
                    // 대표목표가 같다면, 배열에 아이콘 저장
                    if (newGoalList[i]["big_goal_name"] == newGoalList[j]["big_goal_name"]) {
                        iconList.add(newGoalList[j]["icon"].toString())
                    }
                }
            }

            // 대표목표가 같다면, 세부목표 저장
            for (i in 0 until bigGoalList.size) {
                for (j in 0 until newGoalList.size) {
                    if (bigGoalList[i].title == newGoalList[j]["big_goal_name"]) {
                        var icon = newGoalList[i]["icon"].toString()
                        var title = newGoalList[i]["detail_goal_name"].toString()
                        var color = newGoalList[i]["color"].toString()
                        detailGoalList.add(i, DetailGoalItem(icon, title, color))
                    }
                }
            }

            // 리스트에 대표목표 추가
            bigGoalList.add(i, BigGoalItem(newColor, newTitle, iconList, false, detailGoalList))
            ++i
        }

        return bigGoalList
    }
}