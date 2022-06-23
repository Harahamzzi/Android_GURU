package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

// 확인, 취소 버튼이 있는 팝업
class AlertDialog(context: Context, title: String, okString: String, isNeedDrawable: Boolean, val talkText: String?) {
    private val dialog = Dialog(context)

    //팝업 제목, 확인 버튼 글자, 씨앗 표시 유무
    private var title: String = title
    private var okString: String = okString
    private var isNeedDrawable: Boolean = isNeedDrawable

    //팝업의 위젯
    private lateinit var pop_titleTextView: TextView //팝업 제목
    private lateinit var pop_cancelButton: ImageButton //취소 버튼
    private lateinit var pop_confirmSeedButton: Button //확인 버튼(씨앗)
    private lateinit var pop_confrimButton: Button //확인 버튼(텍스트)
    private lateinit var pop_alertPopHamsterTalkTextView: TextView //햄찌 말(말풍선의 말)

    //팝업 표시
    fun AlertDialog() {
        dialog.show()
        dialog.setContentView(R.layout.popup_alert)

        //위젯 연결
        pop_titleTextView = dialog.findViewById(R.id.pop_titleTextView)
        pop_cancelButton = dialog.findViewById(R.id.pop_cancelButton)
        //pop_confirmSeedButton = dialog.findViewById(R.id.pop_confirmSeedButton)
        //pop_confrimButton = dialog.findViewById(R.id.pop_confrimButton)
        //pop_alertPopHamsterTalkTextView = dialog.findViewById(R.id.pop_alertPopHamsterTalkTextView)

        //햄찌 말이 있다면 말풍선 보여줌
        if (talkText != null) {
            pop_alertPopHamsterTalkTextView.text = talkText
        }

        //팝업 제목 설정
        pop_titleTextView.text = title
        //씨앗 표시가 필요하면 씨앗 버튼을 활성화, 아니면 텍스트 버튼을 활성화
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

        //취소 버튼
        pop_cancelButton.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }
    }

    //인자를 넘겨주기 위한 클릭 인터페이스(팝업을 띄우는 화면에서 처리)
    interface ButtonClickListener {
        fun onClicked(isConfirm: Boolean)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

}