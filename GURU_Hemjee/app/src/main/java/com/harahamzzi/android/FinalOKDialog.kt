package com.harahamzzi.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

// 최종 확인 Dialog를 띄우기 위한 클래스
class FinalOKDialog(context: Context, title: String, okString: String, isNeedDrawable: Boolean, val picSource: Int?) {
    private val dialog = Dialog(context)

    //팝업 제목, 확인 버튼 글자, 씨앗 표시 유무
    private var title: String = title
    private var okString: String = okString
    private var isNeedDrawable: Boolean = isNeedDrawable

    //팝업의 위젯
    private lateinit var pop_finalOkTitleTextView: TextView //팝업 제목
    private lateinit var pop_plainOkButton: Button //확인 버튼(택스트)
    private lateinit var pop_seedImageView: ImageView   // 타이틀의 씨앗 이미지
    private lateinit var pop_okPopMainImageView: ImageView //햄찌 확인 이미지

    //팝업 표시
    fun alertDialog(){
        dialog.show()

        dialog.setCancelable(false) // 화면 밖 터치시 팝업창이 닫히지 않게 함
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // 배경 색 투명화

        dialog.setContentView(R.layout.popup_final_ok)

        //위젯 연결
        pop_finalOkTitleTextView = dialog.findViewById(R.id.pop_finalOkTitleTextView)
        pop_plainOkButton = dialog.findViewById(R.id.pop_plainOkButton)
        pop_seedImageView = dialog.findViewById(R.id.seedImage)
        pop_okPopMainImageView = dialog.findViewById(R.id.pop_okPopMainImageView)

        //주어진 사진 표시
        if(picSource != null){
            pop_okPopMainImageView.setImageResource(picSource)
        }

        //확인 버튼
        pop_plainOkButton.text = okString

        pop_plainOkButton.setOnClickListener{
            onClickListener.onClicked(true)
            dialog.dismiss()
        }

        // 타이틀
        pop_finalOkTitleTextView.text = title

        if(isNeedDrawable){
            pop_seedImageView.visibility = View.VISIBLE
        } else {
            pop_seedImageView.visibility = View.GONE
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