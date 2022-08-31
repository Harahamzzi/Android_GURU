package com.example.guru_hemjee.Home.Setting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentSettingBinding

// 홈(MainActivity) -> 설정 화면
// 설정 Fragment 화면
class SettingFragment : Fragment() {

    // 뷰 바인딩 변수
    private var mBinding: FragmentSettingBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        var transaction = requireActivity().supportFragmentManager.beginTransaction()

        // 공지사항 탭 클릭 리스너
        binding.settingNoticeLinearLayout.setOnClickListener {

            // 타이틀 텍스트 바꾸기
            var title: TextView? = activity?.findViewById(R.id.titleTextView)
            title?.text = "공지사항"

            // 공지사항 Fragment 페이지로 이동
            transaction.replace(R.id.fragment_main, SettingNoticeFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // 문의 탭 클릭 리스너
        binding.settingSupportLinearLayout.setOnClickListener {

            // 이메일 앱 켜기
            var emailIntent = Intent(Intent.ACTION_SEND)

            emailIntent.setType("plain/text")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "harahamzzi2022@gmail.com")

            startActivity(emailIntent)
        }

        // 개발진 소개 탭 클릭 리스너
        binding.settingIntroduceLinearLayout.setOnClickListener {

            // 타이틀 텍스트 바꾸기
            var title: TextView? = activity?.findViewById(R.id.titleTextView)
            title?.text = "개발진 소개"

            // 개발진 소개 Fragment 페이지로 이동
            transaction.replace(R.id.fragment_main, SettingIntroduceFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}