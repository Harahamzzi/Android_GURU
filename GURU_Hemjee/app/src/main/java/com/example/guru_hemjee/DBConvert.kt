package com.example.guru_hemjee

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.RadioButton
import androidx.core.content.ContextCompat

// 색상 값, 아이콘 값을 받아서 변환하여주는 클래스
class DBConvert {

    /** 파라미터 값 설명
     * view => 색상 값을 적용할 이미지뷰 객체
     * color => DB에 저장된 색상 이름 ex. Orange
     * icon => DB에 저장된 아이콘 이름 ex. ic_photo
     *
     * 사용 방법
     * 1. 색상 값 적용 함수
     * DBConvert.colorConvert(String)
     *
     * 2. 아이콘 아이디 값 반환 함수
     * = 아이콘의 이름으로 drawable폴더에 찾는 아이콘이 있는지 확인 해주는 함수
     * DBConvert.iconConvert(String, Context)
     * 반환 값으로 받은 아이콘 아이디를 통해 이미지 적용하기
     * ex. (객체 이름).setImageResource(iconId)
     **/

    companion object {

        // 이미지뷰 색상 값 적용 함수
        fun colorConvert(view: ImageView, color: String?, context: Context) {
            when (color) {
                "Orange" -> view.setColorFilter(ContextCompat.getColor(context, R.color.Orange))
                "BrightYellow" -> view.setColorFilter(ContextCompat.getColor(context, R.color.BrightYellow))
                "Yellow" -> view.setColorFilter(ContextCompat.getColor(context, R.color.Yellow))
                "Apricot" -> view.setColorFilter(ContextCompat.getColor(context, R.color.Apricot))
                "DarkBrown" -> view.setColorFilter(ContextCompat.getColor(context, R.color.DarkBrown))
                "SeedBrown" -> view.setColorFilter(ContextCompat.getColor(context, R.color.SeedBrown))
                "NoteYellow" -> view.setColorFilter(ContextCompat.getColor(context, R.color.NoteYellow))
                "LightGreen" -> view.setColorFilter(ContextCompat.getColor(context, R.color.LightGreen))
                "Green" -> view.setColorFilter(ContextCompat.getColor(context, R.color.Green))
                "LightBlue" -> view.setColorFilter(ContextCompat.getColor(context, R.color.LightBlue))
                "Blue" -> view.setColorFilter(ContextCompat.getColor(context, R.color.Blue))
                "Purple" -> view.setColorFilter(ContextCompat.getColor(context, R.color.Purple))
                "Pink" -> view.setColorFilter(ContextCompat.getColor(context, R.color.Pink))
            }
        }

        // 라디오버튼 색상 값 적용 함수
        fun radioColorConvert(rBtn: RadioButton, color: String?, context: Context) {
            when (color) {
                "Orange" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Orange))
                "BrightYellow" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.BrightYellow))
                "Yellow" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Yellow))
                "Apricot" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Apricot))
                "DarkBrown" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.DarkBrown))
                "SeedBrown" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.SeedBrown))
                "NoteYellow" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.NoteYellow))
                "LightGreen" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.LightGreen))
                "Green" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Green))
                "LightBlue" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.LightBlue))
                "Blue" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Blue))
                "Purple" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Purple))
                "Pink" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Pink))
                "Gray" -> rBtn.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
            }
        }

        // 라디오버튼 색상 값 적용 함수2
        fun radioButtonColorConvert(rBtn: RadioButton, color: String?, context: Context) {
            when (color) {
                "Orange" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Orange))
                "BrightYellow" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.BrightYellow))
                "Yellow" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Yellow))
                "Apricot" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Apricot))
                "DarkBrown" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.DarkBrown))
                "SeedBrown" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.SeedBrown))
                "NoteYellow" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.NoteYellow))
                "LightGreen" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.LightGreen))
                "Green" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Green))
                "LightBlue" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.LightBlue))
                "Blue" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Blue))
                "Purple" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Purple))
                "Pink" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Pink))
                "Gray" -> rBtn.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.Gray))
            }
        }

        private var iconId: Int = 0

        // 아이콘 아이디 값 반환 함수
        fun iconConvert(icon: String?, context: Context): Int {

            // 아이콘 이름, 아이콘이 저장된 폴더, 아이콘이 저장된 패키지 이름
            iconId = context.resources.getIdentifier(icon, "drawable", context.packageName)

            return iconId
        }
    }
}