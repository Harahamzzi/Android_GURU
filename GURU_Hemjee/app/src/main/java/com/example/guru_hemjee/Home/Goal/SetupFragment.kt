package com.example.guru_hemjee.Home.Goal

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
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
import kotlin.collections.ArrayList

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
                                detailGoalAddPopUp(bigGoalItem)
                            }
                            3 -> { // 목표 완료
                                // 목표 완료 팝업 띄우기
                            }
                        }
                    }, bigGoalItem
                )
                bottomSheet.show(fragmentManager!!, bottomSheet.tag)
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
            // 기존에 있던 행 초기화
            for (i in 0 until newGoalList.size) {
                if (newGoalList[i]["big_goal_name"] == str_biggoal) {
                    newGoalList[i]["big_goal_name"] = ""
                    newGoalList[i]["detail_goal_name"] = ""
                    newGoalList[i]["icon"] = ""
                    newGoalList[i]["color"] = ""

                    break
                }
            }

            // 리스트에 대표목표 및 세부목표 추가하기
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

        // 리스트에 있는 공백 값 제거
        var size = newGoalList.size - 1
        for (j in size downTo 0) {
            if (newGoalList[j]["big_goal_name"] == "") {
                newGoalList.removeAt(j)
            }
        }

        // 리스트 출력
        Log.d("dataLoading()함수 : 리스트 newgoal", newGoalList.toString())

        // 대표 리사이클러뷰 연결
        var goalBigRecyclerView: RecyclerView = binding.goalBigRecyclerView

        // 대표목표 데이터 저장
        bigGoalList.clear() // 배열 초기화
        bigGoalList = loadGoalItems(newGoalList)

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
            // 확인 버튼을 눌렀을 경우
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

    // 세부목표 추가 팝업
    private fun detailGoalAddPopUp(bigGoalItem: BigGoalItem) {
        // 세부목표 추가 팝업 띄우기
        val dialog = DetailGoalSetupDialog(requireContext(), "", "", bigGoalItem.color, bigGoalItem.title)

        // 팝업 클릭 이벤트 연결
        dialog.detailGoalSetup()

        dialog.setOnClickedListener(object : DetailGoalSetupDialog.ButtonClickListener{
            override fun onClick(
                isChanged: Boolean,
                title: String,
                icon: String,
                color: String,
                bigGoal: String
            ) {
                // 확인 버튼을 눌렀을 경우
                if (isChanged) {
                    // 리사이클러뷰에 있는 대표목표에 세부목표값 저장
                    dataLoading()
                }
            }
        })
    }

    // 대표 목표&세부목표 데이터 저장
    private fun loadGoalItems(
        newGoalList: ArrayList<MutableMap<String, String>>
    ): ArrayList<BigGoalItem> {

        var saveTitleList = ArrayList<String>() // 리스트에 저장된 대표목표 목록

        var i = 0
        while (i < newGoalList.size) {

            var newColor = newGoalList[i]["color"].toString() // 색상
            var newTitle = newGoalList[i]["big_goal_name"].toString() // 대표목표
            var iconList = ArrayList<String>() // 아이콘 리스트
            var tempDetailGoalList = ArrayList<String>() // 초기 세부목표 리스트
            var detailGoalList = ArrayList<DetailGoalItem>() // 세부목표 리스트
            var isDifferent = true // 중복값 여부 확인

            // 대표목표 중복값 확인
            for (j in 0 until saveTitleList.size) {
                if (newGoalList[i]["big_goal_name"] == saveTitleList[j]) {
                    isDifferent = false
                    break
                }
            }

            // 저장되지 않은 대표목표&세부목표 리스트이라면 배열에 저장하기
            if (isDifferent) {
                saveTitleList.add(newGoalList[i]["big_goal_name"].toString()) // 저장된 대표목표 리스트에 대표목표 저장

                for (j in 0 until newGoalList.size) {
                    // 대표목표가 같다면, 배열에 아이콘 저장
                    if (newGoalList[i]["big_goal_name"] == newGoalList[j]["big_goal_name"]) {
                        if (!newGoalList[j]["detail_goal_name"].isNullOrBlank()) {
                            // iconList.add(newGoalList[j]["icon"].toString())
                            // tempDetailGoalList.add(newGoalList[j]["detail_goal_name"].toString())
                            detailGoalList.add(DetailGoalItem(newGoalList[j]["icon"].toString(), newGoalList[j]["detail_goal_name"].toString(), newColor, newTitle))
                        }
                    }
                }

                // 세부목표 리스트 저장
                /*for (j in 0 until tempDetailGoalList.size) {
                    detailGoalList.add(DetailGoalItem(iconList[j], tempDetailGoalList[j], newColor, newTitle))
                }*/
            }

            // 리스트에 대표목표 추가
            Log.d("loadGoalItems() 함수 detailgoal", detailGoalList.toString())
            bigGoalList.add(BigGoalItem(newColor, newTitle, iconList, false, detailGoalList))
            ++i
        }

        // 리스트 출력
        Log.d("loadGoalItems() 함수 biggoal", bigGoalList.toString())
        Log.d("loadGoalItems() 함수 newgoal", newGoalList.toString())

        return bigGoalList
    }
}