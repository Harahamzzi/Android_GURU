package com.example.guru_hemjee.Home.Home

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.R
import kotlinx.android.synthetic.main.container_main_goal_select.view.*

// 홈 화면의 대표 목표 선택 목록을 위한 View Pager Adapter
class HomeViewPagerAdapter(goalNameList: ArrayList<String>, colorIconIDList: ArrayList<Int>, iconIDList: ArrayList<String>): RecyclerView.Adapter<HomeViewPagerAdapter.PagerViewHolder>() {

    var goalName = goalNameList         // 대표 목표 이름 리스트
    var colorIconID = colorIconIDList   // 아이콘 색상값 리스트
    var iconID = iconIDList             // 아이콘 값 리스트(분리작업 필요)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PagerViewHolder((parent))

    override fun getItemCount(): Int = goalName.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {

        // 대표 목표 이름 바인딩
        holder.goalNameTextView.text = goalName[position]
        // 색상값 바인딩
        holder.colorIconImageView.setColorFilter(colorIconID[position], PorterDuff.Mode.SRC_IN)

        /** 아이콘 바인딩 **/
        // 1. 아이콘 값 추출(분리)
        var iconList: ArrayList<Int> = getIconList(iconID, position)
        // 2. 아이콘 레이아웃에 넣기
        for (i in iconList.indices)
        {
            // 이미지 뷰 생성 및 이미지 설정
            var iv = ImageView(MyApplication.applicationContext())
            iv.setImageResource(iconList[i])

            // 아이콘 이미지 추가
            holder.iconListLayout.addView(iv)
        }
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.container_main_goal_select, parent, false)) {

        val goalNameTextView = itemView.goalNameTextView
        val colorIconImageView = itemView.colorIconImageView
        val iconListLayout = itemView.iconListLayout
    }

    // 해당 포지션의 아이콘 값 리스트를 ArrayList<int>형으로 추출하는 함수
    private fun getIconList(mIconList: ArrayList<String>, position: Int): ArrayList<Int> {

        // , 단위로 해당 포지션의 아이콘 값 분할
        var iconStringList = mIconList[position].split(',')
        var result = ArrayList<Int>()

        // 분할한 아이콘 값을 ArrayList<Int>에 담기
        for(i in iconStringList.indices)
        {
            result.add(iconStringList[i].toInt())
        }

        return result
    }
}