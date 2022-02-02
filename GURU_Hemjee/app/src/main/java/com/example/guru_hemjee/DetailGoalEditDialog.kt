package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton

class DetailGoalEditDialog(val context: Context, val goalName: String) {
    private val dialog = Dialog(context)

    //버튼들
    private lateinit var editGoalName: ImageButton
    private lateinit var moveGoal: ImageButton
    private lateinit var deleteGoal: ImageButton

    fun detailGoalEditPopUp() {
    }

    interface ButtonClickListener {
    }
}