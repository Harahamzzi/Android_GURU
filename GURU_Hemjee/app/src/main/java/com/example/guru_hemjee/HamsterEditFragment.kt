package com.example.guru_hemjee

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

class HamsterEditFragment() : Fragment() {

    private lateinit var myHNameEditImageButton: ImageButton
    private lateinit var myHamsterApplyImageButton: ImageButton

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
            hNameEditPopUp()
        }

        //적용 버튼
        myHamsterApplyImageButton = requireView().findViewById(R.id.myHamsterApplyImageButton)
        myHamsterApplyImageButton.setOnClickListener {
            hChangeImagePopUp()
        }
    }

    private fun hNameEditPopUp() {
        val dialog = HamsterEditNameDialog(requireContext())
        dialog.EditName()

        dialog.setOnClickedListener(object : HamsterEditNameDialog.ButtonClickListener{
            override fun onClicked(isChanged: Boolean, name: String?) {
                if(isChanged){
                    //이름 변경한거 db에 반영
                }
            }
        })
    }

    private fun hChangeImagePopUp() {
        val dialog = FinalOK(requireContext(), "적용 완료", "확인", false)
        dialog.alertDialog()

        //햄찌 데코 반영
        dialog.setOnClickedListener(object : FinalOK.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                //생략
            }
        })
    }
}