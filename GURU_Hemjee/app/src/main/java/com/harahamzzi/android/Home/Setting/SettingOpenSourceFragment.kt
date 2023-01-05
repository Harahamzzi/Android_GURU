package com.harahamzzi.android.Home.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.FragmentSettingOpenSourceBinding

class SettingOpenSourceFragment : Fragment() {

    // 뷰 바인딩 변수
    private var mBinding: FragmentSettingOpenSourceBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mBinding = FragmentSettingOpenSourceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroyView()
    }
}