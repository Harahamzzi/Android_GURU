package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton
import android.widget.TextView

class ReceiptDialog(context: Context, originalSeed: Int, itemNames: Array<String>?) {
    private val dialog = Dialog(context)

    //기존 씨앗
    private var seed = originalSeed
    private lateinit var originalSeedTextView: TextView

    //사용 씨앗
    private lateinit var usedSeedsTextView: TextView

    //결과 씨앗
    private lateinit var remnantSeedsTextView: TextView

    fun receiptPop(){
        dialog.show()
        dialog.setContentView(R.layout.popup_receipt)

        //기존 씨앗
        originalSeedTextView = dialog.findViewById(R.id.originalSeedTextView)
        originalSeedTextView.text = seed.toString()

        //사용 씨앗
        usedSeedsTextView = dialog.findViewById(R.id.usedSeedsTextView)
        usedSeedsTextView.text = "10"

        //결과 씨앗
        remnantSeedsTextView = dialog.findViewById(R.id.remnantSeedsTextView)
        remnantSeedsTextView.text = (originalSeedTextView.text.toString().toInt() -
                usedSeedsTextView.text.toString().toInt()).toString()

        //구매 확인
        val buy = dialog.findViewById<ImageButton>(R.id.okBuyImageButton)
        buy.setOnClickListener {
            onClickListener.onClicked(true, remnantSeedsTextView.text.toString().toInt())
            dialog.dismiss()
        }

        //구매 취소
        val cancel = dialog.findViewById<ImageButton>(R.id.receiptCancelImageButton)
        cancel.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }


    }


    interface ButtonClickListener {
        fun onClicked(isBought: Boolean, seed: Int?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}