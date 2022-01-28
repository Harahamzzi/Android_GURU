package com.example.guru_hemjee

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 대표 목표와 세부 목표 관리를 위한 외부 클래스 생성(DBManager)
class DBManager(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {

        /**사용법
         * ###time 타입: 밀리세컨트를 변환하여 사용
         * var sf = SimpleDateFormat("h:mm:ss")
         * var time = Date(<여기에 밀리세컨드>)
         * var result = sf.format(time)
         * >> result를 입력 바람(result는 sting)
         * #time타입 데이터를 받아와서 밀리세컨드로 변환하는 법
         * (위의 result를 사용한다면)
         * var returnToMilli = sf.parse(result)
         * var milli = returnToMilli.getTime() //밀리초로 받아옴
         *
         * DATE 타입: 위와 마찬가지.
         * var sf = SimpleDateFormat("MM-dd", Locale("ko", "KR"))
         * var time = Date(System.currentTimeMillis())
         * var result = sf.format(time)
         * >> result 를 입력 바람
         * 데이터를 받아와서 변환하는 법은 위와 같음
         *
         * #요일  얻는 법
         * var sf = SimpleDateFormat("E", Locale("ko","KR"))
         * 수요일이면 수, 목요일이면 목 이런식으로 나옴.(String형)
         * **/

        //대표 목표 DB: avaiable app 생략)
        db!!.execSQL("CREATE TABLE big_goal_db (big_goal_name text PRIMARY KEY, " +
                "color INT, big_goal_lock_time time);")

        //세부 목표 DB
        db!!.execSQL("CREATE TABLE detail_goal_db (detail_goal_name text PRIMARY KEY, " +
                "icon text, big_goal_name text);")

        //대표 목표 기록 DB
        db!!.execSQL("CREATE TABLE big_goal_time_report_db (big_goal_name TEXT PRIMARY KEY, " +
                "total_lock_time BIGINT, lock_date DATE, lock_day INT);")

        //세부 목표 기록 DB
        db!!.execSQL("CREATE TABLE detail_goal_time_report_db (detail_goal_name text PRIMARY KEY, " +
                "lock_date DATE, lock_day INT, photo_name TEXT)")

        //기본 정보 DB
        db!!.execSQL("CREATE TABLE basic_info_db (user_name TEXT, hamster_name TEXT, seed INT)")

        //씨앗 상점 아이템 DB
        db!!.execSQL("CREATE TABLE hamster_deco_info_db (item_name TEXT PRIMARY KEY, " +
                "price int, type text, category text, is_bought INT,is_using INT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}