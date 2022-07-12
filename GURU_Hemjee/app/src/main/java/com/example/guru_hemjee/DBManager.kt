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
         * var sf = SimpleDateFormat("hh:mm:ss")
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
                "color text, big_goal_create_time time);")

        //세부 목표 DB
        db!!.execSQL("CREATE TABLE detail_goal_db (detail_goal_name text PRIMARY KEY, " +
                "icon text, count int, big_goal_name text, color text, " +
                "FOREIGN KEY (big_goal_name, color) REFERENCES big_goal_db(big_goal_name, color) ON UPDATE CASCADE);")

        //대표 목표 기록 DB
        db!!.execSQL("CREATE TABLE big_goal_time_report_db (big_goal_name text, " +
                "total_report_time BIGINT, color text, lock_date DATE, " +
                "FOREIGN KEY (big_goal_name) REFERENCES big_goal_db(big_goal_name) ON UPDATE CASCADE);")

        //세부 목표 기록 DB
        db!!.execSQL("CREATE TABLE detail_goal_time_report_db (detail_goal_name text, " +
                "lock_date DATE, color text, icon text, photo_name text, big_goal_name text, is_active INT, is_complete INT, " +
                "CONSTRAINT detail_goal_keys FOREIGN KEY (detail_goal_name, big_goal_name) REFERENCES detail_goal_db(detail_goal_name, big_goal_name) ON UPDATE CASCADE);")

        //완료된 대표 목표 DB
        db!!.execSQL("CREATE TABLE complete_big_goal_db (big_goal_name text PRIMARY KEY, color text, " +
                "big_goal_report_time time, big_goal_created_time time, big_goal_completed_time time)")

        //완료된 세부 목표 DB
        db!!.execSQL("CREATE TABLE complete_detail_goal_db (detail_goal_name text, icon text, " +
                "count int, big_goal_name text)")

        //기본 정보 DB
        db!!.execSQL("CREATE TABLE basic_info_db (hamster_name text, seed INT, total_time time)")

        //씨앗 상점 아이템 DB
        db!!.execSQL("CREATE TABLE hamster_deco_info_db (item_name text PRIMARY KEY, price int, " +
                "type text, category text, bg_pic text, market_pic text, hamster_pic text, is_bought INT, is_using INT, is_applied INT)")
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)

        // SQLite 외래키 권한 활성화
        db!!.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}