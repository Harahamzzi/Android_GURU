package com.example.guru_hemjee


import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var menuButton: ImageView

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
        menuButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


//        //lock 화면 연결
//        startButton = findViewById(R.id.startButton)
//        startButton.setOnClickListener {
//            showSettingConfirmPopUp()
//        }


        // fragment 전환을 위한 transaction 생성
        val transaction = supportFragmentManager.beginTransaction()

        // 홈 fragment 띄우기
        transaction.replace(R.id.fragment_main, HomeFragment())
        transaction.commit()
    }

    // Navigation drawer가 열려있을 때 뒤로가기를 처리하는 함수
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers()
        else
            super.onBackPressed()
    }

    // 햄버거 메뉴를 통한 화면 이동
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // fragment 전환을 위한 transaction 생성
        val transaction = supportFragmentManager.beginTransaction()

        when(item.itemId) {
            R.id.action_goalAndTime -> {
                // 목표 및 잠금 시간 설정 탭으로 전환
                transaction.replace(R.id.fragment_main, SetupFragment())
                transaction.addToBackStack(null)
                transaction.commit()

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_report -> {
                // 목표 리포트 탭으로 전환
                transaction.replace(R.id.fragment_main, DailyReportFragment())
                transaction.addToBackStack(null)
                transaction.commit()

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_album -> {
                // 나의 성취 앨범 탭으로 전환
                transaction.replace(R.id.fragment_main, DailyAlbumFragment())
                transaction.addToBackStack(null)
                transaction.commit()

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_store -> {
                // 씨앗 상점 탭으로 전환
                transaction.replace(R.id.fragment_main, SeedMarket())
                transaction.addToBackStack(null)
                transaction.commit()

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_charManagement -> {
                // 나의 햄찌 관리 탭으로 전환
                transaction.replace(R.id.fragment_main, HamsterEditFragment())
                transaction.addToBackStack(null)
                transaction.commit()

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
            R.id.action_preference -> {
                // 설정 탭으로 전환
//                transaction.replace(R.id.fragment_main, 설정탭..())
//                transaction.addToBackStack(null)
//                transaction.commit()

                // Navigation Drawer 닫기
                drawerLayout.closeDrawers()
            }
        }
        return false
    }

//    private fun showSettingConfirmPopUp(){
//        //1차 시도
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.popup_lock_setting_confirm, null)
//        //val hour: TextView = view.findViewById<TextView>(R.id.hourTimeTextView)
//        //val min: TextView = view.findViewById(R.id.minTimeEditText)
//        //val sec: TextView = view.findViewById(R.id.secTimeTextView)
//
//        val alertDialog = AlertDialog.Builder(this)
//            .setTitle("Setting Confirm")
//            .create()
//
//        val settingOkImageButton: ImageButton = view.findViewById(R.id.settingOkImageButton)
//        settingOkImageButton.setOnClickListener {
//            val intent = Intent(this, LockActivity::class.java)
//            startActivity(intent)
//            alertDialog.dismiss()
//        }
//
//        //2차 시도
//        var builder = AlertDialog.Builder(this)
//        builder.setTitle("Setting Confirm")
//        builder.setIcon(R.mipmap.ic_launcher)
//
//        val settingConfirmed = layoutInflater.inflate(R.layout.popup_lock_setting_confirm, null)
//        builder.setView(settingConfirmed)
//
//        var listener = DialogInterface.OnClickListener { dialogInterface, i ->
//            var aler = dialogInterface as AlertDialog
//
//            var settingConfirmButton: ImageButton =
//        }
//
//        builder.show()
//
//        val dialog = LockSettingConfirmDialog(this)
//        dialog.myDig()
//
//
//    }
}
