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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private val bigGoalList: ArrayList<BigGoalItem> = ArrayList() // 목표 리스트

    private var mainActivity: MainActivity? = null // 메인 액티비티 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
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
        bigGoalAdapter = BigGoalItemAdapter(bigGoalList)
        goalBigRecyclerView.adapter = bigGoalAdapter

        // 어댑터에 들어갈 데이터 로딩
        dataLoading()

        // 클릭 이벤트 연결
        initClickEvent()
    }

    // 클릭 이벤트 관리 함수
    private fun initClickEvent() {
        // 대표목표 롱 클릭 이벤트
        bigGoalAdapter.onBigGoalItemLongClickListener = { position ->
            val bigGoalItem = bigGoalAdapter.getBigGoalItem(position)
            val bottomSheet = BigGoalBottomDialogFragment(
                {
                    when (it) {
                        0 -> bigGoalModifyPopUp(bigGoalItem) // 수정
                        1 -> bigGoalDeletePopUp(bigGoalItem) // 삭제
                        2 -> detailGoalAddPopUp(bigGoalItem) // 세부목표 추가
                        3 -> completeGoal(bigGoalItem, position) // 목표 완료
                    }
                }, bigGoalItem
            )
            bottomSheet.show(mainActivity!!.supportFragmentManager, bottomSheet.tag)
        }

        // 세부목표 클릭 이벤트
        bigGoalAdapter.onDetailGoalItemClickListener = { position, bigGoalPosition ->
            val bigGoalItem = bigGoalAdapter.getBigGoalItem(bigGoalPosition)
            val detailGoalItem = bigGoalAdapter.getBigGoalItem(bigGoalPosition).detailGoalList[position]
            val bottomSheet = DetailGoalBottomDialogFragment(
                {
                    when (it) {
                        0 -> detailGoalModifyPopUp(bigGoalItem, detailGoalItem) // 수정
                        1 -> detailGoalDeletePopUp(bigGoalItem, detailGoalItem) // 삭제
                    }
                }, bigGoalItem, detailGoalItem
            )
            bottomSheet.show(mainActivity!!.supportFragmentManager, bottomSheet.tag)
        }

        // 대표 목표 추가 버튼 이벤트
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

        // db에 있는 목표를 어댑터에 저장
        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val str_color = cursor.getString(cursor.getColumnIndex("color"))

            Log.d("SetupFragment ", "대표목표 : " + str_biggoal + "색상 : " + str_color)

            val tempDetailGoalList = ArrayList<DetailGoalItem>()
            val tempIconArray = ArrayList<String>()
            try {
                val cursor2: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '$str_biggoal';", null)
                while (cursor2.moveToNext()) {
                    val str_detailgoal = cursor2.getString(cursor2.getColumnIndex("detail_goal_name"))
                    val str_icon = cursor2.getString(cursor2.getColumnIndex("icon"))

                    tempDetailGoalList.add(DetailGoalItem(str_detailgoal, str_icon, str_color))
                    tempIconArray.add(str_icon)
                }
                cursor2.close()
            } catch (e: Exception) {
                Log.d("SetupFragment", "오류 : " + e.message)
            }

            if (tempDetailGoalList.size == 0)
            {
                bigGoalAdapter.addBigGoalItem(BigGoalItem(str_biggoal, str_color, false))
                for (i in 0 until bigGoalAdapter.itemCount) {
                    Log.d("SetupFragment", "사이즈가 0임 ${bigGoalAdapter.getBigGoalItem(i)}")
                }
            }
            else
            {
                bigGoalAdapter.addBigGoalItem(BigGoalItem(
                    str_biggoal,
                    str_color,
                    false,
                    tempIconArray,
                    tempDetailGoalList
                ))
                for (i in 0 until bigGoalAdapter.itemCount) {
                    Log.d("SetupFragment", "사이즈가 있음 ${tempDetailGoalList.size} ${bigGoalAdapter.getBigGoalItem(i)}")
                }
            }
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 클릭 이벤트 연결
        initClickEvent()
    }

    // 대표목표 추가 팝업
    private fun bigGoalAddPopUp() {
        // 대표 목록 추가 팝업 띄우기
        val dialog = BigGoalSetupDialog(requireContext(), 0)
        dialog.bigGoalSetup()

        dialog.setOnClickedListener(object: BigGoalSetupDialog.ButtonClickListener{
            override fun onClicked(
                isChanged: Boolean,
                code: Int,
                title: String?,
                color: String?,
                initTitle: String?
            ) { // 대표목표 저장
                if (isChanged && code == 0)
                    bigGoalAdapter.addBigGoalItem(BigGoalItem(title!!, color!!, false))
            }
        })
    }

    // 대표목표 수정 팝업
    private fun bigGoalModifyPopUp(bigGoalItem: BigGoalItem) {
        // 대표 목표 수정 팝업 띄우기
        val dialog = BigGoalSetupDialog(requireContext(), 1, bigGoalItem.title, bigGoalItem.color)
        dialog.bigGoalSetup()

        dialog.setOnClickedListener(object : BigGoalSetupDialog.ButtonClickListener {
            override fun onClicked(
                isChanged: Boolean,
                code: Int,
                title: String?,
                color: String?,
                initTitle: String?
            ) { // 대표목표 수정
                if (isChanged && code == 1)
                    bigGoalAdapter.modifyBigGoalItem(title!!, color!!, initTitle!!)
            }
        })
    }

    // 대표목표 삭제 팝업
    private fun bigGoalDeletePopUp(bigGoalItem: BigGoalItem) {
        // 대표목표 삭제 팝업 띄우기
        val dialog = AlertDialog(requireContext(), "해당 목표를 삭제하시겠습니까?", "해당 목표의 모든 기록이 삭제되며\n" +
                "복구는 불가능합니다.", "삭제", 0)
        dialog.showAlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
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
        val dialog = DetailGoalSetupDialog(requireContext(), 0, bigGoalItem.title, bigGoalItem.color)
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
            ) { // 세부목표 저장
                if (isChanged && code == 0)
                    bigGoalAdapter.addDetailGoalItem(title!!, icon!!, color!!, initBigGoal!!)
                }
            })
    }

    // 세부목표 수정 팝업
    private fun detailGoalModifyPopUp(bigGoalItem: BigGoalItem, detailGoalItem: DetailGoalItem) {
        val dialog = DetailGoalSetupDialog(requireContext(), 1, bigGoalItem.title, bigGoalItem.color, detailGoalItem.detailTitle, detailGoalItem.detailIcon)
        dialog.detailGoalSetup()

        dialog.setOnClickedListener(object : DetailGoalSetupDialog.ButtonClickListener {
            override fun onClick(
                isChanged: Boolean,
                code: Int,
                title: String?,
                icon: String?,
                color: String?,
                initTitle: String?,
                initBigGoal: String?
            ) { // 세부목표 수정
                if (isChanged && code == 1)
                    bigGoalAdapter.modifyDetailGoalItem(title!!, icon!!, color!!, initTitle!!, initBigGoal!!)
            }
        })
    }

    // 세부목표 삭제 팝업
    private fun detailGoalDeletePopUp(bigGoalItem: BigGoalItem, detailGoalItem: DetailGoalItem) {
        // 세부목표 삭제 팝업 띄우기
        val dialog = AlertDialog(requireContext(), "해당 목표를 삭제하시겠습니까?", "해당 목표의 모든 기록이 삭제되며\n" +
                "복구는 불가능합니다.", "삭제", 0)
        dialog.showAlertDialog()

        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isConfirm: Boolean) {
                // 삭제 버튼을 눌렀을 경우 db 및 리사이클러뷰에서 아이템 삭제
                if (isConfirm) {
                    // 세부목표 값 삭제
                    dbManager = DBManager(context, "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    sqlitedb.execSQL("DELETE FROM detail_goal_db WHERE big_goal_name = '${bigGoalItem.title}' AND detail_goal_name = '${detailGoalItem.detailTitle}';")
                    bigGoalAdapter.removeDetailGoalItem(bigGoalItem, detailGoalItem)

                    sqlitedb.close()
                    dbManager.close()
                }
            }

        })
    }

    // 대표목표 완료 이벤트
    @SuppressLint("Range", "Recycle")
    private fun completeGoal(bigGoalItem: BigGoalItem, position: Int) {
        // 완료된 세부목표 DB에 값 저장
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        val sqlitedb2: SQLiteDatabase = dbManager.writableDatabase

        /*var tempDetailGoalList = ArrayList<MutableMap<String, String>>() // 세부목표 리스트
        tempDetailGoalList.add(mutableMapOf(
            "big_goal_name" to str_biggoal,
            "detail_goal_name" to str_detailgoal,
            "icon" to str_icon,
            "count" to int_count
        ))*/
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '${bigGoalItem.title}';",null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val str_detailgoal = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
            val str_icon = cursor.getString(cursor.getColumnIndex("icon"))
            val int_count = cursor.getInt(cursor.getColumnIndex("count")).toString()

            try { // 값 저장
                sqlitedb2.execSQL("INSERT INTO complete_detail_goal_db VALUES ('$str_detailgoal', '$str_icon', '$int_count', '$str_biggoal');")
            } catch (e: Exception) {
                Log.d("SetupFragemnt", "세부목표 " + e.printStackTrace().toString())
            }
        }

        // 완료된 대표목표 DB에 값 저장
        // 대표목표 정보 가져오기
        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '${bigGoalItem.title}';", null)
        val cursor2 = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db WHERE big_goal_name = '${bigGoalItem.title}';", null)
        while (cursor.moveToNext()) {
            val str_biggoal = cursor.getString(cursor.getColumnIndex("big_goal_name"))
            val str_color = cursor.getString(cursor.getColumnIndex("color"))
            val str_big_goal_create_time = cursor.getString(cursor.getColumnIndex("big_goal_create_time"))

            try {
                var isFlag = false
                val completeTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E HH:mm:ss"))
                while (cursor2.moveToNext()) {
                    val str_total_report_time = cursor2.getString(cursor2.getColumnIndex("total_report_time"))
                    // 값 저장
                    sqlitedb2.execSQL("INSERT INTO complete_big_goal_db VALUES ('$str_biggoal', '$str_color', '$str_total_report_time', '$str_big_goal_create_time', '$completeTime');")
                    isFlag = true
                }

                // report time값이 없다면
                if (!isFlag) {
                    sqlitedb2.execSQL("INSERT INTO complete_big_goal_db VALUES ('$str_biggoal', '$str_color', '00:00:00', '$str_big_goal_create_time', '$completeTime');")
                }
            } catch (e: Exception) {
                Log.d("SetupFragment", "대표목표 " + e.printStackTrace().toString())
            }
        }

        // 대표목표 DB에 값 삭제
        sqlitedb2.execSQL("DELETE FROM big_goal_db WHERE big_goal_name = '${bigGoalItem.title}';")

        // rv 초기화
        bigGoalAdapter.removeBigGoalItem(bigGoalItem)
        Toast.makeText(context, "목표를 완료했습니다", Toast.LENGTH_SHORT).show()

        cursor2.close()
        cursor.close()
        sqlitedb2.close()
        sqlitedb.close()
        dbManager.close()
    }
}