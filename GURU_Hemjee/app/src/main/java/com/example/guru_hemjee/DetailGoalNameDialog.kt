package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class DetailGoalNameDialog(val context: Context, name: String) {
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
        dialog.setContentView(R.layout.popup_detail_goal_name_edit)

        //기존 이름
        nameTextView = dialog.findViewById(R.id.originDetailGoalNameTextView)
        nameTextView.text = name

        editNameEditText = dialog.findViewById(R.id.detailNameEditText)
        hamsterCancelImageButton = dialog.findViewById(R.id.goalNameEditCancelImageButton)
        nameEditImageButton = dialog.findViewById(R.id.detailGoalNameOkImageButton)

        hamsterCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }

        //이름 설정
        nameEditImageButton.setOnClickListener {
            var changedName = editNameEditText.text.toString()
            if(changedName != ""){
                var isValid = true
                var dbManager = DBManager(context, "hamster_db", null, 1)
                var sqlitedb = dbManager.readableDatabase
                var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db",null)
                while(cursor.moveToNext()){
                    var tempName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
                    if(tempName != name && tempName == changedName)
                        isValid = false
                }
                cursor.close()
                sqlitedb.close()
                dbManager.close()

                if(!isValid){
                    Toast.makeText(context, "이미 있는 목표입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    onClickListener.onClicked(true, changedName)
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(context, "목표를 입력해주세요!", Toast.LENGTH_SHORT).show()
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