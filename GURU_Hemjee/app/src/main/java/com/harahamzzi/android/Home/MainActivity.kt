package com.harahamzzi.android.Home

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.widget.Toast
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.harahamzzi.android.*
import com.harahamzzi.android.Home.Album.AlbumMainActivity
import com.harahamzzi.android.Home.Fame.FameFragment
import com.harahamzzi.android.Home.Goal.SetupFragment
import com.harahamzzi.android.Home.Home.HomeFragment
import com.harahamzzi.android.Home.MyHamsterManage.HamsterEditFragment
import com.harahamzzi.android.Home.Report.DailyReportFragment
import com.harahamzzi.android.Home.Setting.SettingFragment
import com.harahamzzi.android.Home.Store.SeedMarketFragment
import com.harahamzzi.android.databinding.ActivityMainBinding
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

    companion object {
        var isHome = false  // 현재 홈 화면인지 판별
    }

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

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // 상태바 숨기기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) // API 30이상
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else
        {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN )
        }

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
        FunUpDateHamzzi.updateBackground(this@MainActivity, binding.backgroundLayout, false)
    }

    override fun onDestroy() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

        // 세부 목표 리포트 DB에서 필요없는 데이터 제거 및 초기화
        setDetailGoalReportDB()
        // shared preferences 내의 데이터 초기화
        setSharedPreferencesDB()

        // 아이템 가격 변경
        try {
            val dbManager = DBManager(this, "hamster_db", null, 1)
            val sqlitedb = dbManager.writableDatabase

            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_pink', 360, 'clo', 'upper', 'bg_clo_upper_360_pink', 'market_clo_upper_360_pink', 0, 0, 0)")

            dbManager.close()
            sqlitedb.close()
        } catch(e: Exception) {
            Log.d("MainActivity :: 오류", "이미 있는 행입니다.")
            Log.d("MainActivity :: 오류", e.printStackTrace().toString())
        }

        try {
            val dbManager = DBManager(this, "hamster_db", null, 1)
            val sqlitedb = dbManager.writableDatabase

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_bg_300' WHERE item_name = 'bg1';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_bg_300' WHERE item_name = 'bg1';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 300 WHERE item_name = 'bg1';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_bg_500' WHERE item_name = 'bg_airport';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_bg_500' WHERE item_name = 'bg_airport';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 500 WHERE item_name = 'bg_airport';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_bg_700' WHERE item_name = 'bg_line';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_bg_700' WHERE item_name = 'bg_line';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 700 WHERE item_name = 'bg_line';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_bg_900' WHERE item_name = 'bg_desert';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_bg_900' WHERE item_name = 'bg_desert';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 900 WHERE item_name = 'bg_desert';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_bg_1100' WHERE item_name = 'bg_sunset';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_bg_1100' WHERE item_name = 'bg_sunset';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 1100 WHERE item_name = 'bg_sunset';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_bg_1400' WHERE item_name = 'bg_sun';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_bg_1400' WHERE item_name = 'bg_sun';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 1400 WHERE item_name = 'bg_sun';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_190_darkyellow' WHERE item_name = 'furni_lamp_darkyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_190_darkyellow' WHERE item_name = 'furni_lamp_darkyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'furni_lamp_darkyellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_190_green' WHERE item_name = 'furni_lamp_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_190_green' WHERE item_name = 'furni_lamp_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'furni_lamp_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_190_lightyellow' WHERE item_name = 'furni_lamp_lightyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_190_lightyellow' WHERE item_name = 'furni_lamp_lightyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'furni_lamp_lightyellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_190_orange' WHERE item_name = 'furni_lamp_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_190_orange' WHERE item_name = 'furni_lamp_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'furni_lamp_orange';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_260_darkyellow' WHERE item_name = 'furni_biglamp_darkyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_260_darkyellow' WHERE item_name = 'furni_biglamp_darkyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 260 WHERE item_name = 'furni_biglamp_darkyellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_260_green' WHERE item_name = 'furni_biglamp_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_260_green' WHERE item_name = 'furni_biglamp_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 260 WHERE item_name = 'furni_biglamp_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_260_lightyellow' WHERE item_name = 'furni_biglamp_lightyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_260_lightyellow' WHERE item_name = 'furni_biglamp_lightyellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 260 WHERE item_name = 'furni_biglamp_lightyellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_260_orange' WHERE item_name = 'furni_biglamp_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_260_orange' WHERE item_name = 'furni_biglamp_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 260 WHERE item_name = 'furni_biglamp_orange';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_430_blue' WHERE item_name = 'furni_plant_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_430_blue' WHERE item_name = 'furni_plant_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 430 WHERE item_name = 'furni_plant_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_430_darkpurple' WHERE item_name = 'furni_plant_darkpurple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_430_darkpurple' WHERE item_name = 'furni_plant_darkpurple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 430 WHERE item_name = 'furni_plant_darkpurple';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_430_green' WHERE item_name = 'furni_plant_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_430_green' WHERE item_name = 'furni_plant_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 430 WHERE item_name = 'furni_plant_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_430_purple' WHERE item_name = 'furni_plant_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_430_purple' WHERE item_name = 'furni_plant_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 430 WHERE item_name = 'furni_plant_purple';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_490_blue' WHERE item_name = 'furni_chair_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_490_blue' WHERE item_name = 'furni_chair_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 490 WHERE item_name = 'furni_chair_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_490_green' WHERE item_name = 'furni_chair_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_490_green' WHERE item_name = 'furni_chair_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 490 WHERE item_name = 'furni_chair_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_490_yellow' WHERE item_name = 'furni_chair_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_490_yellow' WHERE item_name = 'furni_chair_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 490 WHERE item_name = 'furni_chair_yellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_620_brown' WHERE item_name = 'furni_mirror_brown';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_620_brown' WHERE item_name = 'furni_mirror_brown';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 620 WHERE item_name = 'furni_mirror_brown';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_620_gray' WHERE item_name = 'furni_mirror_gray';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_620_gray' WHERE item_name = 'furni_mirror_gray';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 620 WHERE item_name = 'furni_mirror_gray';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_620_red' WHERE item_name = 'furni_mirror_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_620_red' WHERE item_name = 'furni_mirror_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 620 WHERE item_name = 'furni_mirror_red';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_700_green' WHERE item_name = 'furni_shelf_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_700_green' WHERE item_name = 'furni_shelf_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 700 WHERE item_name = 'furni_shelf_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_700_purple' WHERE item_name = 'furni_shelf_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_700_purple' WHERE item_name = 'furni_shelf_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 700 WHERE item_name = 'furni_shelf_purple';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_700_yellow' WHERE item_name = 'furni_shelf_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_700_yellow' WHERE item_name = 'furni_shelf_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 700 WHERE item_name = 'furni_shelf_yellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_720_green' WHERE item_name = 'furni_sofa_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_720_green' WHERE item_name = 'furni_sofa_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 720 WHERE item_name = 'furni_sofa_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_720_pink' WHERE item_name = 'furni_sofa_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_720_pink' WHERE item_name = 'furni_sofa_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 720 WHERE item_name = 'furni_sofa_pink';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_furni_720_red' WHERE item_name = 'furni_sofa_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_furni_720_red' WHERE item_name = 'furni_sofa_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 720 WHERE item_name = 'furni_sofa_red';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_190_black' WHERE item_name = 'clo_cap_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_190_black' WHERE item_name = 'clo_cap_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_cap_black';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_190_blue' WHERE item_name = 'clo_cap_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_190_blue' WHERE item_name = 'clo_cap_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_cap_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_190_red' WHERE item_name = 'clo_cap_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_190_red' WHERE item_name = 'clo_cap_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_cap_red';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_190_white' WHERE item_name = 'clo_cap_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_190_white' WHERE item_name = 'clo_cap_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_cap_white';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_190_green' WHERE item_name = 'clo_band_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_190_green' WHERE item_name = 'clo_band_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_band_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_190_pink' WHERE item_name = 'clo_band_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_190_pink' WHERE item_name = 'clo_band_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_band_pink';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_190_white2' WHERE item_name = 'clo_band_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_190_white2' WHERE item_name = 'clo_band_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_band_white';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_360_blue' WHERE item_name = 'clo_earmuffs_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_360_blue' WHERE item_name = 'clo_earmuffs_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 360 WHERE item_name = 'clo_earmuffs_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_360_orange' WHERE item_name = 'clo_earmuffs_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_360_orange' WHERE item_name = 'clo_earmuffs_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 360 WHERE item_name = 'clo_earmuffs_orange';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_360_purple' WHERE item_name = 'clo_earmuffs_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_360_purple' WHERE item_name = 'clo_earmuffs_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 360 WHERE item_name = 'clo_earmuffs_purple';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_360_white' WHERE item_name = 'clo_earmuffs_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_360_white' WHERE item_name = 'clo_earmuffs_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 360 WHERE item_name = 'clo_earmuffs_white';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_530_blue' WHERE item_name = 'clo_flower_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_530_blue' WHERE item_name = 'clo_flower_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 530 WHERE item_name = 'clo_flower_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_530_pink' WHERE item_name = 'clo_flower_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_530_pink' WHERE item_name = 'clo_flower_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 530 WHERE item_name = 'clo_flower_pink';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_530_green' WHERE item_name = 'clo_sunglass_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_530_green' WHERE item_name = 'clo_sunglass_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 530 WHERE item_name = 'clo_sunglass_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_530_purple' WHERE item_name = 'clo_sunglass_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_530_purple' WHERE item_name = 'clo_sunglass_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 530 WHERE item_name = 'clo_sunglass_purple';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_hair_530_yellow' WHERE item_name = 'clo_sunglass_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_hair_530_yellow' WHERE item_name = 'clo_sunglass_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 530 WHERE item_name = 'clo_sunglass_yellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_220_black' WHERE item_name = 'clo_running_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_220_black' WHERE item_name = 'clo_running_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_running_black';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_220_green' WHERE item_name = 'clo_running_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_220_green' WHERE item_name = 'clo_running_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_running_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_220_red' WHERE item_name = 'clo_running_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_220_red' WHERE item_name = 'clo_running_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_running_red';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_220_white' WHERE item_name = 'clo_running_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_220_white' WHERE item_name = 'clo_running_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_running_white';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_220_yellow' WHERE item_name = 'clo_running_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_220_yellow' WHERE item_name = 'clo_running_yellow';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_running_yellow';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_360_black' WHERE item_name = 'clo_train_running_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_360_black' WHERE item_name = 'clo_train_running_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 360 WHERE item_name = 'clo_train_running_black';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_360_pink' WHERE item_name = 'clo_train_running_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_360_pink' WHERE item_name = 'clo_train_running_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 360 WHERE item_name = 'clo_train_running_pink';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_360_green' WHERE item_name = 'clo_train_running_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_360_green' WHERE item_name = 'clo_train_running_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 360 WHERE item_name = 'clo_train_running_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_510_blue' WHERE item_name = 'clo_shirt_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_510_blue' WHERE item_name = 'clo_shirt_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 510 WHERE item_name = 'clo_shirt_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_510_pink' WHERE item_name = 'clo_shirt_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_510_pink' WHERE item_name = 'clo_shirt_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 510 WHERE item_name = 'clo_shirt_pink';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_550_blue' WHERE item_name = 'clo_vest_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_550_blue' WHERE item_name = 'clo_vest_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 550 WHERE item_name = 'clo_vest_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_550_purple' WHERE item_name = 'clo_vest_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_550_purple' WHERE item_name = 'clo_vest_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 550 WHERE item_name = 'clo_vest_purple';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_600_green' WHERE item_name = 'clo_cape_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_600_green' WHERE item_name = 'clo_cape_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 600 WHERE item_name = 'clo_cape_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_600_orange' WHERE item_name = 'clo_cape_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_600_orange' WHERE item_name = 'clo_cape_orange';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 600 WHERE item_name = 'clo_cape_orange';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_600_pink' WHERE item_name = 'clo_cape_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_600_pink' WHERE item_name = 'clo_cape_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 600 WHERE item_name = 'clo_cape_pink';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_upper_600_red' WHERE item_name = 'clo_cape_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_upper_600_red' WHERE item_name = 'clo_cape_red';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 600 WHERE item_name = 'clo_cape_red';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_190_black' WHERE item_name = 'clo_running_bottom_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_190_black' WHERE item_name = 'clo_running_bottom_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_running_bottom_black';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_190_blue' WHERE item_name = 'clo_running_bottom_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_190_blue' WHERE item_name = 'clo_running_bottom_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_running_bottom_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_190_purple' WHERE item_name = 'clo_running_bottom_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_190_purple' WHERE item_name = 'clo_running_bottom_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_running_bottom_purple';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_190_white' WHERE item_name = 'clo_running_bottom_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_190_white' WHERE item_name = 'clo_running_bottom_white';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 190 WHERE item_name = 'clo_running_bottom_white';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_220_black' WHERE item_name = 'clo_train_running_bottom_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_220_black' WHERE item_name = 'clo_train_running_bottom_black';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_train_running_bottom_black';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_220_pink' WHERE item_name = 'clo_train_running_bottom_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_220_pink' WHERE item_name = 'clo_train_running_bottom_pink';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_train_running_bottom_pink';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_220_green' WHERE item_name = 'clo_train_running_bottom_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_220_green' WHERE item_name = 'clo_train_running_bottom_green';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_train_running_bottom_green';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_220_blue' WHERE item_name = 'clo_skirt_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_220_blue' WHERE item_name = 'clo_skirt_blue';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_skirt_blue';")

            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET bg_pic = 'bg_clo_bottom_220_purple' WHERE item_name = 'clo_skirt_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET market_pic = 'market_clo_bottom_220_purple' WHERE item_name = 'clo_skirt_purple';")
            sqlitedb.execSQL("UPDATE hamster_deco_info_db SET price = 220 WHERE item_name = 'clo_skirt_purple';")

            dbManager.close()
            sqlitedb.close()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "데이터 로딩에 실패했습니다", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity :: 오류", e.printStackTrace().toString())
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
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
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

                            // drawer menu swipe 활성화
                            binding.homeDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                            // 배경 및 가구 업데이트
                            FunUpDateHamzzi.updateBackground(this@MainActivity, binding.backgroundLayout, false)

                            // isHome 플래그 올리기
                            isHome = true
                        }
                        // 목표 설정 페이지
                        "setUp" -> {
                            binding.titleTextView.setText("목표 설정")
                        }
                        // 목표 리포트 페이지
                        "dailyReport" -> {
                            binding.titleTextView.setText("목표 리포트")
                        }
                        // 나의 성취 앨범 페이지
                        "dailyAlbum" -> {
                            binding.titleTextView.setText("나의 성취 앨범")
                        }
                        // 명예의 전당 페이지
                        "fame" -> {
                            binding.titleTextView.setText("명예의 전당")
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

        // drawer menu swipe 비활성화
        binding.homeDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // 상단바 색 적용
        binding.backgroundLayout.removeAllViews()   // backgroundLayout에 있는 배경과 가구를 모두 제거
        binding.backgroundLayout.setBackgroundResource(R.color.Yellow)

        // isHome 플래그 내리기
        isHome = false
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

            // drawer menu swipe 활성화
            binding.homeDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            // 배경 및 가구 업데이트
            FunUpDateHamzzi.updateBackground(this@MainActivity, binding.backgroundLayout, false)

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
                // 목표 설정 탭으로 전환
                transaction.replace(R.id.fragment_main, SetupFragment(), "setUp")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                binding.titleTextView.setText("목표 설정")

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
            R.id.action_fame -> {
                // 명예의 전당 탭으로 전환
                transaction.replace(R.id.fragment_main, FameFragment(), "fame")
                transaction.addToBackStack(null)
                transaction.commit()

                // 타이틀 텍스트 변경
                binding.titleTextView.setText("명예의 전당")

                // Navigation Drawer 닫기
                binding.homeDrawerLayout.closeDrawers()
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
            R.id.action_goalAndTime, R.id.action_report, R.id.action_fame,
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

    // shared preferences 내의 데이터를 초기화하는 함수
    private fun setSharedPreferencesDB() {
        var spf: SharedPreferences = getSharedPreferences("RecordTime", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = spf.edit()

        // 각 데이터를 0으로 초기화
        editor.putString("recordDate", "0")
        editor.putString("recordedTime", "0")
        editor.putString("beforeTime", "0")
        editor.putBoolean("isPause", false)

        editor.apply()
    }

    // 상단의 툴바의 색상을 바꾸는 함수(팝업용)
    fun hideTopToolbar(isHide: Boolean) {
        if (isHide) {
            binding.mainToolbarCLayout.setBackgroundResource(R.color.BlackTransparent)
        } else {
            binding.mainToolbarCLayout.setBackgroundResource(R.color.Transparent)
        }
    }
}
