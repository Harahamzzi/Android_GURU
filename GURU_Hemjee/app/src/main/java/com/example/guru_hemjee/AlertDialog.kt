package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class AlertDialog(context: Context, title: String, okString: String, isNeedDrawable: Boolean, val talkText: String?) {
    private val dialog = Dialog(context)

    private var title: String = title
    private var okString: String = okString
    private var isNeedDrawable: Boolean = isNeedDrawable

    private lateinit var popTitleTextView: TextView
    private lateinit var cancelImageButton: ImageButton
    private lateinit var confirmSeedButton: Button
    private lateinit var confirmButton: Button

    private lateinit var hamsterTalkTextView: TextView


    fun AlertDialog(){
        dialog.show()
        dialog.setContentView(R.layout.popup_alert)

        popTitleTextView = dialog.findViewById(R.id.popTitleTextView)
        cancelImageButton = dialog.findViewById(R.id.cancelImageButton)
        confirmSeedButton = dialog.findViewById(R.id.confirmSeedButton)
        confirmButton = dialog.findViewById(R.id.confrimButton)
        hamsterTalkTextView = dialog.findViewById(R.id.alertPopHamsterTalkTextView)

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

        cancelImageButton.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
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