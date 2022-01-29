package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast

//햄찌 배경 설정
class FunUpDateHamzzi {

    /*햄찌 배경 설정
    * 인자: 나와야하는 Context,
    * */

    companion object{
        private lateinit var dbManager: DBManager
        private lateinit var sqlitedb: SQLiteDatabase

        fun upDate(context: Context, bgLayout: FrameLayout, clothLayout: FrameLayout) {
            //배경 & 가구 설정
            bgLayout.removeAllViews()
            dbManager = DBManager(context, "hamster_deco_info_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'bg' AND is_using = 1 OR type = 'furni' AND is_using = 1",null)
            while(cursor.moveToNext()){
                //저장 파일 이름으로 ImageView 설정
                var bgPic = cursor.getString(cursor.getColumnIndex("bg_pic"))
                var id = context.resources!!.getIdentifier(bgPic, "drawable", context?.packageName) //파일 id

                var imageView: ImageView = ImageView(context)
                imageView.setImageResource(id)
                imageView.scaleType = ImageView.ScaleType.FIT_XY

                bgLayout.addView(imageView)
            }
            sqlitedb.close()
            dbManager.close()

            //의상 설정
            clothLayout.removeAllViews()
            dbManager = DBManager(context, "hamster_deco_info_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'clo' AND is_using = 1",null)
            while(cursor.moveToNext()){
                //저장 파일 이름으로 ImageView 설정
                var bgPic = cursor.getString(cursor.getColumnIndex("bg_pic"))
                var id = context.resources!!.getIdentifier(bgPic, "drawable", context?.packageName) //파일 id

                var imageView: ImageView = ImageView(context)
                imageView.setImageResource(id)
                imageView.scaleType = ImageView.ScaleType.FIT_XY

                clothLayout.addView(imageView)
            }
            sqlitedb.close()
            dbManager.close()
        }
    }
}