package com.example.guru_hemjee.Home.Album

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentManager
import com.example.guru_hemjee.Home.MainActivity
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ActivityAlbumMainBinding
import com.example.guru_hemjee.databinding.ActivityMainBinding

// 홈(MainActivity) -> 나의 성취 앨범
// 나의 성취 앨범 Fragment 화면들을 보여주는 Activity 화면
// 스피너를 통해 일간, (대표)목표별, 카테고리별 앨범이 보여진다.
class AlbumMainActivity : AppCompatActivity() {

    // 태그
    private val TAG = "AlbumMainActivity"

    // view binding
    private var mBinding: ActivityAlbumMainBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // Floating Button을 눌렀는지 판단
    private var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAlbumMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // 타이틀 뒤로가기 버튼에 리스너 달기
        binding.subTitleButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // flag 초기화
        isFABOpen = false

        // 서브 Floating Button(1) 클릭 리스너
        binding.fabSub1.setOnClickListener {

            // 현재 버튼 목록이 열려있다면
            if (isFABOpen)
            {
                // 해당 페이지로 이동
                goSelectedAlbum(binding.fabSub1.text.toString())

//                // 버튼의 이름을 선택한 페이지 이름으로 변경
//                var tempName = binding.fabSub1.text
//                binding.fabSub1.text = binding.fabMain.text
//                binding.fabMain.text = tempName

                // 목록 닫기 동작 수행
                closeFloatingButton()
                // flag 내리기
                isFABOpen = false
            }
        }

        // 서브 Floating Button(2) 클릭 리스너
        binding.fabSub2.setOnClickListener {
            // 현재 버튼 목록이 열려있다면
            if (isFABOpen)
            {
                // 해당 페이지로 이동
                goSelectedAlbum(binding.fabSub2.text.toString())

//                // 버튼의 이름을 선택한 페이지 이름으로 변경
//                var tempName = binding.fabSub2.text
//                binding.fabSub2.text = binding.fabMain.text
//                binding.fabMain.text = tempName

                // 목록 닫기 동작 수행
                closeFloatingButton()
                // flag 내리기
                isFABOpen = false
            }
        }

        // 메인 Floating Button(3) 클릭 리스너
        binding.fabMain.setOnClickListener {

            // 현재 FAB 목록이 닫혀있다면
            if (!isFABOpen)
            {
                // 목록 열기 동작 수행
                openFloatingButton()
                // flag 올리기
                isFABOpen = true
            }
            // 현재 FAB 목록이 열려있다면
            else
            {
                // 해당 페이지로 이동
                goSelectedAlbum(binding.fabMain.text.toString())

                // 목록 닫기 동작 수행
                closeFloatingButton()
                // flag 내리기
                isFABOpen = false
            }
        }
    }

    override fun onDestroy() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroy()
    }

    // Floating Button을 눌렀을 때 해당 페이지로 이동하게 하는 메소드
    private fun goSelectedAlbum(btnName: String) {

        var transaction = supportFragmentManager.beginTransaction()

        when(btnName)
        {
            // 일간 앨범으로 이동
            "일간" -> {
                transaction.replace(R.id.fragment_album_main, DailyAlbumFragment())
                transaction.commit()
            }

            // 카테고리별 앨범으로 이동
            "카테고리별" -> {
                transaction.replace(R.id.fragment_album_main, CategoryAlbumFragment())
                transaction.commit()
            }

            // 목표별 앨범으로 이동
            "목표별" -> {
                transaction.replace(R.id.fragment_album_main, GoalAlbumFragment())
                transaction.commit()
            }
        }
    }

    // Floating Button 목록을 펼쳤을 때의 동작
    private fun openFloatingButton() {

        // 애니메이션
        ObjectAnimator.ofFloat(binding.fabSub1, "translationY", -230f).apply { start() }
        ObjectAnimator.ofFloat(binding.fabSub2, "translationY", -115f).apply { start() }
    }

    // Floating Button 목록을 닫았을 때의 동작
    private fun closeFloatingButton() {

        // 애니메이션
        ObjectAnimator.ofFloat(binding.fabSub1, "translationY", 0f).apply { start() }
        ObjectAnimator.ofFloat(binding.fabSub2, "translationY", 0f).apply { start() }
    }
}