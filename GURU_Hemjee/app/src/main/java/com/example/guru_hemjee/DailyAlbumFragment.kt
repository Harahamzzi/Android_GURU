package com.example.guru_hemjee
import android.widget.Toast


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import androidx.gridlayout.widget.GridLayout
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DailyAlbumFragment : Fragment() {

    // 화면에 보이는 날짜와 시간
    private lateinit var todayTextView: TextView
    private lateinit var totalTimeTextView: TextView

    // 날짜 이동 버튼
    private lateinit var preButton: ImageButton
    private lateinit var nextButton: ImageButton

    // 현재(오늘) 날짜 관련
    private lateinit var todayDate: LocalDateTime   // 오늘 날짜(전체)
    private lateinit var nowDate: LocalDateTime     // 현재 설정된 날짜

    // 앨범 사진들이 들어갈 레이아웃
    private lateinit var albumGridLayout: GridLayout

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_album, container, false)

    }

    override fun onStart() {
        super.onStart()

        // 오늘 날짜 불러오기
        todayDate = LocalDateTime.now()
        todayDate = todayDate.plusHours(9)   // 9시간을 더해 한국 시간과 맞춤

        // 위젯 연결
        todayTextView = requireView().findViewById(R.id.dailyAlbum_dateTextView)
        totalTimeTextView = requireView().findViewById(R.id.dailyAlbum_timeTextView)

        albumGridLayout = requireView().findViewById(R.id.dailyAlbum_GridLayout)

        preButton = requireView().findViewById(R.id.dailyAlbum_prevButton)
        nextButton = requireView().findViewById(R.id.dailyAlbum_nextButton)

        // 위젯에 오늘 날짜 입력
        // (현재 날짜를 오늘 날짜로 설정)
        nowDate = todayDate
        todayTextView.text = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // 이전/이후 날짜 이동 버튼 클릭 리스너 설정
        preButton.setOnClickListener {
            nowDate = nowDate.minusDays(1)  // 하루 전 날짜로 변경
            todayTextView.text = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))  // 위젯에 날짜 적용

            // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키기
            applyTotalDailyLockTime()

            // 해당 날짜에서 달성한 목표의 사진을 불러와서 띄우기
            applyTotalDailyPhoto()
        }
        nextButton.setOnClickListener {
            // 현재 설정된 날짜가 오늘 날짜인지 확인
            if(nowDate == todayDate)    // 이동하지 X
                Toast.makeText(requireActivity().applicationContext,"현재 화면이 가장 최근 일자입니다.",Toast.LENGTH_SHORT).show()
            else
            {
                nowDate = nowDate.plusDays(1)   // 하루 후 날짜로 변경
                todayTextView.text = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))  // 위젯에 날짜 적용

                // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키기
                applyTotalDailyLockTime()

                // 해당 날짜에서 달성한 목표의 사진을 불러와서 띄우기
                applyTotalDailyPhoto()
            }
        }


        // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키기
        applyTotalDailyLockTime()

        // 해당 날짜에서 달성한 목표의 사진을 불러와서 띄우기
        applyTotalDailyPhoto()
    }

    // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키는 함수
    private fun applyTotalDailyLockTime() {

        // 해당 날짜 불러오기
        var year: String = todayTextView.text.toString().split("-")[0]
        var month: String = todayTextView.text.toString().split("-")[1]
        var day: String = todayTextView.text.toString().split("-")[2]

        var dayTotalLockTime: Int = 0   // 총 잠금한 시간을 저장할 변수

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 대표 목표 리포트 DB 열기
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db", null)
        while(cursor.moveToNext()) {
            var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

            // 1차 분리 - 날짜와 시간 분리, 날짜 가져오기
            var temp2: String = temp1.split(" ")[0]

            // 2차 분리 - 연도/월/일 분리, 가져오기
            var tempYear: String = temp2.split("-")[0]
            var tempMonth: String = temp2.split("-")[1]
            var tempDay: String = temp2.split("-")[2]

            // 오늘 날짜에 해당하는 데이터만 total_lock 시간 합산
            if(year == tempYear && month == tempMonth && day == tempDay)
            {
                dayTotalLockTime += cursor.getInt(cursor.getColumnIndex("total_lock_time"))
            }
        }

        // 위젯에 totalTime 갱신
        var tempHour = dayTotalLockTime / 1000 / 60 / 60 % 24   // 시간
        var tempMin = dayTotalLockTime / 1000 / 60 % 60         // 분
        var tempSec = dayTotalLockTime / 1000 % 60              // 초

        totalTimeTextView.text = "$tempHour : $tempMin : $tempSec"

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 해당 날짜에서 달성한 목표의 사진을 불러와서 보여주는 함수
    private fun applyTotalDailyPhoto() {

        // 모든 뷰 클리어
        albumGridLayout.removeAllViews()

        // 해당 날짜 불러오기
        var year: String = todayTextView.text.toString().split("-")[0]
        var month: String = todayTextView.text.toString().split("-")[1]
        var day: String = todayTextView.text.toString().split("-")[2]

        // 세부 목표 리포트 DB 열기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE photo_name IS NOT NULL", null)
        cursor.moveToLast() // 맨 끝으로 이동
        cursor.moveToNext() // 한 단계 앞으로(빈 곳을 가리키도록 함)

        while(cursor.moveToPrevious()) {
            var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

            // 1차 분리 - 날짜와 시간 분리, 날짜 가져오기
            var temp2: String = temp1.split(" ")[0]

            // 2차 분리 - 연도/월/일 분리, 가져오기
            var tempYear: String = temp2.split("-")[0]
            var tempMonth: String = temp2.split("-")[1]
            var tempDay: String = temp2.split("-")[2]

            // 해당 날짜에 맞는 데이터만 사진 가져와서 적용시키기
            if(year == tempYear && month == tempMonth && day == tempDay)
            {
                var path = requireContext().filesDir.toString() + "/picture/"
                path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()

                try {
                    // imageView 생성
                    var imageView: ImageView = ImageView(requireContext())

                    var imageViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    imageViewParams.gravity = Gravity.CENTER

                    imageView.layoutParams = imageViewParams

                    var bitmap: Bitmap = BitmapFactory.decodeFile(path)
                    // 이미지 배율 크기 작업 - 360x360 크기로 재설정함
                    var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 360, 360, true)
                    imageView.setImageBitmap(reScaledBitmap)

                    // 레이아웃에 이미지 뷰 넣기
                    albumGridLayout.addView(imageView)
                }
                catch(e: Exception) {
                    Log.e("오류태그", "오늘 사진 로드/세팅 실패 \n${e.printStackTrace()}")
                }
            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }
}