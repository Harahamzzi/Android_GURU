package com.example.guru_hemjee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner


class HomeReportFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_report, container, false)

        // spinner 연결
        val spinner: Spinner = requireView().findViewById(R.id.goalSpinner)

        // spinner 어댑터 설정
        spinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.spinnerGoalList, android.R.layout.simple_spinner_item)

        // spinner 아이템 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    // 대표 목표 선택
                    0 -> {
                        // 대표 목표 클릭했을 때의..동작
                    }

                    // 세부 목표 선택
                    1 -> {
                        // 세부 목표 클릭했을 때의 동작..
                    }

                    // 일치하는 게 없을 경우
                    else -> {

                    }
                }
            }
        }
    }
}