package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView

class FinalOK(context: Context, title: String, okString: String, isNeedDrawable: Boolean) {
    private val dialog = Dialog(context)

    var title: String = title
    var okString: String = okString
    var isNeedDrawable: Boolean = isNeedDrawable

    lateinit var popTitleTextView: TextView
    lateinit var confirmSeedButton: Button
    lateinit var confirmButton: Button

    fun AlertDialog(){
        dialog.show()
        dialog.setContentView(R.layout.popup_available_apps)

        popTitleTextView = dialog.findViewById(R.id.finalOkTitleTextView)
        confirmSeedButton = dialog.findViewById(R.id.okButton)
        confirmButton = dialog.findViewById(R.id.plainOkButton)

        popTitleTextView.text = title
        if(isNeedDrawable){
            confirmButton.visibility = View.GONE
            confirmSeedButton.text = okString

            confirmSeedButton.setOnClickListener {
                dialog.dismiss()
            }
        } else {
            confirmSeedButton.visibility = View.GONE
            confirmButton.text = okString
        }


    }
}