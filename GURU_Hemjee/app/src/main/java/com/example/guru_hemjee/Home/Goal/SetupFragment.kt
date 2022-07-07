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
import com.example.guru_hemjee.AlertDialog
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
    private lateinit var cursor: Cursor

    private var mBinding: FragmentSetupBinding? = null // binding변수
    private val binding get() = mBinding!!

    private var bigGoalList = ArrayList<BigGoalItem>() // 대표목표 리스트
    private lateinit var bigGoalAdapter: BigGoalItemAdapter // 대표목표 어댑터
    private lateinit var goalBigRecyclerView: RecyclerView // 아코디언 메뉴 리사이클러뷰

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentSetupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // 리사이클러뷰 설정
        dataLoading()

        // 클릭 이벤트 관리
        initClickEvent()
    }

    // 클릭 이벤트 관리 함수
    private fun initClickEvent() {
        // 대표목표 롱 클릭 이벤트
        bigGoalAdapter.setOnItemLongClickListener(object: BigGoalItemAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, bigGoalItem: BigGoalItem, pos: Int) {

                // 바텀 시트 다이얼로그 띄우기
                val bottomSheet: BigGoalBottomDialogFragment = BigGoalBottomDialogFragment(
                    {
                        when (it) {
                            0 -> { // 수정
                                // 수정 팝업 띄우기
                                bigGoalModifyPopUp(bigGoalItem)
                            }
                            1 -> { // 삭제
                                // 삭제 팝업 띄우기
                                bigGoalDeletePopUp(bigGoalItem)
                            }
                            2 -> { // 세부목표 추가
                                // 세부목표 추가 팝업 띄우기
                            }
                            3 -> { // 목표 완료
                                // 목표 완료 팝업 띄우기
                            }
                        }
                    }, bigGoalItem
                )
                bottomSheet.show(fragmentManager!!, bottomSheet.tag)

                /* 하단 네비게이션 띄우기

                // 레이아웃 변경
                // view.setBackgroundResource(R.drawable.solid_goal_item_select_box)

                binding.goalBigBottomNavigation.visibility = View.VISIBLE
                binding.goalBigBottomNavigation.setOnItemSelectedListener { item ->
                    when(item.itemId) {
                        // 수정 버튼
                        R.id.menu_big_goal_edit -> {
                            return@setOnItemSelectedListener false
                        }
                        // 삭제 버튼
                        R.id.menu_big_goal_delete -> {
                            return@setOnItemSelectedListener false
                        }
                        // 세부목표 추가 버튼
                        R.id.menu_big_goal_add -> {
                            return@setOnItemSelectedListener false
                        }
                        // 목표 완료 버튼
                        R.id.menu_big_goal_complete -> {
                            return@setOnItemSelectedListener false
                        }
                    }
                    true
                }
            }*/
            }
        })

        // 대표 목표 추가 버튼을 눌렀다면
        binding.goalBigAddBigGoalButtton.setOnClickListener {

            // 팝업 띄우기
            bigGoalAddPopUp()
        }
    }

    // 데이터 로드 함수(db값 받아와서 리사이클러뷰를 초기화해주는 함수)
    @SuppressLint("Range")
    private fun dataLoading() {
        // DB
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        // 대표목표 db에 있는 대표목표, 색상을 리스트에 저장하기
        var newGoalList = ArrayList<MutableMap<String, String>>()
        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val str_color = cursor.getString(cursor.getColumnIndex("color"))

            // 대표목표 값 저장
            newGoalList.add(
                mutableMapOf(
                    "big_goal_name" to str_biggoal,
                    "detail_goal_name" to "",
                    "icon" to "",
                    "color" to str_color
                )
            )
        }
        cursor.close()

        // 세부목표 db에 있는 대표목표, 세부목표, 아이콘, 색상을 리스트에 저장하기(key, value)
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db", null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val str_detailgoal = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
            val str_icon = cursor.getString(cursor.getColumnIndex("icon"))
            val str_color = cursor.getString(cursor.getColumnIndex("color"))

            // 만약, 기존 배열에 저장되어 있는 대표목표와 세부목표db에 있는 대표목표가 겹친다면
            // 기존에 있던 대표목표 행 삭제
            for (i in 0 until newGoalList.size) {
                if (newGoalList[i]["big_goal_name"] == str_biggoal) {
                    newGoalList[i].remove("big_goal_name")
                    break
                }
            }

            // 대표목표, 세부목표, 아이콘, 색상 값 넣기
            newGoalList.add(
                mutableMapOf(
                    "big_goal_name" to str_biggoal,
                    "detail_goal_name" to str_detailgoal,
                    "icon" to str_icon,
                    "color" to str_color
                )
            )
        }
        cursor.close()

        // 리스트 출력
        // Log.d("onstart함수 : 리스트 biggoal", bigGoalList.toString())
        // Log.d("onstart함수 : 리스트 newgoal", newGoalList.toString())

        // 대표 리사이클러뷰 연결
        goalBigRecyclerView = binding.goalBigRecyclerView

        // 대표목표 데이터 저장
        bigGoalList.clear() // 배열 초기화
        bigGoalList = loadBigGoalItems(newGoalList)

        // 대표목표 어댑터 연결
        goalBigRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        bigGoalAdapter = BigGoalItemAdapter(bigGoalList)
        goalBigRecyclerView.adapter = bigGoalAdapter

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 클릭 이벤트 함수
        initClickEvent()
    }

    // 대표목표 추가 팝업
    private fun bigGoalAddPopUp() {
        // 대표 목록 추가 팝업 띄우기
        val dialog = BigGoalSetupDialog(requireContext(), "", "")

        // 팝업 클릭 이벤트 연결
        dialog.bigGoalSetup()

        dialog.setOnClickedListener(object: BigGoalSetupDialog.ButtonClickListener{
            // 추가 버튼을 눌렀을 경우
            override fun onClicked(isAdd: Boolean, code: Int, title: String?, color: String?) {
                if (isAdd) {
                    when (code) {
                        1 -> { // 새롭게 추가하는 경우
                            // 리사이클러뷰에 대표목표 값 저장
                            bigGoalAdapter.addBigGoalItem(BigGoalItem(color!!, title!!, null, false, null))
                        }
                    }
                }
            }

            // 취소버튼을 눌렀을 경우
            override fun onClicked(isAdd: Boolean) {
                // 아무런 작업도 안함
            }

        })
    }

    // 대표목표 수정 팝업
    private fun bigGoalModifyPopUp(bigGoalItem: BigGoalItem) {
        // 대표 목표 수정 팝업 띄우기
        val dialog = BigGoalSetupDialog(requireContext(), bigGoalItem.title, bigGoalItem.color)

        // 팝업 클릭 이벤트 연결
        dialog.bigGoalSetup()

        dialog.setOnClickedListener(object : BigGoalSetupDialog.ButtonClickListener{
            // 추가 버튼을 눌렀을 경우
            override fun onClicked(isAdd: Boolean, code: Int, title: String?, color: String?) {
                // 리사이클러뷰에 대표목표 값 저장
                if (isAdd) {
                    when (code) {
                        0 -> { // 기존 값을 수정하는 경우
                            dataLoading()
                        }
                    }
                }
            }

            // 취소 버튼을 눌렀을 경우
            override fun onClicked(isAdd: Boolean) {
                // 아무런 작업도 안함
            }
        })
    }

    // 대표목표 삭제 팝업
    private fun bigGoalDeletePopUp(bigGoalItem: BigGoalItem) {
        // 대표목표 삭제 팝업 띄우기
        val dialog = AlertDialog(requireContext(), "해당 목표를 삭제하시겠습니까?", "해당 목표의 모든 기록이 삭제되며\n" +
                "복구는 불가능합니다.", "삭제", 0)

        // 팝업 클릭 이벤트 연결
        dialog.showAlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                // 삭제 버튼을 눌렀을 경우 db 및 리사이클러뷰에서 아이템 삭제
                if (isConfirm) {
                    bigGoalAdapter.removeBigGoalItem(bigGoalItem)

                    // 대표목표&세부목표 db값 삭제
                    dbManager = DBManager(context, "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    sqlitedb.execSQL("DELETE FROM detail_goal_db WHERE big_goal_name = '" + bigGoalItem.title + "';")
                    sqlitedb.execSQL("DELETE FROM big_goal_db WHERE big_goal_name = '" + bigGoalItem.title + "';")

                    sqlitedb.close()
                    dbManager.close()
                }
            }
        })
    }

    // 대표 목표&세부목표 데이터 저장
    private fun loadBigGoalItems(
        newGoalList: ArrayList<MutableMap<String, String>>
    ): ArrayList<BigGoalItem> {

        var saveTitleList = ArrayList<String>() // 리스트에 저장된 대표목표 목록

        var i = 0
        while (i < newGoalList.size) {

            var newColor = newGoalList[i]["color"].toString() // 색상
            var newTitle = newGoalList[i]["big_goal_name"].toString() // 대표목표
            var iconList = ArrayList<String>() // 아이콘 리스트
            var detailGoalList = ArrayList<DetailGoalItem>() // 세부목표 리스트
            var isSame = false // 중복값 여부 확인

            // 대표목표 중복값 확인
            for (j in 0 until saveTitleList.size) {
                if (newGoalList[i]["big_goal_name"] == saveTitleList[j]) {
                    isSame = true
                    break
                }
            }

            // 대표목표 중복값이 없고, i값 != size값 이라면
            if (!isSame) {
                iconList.add(newGoalList[i]["icon"].toString()) // 아이콘 저장
                saveTitleList.add(newGoalList[i]["big_goal_name"].toString()) // 저장된 대표목표 리스트에 대표목표 저장

                for (j in i + 1 until newGoalList.size) {
                    // 대표목표가 같다면, 배열에 아이콘 저장
                    if (newGoalList[i]["big_goal_name"] == newGoalList[j]["big_goal_name"]) {
                        iconList.add(newGoalList[j]["icon"].toString())
                    }
                }
            }

            // 대표목표가 같고 세부목표가 저장되어 있다면, 세부목표 저장
            if (!newGoalList[i]["detail_goal_name"].isNullOrBlank()) {
                detailGoalList.add(DetailGoalItem(newGoalList[i]["icon"].toString(), newGoalList[i]["detail_goal_name"].toString(), newGoalList[i]["color"].toString()))
                for (k in i + 1 until newGoalList.size) {
                    if (newGoalList[i]["big_goal_name"] == newGoalList[k]["big_goal_name"]) {
                        var icon = newGoalList[k]["icon"].toString()
                        var title = newGoalList[k]["detail_goal_name"].toString()
                        var color = newGoalList[k]["color"].toString()
                        detailGoalList.add(DetailGoalItem(icon, title, color))
                    }
                }
            }

            // 리스트에 대표목표 추가
            // Log.d("detailGoal리스트", detailGoalList.toString())
            bigGoalList.add(BigGoalItem(newColor, newTitle, iconList, false, detailGoalList))
            ++i
        }

        // 리스트 출력
        // Log.d("load함수 : 리스트 biggoal", bigGoalList.toString())
        // Log.d("load함수 : 리스트 newgoal", newGoalList.toString())

        return bigGoalList
    }
}