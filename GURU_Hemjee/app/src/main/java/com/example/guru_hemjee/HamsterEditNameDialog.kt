package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageButton

class HamsterEditNameDialog(context: Context) {
    private val dialog = Dialog(context)

    lateinit var editNameEditText: EditText
    lateinit var hamsterCancelImageButton: ImageButton
    lateinit var nameEditImageButton: ImageButton

    fun EditName() {
        dialog.show()
        dialog.setContentView(R.layout.popup_edit_name)

        editNameEditText = dialog.findViewById(R.id.editNameEditText)
        hamsterCancelImageButton = dialog.findViewById(R.id.hamsterCancelImageButton)
        nameEditImageButton = dialog.findViewById(R.id.nameEditImageButton)

        hamsterCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }

        nameEditImageButton.setOnClickListener {
            onClickListener.onClicked(true, editNameEditText.text.toString())
            dialog.dismiss()
        }
    }

    interface ButtonClickListener {
        fun onClicked(isReduced: Boolean, name: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}