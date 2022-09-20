package com.example.guru_hemjee.Home.Album

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.gridlayout.widget.GridLayout
import com.example.guru_hemjee.AlertDialog
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentCategoryAlbumBinding
import java.io.File
import java.lang.IndexOutOfBoundsException

// 나의 성취 앨범(AlbumMainActivity) -> 카테고리별
// 카테고리 분류의 앨범 폴더들을 생성 후 보여주는 Fragment 화면
class CategoryAlbumFragment : Fragment() {

    // 태그
    private val TAG = "CategoryAlbumFragment"

    // view binding
    private var mBinding: FragmentCategoryAlbumBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 데이터값 관련
    private var goalIconPairList = ArrayList<Pair<String, String>>()  // 세부목표 이름, icon 이름 쌍을 저장
    private var iconNameList = ArrayList<String>()                        // icon 이름을 저장
//    private var picNums = ArrayList<Int>()                      // 사진 개수를 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        mBinding = FragmentCategoryAlbumBinding.inflate(inflater, container, false)
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
        binding.iconListRadioGroup.removeAllViews()
        binding.pictureListLayout.removeAllViews()

        // 변수 초기화
        for (i in goalIconPairList.indices)
        {
            goalIconPairList.removeFirst()
        }
        for (i in iconNameList.indices)
        {
            iconNameList.removeFirst()
        }

        // 사진들을 보여줄 레이아웃 활성화
        binding.pictureListLayout.visibility = View.VISIBLE
        // 사진이 없을 때 보여줄 레이아웃 비활성화
        binding.noneTextFrameLayout.visibility = View.GONE

        // 세부목표 이름 뽑아오기
        // 아이콘 버튼 목록 및 앨범 그룹 세팅
        applyIconList()

        // 사진 세팅
        applyCategoryPhoto()
    }

    // 아이콘 라디오 버튼 목록 및 앨범 그룹을 세팅하는 함수
    @SuppressLint("Range")
    private fun applyIconList() {

        /** 세부목표 이름 목록 뽑아오기 **/
        var goalNameList = ArrayList<String>()

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 세부 목표 리포트 DB 열기 - 세부목표 이름들을 가져오기 위함(중복 데이터 제거해서 들고 옴)
        var cursor: Cursor = sqlitedb.rawQuery("SELECT DISTINCT detail_goal_name FROM detail_goal_time_report_db", null)

        while (cursor.moveToNext())
        {
            // 세부목표 이름 목록 추가
            goalNameList.add(cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString())
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 아이콘 뽑아오기  **/

        var tempIconNameArrayList = ArrayList<String>()
        lateinit var tempIconNameList: List<String>

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // icon 이름 값 가져오기
        for (i in goalNameList.indices)
        {
            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '${goalNameList[i]}'", null)

            if (cursor.moveToNext())
            {
                // 해당 세부목표 이름 & icon 이름 페어로 저장
                // Pair(세부목표 이름, icon 이름)
                goalIconPairList.add(goalNameList[i] to cursor.getString(cursor.getColumnIndex("icon")).toString())

                // icon 값 받아와서 저장하기
                tempIconNameArrayList.add(cursor.getString(cursor.getColumnIndex("icon")).toString())
            }
        }
        Log.d(TAG, "icon 중복제거 전: $tempIconNameArrayList")

        if (!tempIconNameArrayList.isEmpty())
        {
            // icon 이름 중복 제거
            tempIconNameList = tempIconNameArrayList.distinct()

            for (i in tempIconNameList.indices)
            {
                iconNameList.add(tempIconNameList[i])
            }

            Log.d(TAG, "icon 중복제거 후: $iconNameList")
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 아이콘 라디오 버튼 및 앨범 그룹 생성 **/

        // icon 라디오 버튼 목록 생성하기
        for (i in iconNameList.indices)
        {
            /** 라디오 버튼 생성 **/

            // view goalAlbumLayout에 부풀리기
            var view: View = layoutInflater.inflate(R.layout.container_album_icon_selector, binding.iconListRadioGroup, false)

            // 아이콘 변경
            var radioBtn: RadioButton = view.findViewById(R.id.iconRadioButton)

            var drawable: Drawable?

            // 라디오 버튼의 경우 resize 버전 전달
            if (iconNameList[i] == "dumble_icon")
            {
                drawable = requireContext().getDrawable(DBConvert.iconConvert("dumble_resize", requireContext()))
            }
            else
            {
                drawable = requireContext().getDrawable(DBConvert.iconConvert(iconNameList[i], requireContext()))
            }

//            var bitmap: Bitmap = drawable!!.toBitmap(50, 50)
//            var resizeDrawable: Drawable = BitmapDrawable(resources, bitmap)

            radioBtn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

//            // icon 값 tag로 저장하기
//            radioBtn.setTag(iconNameList[i])

            // view에 클릭 리스너 달기
            radioBtn.setOnClickListener {
                // 현재 스크롤의 위치 구하기
                var originY = binding.scrollView.scrollY

                // 해당 view의 스크린 위에서의 위치 계산
                // 0: x좌표, 1: y좌표
                val viewLocation = IntArray(2)
                val scrollLocation = IntArray(2)

                binding.pictureListLayout[radioBtn.id].getLocationOnScreen(viewLocation)
                binding.scrollView.getLocationOnScreen(scrollLocation)

                // 스크롤 이동값 계산
                var scrollY = originY + viewLocation[1] - scrollLocation[1]

                // 해당 카테고리의 사진들이 있는 위치로 이동
                ObjectAnimator.ofInt(binding.scrollView, "scrollY", scrollY).apply {
                    duration = 500
                    start()
                }
            }

            // 라디오 버튼 아이디 추가
            radioBtn.id = i

            // view(라디오 버튼) 추가
            binding.iconListRadioGroup.addView(radioBtn)

            /** 앨범 그룹 생성 **/
            var viewAlbum: View = layoutInflater.inflate(R.layout.container_category_pictures, binding.pictureListLayout, false)

            // 아이콘 변경
            var icon: ImageView = viewAlbum.findViewById(R.id.container_categoryPictures_iconImageView)
            icon.setImageResource(DBConvert.iconConvert(iconNameList[i], requireContext()))

            // view(앨범 그룹) 추가
            binding.pictureListLayout.addView(viewAlbum)

//            //사진 개수 추가
//            picNums.add(0)
        }
    }

    // 카테고리별 앨범 사진 세팅하는 함수
    @SuppressLint("Range")
    private fun applyCategoryPhoto() {

        /** 해당 아이콘 앨범에 맞는 이미지 적용시키기 **/

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 세부 목표 리포트
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

        cursor.moveToLast() // 최근 데이터를 가져오기 위해 맨 마지막으로 커서 이동
        cursor.moveToNext() // 다음 단계로 한 칸 이동(빈 곳을 가리키도록 함)

        while (cursor.moveToPrevious())
        {
            var detailGoalName: String = cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString()

            for (i in goalIconPairList.indices)
            {
                // 해당 세부목표의 아이콘 가져오기 위함
                if (detailGoalName == goalIconPairList[i].first)
                {
                    /** 어떤 아이콘인지 구분하기 **/
                    // 해당 아이콘이 몇 번째 아이콘인지 뽑아오기
                    var iconNum: Int = iconNameList.indexOf(goalIconPairList[i].second)
                    // 해당 뷰 연결
                    var view: View = binding.pictureListLayout[iconNum]

//                    //몇 번째 사진 인지. 3개의 사진이 이미 들어갔다면 사진 추가를 하지 않는다.
//                    if(++picNums[iconNum] >= 4)
//                        continue

                    /** 해당 카테고리에 사진 추가 **/
                    // 사진 경로 가져오기
                    var path = requireContext().filesDir.toString() + "/picture/"
                    var photoName = cursor.getString(cursor.getColumnIndex("photo_name")).toString()
                    path += photoName

                    // 팝업 사진에 들어갈 정보 가져오기
                    var bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()
                    var date = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
                    var color = ""

                    try {
                        var tempCursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '$bigGoalName'", null)

                        if (tempCursor.moveToNext())
                        {
                            color = tempCursor.getString(tempCursor.getColumnIndex("color")).toString()
                        }

                        tempCursor.close()
                    }
                    catch (e: Exception) {
                        Log.e(TAG, "색상값 가져오기 실패 \n${e.printStackTrace()}")
                    }

                    try {
                        var bitmap: Bitmap = BitmapFactory.decodeFile(path)
                        // 이미지 배율 크기 작업 - 대략 266x256 pixel 크기가 나오도록 재설정함
                        var density = requireActivity().resources.displayMetrics.density    // px = dp * density
                        var pictureWidth = (101 * density).toInt()
                        var pictureHeight = (97 * density).toInt()

                        var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, pictureWidth, pictureHeight, true)

                        var iv = ImageView(requireContext())
                        iv.setImageBitmap(reScaledBitmap)

                        // 이미지 클릭 리스너 적용(폴라로이드 팝업 실행)
                        iv.setOnClickListener {
                            val dialog = PhotoDialog(requireContext(), path, goalIconPairList[i].second, detailGoalName, bigGoalName, date.split(" ")[0], color)
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
                        var categoty: GridLayout = view.findViewById(R.id.container_categoryPictures_GridLayout)
                        categoty.addView(iv)
                    }
                    catch (e: Exception) {
                        Log.e(TAG, "카테고리별 사진 로드/세팅 실패 \n${e.printStackTrace()}")
                    }

                    break
                }
            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

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

        /** 현재 불러올 사진이 없어 빈 화면이라면 메시지 띄우기 **/

        // 불러올 사진이 없을 경우 categoryAlbumLayout에 담겨있는 View가 없어 Exception이 발생한다.
        try {
            binding.pictureListLayout[0]
        }
        // Exception이 발생했을 시
        catch (e: IndexOutOfBoundsException) {

            // 사진들을 보여줄 레이아웃 비활성화
            binding.pictureListLayout.visibility = View.GONE
            // 사진이 없을 때 보여줄 레이아웃 활성화
            binding.noneTextFrameLayout.visibility = View.VISIBLE
        }
    }

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