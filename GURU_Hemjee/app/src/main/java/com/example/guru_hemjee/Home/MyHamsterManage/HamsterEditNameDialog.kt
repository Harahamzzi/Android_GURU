package com.example.guru_hemjee.Home.MyHamsterManage

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.guru_hemjee.R

// 햄찌의 이름을 수정할 때 보이는 팝업 화면
class HamsterEditNameDialog(context: Context, name: String?) {
    private val dialog = Dialog(context)

    //기존 이름 textView
    private lateinit var pop_nameTextView: TextView
    private var name = name

    //수정 이름 editText
    private lateinit var pop_editNameEditText: EditText

    //취소, 확인 버튼
    private lateinit var pop_hamsterCancelImageButton: ImageButton
    private lateinit var pop_nameEditImageButton: ImageButton

    fun EditName() {
        dialog.show()
        dialog.setContentView(R.layout.popup_edit_name)

        //기존 이름
        pop_nameTextView = dialog.findViewById(R.id.pop_nameTextView)
        pop_nameTextView.text = name

        //수정 이름
        pop_editNameEditText = dialog.findViewById(R.id.pop_editNameEditText)

        //확인, 취소 버튼
        pop_hamsterCancelImageButton = dialog.findViewById(R.id.pop_hamsterCancelImageButton)
        pop_nameEditImageButton = dialog.findViewById(R.id.pop_nameEditImageButton)

        //취소 버튼 리스터 연결
        pop_hamsterCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }

        //확인(이름 변경) 버튼 리스터 연결
        pop_nameEditImageButton.setOnClickListener {
            if(pop_editNameEditText.text != null){
                name = pop_editNameEditText.text.toString()
            }
            onClickListener.onClicked(true, name)
            dialog.dismiss()
        }
    }

    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, name: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}