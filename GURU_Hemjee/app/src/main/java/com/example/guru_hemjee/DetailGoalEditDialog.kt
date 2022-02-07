package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton
import android.widget.TextView

//세부 목표 수정 팝업(수정, 목표 이동, 삭제)
class DetailGoalEditDialog(val context: Context, goalName: String, bigGoalName: String) {
    private val dialog = Dialog(context)

    //세부 목표 이름
    private lateinit var pop_detailEditGoalTitleTextView: TextView //세부 목표 이름 텍스트 뷰
    private var goalName = goalName
    private var bigGoalName = bigGoalName

    //버튼들
    private lateinit var pop_editDetailGoalNameImageButton: ImageButton   //이름 수정
    private lateinit var pop_moveDetailGoalNameImageButton: ImageButton       //목표 이동
    private lateinit var pop_deleteDetailGoalNameImageButton: ImageButton     //목표 삭제
    private lateinit var pop_detailGoalPopBackImageButton: ImageButton        //뒤로가기

    //팝업 표시
    fun detailGoalEditPopUp() {
        dialog.show()
        dialog.setContentView(R.layout.popup_detail_goal)

        //세부 목표 이름 설정
        pop_detailEditGoalTitleTextView = dialog.findViewById(R.id.pop_detailEditGoalTitleTextView)
        pop_detailEditGoalTitleTextView.text = goalName

        //이름 수정
        pop_editDetailGoalNameImageButton = dialog.findViewById(R.id.pop_editDetailGoalNameImageButton)
        pop_editDetailGoalNameImageButton.setOnClickListener {
            //세부 목표 이름 수정 팝업으로 연결
            val subDialog = DetailGoalNameDialog(context, goalName)
            subDialog.editNamePoPup()

            subDialog.setOnClickedListener(object : DetailGoalNameDialog.ButtonClickListener{
                override fun onClicked(isChanged: Boolean, name: String?) {
                    if(isChanged){
                        goalName = name!!
                        pop_detailEditGoalTitleTextView.text = goalName
                    }
                }
            })
        }

        //목표 이동
        pop_moveDetailGoalNameImageButton = dialog.findViewById(R.id.pop_moveDetailGoalNameImageButton)
        pop_moveDetailGoalNameImageButton.setOnClickListener {
            val subDialog = GoalSelectDialog(context, bigGoalName, "목표 이동", false)
            subDialog.goalSelectPop()

            subDialog.setOnClickedListener(object : GoalSelectDialog.ButtonClickListener{
                override fun onClicked(changedBigGoalTitle: String) {
                    bigGoalName = changedBigGoalTitle
                }
            })
        }

        //목표 삭제
        pop_deleteDetailGoalNameImageButton = dialog.findViewById(R.id.pop_deleteDetailGoalNameImageButton)
        pop_deleteDetailGoalNameImageButton.setOnClickListener {
            onClickListener.onClicked(true, goalName, bigGoalName)
            dialog.dismiss()
        }

        //돌아가기
        pop_detailGoalPopBackImageButton = dialog.findViewById(R.id.pop_detailGoalPopBackImageButton)
        pop_detailGoalPopBackImageButton.setOnClickListener {
            onClickListener.onClicked(false, goalName, bigGoalName)
            dialog.dismiss()
        }
    }

    //인자를 넘겨주기 위한 클릭 인터페이스(팝업을 띄우는 화면에서 처리)
    interface ButtonClickListener {
        fun onClicked(isDeleted: Boolean, goalName: String, bigGoalName: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}