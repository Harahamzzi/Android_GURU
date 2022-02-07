package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton
import android.widget.TextView

class DetailGoalEditDialog(val context: Context, goalName: String, bigGoalName: String) {
    private val dialog = Dialog(context)

    //세부 목표 이름
    private lateinit var goalNameTextView: TextView //세부 목표 이름 텍스트 뷰
    private var goalName = goalName
    private var bigGoalName = bigGoalName

    //버튼들
    private lateinit var editGoalNameImageButton: ImageButton   //이름 수정
    private lateinit var moveGoalImageButton: ImageButton       //목표 이동
    private lateinit var deleteGoalImageButton: ImageButton     //목표 삭제
    private lateinit var popBackImageButton: ImageButton        //뒤로가기

    fun detailGoalEditPopUp() {
        dialog.show()
        dialog.setContentView(R.layout.popup_detail_goal)

        //세부 목표 이름 설정
        goalNameTextView = dialog.findViewById(R.id.detailEditGoalTitleTextView)
        goalNameTextView.text = goalName

        //이름 수정
        editGoalNameImageButton = dialog.findViewById(R.id.editDetailGoalNameImageButton)
        editGoalNameImageButton.setOnClickListener {
            val subDialog = DetailGoalNameDialog(context, goalName)
            subDialog.EditName()

            subDialog.setOnClickedListener(object : DetailGoalNameDialog.ButtonClickListener{
                override fun onClicked(isChanged: Boolean, name: String?) {
                    if(isChanged){
                        goalName = name!!
                        goalNameTextView.text = goalName
                    }
                }
            })
        }

        //목표 이동
        moveGoalImageButton = dialog.findViewById(R.id.moveDetailGoalNameImageButton)
        moveGoalImageButton.setOnClickListener {
            val subDialog = GoalSelectDialog(context, bigGoalName, "목표 이동", false)
            subDialog.goalSelectPop()

            subDialog.setOnClickedListener(object : GoalSelectDialog.ButtonClickListener{
                override fun onClicked(changedBigGoalTitle: String) {
                    bigGoalName = changedBigGoalTitle
                }
            })
        }

        //목표 삭제
        deleteGoalImageButton = dialog.findViewById(R.id.deleteDetailGoalNameImageButton)
        deleteGoalImageButton.setOnClickListener {
            onClickListener.onClicked(true, goalName, bigGoalName)
            dialog.dismiss()
        }

        //돌아가기
        popBackImageButton = dialog.findViewById(R.id.detailGoalPopBackImageButton)
        popBackImageButton.setOnClickListener {
            onClickListener.onClicked(false, goalName, bigGoalName)
            dialog.dismiss()
        }
    }

    interface ButtonClickListener {
        fun onClicked(isDeleted: Boolean, goalName: String, bigGoalName: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}