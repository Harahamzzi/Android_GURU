package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton

class GoalSelecDialog(context: Context) {
    private var dialog = Dialog(context)

    private lateinit var backImageButton: ImageButton

    fun goalSelectPop(){
        dialog.show()
        dialog.setContentView(R.layout.popup_goal_select)

        backImageButton = dialog.findViewById(R.id.popBackImageButton)
        backImageButton.setOnClickListener {
            onClickListener.onClicked("제목제목")
            dialog.dismiss()
        }

    }

    interface ButtonClickListener {
        fun onClicked(bigGoalTitle: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}