package com.example.guru_hemjee

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner


class CategoryAlbumFragment : Fragment() {

    // 드롭다운 메뉴
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_album, container, false)
    }

    override fun onStart() {
        super.onStart()

        // spinner 연결
        spinner = requireView().findViewById(R.id.category_albumMenuSpinner)

        // spinner 어댑터 설정
        spinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, resources.getStringArray(R.array.spinnerAlbumList))
        spinner.setSelection(2)

        // spinner 아이템 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()

                when(position){

                    // 목표 선택
                    0 -> {
                        Log.i ("정보태그", "일간 앨범으로 이동했다..")
                        transaction.replace(R.id.fragment_main, DailyAlbumFragment())
                        transaction.commit()
                    }

                    // 카테고리 선택
                    1 -> {
                        Log.i ("정보태그", "목표 앨범으로 이동했다..")
                        transaction.replace(R.id.fragment_main, GoalAlbumFragment())
                        transaction.commit()
                    }
                }
            }
        }
    }
}