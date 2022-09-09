package com.example.guru_hemjee.Home.Album

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.gridlayout.widget.GridLayout
import com.example.guru_hemjee.AlertDialog
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentDailyAlbumBinding
import kotlinx.android.synthetic.main.popup_bottom_summary.*
import java.io.File
import java.lang.IndexOutOfBoundsException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

// 나의 성취 앨범(AlbumMainActivity) -> 일간
// 해당 날짜의 일간 사진과 총 잠금 시간을 보여주는 Fragment 화면
class DailyAlbumFragment : Fragment() {

    // 태그
    private val TAG = "DailyAlbumFragment"

    // view binding
    private var mBinding: FragmentDailyAlbumBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 현재(오늘) 날짜 관련
    private lateinit var todayDate: LocalDateTime   // 오늘 날짜(전체)
    private lateinit var nowDate: LocalDateTime     // 현재 설정된 날짜

    // 그 외
    private var isOpened = true     // 날짜 목록이 펼쳐졌는지 판단하는 플래그

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentDailyAlbumBinding.inflate(inflater, container, false)

        // 오늘 날짜 불러오기
        todayDate = LocalDateTime.now()

        // 위젯에 오늘 날짜 입력
        // (현재 날짜를 오늘 날짜로 설정)
        nowDate = todayDate

        return binding.root
    }

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        // 모든 뷰 제거
        binding.dayOfTheWeekListLayout.removeAllViews()
        binding.dayListRadioGroup.removeAllViews()
        binding.pictureListLayout.removeAllViews()

        // 상단 요일/날짜 목록 및 앨범 그룹 동적 생성
        applyInitList()

        // 사진 추가(세팅)
        applyDailyPhoto()

        // 날짜 목록 열고 닫기 클릭 리스너
        binding.albumDailyOpenImageButton.setOnClickListener {
            // 현재 날짜 목록이 열려있다면
            if (isOpened)
            {
                // 버튼 180도 회전
                ObjectAnimator.ofFloat(binding.albumDailyOpenImageButton, View.ROTATION, 0f, -180f).apply {
                    duration = 600
                    start()
                }

                // 날짜 목록 닫기 애니메이션 실행
                ObjectAnimator.ofFloat(binding.albumDailyBottomConstraintLayout, "translationY", -300f).apply {
                    duration = 600
                    start()
                }
            }
            else
            {
                // 버튼 180도 회전
                ObjectAnimator.ofFloat(binding.albumDailyOpenImageButton, View.ROTATION, -180f, 0f).apply {
                    duration = 600
                    start()
                }

                // 날짜 목록 열기 애니메이션 실행
                ObjectAnimator.ofFloat(binding.albumDailyBottomConstraintLayout, "translationY", 0f).apply {
                    duration = 600
                    start()
                }
            }

            binding.albumDailyTopConstraintLayout.bringToFront()

            isOpened = !isOpened
        }

        // 팝업 캘린더 열기 리스너
        binding.albumDailyMonthTextView.setOnClickListener {

            // 데이터를 가져오기 위한 리스너 설정
            var mListener: DatePickerDialog.OnDateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
                    // 팝업 캘린더에서 선택한 날짜 저장
                    nowDate = nowDate.withYear(year).withMonth(month + 1).withDayOfMonth(day)

                    Log.d(TAG, "선택한 날짜: ${nowDate.year}. ${nowDate.monthValue}. ${nowDate.dayOfMonth}")

                    onStart()
                }
            }

            // 캘린더 띄우기
            var dialog = DatePickerDialog(requireContext(), R.style.MyDatePickerStyle, mListener, nowDate.year, nowDate.monthValue - 1, 1)
            dialog.datePicker.maxDate = System.currentTimeMillis()  // 선택할 수 있는 최대 날짜를 오늘로 두기
            dialog.show()
        }

        // 좌측 버튼(이전 달로 이동) 리스너
        binding.prevButton.setOnClickListener {
            // 한 달 감소
            nowDate = nowDate.minusMonths(1)

            // 모든 뷰 제거
            binding.dayOfTheWeekListLayout.removeAllViews()
            binding.dayListRadioGroup.removeAllViews()
            binding.pictureListLayout.removeAllViews()

            // 상단 요일/날짜 목록 및 앨범 그룹 동적 생성
            applyInitList()

            // 사진 추가(세팅)
            applyDailyPhoto()
        }

        // 우측 버튼(다음 달로 이동) 리스너
        binding.nextButton.setOnClickListener {

            // 설정한 날짜가 오늘 날짜보다 더 이전일 시
            if ((nowDate.year == todayDate.year && nowDate.monthValue < todayDate.monthValue) || (nowDate.year < todayDate.year))
            {
                // 한 달 증가
                nowDate = nowDate.plusMonths(1)

                // 모든 뷰 제거
                binding.dayOfTheWeekListLayout.removeAllViews()
                binding.dayListRadioGroup.removeAllViews()
                binding.pictureListLayout.removeAllViews()

                // 상단 요일/날짜 목록 및 앨범 그룹 동적 생성
                applyInitList()

                // 사진 추가(세팅)
                applyDailyPhoto()
            }
            else
            {
                // 아닐 경우 토스트 메시지 띄움
                Toast.makeText(context, "가장 최근 기록입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 상단 요일 및 날짜 목록 세팅
    private fun applyInitList() {

        // 현재 설정한 날짜를 tempDate로 옮기기
        var tempDate = nowDate

        // 해당 달의 1일로 날짜 변경
        var tempMonthInt: Int = tempDate.monthValue
        tempDate = tempDate.withMonth(tempMonthInt).withDayOfMonth(1)

        // 상단 TextView 변경
        binding.albumDailyMonthTextView.text = tempDate.month.getDisplayName(TextStyle.SHORT, Locale.KOREAN)    // ex) 1월

        // tempDate의 날짜가 다음달로 넘어가면 중단
        while (tempDate.monthValue == tempMonthInt)
        {
            /** 요일 생성 **/
            var view: View = layoutInflater.inflate(R.layout.container_album_day_of_the_week, binding.dayOfTheWeekListLayout, false)

            // 해당 요일 집어넣기
            var dayOfWeekTextView: TextView = view.findViewById(R.id.dayOfTheWeekTextView)
            dayOfWeekTextView.text = tempDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)  // ex) 월

            binding.dayOfTheWeekListLayout.addView(dayOfWeekTextView)

            /** 날짜 생성 **/
            var dayView: View = layoutInflater.inflate(R.layout.container_album_day_selector, binding.dayListRadioGroup, false)

            // 해당 날짜 집어넣기
            var dayRadioButton: RadioButton = dayView.findViewById(R.id.dayRadioButton)
            dayRadioButton.text = tempDate.dayOfMonth.toString()

            // (해당 날짜 - 1)의 값을 id로 적용
            dayRadioButton.id = tempDate.dayOfMonth - 1

            // 클릭 리스너
            dayRadioButton.setOnClickListener {

                // 현재 스크롤의 위치 구하기
                var originY = binding.scrollView.scrollY

                // 해당 view의 스크린 위에서의 위치 계산
                // 0: x좌표, 1: y좌표
                val viewLocation = IntArray(2)
                val scrollLocation = IntArray(2)

                binding.pictureListLayout[dayRadioButton.id].getLocationOnScreen(viewLocation)
                binding.scrollView.getLocationOnScreen(scrollLocation)

                // 스크롤 이동값 계산
                var scrollY = originY + viewLocation[1] - scrollLocation[1]

                // 해당 카테고리의 사진들이 있는 위치로 이동
                ObjectAnimator.ofInt(binding.scrollView, "scrollY", scrollY).apply {
                    duration = 500
                    start()
                }
            }

            binding.dayListRadioGroup.addView(dayRadioButton)

            /** 앨범 그룹 생성 **/
            var viewAlbum: View = layoutInflater.inflate(R.layout.container_daily_pictures, binding.pictureListLayout, false)

            // 일자 변경
            var pictureDayTextView: TextView = viewAlbum.findViewById(R.id.container_dailyPictures_dayTextView)
            pictureDayTextView.text = tempDate.dayOfMonth.toString()

            // 요일 변경
            var pictureDayOfTheWeekTextView: TextView = viewAlbum.findViewById(R.id.container_dailyPictures_dayOfWeekTextView)
            pictureDayOfTheWeekTextView.text = tempDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)    // ex) 월요일

            binding.pictureListLayout.addView(viewAlbum)

            /** 다음날로 넘어가기 **/
            tempDate = tempDate.plusDays(1)
        }
    }

    // 일간 앨범 사진 세팅
    @SuppressLint("Range")
    private fun applyDailyPhoto() {

        // 설정한 날짜의 1일자 가져오기
        var tempDate = nowDate
        var tempMonthInt: Int = tempDate.monthValue
        tempDate = tempDate.withMonth(tempMonthInt).withDayOfMonth(1)

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // tempDate의 날짜가 다음달로 넘어가면 중단
        while (tempDate.monthValue == tempMonthInt)
        {
            var tempDateStr: String = tempDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // 세부 목표 리포트
            // 해당 날짜의 기록만 가져옴
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE lock_date LIKE '$tempDateStr%'", null)

            cursor.moveToLast() // 최근 데이터를 가져오기 위해 맨 마지막으로 커서 이동
            cursor.moveToNext() // 다음 단계로 한 칸 이동(빈 곳을 가리키도록 함)

            while (cursor.moveToPrevious())
            {
                // 해당 뷰 연결
                var view: View = binding.pictureListLayout[tempDate.dayOfMonth - 1]

                /** 해당 일자에 사진 추가 **/

                // 사진 경로 가져오기
                var path = requireContext().filesDir.toString() + "/picture/"
                var photoName = cursor.getString(cursor.getColumnIndex("photo_name")).toString()
                path += photoName

                // 팝업 사진에 들어갈 정보 가져오기
                var bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
                var detailGoalName = cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString()
                var colorName = ""
                var iconName = ""

                try {
                    var tempCursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '$bigGoalName'", null)

                    if (tempCursor.moveToNext())
                    {
                        colorName = tempCursor.getString(tempCursor.getColumnIndex("color")).toString()
                    }

                    tempCursor.close()
                }
                catch (e: Exception) {
                    Log.e(TAG, "색상값 가져오기 실패 \n${e.printStackTrace()}")
                }

                try {
                    var tempCursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '$detailGoalName'", null)

                    if (tempCursor.moveToNext())
                    {
                        iconName = tempCursor.getString(tempCursor.getColumnIndex("icon")).toString()
                    }

                    tempCursor.close()
                }
                catch (e: Exception) {
                    Log.e(TAG, "아이콘값 가져오기 실패 \n${e.printStackTrace()}")
                }

                try {
                    var bitmap: Bitmap = BitmapFactory.decodeFile(path)
                    // 이미지 배율 크기 작업 - 266x256 크기로 재설정함
                    var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 266, 256, true)

                    var iv = ImageView(requireContext())
                    iv.setImageBitmap(reScaledBitmap)

                    // 이미지 클릭 리스너 적용(폴라로이드 팝업 실행)
                    iv.setOnClickListener {
                        val dialog = PhotoDialog(requireContext(), path, iconName, detailGoalName, bigGoalName, tempDateStr, colorName)
                        dialog.photoPopUp()
                    }

                    // 이미지 롱클릭 리스너 적용(사진 삭제 팝업 실행)
                    iv.setOnLongClickListener {
                        // 선택 표시
                        iv.setColorFilter(Color.parseColor("#73000000"), PorterDuff.Mode.SRC_ATOP)

                        // 팝업 띄우기
                        showDeletePopup(iv, photoName, path)
                        return@setOnLongClickListener true
                    }

                    // 해당 레이아웃에 사진 추가
                    var dailyPictureLayout: GridLayout = view.findViewById(R.id.container_dailyPictures_GridLayout)
                    dailyPictureLayout.addView(iv)
                }
                catch (e: Exception) {
                    Log.e(TAG, "일간 사진 로드/세팅 실패 \n${e.printStackTrace()}")
                }
            }

            cursor.close()

            /** 만약 해당 날짜의 사진이 아무것도 없을 시 **/
            var view: View = binding.pictureListLayout[tempDate.dayOfMonth - 1]
            var gridLayout: GridLayout = view.findViewById(R.id.container_dailyPictures_GridLayout)

            // 불러올 사진이 없을 경우 dailyGridLayout에 담겨있는 View가 없어 Exception이 발생한다.
            try {
                gridLayout[0]
            }
            catch (e: IndexOutOfBoundsException) {

                // 해당 View 숨김
                view.visibility = View.GONE
                // 해당 날짜의 라디오 버튼 비활성화
                binding.dayListRadioGroup[tempDate.dayOfMonth - 1].isEnabled = false
                // 해당 날짜의 요일 색 변경
                var temp: View = binding.dayOfTheWeekListLayout[tempDate.dayOfMonth - 1]
                temp.findViewById<TextView>(R.id.dayOfTheWeekTextView).setTextColor(resources.getColor(R.color.DarkGray))
            }

            /** 다음날로 넘어가기 **/
            tempDate = tempDate.plusDays(1)
        }

        sqlitedb.close()
        dbManager.close()
    }

    // 사진 삭제 팝업 실행
    private fun showDeletePopup(image: ImageView, photoName: String, path: String) {
        val dialog = AlertDialog(requireContext(), "해당 사진을 삭제하시겠습니까?", "해당 사진의 수행 기록 또한 삭제되며\n복구는 불가능합니다.", "삭제", 0)

        // 버튼 클릭 리스너
        dialog.setOnClickedListener(object : AlertDialog.ButtonClickListener {
            override fun onClicked(isConfirm: Boolean) {
                // 삭제 버튼을 눌렀을 경우
                if (isConfirm)
                {
                    // 해당 사진 파일 삭제
                    var file = File(path)
                    file.delete()

                    // 세부목표 기록 DB에서 해당 데이터 삭제
                    dbManager = DBManager(context, "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    sqlitedb.execSQL("DELETE FROM detail_goal_time_report_db WHERE photo_name = '$photoName'")

                    sqlitedb.close()
                    dbManager.close()

                    // 다시 세팅하기 위해 onStart() 호출
                    onStart()
                }
                // 취소 버튼을 눌렀을 경우
                else
                {
                    // 해당 이미지의 색상 필터 해제
                    image.colorFilter = null
                }
            }

            // 팝업창 종료시 메소드
            override fun onDismiss() {
                // 해당 이미지의 색상 필터 해제
                image.colorFilter = null
            }
        })

        dialog.showAlertDialog()
    }
}