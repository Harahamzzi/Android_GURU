package com.example.guru_hemjee

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.FrameLayout
import android.widget.ImageView

//햄찌 배경 설정
class FunUpDateHamzzi {

    /*햄찌 배경 설정
    * 인자: 나와야하는 Context, 배경을 담을 FrameLayout, 옷을 담을 FrameLayout,
    * 홈화면의 햄찌가 아닌지 맞는지(Boolean: 파일이 다름), 마켓인지 아닌지(Boolean: 가져오는 인자가 다름)
    * 사용법: FunUpDateHamzzi.upDate(Context, FrameLayout, FrameLayout, Boolean, Boolean)
    * */

    companion object{
        private lateinit var dbManager: DBManager
        private lateinit var sqlitedb: SQLiteDatabase

        // 배경 및 가구 업데이트
        @SuppressLint("Range")
        fun updateBackground(context: Context, bgLayout: FrameLayout, isList: Boolean, isMarket: Boolean) {

            //배경 & 가구 설정
            bgLayout.removeAllViews()
            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            //씨앗 상점이 아니면 적용(저장)된 장식만 보여줌.
            val cursor: Cursor =
                if (isMarket)
                    sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'bg'" +
                        " AND is_using = 1 OR type = 'furni' AND is_using = 1",null)
                else
                    sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'bg'" +
                        " AND is_applied = 1 OR type = 'furni' AND is_applied = 1",null)

            while (cursor.moveToNext()) {
                //저장 파일 이름으로 ImageView 설정
                val bgPic = cursor.getString(cursor.getColumnIndex("bg_pic"))
                val id = context.resources!!.getIdentifier(bgPic, "drawable", context.packageName) //파일 id

                val imageView = ImageView(context)
                imageView.setImageResource(id)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                bgLayout.addView(imageView)
            }
            sqlitedb.close()
            dbManager.close()
        }

        // 옷 및 장신구 업데이트
        @SuppressLint("Range", "Recycle")
        fun updateCloth(context: Context, clothLayout: FrameLayout, bottomLayout: FrameLayout, isMarket: Boolean) {

            // 의상 초기화
            clothLayout.removeAllViews()
            bottomLayout.removeAllViews()

            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 씨앗 상점 또는 나의 햄찌 관리 화면이 아니면 적용(저장)된 장식만 보여줌.
            val cursor: Cursor =
                if (isMarket)
                    sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'clo' AND is_using = 1",null)
                else
                    sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'clo' AND is_applied = 1",null)

            while (cursor.moveToNext()) {
                // 저장 파일 이름으로 ImageView 설정
                val bgPic = cursor.getString(cursor.getColumnIndex("bg_pic"))
                val id = context.resources!!.getIdentifier(bgPic, "drawable", context.packageName) //파일 id
                val category = cursor.getString(cursor.getColumnIndex("category")) // 카테고리
                val itemName = cursor.getString(cursor.getColumnIndex("item_name"))

                // 카테고리가 하의라면
                if (category == "bottom") {
                    val imageView = ImageView(context)
                    imageView.setImageResource(id)
                    bottomLayout.addView(imageView)
                }
                else {
                    val imageView = ImageView(context)
                    imageView.setImageResource(id)
                    clothLayout.addView(imageView)
                }
            }
            sqlitedb.close()
            dbManager.close()
        }
    }
}