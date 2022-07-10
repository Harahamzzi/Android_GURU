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
import android.widget.Toast
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

        // 대표 리사이클러뷰 연결
        goalBigRecyclerView = binding.goalBigRecyclerView

        // 대표목표 어댑터 연결
        goalBigRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        bigGoalAdapter = BigGoalItemAdapter()
        goalBigRecyclerView.adapter = bigGoalAdapter

        // 어댑터에 들어갈 데이터 로딩
        dataLoading()

        // 클릭 이벤트 연결
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
            bigGoalAddPopUp() // 팝업 띄우기
        }
    }

    // 데이터 로드 함수(db값 받아서 어댑터에 반영)
    @SuppressLint("Range")
    private fun dataLoading() {
        // DB
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        // 대표목표 db에 있는 대표목표, 색상을 어댑터에 저장
        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)
        var k = 0
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val str_color = cursor.getString(cursor.getColumnIndex("color"))

            bigGoalAdapter.addBigGoalItem(BigGoalItem(str_biggoal!!, str_color!!, null, false, null))

            Log.d("DEFAULT $k 번째 대표목표 ", str_biggoal.toString())
            Log.d("DEFAULT $k 번째 아이콘 ", str_color.toString())
            Log.d("DEFAULT $k 번째 어댑터 ", bigGoalAdapter.toString())
            Log.d("DEFAULT $k 번째 개수 ", bigGoalAdapter.itemCount.toString())
            ++k
        }
        cursor.close()

        // 세부목표 db에 있는 대표목표, 세부목표, 아이콘, 색상을 어댑터에 저장
        var tempGoalList = ArrayList<MutableMap<String, String>>() // 대표목표에 따른 리스트

        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db", null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val str_detailgoal = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
            val str_icon = cursor.getString(cursor.getColumnIndex("icon"))
            val str_color = cursor.getString(cursor.getColumnIndex("color"))

            // 대표목표에 대한 세부목표, 아이콘을 리스트에 저장
            for (i in 0 until bigGoalAdapter.itemCount) {
                if (bigGoalAdapter.getItemTitle(i) == str_biggoal) {

                    tempGoalList.add(mutableMapOf(
                        "big_goal_name" to str_biggoal,
                        "detail_goal_name" to str_detailgoal,
                        "icon" to str_icon,
                        "color" to str_color
                    ))

                    break
                }
            }
        }
        cursor.close()

        // 만약, 기존 배열에 저장되어 있는 대표목표와 세부목표db에 있는 대표목표가 겹친다면
        // 기존에 있던 행 수정
        for (i in 0 until bigGoalAdapter.itemCount) {
            var bigGoal: String? = null // 대표목표
            var color: String? = null // 색상
            var iconList = ArrayList<String>() // 아이콘
            var detailGoalList = ArrayList<DetailGoalItem>() // 세부목표
            var isFlag = false // 값이 있는지 확인하기 위한 변수

            // 대표목표에 대한 세부목표와 아이콘을 배열에 저장
            for (j in 0 until tempGoalList.size) {
                if (bigGoalAdapter.getItemTitle(i) == tempGoalList[j]["big_goal_name"]) {
                    color = tempGoalList[j]["color"].toString()
                    bigGoal = tempGoalList[j]["big_goal_name"].toString()
                    iconList.add(tempGoalList[j]["icon"].toString())
                    detailGoalList.add(DetailGoalItem(tempGoalList[j]["detail_goal_name"].toString(), tempGoalList[j]["icon"].toString(), color, bigGoal))
                    isFlag = true
                }
            }

            if (isFlag) {
                Log.d("isFLAG 대표목표 $i 번째", bigGoal.toString())
                Log.d("isFLAG 아이콘 $i 번째", iconList.toString())
                Log.d("isFLAG 세부목표 $i 번째", detailGoalList.toString())
                bigGoalAdapter.setItem(i, iconList, detailGoalList)
            }
        }

        sqlitedb.close()
        dbManager.close()

        // 클릭 이벤트 연결
        initClickEvent()
    }

    // 대표목표 추가 팝업
    private fun bigGoalAddPopUp() {
        // 대표 목록 추가 팝업 띄우기
        val dialog = BigGoalSetupDialog(requireContext(), null, null, 0)

        // 팝업 클릭 이벤트 연결
        dialog.bigGoalSetup()

        dialog.setOnClickedListener(object: BigGoalSetupDialog.ButtonClickListener{
            override fun onClicked(
                isChanged: Boolean,
                code: Int,
                title: String?,
                color: String?,
                initTitle: String?
            ) { // 확인 버튼을 눌렀을 경우
                // 리사이클러뷰에 대표목표 값 저장
                if (isChanged && code == 0) { // 새롭게 추가하는 경우
                    bigGoalAdapter. addBigGoalItem(BigGoalItem(title!!, color!!, null, false, null))
                }
            }
        })
    }

    // 대표목표 수정 팝업
    private fun bigGoalModifyPopUp(bigGoalItem: BigGoalItem) {
        // 대표 목표 수정 팝업 띄우기
        val dialog = BigGoalSetupDialog(requireContext(), bigGoalItem.title, bigGoalItem.color, 1)

        // 팝업 클릭 이벤트 연결
        dialog.bigGoalSetup()

        dialog.setOnClickedListener(object : BigGoalSetupDialog.ButtonClickListener { // 추가 버튼을 눌렀을 경우
            override fun onClicked(
                isChanged: Boolean,
                code: Int,
                title: String?,
                color: String?,
                initTitle: String?
            ) {
                if (isChanged && code == 1) {
                    for (i in 0 until bigGoalAdapter.itemCount) {
                        // 아이템 수정
                        if (bigGoalAdapter.getItemTitle(i) == initTitle) {
                            Toast.makeText(context, bigGoalAdapter.getItemTitle(i), Toast.LENGTH_SHORT).show()
                            bigGoalAdapter.setModifyItem(i, title!!, color!!)
                            break
                        }
                    }
                }
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
                    // 대표목표&세부목표 값 삭제
                    dbManager = DBManager(context, "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    sqlitedb.execSQL("DELETE FROM detail_goal_db WHERE big_goal_name = '" + bigGoalItem.title + "';")
                    sqlitedb.execSQL("DELETE FROM big_goal_db WHERE big_goal_name = '" + bigGoalItem.title + "';")

                    bigGoalAdapter.removeBigGoalItem(bigGoalItem)

                    sqlitedb.close()
                    dbManager.close()
                }
            }
        })
    }

    // 세부목표 추가 팝업
    private fun detailGoalAddPopUp(bigGoalItem: BigGoalItem) {
        // 세부목표 추가 팝업 띄우기
        val dialog = DetailGoalSetupDialog(requireContext(), null, null, bigGoalItem.color, bigGoalItem.title, 0)

        // 팝업 클릭 이벤트 연결
        dialog.detailGoalSetup()

        dialog.setOnClickedListener(object : DetailGoalSetupDialog.ButtonClickListener{
            override fun onClick(
                isChanged: Boolean,
                code: Int,
                title: String?,
                icon: String?,
                color: String?,
                initTitle: String?,
                initBigGoal: String?
            ) { // 확인 버튼을 눌렀을 경우
                // 리사이클러뷰에 있는 대표목표에 세부목표값 저장
                if (isChanged) {
                    var j = 0
                    var isFlag = true

                    while (isFlag) {
                        for (i in 0 until bigGoalAdapter.itemCount) {
                            if (bigGoalAdapter.getItemTitle(i) == initBigGoal) { // 아이템 추가
                                Toast.makeText(context, bigGoalAdapter.getItemTitle(i), Toast.LENGTH_SHORT).show()
                                bigGoalAdapter.addDetailGoalItem(i, DetailGoalItem(title!!, icon!!, color!!, initBigGoal))
                                isFlag = false
                                break
                            }
                        }
                        j++
                    }
                }
            }
        })
    }

    // 세부목표 수정 팝업
    /*private fun detailGoalModifyPopUp(bigGoalItem: BigGoalItem) {
        // 세부목표 수정 팝업 띄우기
        val dialog = DetailGoalSetupDialog(requireContext(), 1)

        // 팝업 클릭 이벤트 연결
        dialog.detailGoalSetup()

        dialog.setOnClickedListener(object : DetailGoalSetupDialog.ButtonClickListener {
            override fun onClick(isChanged: Boolean, code: Int, title: String?, icon: String?, color: String?, bigGoal: String?) { // 확인 버튼을 눌렀을 경우
                // 리사이클러뷰에 세부목표 값 저장
                if (isChanged) { // 기존 값을 수정하는 경우
                    dataLoading()
                }
            }

        })
    }

    // 세부목표 삭제 팝업
    private fun detailGoalDeletePopUp(bigGoalItem: BigGoalItem) {

    }*/
}