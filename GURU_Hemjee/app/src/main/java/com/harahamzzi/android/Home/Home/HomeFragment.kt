package com.harahamzzi.android.Home.Home

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.harahamzzi.android.*
import com.harahamzzi.android.Home.Goal.SetupFragment
import com.harahamzzi.android.Home.MainActivity
import com.harahamzzi.android.databinding.FragmentHomeBinding
import java.lang.Exception
import java.math.BigInteger
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

// MainActivity -> 홈
// 햄찌, 잠금 시작 등을 할 수 있는 홈 Fragment 화면
class HomeFragment : Fragment() {

    // 태그
    private val TAG = "HomeFragment"

    // 뷰 바인딩 변수
    private var mBinding: FragmentHomeBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // 데이터 값
    private var bigGoalNameList = ArrayList<String>()   // 대표 목표 이름 목록
    private var iconColorNameList = ArrayList<String>() // 아이콘 색상 이름 목록
    private var iconIDList = ArrayList<String>()        // 아이콘 목록

    // DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

    // 팝업 관련
    private lateinit var bottomDialog:BottomSummaryDialog   // 하단 요약본 페이지

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

        binding.goalViewPager.clipToPadding = false
        binding.goalViewPager.clipChildren = false

        // 여백 및 너비 설정
        val screenWidth = resources.displayMetrics.widthPixels  // 스마트폰의 너비 길이를 가져옴
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.view_pager_width)
        val pagerMarginPx = resources.getDimensionPixelOffset(R.dimen.view_pager_margin)

        val offsetPx = screenWidth - pagerMarginPx - pagerWidth

        // 페이지의 시작 X좌표 값을 설정
        binding.goalViewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx + binding.linearLayout4.x
        }

        // 데이터 초기화
        for (i in bigGoalNameList.indices)
        {
            bigGoalNameList.removeFirst()
            iconColorNameList.removeFirst()
            iconIDList.removeFirst()
        }

        // 1. 데이터 값 불러오기
        getViewPagerData()

        // 1-2. 생성된 데이터가 없을 시에는 빈 박스 보이기
        if (bigGoalNameList.isEmpty())
        {
            binding.emptyLayout.visibility = View.VISIBLE
        }
        else
        {
            binding.emptyLayout.visibility = View.GONE

            // 2. 어댑터 생성
            binding.goalViewPager.adapter = HomeViewPagerAdapter(requireContext(), bigGoalNameList, iconColorNameList, iconIDList)
        }

        /** 빈 박스 내의 버튼 설정 **/
        binding.addGoalButton.setOnClickListener {

            var transaction = requireActivity().supportFragmentManager.beginTransaction()

            // 타이틀 텍스트 바꾸기
            var titleText: TextView? = activity?.findViewById(R.id.titleTextView)
            titleText?.text = "목표 설정"

            // 타이틀 디자인 변경
            var titleImage: ImageView? = activity?.findViewById(R.id.titleImageView)
            titleImage?.visibility = View.INVISIBLE
            titleText?.visibility = View.VISIBLE

            // 상단바 색 적용
            var backgroundLayout: FrameLayout? = activity?.findViewById(R.id.backgroundLayout)
            backgroundLayout?.removeAllViews()
            backgroundLayout?.setBackgroundResource(R.color.Yellow)

            // drawer menu swipe 비활성화
            var drawerLayout: DrawerLayout? = activity?.findViewById(R.id.home_drawerLayout)
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            // 툴바 좌측 버튼 변경
            var titleButton: ImageView? = activity?.findViewById(R.id.titleButton)
            titleButton?.setImageResource(R.drawable.ic_outline_west_24)
            titleButton?.setOnClickListener {
                // 뒤로가기(홈 화면으로 가기) 수행
                titleText?.visibility = View.INVISIBLE
                titleText?.text = ""
                titleImage?.visibility = View.VISIBLE

                // 툴바 좌측 버튼 햄버거로 변경
                titleButton.setImageResource(R.drawable.menu_icon)
                titleButton.setOnClickListener {
                    drawerLayout?.openDrawer(GravityCompat.START)
                }

                // drawer menu swipe 활성화
                drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                try {
                    // 배경 및 가구 업데이트
                    FunUpDateHamzzi.updateBackground(requireContext(), backgroundLayout!!, false)
                }
                catch (e: Exception) {
                    Log.e(TAG, "툴바 좌측 버튼 클릭 -> 배경 및 가구 업데이트 오류")
                }

                // home 화면으로 전환
                var transaction = requireActivity().supportFragmentManager.beginTransaction()

                transaction.replace(R.id.fragment_main, HomeFragment(), "home")
                transaction.addToBackStack(null)
                transaction.commit()

                MainActivity.isHome = true
            }

            // 목표 설정 Fragment 페이지로 이동
            transaction.replace(R.id.fragment_main, SetupFragment(), "setUp")
            transaction.addToBackStack(null)
            transaction.commit()

            MainActivity.isHome = false
        }

        /** 하단 요약본 페이지 버튼 설정 **/
        bottomDialog = BottomSummaryDialog(requireContext())
        binding.summarySheetStartButton.setOnClickListener {
            bottomDialog.showPopup()
        }

        /** 홈 화면 정보 갱신 **/
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 씨앗 및 햄스터 이름 갱신
        cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)

        if(cursor.moveToNext()) {
            binding.homeSeedPointTextView.text = cursor.getString(cursor.getColumnIndex("seed")).toString() // 씨앗 갱신
            binding.hamName.text = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()       // 홈 화면 햄스터 이름 갱신

            // 네비게이션 헤더 접근
            var navView: NavigationView = requireActivity().findViewById(R.id.navigationView)
            var navHeaderView: View = navView.getHeaderView(0)
            var navNameTextView: TextView = navHeaderView.findViewById(R.id.header_helloNameTextView)
            navNameTextView.text = binding.hamName.text                                                     // 햄버거 메뉴 햄스터 이름 갱신
        }

        // 오늘 함께한 시간 갱신
        var totalMsTime = BigInteger.ZERO
        var todayDate = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-E"))

        cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db WHERE lock_date LIKE '${todayDate}%'", null)

        while (cursor.moveToNext())
        {
            totalMsTime += TimeConvert.timeToMsConvert(cursor.getString(cursor.getColumnIndex("total_report_time")).toString())
        }

        var totalStrTime = TimeConvert.msToTimeConvert(totalMsTime)

        var hour = totalStrTime.split(':')[0]
        var min = totalStrTime.split(':')[1]
        var sec = totalStrTime.split(':')[2]

        if (hour == "00" && min == "00")
        {
            binding.totalTime.text = "${sec}초"
        }
        else
        {
            binding.totalTime.text = "${hour}시간 ${min}분"
        }

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

        FunUpDateHamzzi.updateCloth(requireContext(), binding.homeClothFrameLayout, binding.homeBottomClothFrameLayout, binding.homeCapeFrameLayout, false)

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
            var iconIDStr: String = ""
            // 임시로 아이콘 아이디값을 담을 변수(단일)
            var tempIconID: String

            while(cursor.moveToNext())
            {
                // 해당 대표 목표의 아이콘 이름 가져오기 -> 리소스값으로 변환 (컨버터 이용)
                tempIconID = DBConvert.iconConvert(cursor.getString(cursor.getColumnIndex("icon")), requireContext()).toString()

                // 해당 대표 목표의 아이콘 이름 문자열 갱신(추가)
                // ',' 문자로 각각의 아이콘 구별하도록 함
                iconIDStr += tempIconID + ','
            }

            // 해당 대표 목표의 아이콘들 temp 리스트에 저장
            iconIDList.add(iconIDStr)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }
}