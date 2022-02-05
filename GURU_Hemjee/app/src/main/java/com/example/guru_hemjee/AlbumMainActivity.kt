package com.example.guru_hemjee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentTransaction

class AlbumMainActivity : AppCompatActivity() {

    // 타이틀 관련
    lateinit var titleTextView: TextView
    lateinit var titleButton: ImageView

    // 스피너
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_main)

        // 타이틀 관련 연결
        titleTextView = findViewById(R.id.sub_titleTextView)
        titleButton = findViewById(R.id.sub_titleButton)

        // 타이틀 뒤로가기 버튼에 리스너 달기
        titleButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // spinner 연결
        spinner = findViewById(R.id.albumMenuSpinner)

        // spinner 어댑터 설정
        spinner.adapter = ArrayAdapter.createFromResource(this, R.array.spinnerAlbumList, R.layout.spinner_item)

        // spinner 아이템 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val transaction = supportFragmentManager.beginTransaction()

                when(position){

                    // 일간 선택
                    0 -> {
                        Log.i ("정보태그", "일간 앨범으로 이동했다..")
                        transaction.replace(R.id.fragment_main, DailyAlbumFragment())
                        transaction.commit()
                    }

                    // 목표 선택
                    1 -> {
                        Log.i ("정보태그", "목표 앨범으로 이동했다..")
                        transaction.replace(R.id.fragment_main, GoalAlbumFragment())
                        transaction.commit()
                    }

                    // 카테고리 선택
                    2 -> {
                        Log.i ("정보태그", "카테고리 앨범으로 이동했다..")
                        transaction.replace(R.id.fragment_main, CategoryAlbumFragment())
                        transaction.commit()
                    }

                    // 그 외
                    else -> {
                        Log.e("오류태그", "그 외..스피너 메뉴 눌림")
                    }
                }
            }
        }
    }
}