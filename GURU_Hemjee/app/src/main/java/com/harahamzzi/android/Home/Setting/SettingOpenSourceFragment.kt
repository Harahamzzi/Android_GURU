package com.harahamzzi.android.Home.Setting

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import com.harahamzzi.android.R
import com.harahamzzi.android.databinding.FragmentSettingOpenSourceBinding

class SettingOpenSourceFragment : Fragment(), View.OnClickListener {

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

    override fun onStart() {
        super.onStart()

        binding.toggleButton1.setOnClickListener(this)
        binding.toggleButton2.setOnClickListener(this)
        binding.toggleButton3.setOnClickListener(this)
        binding.toggleButton4.setOnClickListener(this)
        binding.toggleButton5.setOnClickListener(this)
        binding.toggleButton6.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        var toggleButton: ToggleButton = v as ToggleButton

        when (toggleButton.id) {

            R.id.toggleButton1, R.id.toggleButton2, R.id.toggleButton3, R.id.toggleButton4
                , R.id.toggleButton5, R.id.toggleButton6 -> {

                if (toggleButton.isChecked)
                {
                    ObjectAnimator.ofFloat(v, View.ROTATION, 0f, 90f).apply {
                        duration = 300
                        start()
                    }

                    // 라이센스 텍스트 박스 보이기
                    when (toggleButton.id) {

                        R.id.toggleButton1 -> binding.textView1.visibility = View.VISIBLE
                        R.id.toggleButton2 -> binding.textView2.visibility = View.VISIBLE
                        R.id.toggleButton3 -> binding.textView3.visibility = View.VISIBLE
                        R.id.toggleButton4 -> binding.textView4.visibility = View.VISIBLE
                        R.id.toggleButton5 -> binding.textView5.visibility = View.VISIBLE
                        R.id.toggleButton6 -> binding.textView6.visibility = View.VISIBLE
                    }
                }
                else
                {
                    ObjectAnimator.ofFloat(v, View.ROTATION, 90f, 0f).apply {
                        duration = 300
                        start()
                    }

                    // 라이센스 텍스트 박스 감추기
                    when (toggleButton.id) {

                        R.id.toggleButton1 -> binding.textView1.visibility = View.GONE
                        R.id.toggleButton2 -> binding.textView2.visibility = View.GONE
                        R.id.toggleButton3 -> binding.textView3.visibility = View.GONE
                        R.id.toggleButton4 -> binding.textView4.visibility = View.GONE
                        R.id.toggleButton5 -> binding.textView5.visibility = View.GONE
                        R.id.toggleButton6 -> binding.textView6.visibility = View.GONE
                    }
                }
            }
        }
    }
}