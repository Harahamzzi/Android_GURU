package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class AlertDialog(context: Context, title: String, okString: String, isNeedDrawable: Boolean) {
    private val dialog = Dialog(context)

    var title: String = title
    var okString: String = okString
    var isNeedDrawable: Boolean = isNeedDrawable

    lateinit var popTitleTextView: TextView
    lateinit var cancelImageButton: ImageButton
    lateinit var confirmSeedButton: Button
    lateinit var confirmButton: Button

    fun AlertDialog(){
        dialog.show()
        dialog.setContentView(R.layout.popup_available_apps)

        popTitleTextView = dialog.findViewById(R.id.popTitleTextView)
        cancelImageButton = dialog.findViewById(R.id.cancelImageButton)
        confirmSeedButton = dialog.findViewById(R.id.confirmSeedButton)
        confirmButton = dialog.findViewById(R.id.confrimButton)

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
        }

        cancelImageButton.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }


    }

    interface ButtonClickListener {
        fun onClicked(isReduced: Boolean)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

}