package com.example.guru_hemjee.Home.Home

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.R
import kotlinx.android.synthetic.main.container_main_goal_select.view.*

// 홈 화면의 대표 목표 선택 목록을 위한 View Pager Adapter
class HomeViewPagerAdapter(context: Context, goalNameList: ArrayList<String>, iconColorNameList: ArrayList<String>, iconIDList: ArrayList<String>): RecyclerView.Adapter<HomeViewPagerAdapter.PagerViewHolder>() {

    private val TAG = "HomeViewPagerAdapter"    // 로그 출력시 사용할 태그

    private var context = context               // Context 변수

    private var goalName = goalNameList         // 대표 목표 이름 리스트
    private var iconColorID = iconColorNameList // 아이콘 색상값 리스트
    private var iconID = iconIDList             // 아이콘 값 리스트(분리작업 필요)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PagerViewHolder((parent))

    override fun getItemCount(): Int = goalName.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {

        // 대표 목표 이름 바인딩
        holder.goalNameTextView.text = goalName[position]
        // 색상값 바인딩
        DBConvert.colorConvert(holder.iconColorImageView, iconColorID[position], context)

        /** 아이콘 바인딩 **/
        // 1. 선행작업 - 아이콘 값 추출(분리)
        var iconList: ArrayList<Int> = getIconList(iconID, position)
        // 1-2. 선행작업 - 아이콘 이미지 파라미터 생성
        val iconLayoutParams = LinearLayout.LayoutParams(50, 50)
        iconLayoutParams.gravity = Gravity.CENTER
        iconLayoutParams.marginStart = 18

        // 2. 아이콘 레이아웃에 넣기
        for(i in iconList.indices)
        {
            // 이미지 뷰 생성 및 이미지 설정 & 색상 적용
            var iv = ImageView(context)
            iv.setImageResource(iconList[i])
            iv.layoutParams = iconLayoutParams
            DBConvert.colorConvert(iv, iconColorID[position], context)

            // 아이콘 이미지 추가
            holder.iconListLayout.addView(iv)

            // 현재 네 번째 아이콘을 추가한 상태라면
            if(i >= 3)
            {
                // 생략 이미지 넣고 break
                var tIv = ImageView(context)

                tIv.setImageResource(R.drawable.ic_sebumenu)      // 이미지 적용
                tIv.layoutParams = iconLayoutParams
                DBConvert.colorConvert(tIv, iconColorID[position], context)  // 색상 적용

                holder.iconListLayout.addView(tIv)

                Log.i(TAG, "생략 아이콘 추가완료!")

                break;
            }
        }

        // 버튼 클릭 리스너 바인딩
        holder.startButton.setOnClickListener {
            // 팝업 띄우기
            val dialog = RecordSettingDialog(context, goalName[position], iconColorID[position])
            dialog.showPopup()
        }
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.container_main_goal_select, parent, false)) {

        val goalNameTextView = itemView.goalNameTextView
        val iconColorImageView = itemView.colorIconImageView
        val iconListLayout = itemView.iconListLayout
        val startButton = itemView.startButtonImageView
    }

    // 해당 포지션의 아이콘 값 리스트를 ArrayList<int>형으로 추출하는 함수
    private fun getIconList(mIconList: ArrayList<String>, position: Int): ArrayList<Int> {

        // , 단위로 해당 포지션의 아이콘 값 분할
        var iconStringList: List<String> = mIconList[position].split(',')
        var result = ArrayList<Int>()

        Log.d(TAG, "분리작업 전 iconStringList: $iconStringList")

        // 분할한 아이콘 값을 ArrayList<Int>에 담기
        for(i in iconStringList.indices)
        {
            try {
                // 만일 리스트 내에 공백이 포함되어있을 경우 스킵
                if(iconStringList[i] == "")
                    continue

                result.add(iconStringList[i].toInt())
            }
            catch (e: java.lang.NumberFormatException) {
                Log.e(TAG, "해당 대표 목표의 아이콘 값이 없습니다.")
                Log.e(TAG, "${e.printStackTrace()}")
            }
        }

        return result
    }
}