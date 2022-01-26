package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView

class FinalOK(context: Context, title: String, okString: String, isNeedDrawable: Boolean) {
    private val dialog = Dialog(context)

    private var title: String = title
    private var okString: String = okString
    private var isNeedDrawable: Boolean = isNeedDrawable

    private lateinit var popTitleTextView: TextView
    private lateinit var confirmSeedButton: Button
    private lateinit var confirmButton: Button

    fun alertDialog(){
        dialog.show()
        dialog.setContentView(R.layout.popup_final_ok)

        popTitleTextView = dialog.findViewById(R.id.finalOkTitleTextView)
        confirmSeedButton = dialog.findViewById(R.id.okButton)
        confirmButton = dialog.findViewById(R.id.plainOkButton)

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