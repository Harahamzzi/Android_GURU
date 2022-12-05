package com.harahamzzi.android.Home.Setting

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.harahamzzi.android.DBManager
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.FragmentSettingBinding

// 홈(MainActivity) -> 설정 화면
// 설정 Fragment 화면
class SettingFragment : Fragment() {

    // 뷰 바인딩 변수
    private var mBinding: FragmentSettingBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

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

    @SuppressLint("Range")
    override fun onStart() {
        super.onStart()

        // 상단 텍스트 햄찌 이름 적용
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)

        if (cursor.moveToNext())
        {
            binding.settingHamzziNameTextView.text = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

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

            emailIntent.type = "plain/text"

            var receiveEmail: Array<String> = arrayOf("harahamzzi2022@gmail.com")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, receiveEmail)  // 받는 사람 설정
            emailIntent.setPackage("com.google.android.gm")         // Gmail 실행되도록 함

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