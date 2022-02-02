package com.example.guru_hemjee

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.widget.Toast

import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView

import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

// fragment 페이지 수(슬라이드 전환시)
private const val NUM_PAGES = 3

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Navagation Drawer 관련(햄버거 메뉴를 통한 화면 전환)
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    // title 관련 위젯
    lateinit var titleButton: ImageView
    lateinit var titleImage: ImageView

    // viewPager(스와이프를 통한 화면 전환)
    lateinit var viewPager: ViewPager2
    // indicator(현재 페이지 표시 목적)
    lateinit var viewPagerIndicator: DotsIndicator

    // (폰) 뒤로가기 클릭시 앱 종료 알림을 위한 변수
    private var backPressedTime: Long = 0
    var isHome = false

    //튜토리얼 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    //햄찌 관련
    private var preselected = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 권한을 얻었는지 체크
        checkPermissions()

        //튜토리얼 확인(basic_info_db에 데이터가 있는지 확인)
        dbManager = DBManager(this, "basic_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase // 데이터 읽기

        var cursor = sqlitedb.rawQuery("SELECT count(*) FROM basic_info_db", null)
        cursor.moveToFirst()
        var count = cursor.getInt(0);
        if(!(count>0)){
            Toast.makeText(this, "튜토리얼 시작", Toast.LENGTH_SHORT).show()
            tutorial()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

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
//        titleText = findViewById(R.id.titleTextView)


        // fragment 전환을 위한 transaction 생성
        val transaction = supportFragmentManager.beginTransaction()

        // 빈 fragment 화면 띄우기 (홈 화면을 보여주기 위함 - 스와이프 화면과 중복 방지)
        transaction.replace(R.id.fragment_main, BlankFragment(), "blank")
        transaction.commit()
        isHome = true

        // ViewPager 연결
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ScreenSlidePagerAdapter(this)    // 어댑터 생성
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL   // 방향을 가로로

        // indicator 연결
        viewPagerIndicator = findViewById(R.id.viewPagerIndicator)
        viewPagerIndicator.setViewPager2(viewPager)
    }

    //튜토리얼 시작
    private fun tutorial(){
        dbManager = DBManager(this, "basic_info_db", null, 1)
        sqlitedb = dbManager.writableDatabase
        sqlitedb.execSQL("INSERT INTO basic_info_db VALUES('김슈니', '햄찌햄찌', 3000, '01:30:00')")
        sqlitedb.close()
        dbManager.close()

        dbManager = DBManager(this, "hamster_deco_info_db", null, 1)
        sqlitedb = dbManager.writableDatabase
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg1', 10, 'bg', 'bg', 'bg_bg_10', 'market_bg_10', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg2', 1000, 'bg', 'bg', 'bg_bg_1000', 'market_bg_1000', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_toystory', 1500, 'bg', 'bg', 'bg_bg_1500', 'market_bg_1500', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_beach', 1800, 'bg', 'bg', 'bg_bg_1800', 'market_bg_1800', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_snow', 2100, 'bg', 'bg', 'bg_bg_2100', 'market_bg_2100', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_sunset', 2500, 'bg', 'bg', 'bg_bg_2500', 'market_bg_2500', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sandcattle', 390, 'furni1', 'furni', 'bg_furni_390', 'market_furni_390', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant', 390, 'furni', 'furni2', 'bg_furni_390_2', 'market_furni_390_2', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_pics', 560, 'furni', 'furni3', 'bg_furni_560', 'market_furni_560', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_house', 730, 'furni', 'furni5', 'bg_furni_730_2', 'market_furni_730_2', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_surf1', 730, 'furni', 'furni4', 'bg_furni_730', 'market_furni_730', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_surf2', 790, 'furni', 'furni6', 'bg_furni_790', 'market_furni_790', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_snowman', 920, 'furni', 'right', 'bg_furni_920', 'market_furni_920', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_tree1', 1000, 'furni', 'furni8', 'bg_furni_1000', 'market_furni_1000', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_tree2', 1020, 'furni', 'right', 'bg_furni_1020', 'market_furni_1020', '', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_glasses', 390, 'clo', 'gla', 'bg_clo_390', 'market_clo_390', 'hamster_clo_390', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap', 390, 'clo', 'hat', 'bg_clo_390_2', 'market_clo_390_2', 'hamster_clo_390_2', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_workhard', 560, 'clo', 'hat', 'bg_clo_560', 'market_clo_560', 'hamster_clo_560', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs', 560, 'clo', 'hat', 'bg_clo_560_2', 'market_clo_560_2', 'hamster_clo_560_2', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sungla', 730, 'clo', 'hat', 'bg_clo_730', 'market_clo_730', 'hamster_clo_730', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_flower', 730, 'clo', 'hat', 'bg_clo_730_2', 'market_clo_730_2', 'hamster_clo_730_2', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_strap', 420, 'clo', 'clo', 'bg_clo_420', 'market_clo_420', 'hamster_clo_420', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_winter', 710, 'clo', 'clo', 'bg_clo_710', 'market_clo_710', 'hamster_clo_710', 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_summer', 1000, 'clo', 'clo', 'bg_clo_1000', 'market_clo_1000', 'hamster_clo_1000', 0, 0)")
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
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers()
        // 그렇지 않다면 뒤로가기를 눌러 앱을 종료할 수 있도록 함
        else
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
            }
        }
    }

    // 햄버거 메뉴를 통한 화면 이동
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // 화면 이동을 위한 intent(서브 페이지들이 있는 액티비티로 보내기 위함)
        var intent = Intent(this, SubMainActivity::class.java)

        // 각 탭으로 전환
        when(item.itemId) {

            R.id.action_goalAndTime -> {
                intent.putExtra("titleName", "목표/잠금 시간 설정")
                intent.putExtra("tag", "setUp")
            }
            R.id.action_report -> {
                intent.putExtra("titleName", "목표 리포트")
                intent.putExtra("tag", "dailyReport")
            }
            R.id.action_album -> {
                intent.putExtra("titleName", "나의 성취 앨범")
                intent.putExtra("tag", "dailyAlbum")
            }
            R.id.action_store -> {
                intent.putExtra("titleName", "씨앗 상점")
                intent.putExtra("tag", "seedMarket")
            }
            R.id.action_charManagement -> {
                intent.putExtra("titleName", "나의 햄찌 관리")
                intent.putExtra("tag", "hamsterEdit")
            }
            R.id.action_preference -> {
                intent.putExtra("titleName", "설정")
                intent.putExtra("tag", "preference")
            }
        }

        // Navigation Drawer를 통해 각 탭으로 전환시 동작(변경사항)
        when(item.itemId) {
            R.id.action_goalAndTime, R.id.action_report, R.id.action_album,
            R.id.action_store, R.id.action_charManagement, R.id.action_preference -> {

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()

                // SubMainActivity로 전환
                startActivity(intent)
            }
        }

        return false
    }

}
