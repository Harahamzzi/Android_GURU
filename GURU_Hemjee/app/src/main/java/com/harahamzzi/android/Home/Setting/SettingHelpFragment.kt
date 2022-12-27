package com.harahamzzi.android.Home.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.FragmentSettingHelpBinding

// 설정 - 도움말 페이지
class SettingHelpFragment : Fragment() {

    // 뷰 바인딩 변수
    private var mBinding: FragmentSettingHelpBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // 데이터 값 관련 변수
    private var imageNameList = ArrayList<String>()     // 이미지 파일명 목록
    private lateinit var descriptionList: List<String>  // 설명 텍스트 목록

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mBinding = FragmentSettingHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        // 이미지 파일명 넣기
        for (i in 1..6) // 6개
        {
            imageNameList.add("help_picture$i")
        }

        // 설명 넣기
        descriptionList = listOf(
                // 첫 번째 페이지(메인)
                "1. 버튼을 누르거나 오른쪽으로 밀어 페이지 목록을 열 수 있습니다.\n\n" +
                    "2. 현재 보유한 씨앗 포인트 개수입니다.\n\n" +
                    "3. 꾸미기 아이템이 적용되는 위치입니다.\n" +
                    "『씨앗 상점』에서 아이템을 구매할 수 있고, 『나의 햄찌 관리』에서 구매한 아이템을 적용할 수 있습니다.\n\n" +
                    "4. 설정한 대표목표와 세부목표들을 볼 수 있는 목록입니다.\n" +
                    "버튼을 눌러 수행할 세부목표를 선택하고 기록을 시작할 수 있습니다.\n\n" +
                    "5. 버튼을 눌러 오늘의 일간 리포트와 이번주의 요약 리포트를 확인할 수 있습니다.",

                // 두 번째 페이지(기록화면)
                "1. 타이머를 일시정지 할 수 있습니다.\n" +
                    "다시 버튼을 누르면 타이머가 이어서 재생됩니다.\n\n" +
                    "2. 기록을 종료할 수 있습니다.\n\n" +
                    "3. 수행할 세부목표 목록을 확인할 수 있습니다.\n\n" +
                    "4. 3번 버튼을 누르면 보이는 목록입니다.\n" +
                    "카메라 버튼을 눌러 해당 세부목표의 달성 인증 사진을 촬영할 수 있습니다.\n" +
                    "찍은 사진은 『나의 성취 앨범』에서 확인할 수 있습니다.",

                // 세 번째 페이지(목표 설정)
                "1. 대표목표를 추가할 수 있습니다.\n\n" +
                    "2. 가볍게 누르면 해당 대표목표에 추가된 세부목표들을 확인할 수 있습니다.\n" +
                    "길게 누르면 대표목표 수정, 삭제, 완료와 세부목표 추가를 할 수 있습니다.\n" +
                    "목표 완료를 누르면 해당 목표는 목록에서 사라지며 명예의 전당으로 이동합니다.\n\n" +
                    "3. 누르면 해당 세부목표를 수정, 삭제할 수 있습니다.\n\n\n" +
                    "※ 주의사항\n" +
                    "삭제 또는 완료한 목표는 복구할 수 없습니다.",

                // 네 번째 페이지(명예의 전당)
                "1. 왼쪽 또는 오른쪽으로 밀어 각 기준에 제일 알맞은 목표를 확인할 수 있습니다.\n\n" +
                    "2. 지금까지 완료한 목표들을 볼 수 있습니다.\n" +
                    "가볍게 누르면 해당 목표의 총 기록 시간, 기록한 기간과 날짜, 각 세부목표를 수행한 횟수, 인증 사진을 확인할 수 있습니다.",

                // 다섯 번째 페이지(앨범)
                "1. 세부목표 달성 인증 사진을 확인 할 수 있습니다.\n" +
                    "하단 버튼을 누르면 일간, 카테고리 별, 목표 별 앨범에 접근할 수 있습니다.\n\n" +
                    "2. 캘린더를 통해 날짜 이동을 할 수 있습니다.\n\n" +
                    "3. 상단 목록의 버튼을 누르면 해당 주제의 사진 묶음으로 스크롤바가 이동합니다.",

                // 여섯 번재 페이지(리포트)
                "1. 일간, 주간 버튼을 통해 각각 일간 리포트, 주간 리포트를 확인할 수 있습니다.\n\n" +
                    "2. 버튼을 누르면 가장 최근 날짜(상단)로 스크롤바가 이동됩니다."
                )

        // 어댑터 생성
        binding.helpViewPager.adapter = HelpViewPagerAdapter(requireContext(), imageNameList, descriptionList)

        // 인디케이터 설정
        binding.helpIndicator.setViewPager2(binding.helpViewPager)
    }
}