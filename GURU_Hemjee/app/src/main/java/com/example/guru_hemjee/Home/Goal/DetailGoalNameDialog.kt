package com.example.guru_hemjee.Home.Goal

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R

//세부 목표 이름 수정 팝업
class DetailGoalNameDialog(val context: Context, name: String) {
    private val dialog = Dialog(context)

    //기존 이름 textView
    private lateinit var pop_originDetailGoalNameTextView: TextView
    private var name = name

    //수정 이름 editText
    private lateinit var pop_detailNameEditText: EditText

    //취소, 확인 버튼
    private lateinit var pop_goalNameEditCancelImageButton: ImageButton
    private lateinit var pop_detailGoalNameOkImageButton: ImageButton

    //팝업 표시
    fun editNamePoPup() {
        dialog.show()
        dialog.setContentView(R.layout.popup_detail_goal_name_edit)

        //기존 이름
        pop_originDetailGoalNameTextView = dialog.findViewById(R.id.pop_originDetailGoalNameTextView)
        pop_originDetailGoalNameTextView.text = name

        //변경 이름
        pop_detailNameEditText = dialog.findViewById(R.id.pop_detailNameEditText)
        pop_goalNameEditCancelImageButton = dialog.findViewById(R.id.pop_goalNameEditCancelImageButton)
        pop_detailGoalNameOkImageButton = dialog.findViewById(R.id.pop_detailGoalNameOkImageButton)

        //취소 버튼
        pop_goalNameEditCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }

        //이름 설정(확인 버튼, 공백이거나 이미 있는 목표면 수정하지 않음)
        pop_detailGoalNameOkImageButton.setOnClickListener {
            var changedName = pop_detailNameEditText.text.toString()
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

    //인자를 넘겨주기 위한 클릭 인터페이스(팝업을 띄우는 화면에서 처리)
    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, name: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}