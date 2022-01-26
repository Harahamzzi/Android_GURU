package com.example.guru_hemjee

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

class HamsterEditFragment(requireContext: Context) : Fragment() {

    lateinit var myHNameEditImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hamster_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //이름 변경 팝업 연결
        myHNameEditImageButton = requireView().findViewById(R.id.myHNameEditImageButton)
        myHNameEditImageButton.setOnClickListener {

        }
    }

    private fun hNameEditPopUp() {
        val dialog = HamsterEditFragment(requireContext())
        dialog.hNameEditPopUp()

        //이름 받아오는거 해야함..
//        dialog.setOn
    }
}