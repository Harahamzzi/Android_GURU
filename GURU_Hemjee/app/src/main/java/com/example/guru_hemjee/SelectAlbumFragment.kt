package com.example.guru_hemjee
import android.content.Context
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
import androidx.gridlayout.widget.GridLayout
import androidx.activity.OnBackPressedCallback


class SelectAlbumFragment : Fragment() {

    // 위젯
    private lateinit var iconImageView: ImageView
    private lateinit var nameTextView: TextView

    private lateinit var preButton: ImageButton
    private lateinit var nextButton: ImageButton

    private lateinit var pictureGridLayout: GridLayout

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 번들을 통해 전달받은 값
    private lateinit var flag: String
    private lateinit var goalName: String
    private lateinit var categoryName: String

    // (폰) 뒤로가기 처리 콜백
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // (폰) 뒤로가기를 눌렀을 때의 동작
        callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                // Spinner 보이기
                var spinner: Spinner = requireActivity().findViewById(R.id.albumMenuSpinner)
                spinner.visibility = View.VISIBLE

                var transaction = requireActivity().supportFragmentManager.beginTransaction()

                if(flag == "GOAL")
                {   // 목표 앨범으로 이동하도록 함
                    transaction.replace(R.id.fragment_main, GoalAlbumFragment())
                }
                else if(flag == "CATEGORY")
                {   // 카테고리 앨범으로 이동하도록 함
                    transaction.replace(R.id.fragment_main, CategoryAlbumFragment())
                }

                transaction.commit()
            }
        }
        // 콜백 추가
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()

        // 콜백 제거
        callback.remove()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_album, container, false)
    }

    override fun onStart() {
        super.onStart()

        // 위젯 연결
        iconImageView = requireView().findViewById(R.id.select_iconImageView)
        nameTextView = requireView().findViewById(R.id.select_nameTextView)

        preButton = requireView().findViewById(R.id.select_prevButton)
        nextButton = requireView().findViewById(R.id.select_nextButton)

        pictureGridLayout = requireView().findViewById(R.id.select_GridLayout)

        // 다른 fragment로부터 온 값 전달받기
        if(arguments != null)
        {
            flag = arguments?.getString("flag").toString()

            // 플래그(목표/카테고리) 판단
            if(flag == "GOAL")
            {
                // textView 나타내기
                nameTextView.visibility = View.VISIBLE

                goalName = arguments?.getString("goalName").toString()
                nameTextView.text = goalName
            }
            else if(flag == "CATEGORY")
            {
                // textView 숨기기
                nameTextView.visibility = View.INVISIBLE

                // TODO: 카테고리 아이콘 적용
            }
            else
            {
                Log.e("오류태그", "값을 전달받지 못했습니다!")
            }
        }
    }

    // 해당 대표 목표의 전체 사진들을 보여주는 함수
    private fun viewSelectedGoalAlbum()
    {
        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 대표 목표 DB 열기 - 세부 목표 리포트 DB + 세부 목표 DB (해당하는 대표 목표의 세부 목표 관련 데이터들만 가져옴)
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db "
                + " INNER JOIN detail_goal_db USING (detail_goal_name) WHERE big_goal_name = '$goalName'", null)

        cursor.moveToLast()    // 가장 최근 데이터를 가져오기 위해 커서를 마지막으로 이동
        cursor.moveToNext()    // 한 단계 앞으로(빈 곳을 가리키도록 함)

        // 세부 목표 리포트에서 파일명 가져와서 이미지 추가하기
        while(cursor.moveToPrevious())
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
            }
            catch(e: Exception) {
                Log.e("오류태그", "대표 목표별 사진 로드/세팅 실패 => 강제 탈출 \n${e.printStackTrace()}")
                break
            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 해당 카테고리의 전체 사진들을 보여주는 함수
    private fun viewSelectedCategoryAlbum()
    {

    }
}