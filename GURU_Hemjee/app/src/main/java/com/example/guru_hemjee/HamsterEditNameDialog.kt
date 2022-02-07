package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

// 햄찌의 이름을 수정할 때 보이는 팝업 화면
class HamsterEditNameDialog(context: Context, name: String?) {
    private val dialog = Dialog(context)

    //기존 이름 textView
    private lateinit var nameTextView: TextView
    private var name = name

    //수정 이름 editText
    private lateinit var editNameEditText: EditText

    //취소, 확인 버튼
    private lateinit var hamsterCancelImageButton: ImageButton
    private lateinit var nameEditImageButton: ImageButton

    fun EditName() {
        dialog.show()
        dialog.setContentView(R.layout.popup_edit_name)

        //기존 이름
        nameTextView = dialog.findViewById(R.id.pop_nameTextView)
        nameTextView.text = name

        editNameEditText = dialog.findViewById(R.id.pop_editNameEditText)
        hamsterCancelImageButton = dialog.findViewById(R.id.pop_hamsterCancelImageButton)
        nameEditImageButton = dialog.findViewById(R.id.pop_nameEditImageButton)

        hamsterCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }

        nameEditImageButton.setOnClickListener {
            if(editNameEditText.text != null){
                name = editNameEditText.text.toString()
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