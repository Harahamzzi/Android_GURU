package com.harahamzzi.android.Home.Album

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import com.harahamzzi.android.DBConvert
import com.harahamzzi.android.R

// 앨범 폴더가 아닌 개별 사진을 클릭했을 때 나오는 Dialog
class PhotoDialog(private val context: Context, private val photoPath: String, private val iconName: String,
                  private val detailGoalName: String, private val bigGoalName: String,
                  private val date: String, private val colorName: String) {
    private val dialog = Dialog(context)

    //사진
    private lateinit var pop_photoImageView: ImageView

    //아이콘
    private lateinit var pop_photoIconImageView: ImageView

    //목표 제목들
    private lateinit var pop_photoDetailGoalTextView: TextView
    private lateinit var pop_photoBigGoalTextView: TextView

    //날짜
    private lateinit var pop_photoDateTextView: TextView

    //팝업 표시
    fun photoPopUp() {
        dialog.show()
        dialog.setContentView(R.layout.popup_photo_detail)

        //사진
        pop_photoImageView = dialog.findViewById(R.id.pop_photoImageView)
        //아이콘
        pop_photoIconImageView = dialog.findViewById(R.id.pop_photoIconImageView)
        //목표 제목들
        pop_photoDetailGoalTextView = dialog.findViewById(R.id.pop_photoDetailGoalTextView)
        pop_photoBigGoalTextView = dialog.findViewById(R.id.pop_photoBigGoalTextView)
        //날짜
        pop_photoDateTextView = dialog.findViewById(R.id.pop_photoDateTextView)

        //사진 적용
        var bitmap = BitmapFactory.decodeFile(photoPath)
        pop_photoImageView.setImageBitmap(bitmap)

        //아이콘 적용
        pop_photoIconImageView.setImageResource(DBConvert.iconConvert(iconName, context))
        DBConvert.colorConvert(pop_photoIconImageView, colorName, context)

        //목표 제목 적용
        pop_photoDetailGoalTextView.text = detailGoalName
        pop_photoBigGoalTextView.text = bigGoalName

        //날짜 적용
        pop_photoDateTextView.text = date.replace('-', '.')
    }
}