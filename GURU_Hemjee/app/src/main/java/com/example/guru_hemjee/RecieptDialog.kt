package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton

class ReceiptDialog(context: Context, itemNames: Array<String>?) {
    private val dialog = Dialog(context)

    fun receiptPop(){
        dialog.show()
        dialog.setContentView(R.layout.popup_receipt)

        //영수증에 띄울 것 추가해야함

        val buy = dialog.findViewById<ImageButton>(R.id.okBuyImageButton)
        buy.setOnClickListener {
            onClickListener.onClicked(true)
            dialog.dismiss()
        }

        val cancel = dialog.findViewById<ImageButton>(R.id.receiptCancelImageButton)
        cancel.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }


    }


    interface ButtonClickListener {
        fun onClicked(isBought: Boolean)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}