package com.example.guru_hemjee

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.navigation.NavigationView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 툴바 적용
        val toolbar: Toolbar = findViewById(R.id.toolBar)
        toolbar.setContentInsetsAbsolute(0, 0); // 왼쪽 여백 제거
        setActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu_icon)    //홈 버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안 보이게 설정

        // 네비게이션 드로어 생성
        drawerLayout = findViewById(R.id.home_drawerLayout)

        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        //lock 화면 연결
        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            showSettingConfirmPopUp()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId) {
            // 햄버거 버튼 클릭시 네비게이션 드로어 열기
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_goalAndTime -> {
                // 목표 및 잠금 시간 설정 탭으로 전환
            }
            R.id.action_report -> {
                // 목표 리포트 탭으로 전환
            }
            R.id.action_album -> {
                // 나의 성취 앨범 탭으로 전환
            }
            R.id.action_store -> {
                // 씨앗 상점 탭으로 전환
            }
            R.id.action_charManagement -> {
                // 나의 햄찌 관리 탭으로 전환
            }
            R.id.action_preference -> {
                // 설정 탭으로 전환
            }
        }
        return false
    }

    private fun showSettingConfirmPopUp(){
        //1차 시도
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

        //2차 시도
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

        val dialog = LockSettingConfirmDialog(this)
        dialog.myDig()


    }
}
