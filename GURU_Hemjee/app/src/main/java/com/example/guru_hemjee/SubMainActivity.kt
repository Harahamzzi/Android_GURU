package com.example.guru_hemjee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar

//// 홈(MainActivity)의 Navigation Drawer 메뉴 -> SubMainActivity
//// 홈에서 Navigation Drawer Menu로 접근가능한 Fragment 화면들을 보여주는 Activity 화면
//class SubMainActivity : AppCompatActivity() {
//
//    // 타이틀 관련
//    private lateinit var sub_titleTextView: TextView
//    private lateinit var sub_titleButton: ImageView
//
//    // fragment 페이지 태그
//    private var pageTag: String = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sub_main)
//
//        // 액션바 숨기기
//        var actionBar: ActionBar? = supportActionBar
//        actionBar?.hide()
//
//        // 타이틀 관련 연결
//        sub_titleTextView = findViewById(R.id.sub_titleTextView)
//        sub_titleButton = findViewById(R.id.sub_titleButton)
//
//        // 타이틀 이름 설정
//        sub_titleTextView.text = intent.getStringExtra("titleName")
//
//        // 타이틀 뒤로가기 버튼에 리스너 달기
//        sub_titleButton.setOnClickListener {
//            var intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        // 각 태그에 따라 보여지는 fragment 페이지 설정
//        pageTag = intent.getStringExtra("tag")
//        val transaction = supportFragmentManager.beginTransaction()
//
//        when(pageTag)
//        {
//            // 목표/잠금 시간 설정 페이지 띄우기
//            "setUp" -> {
//                transaction.replace(R.id.fragment_main, SetupFragment())
//                transaction.commit()
//            }
//
//            // 목표 리포트 페이지 띄우기
//            "dailyReport" -> {
//                transaction.replace(R.id.fragment_main, DailyReportFragment())
//                transaction.commit()
//            }
//
//            // 나의 성취 앨범은 MainActivity에서 AlbumMainActivity로 가도록 함
//
//            // 씨앗 상점 페이지 띄우기
//            "seedMarket" -> {
//                transaction.replace(R.id.fragment_main, SeedMarketFragment())
//                transaction.commit()
//            }
//
//            // 나의 햄찌 관리 페이지 띄우기
//            "hamsterEdit" -> {
//                transaction.replace(R.id.fragment_main, HamsterEditFragment())
//                transaction.commit()
//            }
//
//            // 설정 페이지 띄우기
//            "preference" -> {
//                transaction.replace(R.id.fragment_main, SettingFragment())
//                transaction.commit()
//            }
//        }
//    }
//}