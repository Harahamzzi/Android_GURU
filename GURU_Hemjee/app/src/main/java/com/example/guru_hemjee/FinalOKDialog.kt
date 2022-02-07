package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

// 최종 확인 Dialog를 띄우기 위한 클래스
class FinalOKDialog(context: Context, title: String, okString: String, isNeedDrawable: Boolean, val picSource: Int?, val talkText: String?) {
    private val dialog = Dialog(context)

    private var title: String = title
    private var okString: String = okString
    private var isNeedDrawable: Boolean = isNeedDrawable

    private lateinit var popTitleTextView: TextView
    private lateinit var confirmSeedButton: Button
    private lateinit var confirmButton: Button
    private lateinit var hamsterTalkTextView: TextView
    private lateinit var okPopMainImageView: ImageView

    fun alertDialog(){
        dialog.show()
        dialog.setCancelable(false) // 화면 밖 터치시 팝업창이 닫히지 않게 함
        dialog.setContentView(R.layout.popup_final_ok)

        popTitleTextView = dialog.findViewById(R.id.pop_finalOkTitleTextView)
        confirmSeedButton = dialog.findViewById(R.id.pop_okButton)
        confirmButton = dialog.findViewById(R.id.pop_plainOkButton)
        hamsterTalkTextView = dialog.findViewById(R.id.pop_okPopHamsterTalkTextView)
        okPopMainImageView = dialog.findViewById(R.id.pop_okPopMainImageView)

        if(picSource != null){
            okPopMainImageView.setImageResource(picSource)
        }

        if(talkText != null){
            hamsterTalkTextView.text = talkText
        }

        popTitleTextView.text = title
        if(isNeedDrawable){
            confirmButton.visibility = View.GONE
            confirmSeedButton.text = okString

            confirmSeedButton.setOnClickListener {
                onClickListener.onClicked(true)
                dialog.dismiss()
            }
        } else {
            confirmSeedButton.visibility = View.GONE
            confirmButton.text = okString

            confirmButton.setOnClickListener{
                onClickListener.onClicked(true)
                dialog.dismiss()
            }
        }
    }


    interface ButtonClickListener {
        fun onClicked(isConfirm: Boolean)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}