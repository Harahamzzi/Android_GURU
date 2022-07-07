package com.example.guru_hemjee.Home

import android.Manifest
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.guru_hemjee.*
import com.example.guru_hemjee.Home.Album.AlbumMainActivity
import com.example.guru_hemjee.Home.Goal.SetupFragment
import com.example.guru_hemjee.Home.Home.HomeFragment
import com.example.guru_hemjee.Home.MyHamsterManage.HamsterEditFragment
import com.example.guru_hemjee.Home.Report.DailyReportFragment
import com.example.guru_hemjee.Home.Setting.SettingFragment
import com.example.guru_hemjee.Home.Store.SeedMarketFragment
import com.example.guru_hemjee.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

// 홈 Fragment, 홈 앨범 Fragment, 홈 리포트 Fragment를 보여주는 Activity 메인 화면

// 하단 슬라이드를 통해 요약본 뷰 확인 가능
// Navigation Drawer Menu를 통해 각 페이지에 접근 가능
// -> 목표/시간 설정, 목표 리포트, 나의 성취 앨범, 씨앗 상점, 나의 햄찌 관리, 설정 페이지

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // view binding
    private var mBinding: ActivityMainBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // (폰) 뒤로가기 클릭시 앱 종료 알림을 위한 변수
    private var backPressedTime: Long = 0
    private var isHome = false

    //튜토리얼 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한을 얻었는지 체크
        checkPermissions()

        //튜토리얼 확인(basic_info_db에 데이터가 있는지 확인)
        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        var cursor = sqlitedb.rawQuery("SELECT count(*) FROM basic_info_db", null)
        cursor.moveToFirst()
        var count = cursor.getInt(0);
        if(!(count>0)){
            //튜토리얼 엑티비티 연결
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 세부 목표 리포트 DB에서 필요없는 데이터 제거 및 초기화
        setDetailGoalReportDB()

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // 네비게이션 드로어 생성
        binding.navigationView.setNavigationItemSelectedListener(this)

        // menuButton 리스너 연결
        // (메뉴 버튼 클릭으로 네비게이션 드로어 열기)
        binding.titleButton.setOnClickListener {
            binding.homeDrawerLayout.openDrawer(GravityCompat.START)
        }

//        // ViewPager 연결
//        viewPager = findViewById(R.id.viewPager)
//        viewPager.adapter = ScreenSlidePagerAdapter(this)    // 어댑터 생성
//        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL   // 방향을 가로로
//
//        // indicator 연결
//        viewPagerIndicator = findViewById(R.id.viewPagerIndicator)
//        viewPagerIndicator.setViewPager2(viewPager)

        // 홈 화면 띄우기
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_main, HomeFragment(), "home")
        transaction.addToBackStack(null)
        transaction.commit()

        // isHome 플래그 초기화
        isHome = true

        // 배경 및 가구 업데이트
        FunUpDateHamzzi.updateBackground(this@MainActivity, binding.backgroundLayout, false, false)
    }

    override fun onDestroy() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroy()
    }

    // 권한 체크를 위한 리스너
    private var permissionListener: PermissionListener = object: PermissionListener {
        // 권한 허가시 실행할 함수
        override fun onPermissionGranted() {
            // 그대로 진행
        }

        // 권한 거부시 실행할 함수
        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            // 앱 종료
            finish()
        }
    }
    // 권한을 얻었는지 체크하는 함수(통합)
    private fun checkPermissions() {
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("앱에서 요구하는 권한 설정이 필요합니다.\n[권한]에서 허용으로 설정해주세요.")
            .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.FOREGROUND_SERVICE,
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    // (핸드폰)뒤로가기를 눌렀을 때의 동작 함수
    override fun onBackPressed() {
        // 만일 드로어가 열려있는 상태라면 드로어를 닫음
        if(binding.homeDrawerLayout.isDrawerOpen(GravityCompat.START))
            binding.homeDrawerLayout.closeDrawers()
        // 현재 홈 화면일 경우
        else if (isHome)
        {
//            super.onBackPressed()

            if(System.currentTimeMillis() > backPressedTime + 2000)
            {
                backPressedTime = System.currentTimeMillis()
                Toast.makeText(this, "\'뒤로 \' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
            else if(System.currentTimeMillis() <= backPressedTime + 2000)   // 토스트 메시지가 사라지기 전에 누르면 앱이 종료됨
            {
                // 현재 액티비티 종료(앱이 종료 되게 함)
                finish()
                finishAffinity()
            }
        }
        // 그렇지 않다면 이전 화면으로 돌아가기
        else
        {
            super.onBackPressed()

            // 현재 보여지는 fragment 찾기
            for(fragment : Fragment in supportFragmentManager.fragments) {
                // 만일 보이는 fragment를 찾았다면 tag 저장
                if (fragment.isVisible) {
                    val tag = fragment.tag

                    // tag 비교/적용
                    when (tag) {
                        // tag가 home일 때의 동작
                        "home" -> {
                            // titleImage 보이기
                            binding.titleImageView.visibility = View.VISIBLE
                            // titleText 숨기기
                            binding.titleTextView.visibility = View.INVISIBLE

                            // 툴바 좌측 이미지 변경(드로어 열기)
                            binding.titleButton.setImageResource(R.drawable.menu_icon)
                            // 드로어 열기 리스너로 교체
                            binding.titleButton.setOnClickListener {
                                binding.homeDrawerLayout.openDrawer(GravityCompat.START)
                            }

                            // isHome 플래그 올리기
                            isHome = true
                        }
                        // 목표 및 잠금 시간 설정 페이지
                        "setUp" -> {
                            binding.titleTextView.setText("목표/잠금 시간 설정")
                        }
                        // 목표 리포트 페이지
                        "dailyReport" -> {
                            binding.titleTextView.setText("목표 리포트")
                        }
                        // 나의 성취 앨범 페이지
                        "dailyAlbum" -> {
                            binding.titleTextView.setText("나의 성취 앨범")
                        }
                        // 씨앗 상점 페이지
                        "seedMarket" -> {
                            binding.titleTextView.setText("씨앗 상점")
                        }
                        // 나의 햄찌 관리 페이지
                        "hamsterEdit" -> {
                            binding.titleTextView.setText("나의 햄찌 관리")
                        }
                        // 설정 탭
                        "setting" -> {
                            binding.titleTextView.setText("설정")
                        }
                    }

                    // home을 제외한 화면일 때의 공통 동작
                    if(tag != "home")
                    {
                        setOtherPagesAction()
                    }
                }
            }
        }
    }

    // 현재 페이지가 Home 페이지 외 다른 페이지일 때의 동작과 디자인을 설정해주는 함수
    private fun setOtherPagesAction() {
        // titleImage 숨기기
        binding.titleImageView.visibility = View.INVISIBLE
        // titleText 보이기
        binding.titleTextView.visibility = View.VISIBLE

        // 툴바 좌측 이미지 변경(뒤로가기)
        binding.titleButton.setImageResource(R.drawable.ic_outline_west_24)
        // 좌측 이미지에 뒤로가기(홈 화면으로 가기) 리스너 달기 실행
        backHomeListener(binding.titleButton)


        // isHome 플래그 내리기
        isHome = false

        //햄찌 설정 초기화
    }

    // 툴바 좌측 이미지를 눌렀을 때 홈 화면으로 가기를 처리하는 함수(리스너)
    private fun backHomeListener(icon: ImageView) {
        icon.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()

            // title 이미지 보이기
            binding.titleImageView.visibility = View.VISIBLE

            // 타이틀 텍스트 숨기기
            binding.titleTextView.visibility = View.INVISIBLE

            // 타이틀 초기화
            binding.titleTextView.setText("")

            // 툴바 좌측 이미지 햄버거로 변경/리스너 변경
            icon.setImageResource(R.drawable.menu_icon)
            icon.setOnClickListener {
                binding.homeDrawerLayout.openDrawer(GravityCompat.START)
            }

            // home 화면으로 전환
            transaction.replace(R.id.fragment_main, HomeFragment(), "home")
            transaction.addToBackStack(null)
            transaction.commit()

            // isHome 플래그 올리기
            isHome = true
        }
    }

    // 햄버거 메뉴를 통한 화면 이동
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // fragment 전환을 위한 transaction 생성
        val transaction = supportFragmentManager.beginTransaction()

        // 각 탭으로 전환
        when(item.itemId) {

            R.id.action_goalAndTime -> {
                // 목표 및 잠금 시간 설정 탭으로 전환
                transaction.replace(R.id.fragment_main, SetupFragment(), "setUp")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                binding.titleTextView.setText("목표/잠금 시간 설정")

                // Navigation Drawer 닫기
                binding.homeDrawerLayout.closeDrawers()
            }
            R.id.action_report -> {
                // 목표 리포트 탭으로 전환
                transaction.replace(R.id.fragment_main, DailyReportFragment(), "dailyReport")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                binding.titleTextView.setText("목표 리포트")

                // Navigation Drawer 닫기
                binding.homeDrawerLayout.closeDrawers()
            }
            R.id.action_album -> {

                // Navigation Drawer 닫기
                binding.homeDrawerLayout.closeDrawers()

                // AlbumMainActivity로 바로 이동
                var tempIntent = Intent(this, AlbumMainActivity::class.java)
                startActivity(tempIntent)
            }
            R.id.action_store -> {
                // 씨앗 상점 탭으로 전환
                transaction.replace(R.id.fragment_main, SeedMarketFragment(), "seedMarket")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                binding.titleTextView.setText("씨앗 상점")

                // Navigation Drawer 닫기
                binding.homeDrawerLayout.closeDrawers()
            }
            R.id.action_charManagement -> {
                // 나의 햄찌 관리 탭으로 전환
                transaction.replace(R.id.fragment_main, HamsterEditFragment(), "hamsterEdit")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                binding.titleTextView.setText("나의 햄찌 관리")

                // Navigation Drawer 닫기
                binding.homeDrawerLayout.closeDrawers()
            }
            R.id.action_preference -> {
                // 설정 탭으로 전환
                transaction.replace(R.id.fragment_main, SettingFragment(), "setting")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                binding.titleTextView.setText("설정")

                // Navigation Drawer 닫기
                binding.homeDrawerLayout.closeDrawers()
            }
        }

        // Navigation Drawer를 통해 각 탭으로 전환시 동작(변경사항)
        when(item.itemId) {
            R.id.action_goalAndTime, R.id.action_report,
            R.id.action_store, R.id.action_charManagement, R.id.action_preference -> {

                setOtherPagesAction()
            }
        }

        return false
    }

    // 어플을 킬 때 세부 목표 리포트 DB 데이터를 세팅해주는 함수
    private fun setDetailGoalReportDB() {

        // 세부 목표 리포트 DB의 is_active 필드 초기화
        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.writableDatabase
        sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET is_active = 0 WHERE is_active = 1")

        sqlitedb.close()
        dbManager.close()

        // 세부 목표 리포트 DB에서 파일명이 저장되지 않은 데이터들은 삭제
        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        sqlitedb.execSQL("DELETE FROM detail_goal_time_report_db WHERE photo_name IS NULL")

        sqlitedb.close()
        dbManager.close()
    }
}
