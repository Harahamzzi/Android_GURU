package com.example.guru_hemjee.Home.Home

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.FunUpDateHamzzi
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentHomeBinding
import kotlin.collections.ArrayList

// MainActivity -> 홈
// 햄찌, 잠금 시작 등을 할 수 있는 홈 Fragment 화면
class HomeFragment : Fragment() {

    // 뷰 바인딩 변수
    private var mBinding: FragmentHomeBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // 데이터 값
    private var bigGoalNameList = ArrayList<String>()   // 대표 목표 이름 목록
    private var iconColorNameList = ArrayList<String>() // 아이콘 색상 이름 목록
    private var iconIDList = ArrayList<String>()        // 아이콘 목록

    //db관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

//    //시작 버튼
//    private lateinit var home_startButton: Button
//
//    //잠금 수정 버튼
//    private lateinit var home_goalSelectButton: MaterialButton
//    private var bigGoalName: String = ""
//    private var bigGoalColor: Int = 0
//
//    //잠금 시간 안내
//    private var time = "00:00:00"
//    private lateinit var home_goalTimeTextView: TextView
//
//    //햄찌 관련
//    private lateinit var home_BGFrameLayout: FrameLayout
//    private lateinit var home_clothFrameLayout: FrameLayout
//    private lateinit var hamName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroyView()
    }

    @SuppressLint("Range")
    override fun onStart() {
        super.onStart()

        /** 대표 목표 선택 View Pager Adapter 생성 **/
        // 0. View Pager 관련 설정
        // 1개의 페이지를 미리 로드해 두도록 설정
        binding.goalViewPager.offscreenPageLimit = 1

        // 여백 및 너비 설정
        val pagerMarginPx = resources.getDimensionPixelOffset(R.dimen.view_pager_margin)
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.view_pager_width)
        val screenWidth = resources.displayMetrics.widthPixels  // 스마트폰의 너비 길이를 가져옴
        val offsetPx = screenWidth - pagerMarginPx - pagerWidth

        val startX = resources.getDimensionPixelOffset(R.dimen.view_pager_start_x)

        // 페이지의 시작 X좌표 값을 설정
        binding.goalViewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx + startX
        }

        // 1. 데이터 값 불러오기
        getViewPagerData()

        // 2. 어댑터 생성
        binding.goalViewPager.adapter = HomeViewPagerAdapter(requireContext(), bigGoalNameList, iconColorNameList, iconIDList)

        /** 홈 화면 정보 갱신 **/
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)

        // 함께한 시간을 담을 temp 변수
        var tempTotalTime: String

        if(cursor.moveToNext()) {
            binding.homeSeedPointTextView.text = cursor.getString(cursor.getColumnIndex("seed")).toString() // 씨앗 갱신
            binding.hamName.text = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()       // 햄스터 이름 갱신

            // 함께한 시간 갱신(분리 및 적용)
            tempTotalTime = cursor.getString(cursor.getColumnIndex("total_time")).toString()
            binding.totalTime.text = tempTotalTime.split(':')[0] + "시간 " + tempTotalTime.split(':')[1] + "분"
        }

//        //잠금 버튼 수정
//        home_goalSelectButton = requireView().findViewById(R.id.home_goalSelectButton)
//        dbManager = DBManager(context, "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//        cursor= sqlitedb.rawQuery("SELECT * FROM big_goal_db",null)
//        var isThereBigGoal = false
//        if(cursor.moveToNext()){
//            bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name"))
//            bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))
//            time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time"))
//
//            home_goalSelectButton.text = bigGoalName
//            home_goalSelectButton.setTextColor(resources.getColor(R.color.Black))
//            home_goalSelectButton.iconTint = ColorStateList.valueOf(bigGoalColor)
//
//            isThereBigGoal = true
//        }
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        if(!isThereBigGoal){
//            bigGoalName = "목표를 생성해주세요"
//            bigGoalColor = R.color.Gray
//            home_goalSelectButton.text = bigGoalName
//            home_goalSelectButton.setTextColor(bigGoalColor)
//            home_goalSelectButton.setIconTintResource(R.color.Gray)
//        }
//        home_goalSelectButton.setOnClickListener {
//            showLockSettingPopUp()
//        }
//
        //메인 화면 햄찌 설정(선처리)
        var preselectedItems = ArrayList<String>()
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_applied = 1",null)
        while(cursor.moveToNext()){
            preselectedItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }

        var preusingItems = ArrayList<String>()
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
        while(cursor.moveToNext()){
            preusingItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }

        sqlitedb = dbManager.writableDatabase
        for(item in preusingItems){
            if(!preselectedItems.contains(item))
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
        }

        FunUpDateHamzzi.updateCloth(requireContext(), binding.clothFrameLayout, false, false)
//
//        //잠금 시간 안내
//        home_goalTimeTextView = requireView().findViewById(R.id.home_goalTimeTextView)
//        home_goalTimeTextView.text = (time.split(':')[0]+"시 " + time.split(':')[1] + "분 "
//                + time.split(':')[2]+"초")
//
//        //lock 화면 연결
//        home_startButton = requireView().findViewById(R.id.home_startButton)
//        home_startButton.setOnClickListener {
//            if(!isThereBigGoal)   {
//                Toast.makeText(context, "목표를 생성해주세요!", Toast.LENGTH_SHORT).show()
//            } else {
//                showSettingConfirmPopUp()
//                home_hamsterTalkTextView.callOnClick()
//            }
//        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 대표 목표 선택 View Pager를 구성할 데이터 값을 불러오는 함수
    @SuppressLint("Range")
    private fun getViewPagerData()
    {
        /** 대표 목표 이름 & 아이콘 색상 불러오기 **/

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)

        while(cursor.moveToNext())
        {
            // 대표 목표 이름 추가
            bigGoalNameList.add(cursor.getString(cursor.getColumnIndex("big_goal_name")))

            // 아이콘 색상 이름 추가
            iconColorNameList.add(cursor.getString(cursor.getColumnIndex("color")))
        }

        /** 아이콘 목록 불러오기 **/

        for(i in bigGoalNameList.indices)
        {
            // 해당 대표 목표의 세부 목표들 가져오기
            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '${bigGoalNameList[i]}'", null)

            // 해당 대표 목표의 아이콘 아이디값들을 전부 담을 문자열 변수
            var iconNameStr: String = ""
            // 임시로 아이콘 아이디값을 담을 변수(단일)
            var tempIconID: String

            while(cursor.moveToNext())
            {
                // 해당 대표 목표의 아이콘 이름 가져오기 -> 리소스값으로 변환 (컨버터 이용)
                tempIconID = DBConvert.iconConvert(cursor.getString(cursor.getColumnIndex("icon")), requireContext()).toString()

                // 해당 대표 목표의 아이콘 이름 문자열 갱신(추가)
                // ',' 문자로 각각의 아이콘 구별하도록 함
                iconNameStr += tempIconID + ','
            }

            // 해당 대표 목표의 아이콘들 temp 리스트에 저장
            iconIDList.add(iconNameStr)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

//    //잠금 시작
//    private fun showSettingConfirmPopUp() {
//        val dialog = LockSettingConfirmDialog(requireContext(),bigGoalName,bigGoalColor,time)
//        dialog.myDig()
//
//        dialog.setOnClickedListener(object : LockSettingConfirmDialog.ButtonClickListener{
//            override fun onClicked(isLock: Boolean) {
//                if(isLock){
//                    // 잠금 서비스 실행
//                    LockScreenUtil.active()
//
////                    var intent = Intent(requireActivity(), LockActivity::class.java)
////
////                    // 유저 정보 보내기
////                    intent.putExtra("seed", home_seedPointTextView.text)
////                    intent.putExtra("hamsterName", hamName)
////
////                    // 대표 목표 이름 보내기
////                    intent.putExtra("bigGoalName", home_goalSelectButton.text)
////
////                    // 타이머 시간 데이터 보내기
////                    intent.putExtra("hour", time.split(':')[0])
////                    intent.putExtra("min", time.split(':')[1])
////                    intent.putExtra("sec", time.split(':')[2])
////
////                    startActivity(intent)
//
//                }
//            }
//        })
//    }

//    //잠금 설정
//    private fun showLockSettingPopUp() {
//        val dialog = LockSettingDialog(requireContext(), bigGoalName, bigGoalColor, time)
//        //dialog.lockSetting()
//
//        dialog.setOnClickedListener(object : LockSettingDialog.ButtonClickListener{
//            override fun onClicked(isChanged: Boolean, bigGoalTitle: String, changedBigGoalColor: Int, getTime: String) {
//                if(isChanged){
//                    bigGoalName = bigGoalTitle
//                    home_goalSelectButton.text = bigGoalTitle
//                    bigGoalColor = changedBigGoalColor
//                    home_goalSelectButton.iconTint = ColorStateList.valueOf(bigGoalColor)
//                    if(getTime != "00:00:00"){
//                        time = getTime
//                    }
//                    var timeArray = time.split(':')
//                    home_goalTimeTextView.text = timeArray[0] + "시 " + timeArray[1]+"분 " + timeArray[2]+"초"
//                }
//            }
//        })
//    }
}