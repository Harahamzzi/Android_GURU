package com.example.guru_hemjee

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

// 대표 목록 커스텀 리스트뷰의 어댑터
class BigGoalListViewAdapter(private val items: MutableList<BigGoalListViewItem>) : BaseAdapter() {

    override fun getCount() : Int { // 어댑터 뷰의 자식 뷰들의 개수 리턴
        Log.d("size", items.size.toString()) // 값 확인용, 추후에 삭제하기
        return items.size
    }

    // 어댑터 객체가 갖는 항목 중 한개 리턴, position : 리턴할 항목 위치
    override fun getItem(position : Int) : BigGoalListViewItem = items[position]

    // 어댑터가 갖는 항목의 ID를 리턴, position : 리턴할 항목의 위치
    override fun getItemId(position : Int): Long = position.toLong()

    // 자식 뷰들 중 한개를 리턴, 리스트뷰에 넣을 아이템 설정
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view // 해당 메소드가 호출되는 시점에서 position에 위치하는 자식 뷰
        if (convertView == null) {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.container_biggoal_item, parent, false)
        }

        // 커스텀한 리스트뷰에 있는 요소들을 연결
        val item : BigGoalListViewItem = items[position]
        var colorImg : ImageView = convertView!!.findViewById(R.id.bigGoalColor)
        var bigGoalText : TextView = convertView!!.findViewById(R.id.bigGoalList)
        var rightImg : ImageView = convertView!!.findViewById(R.id.rightImageView)

        // 아이템 내의 각 위젯에 데이터 반영
        colorImg.setImageDrawable(item.colorImg)
        bigGoalText.setText(item.bigGoalText)
        rightImg.setImageDrawable(item.rightImg)

        return convertView
    }
}