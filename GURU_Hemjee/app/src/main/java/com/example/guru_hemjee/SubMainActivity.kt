package com.example.guru_hemjee
import android.util.Log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar

class SubMainActivity : AppCompatActivity() {

    // 타이틀 관련
    lateinit var titleTextView: TextView
    lateinit var titleButton: ImageView

    // fragment 페이지 태그
    private var pageTag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_main)

        // 타이틀 관련 연결
        titleTextView = findViewById(R.id.sub_titleTextView)
        titleButton = findViewById(R.id.sub_titleButton)

        // 타이틀 이름 설정
        titleTextView.text = intent.getStringExtra("titleName")

        // 타이틀 뒤로가기 버튼에 리스너 달기
        titleButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 각 태그에 따라 보여지는 fragment 페이지 설정
        pageTag = intent.getStringExtra("tag")
        val transaction = supportFragmentManager.beginTransaction()

        when(pageTag)
        {
            // 목표/잠금 시간 설정 페이지 띄우기
            "setUp" -> {
                transaction.replace(R.id.fragment_main, SetupFragment())
                transaction.commit()
            }

            // 목표 리포트 페이지 띄우기
            "dailyReport" -> {
                transaction.replace(R.id.fragment_main, DailyReportFragment())
                transaction.commit()
            }

            // 나의 성취 앨범 페이지 띄우기
            "dailyAlbum" -> {
                transaction.replace(R.id.fragment_main, DailyAlbumFragment())
                transaction.commit()
            }

            // 씨앗 상점 페이지 띄우기
            "seedMarket" -> {
                transaction.replace(R.id.fragment_main, SeedMarket())
                transaction.commit()
            }

            // 나의 햄찌 관리 페이지 띄우기
            "hamsterEdit" -> {
                transaction.replace(R.id.fragment_main, HamsterEditFragment())
                transaction.commit()
            }

            // 설정 페이지 띄우기
            "preference" -> {
                Log.i ("정보태그", "설정 페이지..띄워졌다!")
            }
        }
    }

    // (핸드폰)뒤로가기를 눌렀을 때의 동작 함수
    override fun onBackPressed() {
        super.onBackPressed()
    }
}