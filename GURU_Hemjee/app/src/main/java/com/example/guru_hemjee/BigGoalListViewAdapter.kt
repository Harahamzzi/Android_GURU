package com.example.guru_hemjee

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

// 대표 목록 커스텀 리스트뷰의 어댑터
/* class BigGoalListViewAdapter : BaseAdapter() { */
class BigGoalListViewAdapter(private val items: MutableList<BigGoalListViewItem>) : BaseAdapter() {

    // 어댑터에 추가된 데이터를 저장하기 위한 list
    //private var items = ArrayList<BigGoalListViewItem>()

    override fun getCount() : Int { // 어댑터 뷰의 자식 뷰들의 개수 리턴
        Log.d("size", items.size.toString()) // 값 확인용, 추후에 삭제하기
        return items.size
    }

    override fun getItem(position : Int) : BigGoalListViewItem = items[position] // 어댑터 객체가 갖는 항목 중 한개 리턴, position : 리턴할 항목 위치

    override fun getItemId(position : Int): Long = position.toLong() // 어댑터가 갖는 항목의 ID를 리턴, position : 리턴할 항목의 위치

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View { // 자식 뷰들 중 한개를 리턴, 리스트뷰에 넣을 아이템 설정
        var convertView = view // 해당 메소드가 호출되는 시점에서 position에 위치하는 자식 뷰
        if (convertView == null) {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.listview_biggoal_item, parent, false)
        }

        // 커스텀한 리스트뷰에 있는 요소들을 연결
        val item : BigGoalListViewItem = items[position]
        // val resourceId = context.resources.getIdentifier(item.color.toString(), "drawable", context.packageName)
        var colorImg : ImageView = convertView!!.findViewById(R.id.bigGoalColor)
        var bigGoalText : TextView = convertView!!.findViewById(R.id.bigGoalList)
        var rightImg : ImageView = convertView!!.findViewById(R.id.rightImageView)

        // 아이템 내의 각 위젯에 데이터 반영
        colorImg.setImageDrawable(item.colorImg)
        bigGoalText.setText(item.bigGoalText)
        rightImg.setImageDrawable(item.rightImg)

        /*colorImg.setImageDrawable(item.color)
        bigGoalText.setText(item.bigGoal)
        rightImg.setImageDrawable(item.rightImage)*/

        //color.setImageDrawable(item.color.drawable)
        //bigGoal.text = item.bigGoal.toString()
        //rightImage.setImageDrawable(item.rightImage.drawable)
        //rightImage.setImageResource(rightImage.id)

        return convertView
    }

    // 대표 목표 리스트를 추가하는 함수
    /*fun addItem(color : String, bigGoalText : String, rightImgColor : String) {
        val item = BigGoalListViewItem()

        item.color = color
        item.bigGoal = bigGoal
        item.rightImage = rightImg

        items.add(item)
    }*/
}