package com.example.guru_hemjee.Home.Album

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import java.lang.IndexOutOfBoundsException

// 나의 성취 앨범(AlbumMainActivity) -> 카테고리별
// 카테고리 분류의 앨범 폴더들을 생성 후 보여주는 Fragment 화면
class CategoryAlbumFragment : Fragment() {

    // 사진을 담을 레이아웃
    private lateinit var albumCategory_CategoryLinearLayout: LinearLayout
    // 저장된 사진이 없을 때 보여줄 레이아웃
    private lateinit var albumCategory_FrameLayout: FrameLayout

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_album, container, false)
    }

    override fun onStart() {
        super.onStart()

//        // 위젯 연결
//        albumCategory_CategoryLinearLayout = requireView().findViewById(R.id.albumCategory_CategoryLinearLayout)
//        albumCategory_FrameLayout = requireView().findViewById(R.id.albumCategory_FrameLayout)
//
//        // 사진 세팅
//        applyCategoryPhoto()
    }

//    // 카테고리별 앨범 사진 세팅하는 함수
//    @SuppressLint("Range")
//    private fun applyCategoryPhoto() {
//
//        // 모든 뷰 제거
//        albumCategory_CategoryLinearLayout.removeAllViews()
//
//        // 사진들을 보여줄 레이아웃 활성화
//        albumCategory_CategoryLinearLayout.visibility = View.VISIBLE
//        // 사진이 없을 때 보여줄 레이아웃 비활성화
//        albumCategory_FrameLayout.visibility = View.GONE
//
//        /** 아이콘 뽑아오기 & 뷰 생성해놓기 **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 세부 목표 리포트 DB 열기 - icon 가져오기 위함(중복 데이터 제거해서 들고 옴)
//        var cursor: Cursor = sqlitedb.rawQuery("SELECT DISTINCT icon FROM detail_goal_time_report_db", null)
//
//        // icon 값을 저장할 ArrayList
//        var iconList = ArrayList<Int>()
//
//        //사진 개수를 저장할 ArrayList
//        var picNums = ArrayList<Int>()
//
//        while(cursor.moveToNext())
//        {
//            // view goalAlbumLayout에 부풀리기
//            var view: View = layoutInflater.inflate(R.layout.container_big_album, albumCategory_CategoryLinearLayout, false)
//
//            // 아이콘 변경
//            var icon: ImageView = view.findViewById(R.id.bigAlbum_albumIconImageView)
//            icon.setImageResource(cursor.getInt(cursor.getColumnIndex("icon")))
//
//            // icon 값 받아와서 저장하기
//            iconList.add(cursor.getInt(cursor.getColumnIndex("icon")))
//
//            // icon 값 tag로 저장하기
//            view.setTag(cursor.getInt(cursor.getColumnIndex("icon")))
//
//            // view에 클릭 리스너 달기
//            view.setOnClickListener {
////                // Spinner 숨기기
////                var spinner: Spinner = requireActivity().findViewById(R.id.albumMenuSpinner)
////                spinner.visibility = View.GONE
//
//                // 번들 생성(보낼 값 세팅)
//                var bundle = Bundle()
//                bundle.putString("flag", "CATEGORY")    // 카테고리 플래그
//                bundle.putInt("icon", view.tag as Int)       // 아이콘 값 전달
//
//                var fragment = SelectAlbumFragment()
//                fragment.arguments = bundle
//
//                // 해당 카테고리의 전체 앨범 펼치기
//                var transaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.fragment_main, fragment)
//                transaction.commit()
//            }
//
//            // view 추가
//            albumCategory_CategoryLinearLayout.addView(view)
//
//            //사진 개수 추가
//            picNums.add(0)
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 해당 아이콘 앨범에 맞는 이미지 적용시키기 **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 세부 목표 리포트
//        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db", null)
//
//        cursor.moveToLast() // 최근 데이터를 가져오기 위해 맨 마지막으로 커서 이동
//        cursor.moveToNext() // 다음 단계로 한 칸 이동(빈 곳을 가리키도록 함)
//
//        while(cursor.moveToPrevious())
//        {
//            /** 어떤 아이콘인지 구분하기 **/
//            var tempIcon: Int = cursor.getInt(cursor.getColumnIndex("icon"))
//            // 몇 번째 아이콘인지 뽑아오기
//            var iconNum: Int = iconList.indexOf(tempIcon)
//            // 해당 뷰 연결
//            var view: View = albumCategory_CategoryLinearLayout.get(iconNum)
//
//            //몇 번째 사진 인지. 3개의 사진이 이미 들어갔다면 사진 추가를 하지 않는다.
//            if(++picNums[iconNum] >= 4)
//                continue
//
//            /** 해당 카테고리에 사진 추가 **/
//            var path = requireContext().filesDir.toString() + "/picture/"
//            path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()
//
//            try {
//                var bitmap: Bitmap = BitmapFactory.decodeFile(path)
//                // 이미지 배율 크기 작업 - 156x155 크기로 재설정함
//                var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 156, 155, true)
//
//                var categotyPhoto: ImageView = view.findViewById(resources.getIdentifier("bigAlbum_bigAlbumImageView"
//                        + picNums[iconNum], "id", requireContext().packageName))
//                categotyPhoto.setImageBitmap(reScaledBitmap)
//            }
//            catch(e: Exception) {
//                Log.e("오류태그", "카테고리별 사진 로드/세팅 실패 => 강제 탈출 \n${e.printStackTrace()}")
//                break
//            }
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 현재 사진이 없는 카테고리는 제거 **/
//
//        // 레이아웃에 있는 View 하나를 제거하면 나머지 View들이 자동으로
//        // 그 빈 공간을 채워 앞으로 당겨지기 때문에
//        // 아래와 같은 삭제 횟수 변수를 사용함
//        var removeCount = 0
//
//        for(index in picNums.indices)
//        {
//            // 해당 카테고리에 들어가 있는 사진이 없다면
//            if(picNums[index - removeCount] == 0)
//            {
//                // 해당 카테고리 폴더를 삭제한다
//                albumCategory_CategoryLinearLayout.removeViewAt(index - removeCount)
//
//                // 삭제한 횟수 증가
//                removeCount++
//            }
//        }
//
//        /** 현재 불러올 사진이 없어 빈 화면이라면 메시지 띄우기 **/
//
//        // 불러올 사진이 없을 경우 categoryAlbumLayout에 담겨있는 View가 없어 Exception이 발생한다.
//        try {
//            albumCategory_CategoryLinearLayout.get(0)
//        }
//        // Exception이 발생했을 시
//        catch(e: IndexOutOfBoundsException) {
//
//            // 사진들을 보여줄 레이아웃 비활성화
//            albumCategory_CategoryLinearLayout.visibility = View.GONE
//            // 사진이 없을 때 보여줄 레이아웃 활성화
//            albumCategory_FrameLayout.visibility = View.VISIBLE
//        }
//    }
}