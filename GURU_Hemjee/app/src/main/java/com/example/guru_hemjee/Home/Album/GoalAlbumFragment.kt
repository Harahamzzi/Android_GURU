package com.example.guru_hemjee.Home.Album

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.gridlayout.widget.GridLayout
import com.example.guru_hemjee.AlertDialog
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentCategoryAlbumBinding
import com.example.guru_hemjee.databinding.FragmentGoalAlbumBinding
import java.io.File
import java.lang.IndexOutOfBoundsException

// 나의 성취 앨범(AlbumMainActivity) -> 목표별
// 대표 목표별 앨범 폴더들을 보여주는 Fragment 화면
class GoalAlbumFragment : Fragment() {

    // 태그
    private val TAG = "GoalAlbumFragment"

    // view binding
    private var mBinding: FragmentGoalAlbumBinding? = null
    // 매번 null 체크를 하지 않아도 되도록 함
    private val binding get() = mBinding!!

    // DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 데이터값 관련
    private var goalNameList = ArrayList<String>()  // 대표목표 이름 목록
    private var colorNameList = ArrayList<String>() // 색상 이름 목록

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        mBinding = FragmentGoalAlbumBinding.inflate(inflater, container, false)
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
        binding.colorIconListRadioGroup.removeAllViews()
        binding.pictureListLayout.removeAllViews()

        // 변수 초기화
        for (i in goalNameList.indices)
        {
            goalNameList.removeFirst()
            colorNameList.removeFirst()
        }

        // 사진들을 보여줄 레이아웃 활성화
        binding.pictureListLayout.visibility = View.VISIBLE
        // 사진이 없을 때 보여줄 레이아웃 비활성화
        binding.noneTextFrameLayout.visibility = View.GONE

        // 라디오 버튼 생성 및 앨범 그룹 생성
        applyIconList()

        // 앨범 생성
        applyBigGoalPhoto()
    }

    // 아이콘 라디오 버튼 목록 및 앨범 그룹을 세팅하는 함수
    @SuppressLint("Range")
    private fun applyIconList() {

        /** 대표목표 이름 목록 뽑아오기 **/

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 세부 목표 리포트 DB 열기 - 대표목표 이름들을 가져오기 위함(중복 데이터 제거해서 들고 옴)
        var cursor: Cursor = sqlitedb.rawQuery("SELECT DISTINCT big_goal_name FROM detail_goal_time_report_db", null)

        while (cursor.moveToNext())
        {
            // 대표목표 이름 목록 추가
            goalNameList.add(cursor.getString(cursor.getColumnIndex("big_goal_name")).toString())
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 색상 뽑아오기  **/

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 색상 이름 값 가져오기
        for (i in goalNameList.indices)
        {
            cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '${goalNameList[i]}'", null)

            if (cursor.moveToNext())
            {
                // 색상 차례대로 저장
                colorNameList.add(cursor.getString(cursor.getColumnIndex("color")).toString())
            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 아이콘 라디오 버튼 및 앨범 그룹 생성 **/

        // color icon 라디오 버튼 목록 생성하기
        for (i in goalNameList.indices)
        {
            /** 라디오 버튼 생성 **/

            // view goalAlbumLayout에 부풀리기
            var view: View = layoutInflater.inflate(R.layout.container_album_color_icon_selector, binding.colorIconListRadioGroup, false)

            // 아이콘 색상 변경
            var radioBtn: RadioButton = view.findViewById(R.id.colorIconRadioButton)
            DBConvert.radioButtonColorConvert(radioBtn, colorNameList[i], requireContext())

//            // 대표목표 값 tag로 저장하기
//            radioBtn.setTag(goalNameList[i])

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
            binding.colorIconListRadioGroup.addView(radioBtn)

            /** 앨범 그룹 생성 **/
            var viewAlbum: View = layoutInflater.inflate(R.layout.container_goal_pictures, binding.pictureListLayout, false)

            // 아이콘 색상 변경
            var colorIcon: ImageView = viewAlbum.findViewById(R.id.container_goalPictures_iconImageView)
            DBConvert.colorConvert(colorIcon, colorNameList[i], requireContext())

            // 대표목표 이름 변경
            var goalTextView: TextView = viewAlbum.findViewById(R.id.container_goalPictures_nameTextView)
            goalTextView.text = goalNameList[i]

            // view(앨범 그룹) 추가
            binding.pictureListLayout.addView(viewAlbum)

//            //사진 개수 추가
//            picNums.add(0)
        }
    }

    // 대표 목표별 앨범 사진 세팅하는 함수
    @SuppressLint("Range")
    private fun applyBigGoalPhoto() {

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
            var bigGoalName: String = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()

            /** 어떤 대표목표인지 구분하기 **/
            // 해당 목표가 몇 번째 대표목표인지 뽑아오기
            var goalNum: Int = goalNameList.indexOf(bigGoalName)
            // 해당 뷰 연결
            var view: View = binding.pictureListLayout[goalNum]

            /** 해당 카테고리에 사진 추가 **/
            // 사진 경로 가져오기
            var path = requireContext().filesDir.toString() + "/picture/"
            var photoName = cursor.getString(cursor.getColumnIndex("photo_name")).toString()
            path += photoName

            // 팝업 사진에 들어갈 정보 가져오기
            var detailGoalName = cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString()
            var date = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
            var icon = ""

            try {
                var tempCursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '$detailGoalName'", null)

                if (tempCursor.moveToNext())
                {
                    icon = tempCursor.getString(tempCursor.getColumnIndex("icon")).toString()
                }

                tempCursor.close()
            }
            catch (e: Exception) {
                Log.e(TAG, "아이콘 이름 가져오기 실패 \n${e.printStackTrace()}")
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
                    val dialog = PhotoDialog(requireContext(), path, icon, detailGoalName, bigGoalName, date.split(" ")[0], colorNameList[goalNum])
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
                var goalAlbumLayout: GridLayout = view.findViewById(R.id.container_goalPictures_GridLayout)
                goalAlbumLayout.addView(iv)
            }
            catch (e: Exception) {
                Log.e(TAG, "목표별 사진 로드/세팅 실패 \n${e.printStackTrace()}")
            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 현재 불러올 사진이 없어 빈 화면이라면 메시지 띄우기 **/

        // 불러올 사진이 없을 경우 goalAlbumLayout에 담겨있는 View가 없어 Exception이 발생한다.
        try {
            binding.pictureListLayout.get(0)
        }
        // Exception이 발생했을 시
        catch(e: IndexOutOfBoundsException) {

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

            override fun onDismiss() {
                // 해당 이미지의 색상 필터 해제
                image.colorFilter = null
            }
        })

        dialog.showAlertDialog()
    }
}