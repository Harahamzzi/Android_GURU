package com.example.guru_hemjee

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.gridlayout.widget.GridLayout
import java.lang.IndexOutOfBoundsException

//// 나의 성취 앨범(AlbumMainActivity) -> 목표별
//// 대표 목표별 앨범 폴더들을 보여주는 Fragment 화면
//class GoalAlbumFragment : Fragment() {
//
//    //DB 관련
//    private lateinit var dbManager: DBManager
//    private lateinit var sqlitedb: SQLiteDatabase
//
//    // 대표 목표별 앨범 사진이 들어갈 레이아웃
//    private lateinit var albumGoal_albumGoalGridLayout: GridLayout
//
//    // 저장된 사진이 없을 때 보여줄 레이아웃
//    private lateinit var albumGoal_FrameLayout: FrameLayout
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_goal_album, container, false)
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        // 위젯 연결
//        albumGoal_albumGoalGridLayout = requireView().findViewById(R.id.albumGoal_albumGoalGridLayout)
//        albumGoal_FrameLayout = requireView().findViewById(R.id.albumGoal_FrameLayout)
//
//        // 앨범 생성
//        applyBigGoalPhoto()
//    }
//
//    // 대표 목표별 앨범 사진 세팅하는 함수
//    @SuppressLint("Range")
//    private fun applyBigGoalPhoto() {
//
//        // 레이아웃 안의 모든 뷰 제거
//        albumGoal_albumGoalGridLayout.removeAllViews()
//
//        // 사진들을 보여줄 레이아웃 활성화
//        albumGoal_albumGoalGridLayout.visibility = View.VISIBLE
//        // 사진이 없을 때 보여줄 레이아웃 비활성화
//        albumGoal_FrameLayout.visibility = View.GONE
//
//        // column 수를 2으로 세팅
//        albumGoal_albumGoalGridLayout.columnCount = 2
//
//        /** view 동적 생성, 대표 목표 이름 가져와서 세팅하기 - big_goal_time_report_db **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 대표 목표 리포트 DB 열기 - 대표 이름만 중복 제외하고 가져옴
//        var cursor: Cursor =sqlitedb.rawQuery("SELECT DISTINCT big_goal_name FROM big_goal_time_report_db", null)
//
//        // 대표 목표 이름을 저장할 ArrayList
//        var tempBigGoalNameList = ArrayList<String>()
//
//        while(cursor.moveToNext())
//        {
//            // view goalAlbumLayout에 부풀리기
//            var view: View = layoutInflater.inflate(R.layout.container_small_album, albumGoal_albumGoalGridLayout, false)
//
//            // 제목을 대표 목표 이름으로 변경
//            var goalName: TextView = view.findViewById(R.id.smallAlbum_goalNameTextView)
//            goalName.text = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
//
//            // 대표 이름 리스트에 이름 추가
//            tempBigGoalNameList.add(cursor.getString(cursor.getColumnIndex("big_goal_name")).toString())
//
//            // view에 클릭 리스너 달기
//            view.setOnClickListener {
////                // Spinner 숨기기
////                var spinner: Spinner = requireActivity().findViewById(R.id.albumMenuSpinner)
////                spinner.visibility = View.GONE
//
//                // 번들 생성(보낼 값 세팅)
//                var bundle = Bundle()
//                bundle.putString("flag", "GOAL")    // 목표 플래그
//                bundle.putString("goalName", goalName.text.toString())  // 목표이름
//
//                var fragment = SelectAlbumFragment()
//                fragment.arguments = bundle
//
//                // 해당 대표 목표의 전체 앨범 펼치기
//                var transaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.fragment_main, fragment)
//                transaction.commit()
//            }
//
//            // view 추가
//            albumGoal_albumGoalGridLayout.addView(view)
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 대표 목표 색상 가져와서 세팅하기 - big_goal_time_report_db **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 가져온 대표 목표 이름만큼 반복문 돌리기
//        for (index in tempBigGoalNameList.indices)
//        {
//            // 대표 목표 리포트 DB 열기 - 해당 대표 목표의 데이터들만 가져옴
//            cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db WHERE " +
//                    "big_goal_name = '${tempBigGoalNameList[index]}'", null)
//
//            if (cursor.moveToNext())
//            {
//                // 해당 뷰 가져오기
//                var view: View = albumGoal_albumGoalGridLayout.get(index)
//
//                // 해당 뷰의 아이콘 색상을 대표 목표 색상으로 변경
//                var icon: ImageView = view.findViewById(R.id.smallAlbum_iconImageView)
//                icon.setColorFilter(cursor.getInt(cursor.getColumnIndex("color")), PorterDuff.Mode.SRC_IN)
//            }
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 세부 목표 리포트 + 세부 목표 DB에서 사진 뽑아오기 - detail_goal_time_report_db + detail_goal_db **/
//
//        // goalAlbumLayout에 생성한 뷰 개수 가져오기
//        var totalCountIndex: Int = albumGoal_albumGoalGridLayout.childCount - 1
//        var removeCount = 0 // 삭제한 뷰의 개수
//
//        // 0 ~ totalCountIndex만큼 반복
//        for(index in 0..totalCountIndex)
//        {
//            // DB 불러오기
//            dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//            sqlitedb = dbManager.readableDatabase
//
//            // 해당 뷰 가져오기
//            var view: View = albumGoal_albumGoalGridLayout.get(index - removeCount)
//
//            // 대표 목표 이름 가져오기
//            var goalNameTextView: TextView = view.findViewById(R.id.smallAlbum_goalNameTextView)
//            var goalName: String = goalNameTextView.text.toString()
//
//            // 세부 목표 리포트 DB 열기
//            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db"
//                    + " WHERE big_goal_name = '$goalName'", null)
//
//            // 만일 해당 대표 목표에서 저장된 사진이 없다면
//            if(!cursor.moveToNext())
//            {
//                // 해당 대표 목표 폴더 삭제
//                albumGoal_albumGoalGridLayout.removeViewAt(index - removeCount)
//
//                // 삭제한 횟수 증가
//                removeCount++
//            }
//
//            cursor.moveToLast()    // 가장 최근 데이터를 가져오기 위해 커서를 마지막으로 이동
//            cursor.moveToNext()    // 한 단계 앞으로(빈 곳을 가리키도록 함)
//
//            // 세부 목표 리포트에서 파일명 가져와서 이미지 변경하기
//            if(cursor.moveToPrevious())
//            {
//                var path = requireContext().filesDir.toString() + "/picture/"
//                path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()
//
//                try {
//                    var bitmap: Bitmap = BitmapFactory.decodeFile(path)
//                    // 이미지 배율 크기 작업 - 156x156 크기로 재설정함
//                    var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 156, 156, true)
//
//                    var goalPhoto: ImageView = view.findViewById(R.id.smallAlbum_goalAlbumImageView)
//                    goalPhoto.setImageBitmap(reScaledBitmap)
//                }
//                catch(e: Exception) {
//                    Log.e("오류태그", "대표 목표별 사진 로드/세팅 실패 => 강제 탈출 \n${e.printStackTrace()}")
//                    break
//                }
//            }
//
//            cursor.close()
//            sqlitedb.close()
//            dbManager.close()
//
//            /** 대표 목표의 총 잠금한 시간 가져와서 세팅하기 - big_goal_time_report_db **/
//
//            // DB 불러오기
//            dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//            sqlitedb = dbManager.readableDatabase
//
//            // 대표 목표 리포트 DB 열기 - 해당 대표 목표의 데이터만 가져오기
//            cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db WHERE " +
//                    "big_goal_name = '$goalName'", null)
//
//            // 대표 목표별 총 잠금한 시간 변수
//            var totalGoalLockTime: Int = 0
//
//            // 대표 목표 리포트에서 총 잠금한 시간 더하기
//            while(cursor.moveToNext())
//            {
//                totalGoalLockTime += cursor.getInt(cursor.getColumnIndex("total_lock_time"))
//            }
//
//            // 위젯에 totalTime 갱신
//            var tempHour = totalGoalLockTime / 1000 / 60 / 60   // 시간
//            var tempMin = totalGoalLockTime / 1000 / 60 % 60    // 분
//            var tempSec = totalGoalLockTime / 1000 % 60         // 초
//
//            var timeTextView: TextView = view.findViewById(R.id.smallAlbum_timeTextView)
//            timeTextView.text = "$tempHour:$tempMin:$tempSec"
//
//            cursor.close()
//            sqlitedb.close()
//            dbManager.close()
//        }
//
//        /** 현재 불러올 사진이 없어 빈 화면이라면 메시지 띄우기 **/
//
//        // 불러올 사진이 없을 경우 goalAlbumLayout에 담겨있는 View가 없어 Exception이 발생한다.
//        try {
//            albumGoal_albumGoalGridLayout.get(0)
//        }
//        // Exception이 발생했을 시
//        catch(e: IndexOutOfBoundsException) {
//
//            // 사진들을 보여줄 레이아웃 비활성화
//            albumGoal_albumGoalGridLayout.visibility = View.GONE
//            // 사진이 없을 때 보여줄 레이아웃 활성화
//            albumGoal_FrameLayout.visibility = View.VISIBLE
//        }
//    }
//}