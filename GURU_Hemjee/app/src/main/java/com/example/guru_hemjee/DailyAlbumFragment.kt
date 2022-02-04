package com.example.guru_hemjee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

class DailyAlbumFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_album, container, false)

        // spinner 연결
        val spinner: Spinner = requireView().findViewById(R.id.daily_albumMenuSpinner)

        // spinner 어댑터 설정
        spinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.spinnerAlbumList, android.R.layout.simple_spinner_item)

        // spinner 아이템 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    // 일간 선택
                    0 -> {

                    }

                    // 목표 선택
                    1 -> {

                    }

                    // 카테고리 선택
                    2 -> {

                    }

                    // 일치하는 게 없을 경우
                    else -> {

                    }
                }
            }
        }
    }
}