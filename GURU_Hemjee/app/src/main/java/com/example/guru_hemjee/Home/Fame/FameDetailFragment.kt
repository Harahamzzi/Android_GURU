package com.example.guru_hemjee.Home.Fame

import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.databinding.FragmentFameDetailBinding

class FameDetailFragment : Fragment() {

    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

    private var mBinding: FragmentFameDetailBinding? = null // binding변수
    private val binding get() = mBinding!!

    private lateinit var callback: OnBackPressedCallback // 뒤로가기 처리 콜백

    private var fameItem: FameItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 핸드폰 뒤로가기 이벤트
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove() // 콜백 제거
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentFameDetailBinding.inflate(inflater, container, false)

        if (arguments != null) {
            fameItem = requireArguments().getParcelable("fameItem")

            // 뷰에 데이터 적용하기
            DBConvert.colorConvert(binding.containerFameDetailColorIv, fameItem?.color, requireContext())
            binding.containerFameDetailTitleTv.text = fameItem?.title
            binding.containerFameDetailTimeTv.text = fameItem?.time
            binding.containerFameDetailDateTv.text = fameItem?.totalDate + " (" + fameItem?.totalPeriod  + "일)"
        }

        return binding.root
    }

    override fun onDestroyView() {
        // binding class 인스턴트 참조 정리
        mBinding = null
        super.onDestroyView()
    }

    @SuppressLint("Range")
    override fun onStart() {
        super.onStart()

        // 레이아웃 초기화
        binding.containerFameDetailPhotoList.removeAllViews()

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM complete_detail_goal_db WHERE big_goal_name = '${fameItem?.title}' " +
                "AND big_goal_created_time = '${fameItem?.createdDate}';", null)

        var fameDetailList = ArrayList<FameDetailItem>()

        // 세부목표 리스트 띄우기
        try {
            while (cursor.moveToNext()) {
                // 아이콘, 색상, 세부목표 이름, 횟수
                val str_detail_goal = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
                val str_icon = cursor.getString(cursor.getColumnIndex("icon"))
                val int_count = cursor.getInt(cursor.getColumnIndex("count"))

                // 사진 목록 보내기
                if (cursor.getString(cursor.getColumnIndex("photo_name_list")) != "")
                {
                    addPictures(cursor.getString(cursor.getColumnIndex("photo_name_list")))
                }

                fameDetailList.add(FameDetailItem(fameItem?.color, str_icon, str_detail_goal, int_count))
            }
        } catch (e: Exception) {
            Log.d("FameDetailFragment", "세부목표 없음 or 오류 : " + e.message)
        }

        // 어댑터 연결 및 리스트 띄우기
        val fameDetailListAdapter = FameDetailListAdapter(fameDetailList, requireContext())
        binding.containerFameDetailRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.containerFameDetailRv.adapter = fameDetailListAdapter

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 레이아웃에 사진들을 추가하는 함수
    private fun addPictures(photoNameList: String)
    {
        var photoList = photoNameList.split(',')

        for (i in photoList.indices - 1)
        {
            try {
                // 사진 경로 가져오기
                var path = requireContext().filesDir.toString() + "/picture/" + photoList[i]
                var bitmap: Bitmap = BitmapFactory.decodeFile(path)

                // 이미지 배율 크기 작업
                var density = requireActivity().resources.displayMetrics.density    // px = dp * density
                var pictureWidth = (106 * density).toInt()
                var pictureHeight = (100 * density).toInt()

                var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, pictureWidth, pictureHeight, true)

                var iv = ImageView(requireContext())
                iv.setImageBitmap(reScaledBitmap)

                binding.containerFameDetailPhotoList.addView(iv)
            }
            catch (e: Exception) {
                Log.e("FameDetailFragment", "사진 가져오기 실패")
                Log.d("FameDetailFragment", "사진 이름: ${photoList[i]}, $i \n ${e.printStackTrace()}")
            }
        }
    }
}