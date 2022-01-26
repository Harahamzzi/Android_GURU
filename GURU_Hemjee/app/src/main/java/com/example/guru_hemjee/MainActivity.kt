package com.example.guru_hemjee

import android.app.PendingIntent.getActivity
import android.widget.Toast


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView

// fragment 페이지 수(슬라이드 전환시)
private const val NUM_PAGES = 3

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    // title 관련 위젯
    lateinit var titleButton: ImageView
    lateinit var titleImage: ImageView
    lateinit var titleText: TextView

    lateinit var viewPager: ViewPager2

//    lateinit var startButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 툴바 적용
        val toolbar: Toolbar = findViewById(R.id.toolBar)
        toolbar.setContentInsetsAbsolute(0, 0); // 왼쪽 여백 제거
        setActionBar(toolbar)


        // 드로어를 꺼낼 홈 버튼 비활성화(이미 툴바에 있기 때문)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        // 이미 타이틀은 없애놓은 상태라..필요없는 구문이므로 주석 처리함
//        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안 보이게 설정

        // 네비게이션 드로어 생성
        drawerLayout = findViewById(R.id.home_drawerLayout)

        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // menuButton 리스너 연결
        // (메뉴 버튼 클릭으로 네비게이션 드로어 열기)
        titleButton = findViewById(R.id.titleButton)
        titleButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // titleImage 연결
        titleImage = findViewById(R.id.titleImageView)

        // titleText 연결
        titleText = findViewById(R.id.titleTextView)

//        //lock 화면 연결
//        startButton = findViewById(R.id.startButton)
//        startButton.setOnClickListener {
//            showSettingConfirmPopUp()
//        }


        // fragment 전환을 위한 transaction 생성
        val transaction = supportFragmentManager.beginTransaction()

        // 빈 fragment 화면 띄우기 (홈 화면을 보여주기 위함 - 스와이프 화면과 중복 방지)
        transaction.replace(R.id.fragment_main, BlankFragment())
        transaction.commit()

        // ViewPager 연결
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ScreenSlidePagerAdapter(this)    // 어댑터 생성
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL   // 방향을 가로로
    }

    // 내부 클래스 선언
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        // 페이지 수 리턴하는 함수
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            // 페이지 포지션에 따라 그에 알맞은 fragment 보이기
            return when(position) {
                0 -> HomeFragment()
                1 -> HomeReportFragment()
                2 -> HomeAlbumFragment()
                else -> HomeFragment()
            }
        }
    }

    // (핸드폰)뒤로가기를 눌렀을 때의 동작 함수
    override fun onBackPressed() {
        // 만일 드로어가 열려있는 상태라면 드로어를 닫음
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers()
        // 그렇지 않다면 이전 화면으로 돌아가기
        else
        {
            super.onBackPressed()


            // TODO: 현재 띄워진 fragment의 Tag 가져와서 비교 & 동작하기(수정 필요)
            // 돌아간 화면 가져오기
            var fragment: Fragment? = supportFragmentManager.findFragmentByTag("home")

            // 돌아간 화면이 home이라면(HomeFragment가 현재 열려있다면)
            if (fragment != null && fragment == HomeFragment())
            {
                // titleImage 보이기
                titleImage.visibility = View.VISIBLE
                // titleText 숨기기
                titleText.visibility = View.INVISIBLE

                // 툴바 좌측 이미지 변경(드로어 열기)
                titleButton.setImageResource(R.drawable.menu_icon)
                // 드로어 열기 리스너로 교체
                titleButton.setOnClickListener {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            else
            {
                // TODO: 각 태그에 따라 title text 조정하기

                // titleImage 숨기기
                titleImage.visibility = View.INVISIBLE
                // titleText 보이기
                titleText.visibility = View.VISIBLE

                // 툴바 좌측 이미지 변경(뒤로가기)
                titleButton.setImageResource(R.drawable.ic_outline_west_24)
                // 좌측 이미지에 뒤로가기(홈 화면으로 가기) 리스너 달기 실행
                backHomeListener(titleButton)
            }

        }
    }

    // 툴바 좌측 이미지를 눌렀을 때 홈 화면으로 가기를 처리하는 함수(리스너)
    private fun backHomeListener(icon: ImageView) {
        icon.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()

            // title 이미지 보이기
            titleImage.visibility = View.VISIBLE

            // 타이틀 텍스트 숨기기
            titleText.visibility = View.INVISIBLE

            // 타이틀 초기화
            titleText.setText("")

            // 빈 화면으로 전환(홈 화면을 보여주기 위함 - 스와이프 화면과 중복 방지)
            transaction.replace(R.id.fragment_main, BlankFragment())
            transaction.addToBackStack(null)
            transaction.commit()

            // 툴바 좌측 이미지 햄버거로 변경/리스너 변경
            icon.setImageResource(R.drawable.menu_icon)
            icon.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            // ViewPager adapter 생성
            viewPager.adapter = ScreenSlidePagerAdapter(this)
            // ViewPager 스와이프 터치 활성화
            viewPager.isUserInputEnabled = true
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
                titleText.setText("목표/잠금 시간 설정")

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_report -> {
                // 목표 리포트 탭으로 전환
                transaction.replace(R.id.fragment_main, DailyReportFragment(), "dailyReport")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                titleText.setText("목표 리포트")

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_album -> {
                // 나의 성취 앨범 탭으로 전환
                transaction.replace(R.id.fragment_main, DailyAlbumFragment(), "dailyAlbum")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                titleText.setText("나의 성취 앨범")

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_store -> {
                // 씨앗 상점 탭으로 전환
                transaction.replace(R.id.fragment_main, SeedMarket(), "seedMarket")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                titleText.setText("씨앗 상점")

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_charManagement -> {
                // 나의 햄찌 관리 탭으로 전환
                transaction.replace(R.id.fragment_main, HamsterEditFragment(), "hamsterEdit")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                titleText.setText("나의 햄찌 관리")

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_preference -> {
                // 설정 탭으로 전환
//                transaction.replace(R.id.fragment_main, 설정탭..(), "설정탭 태그")
//                transaction.addToBackStack(null)
//                transaction.commit()

                // 타이틀 텍스트 변경
                titleText.setText("설정")

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
        }

        // Navigation Drawer를 통해 각 탭으로 전환시 동작(변경사항)
        when(item.itemId) {
            R.id.action_goalAndTime, R.id.action_report, R.id.action_album,
            R.id.action_store, R.id.action_charManagement, R.id.action_preference -> {

                // ViewPager 스와이프 터치 비활성화
                viewPager.isUserInputEnabled = false
                // ViewPager adapter 없애기
                viewPager.adapter = null

                // title 이미지 숨기기
                titleImage.visibility = View.GONE

                // 타이틀 텍스트 보이기
                titleText.visibility = View.VISIBLE

                // 툴바 좌측 이미지 변경(뒤로가기)
                titleButton.setImageResource(R.drawable.ic_outline_west_24)
                // 좌측 이미지에 뒤로가기(홈 화면으로 가기) 리스너 달기 실행
                backHomeListener(titleButton)
            }
        }

        return false
    }

}
