package com.example.guru_hemjee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentTransaction

// 앨범 fragment 화면들을 보여주는 Activity 화면
// 스피터를 통해 일간, (대표)목표별, 카테고리별 앨범이 보여진다.
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

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

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

        /** 만일 Home 앨범에서 온 경우 **/

        if(intent.getBooleanExtra("isHome", false))
        {
            spinner.visibility = View.GONE

            // Home - 목표 앨범에서 온 경우
            if(intent.getStringExtra("homeFlag") == "GOAL")
            {
                // 번들 생성(보낼 값 세팅)
                var bundle = Bundle()
                bundle.putString("flag", "GOAL")    // 목표 플래그
                bundle.putString("goalName", intent.getStringExtra("goalName"))  // 목표이름

                var fragment = SelectAlbumFragment()
                fragment.arguments = bundle

                // 해당 대표 목표의 전체 앨범 펼치기
                var transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_main, fragment)
                transaction.commit()
            }
            // Home - 카테고리 앨범에서 온 경우
            else if(intent.getStringExtra("homeFlag") == "CATEGORY")
            {
                // 번들 생성(보낼 값 세팅)
                var bundle = Bundle()
                bundle.putString("flag", "CATEGORY")    // 카테고리 플래그
                bundle.putInt("icon", intent.getIntExtra("icon", 0))    // 아이콘 값 전달

                var fragment = SelectAlbumFragment()
                fragment.arguments = bundle

                // 해당 카테고리의 전체 앨범 펼치기
                var transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_main, fragment)
                transaction.commit()
            }
        }
    }

    // (폰)뒤로가기를 눌렀을 때 동작하는 함수 오버라이딩
    override fun onBackPressed() {
        // 만일 홈 앨범에서 왔다면
        if(intent.getBooleanExtra("isHome", false))
        {
            // (폰)뒤로가기를 눌렀을 때 다시 홈으로 돌아가기
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        else
        {
            super.onBackPressed()
        }
    }
}