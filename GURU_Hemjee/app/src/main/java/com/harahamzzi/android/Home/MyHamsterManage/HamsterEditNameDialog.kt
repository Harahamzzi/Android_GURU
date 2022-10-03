package com.harahamzzi.android.Home.MyHamsterManage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.harahamzzi.android.R

// 나의 햄찌 관리화면 중 햄찌 이름 변경 팝업
class HamsterEditNameDialog(val context: Context, private val name: String?) {

    fun editName() {
        val dialog = Dialog(context)
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.popup_edit_name)

        // 기존 이름
        val defaultName: TextView = dialog.findViewById(R.id.pop_nameTextView)
        defaultName.text = name

        // 수정할 이름
        val editName: EditText = dialog.findViewById(R.id.pop_editNameEditText)

        // 확인, 취소 버튼
        val cancelBtn: ImageButton = dialog.findViewById(R.id.pop_hamsterCancelImageButton)
        val confirmBtn: ImageButton = dialog.findViewById(R.id.pop_nameEditImageButton)

        // 취소 버튼 클릭 이벤트
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        // 확인(이름 변경) 버튼 클릭 이벤트
        confirmBtn.setOnClickListener {
            when {
                // 기존 이름이랑 같다면
                editName.text.toString() == name -> {
                    Toast.makeText(context, "기존 이름과 동일합니다", Toast.LENGTH_SHORT).show()
                }
                // 기존 이름이랑 다르다면
                editName.text.isNotBlank() -> {
                    val newName = editName.text.toString()
                    onClickListener.onClicked(true, newName)
                    dialog.dismiss()
                }
                // 이름을 안 입력했다면
                else -> {
                    Toast.makeText(context, "변경할 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
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