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

    private lateinit var pop_titleTextView: TextView
    private lateinit var pop_cancelButton: ImageButton
    private lateinit var pop_confirmSeedButton: Button
    private lateinit var pop_confrimButton: Button

    private lateinit var pop_alertPopHamsterTalkTextView: TextView


    fun AlertDialog() {
        dialog.show()
        dialog.setContentView(R.layout.popup_alert)

        pop_titleTextView = dialog.findViewById(R.id.pop_titleTextView)
        pop_cancelButton = dialog.findViewById(R.id.pop_cancelButton)
        pop_confirmSeedButton = dialog.findViewById(R.id.pop_confirmSeedButton)
        pop_confrimButton = dialog.findViewById(R.id.pop_confrimButton)
        pop_alertPopHamsterTalkTextView = dialog.findViewById(R.id.pop_alertPopHamsterTalkTextView)

        if (talkText != null) {
            pop_alertPopHamsterTalkTextView.text = talkText
        }

        pop_titleTextView.text = title
        if (isNeedDrawable) {
            pop_confrimButton.visibility = View.GONE
            pop_confirmSeedButton.text = okString

            pop_confirmSeedButton.setOnClickListener {
                onClickListener.onClicked(true)
                dialog.dismiss()
            }
        } else {
            pop_confirmSeedButton.visibility = View.GONE
            pop_confrimButton.text = okString

            pop_confrimButton.setOnClickListener{
                onClickListener.onClicked(true)
                dialog.dismiss()
            }
        }

        pop_cancelButton.setOnClickListener {
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