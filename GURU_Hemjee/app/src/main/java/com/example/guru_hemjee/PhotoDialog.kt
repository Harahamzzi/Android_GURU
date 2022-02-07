package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.widget.ImageView
import android.widget.TextView

// 앨범 폴더가 아닌 개별 사진을 클릭했을 때 나오는 Dialog
class PhotoDialog(private val context: Context, private val photoPath: String, private val icon: Int,
                  private val detailGoalName: String, private val bigGoalName: String, private val date: String, private val color: Int) {
    private val dialog = Dialog(context)

    //사진
    private lateinit var photoImageView: ImageView

    //아이콘
    private lateinit var iconImageView: ImageView

    //목표 제목들
    private lateinit var detailGoalTextView: TextView
    private lateinit var bigGoalTextView: TextView

    //날짜
    private lateinit var dateTextView: TextView

    fun photoPopUp() {
        dialog.show()
        dialog.setContentView(R.layout.popup_photo_detail)

        //사진
        photoImageView = dialog.findViewById(R.id.photoImageView)
        //아이콘
        iconImageView = dialog.findViewById(R.id.photoIconImageView)
        //목표 제목들
        detailGoalTextView = dialog.findViewById(R.id.photoDetailGoalTextView)
        bigGoalTextView = dialog.findViewById(R.id.photoBigGoalTextView)
        //날짜
        dateTextView = dialog.findViewById(R.id.photoDateTextView)

        //사진 적용
        var bitmap = BitmapFactory.decodeFile(photoPath)
        photoImageView.setImageBitmap(bitmap)

        //아이콘 적용
        iconImageView.setImageResource(icon)
        iconImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        //목표 제목 적용
        detailGoalTextView.text = detailGoalName
        bigGoalTextView.text = bigGoalName

        //날짜 적용
        dateTextView.text = date.replace('-', '.')
    }
}