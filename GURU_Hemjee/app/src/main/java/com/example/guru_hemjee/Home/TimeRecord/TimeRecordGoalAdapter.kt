package com.example.guru_hemjee.Home.TimeRecord

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.R

// 기록화면의 세부 목표 생성 어댑터
class TimeRecordGoalAdapter(context: Context) : RecyclerView.Adapter<TimeRecordGoalAdapter.ItemViewHolder>() {

    // Log 태그값
    private var TAG = "TimeRecordGoalAdapter"

    // 어댑터에 들어갈 list
    private var item = ArrayList<TimeRecordGoalItem>()

    private var context = context

    inner class ItemViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        val iconImageView: ImageView = view!!.findViewById(R.id.iconImageView)
        val goalNameTextView: TextView = view!!.findViewById(R.id.goalNameTextView)
        val startCameraButton: ImageButton = view!!.findViewById(R.id.cameraImageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // LayoutInflater를 이용하여 해당 레이아웃을 inflate 시킴
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.container_record_detail_goal, parent, false)

        return ItemViewHolder(view)
    }

    // Item을 하나하나 binding 시키는 함수
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.iconImageView.setImageResource(DBConvert.iconConvert(item.get(position).getIconName(), context))  // 아이콘 모양 적용
        DBConvert.colorConvert(holder.iconImageView, item.get(position).getIconColor(), context)                 // 아이콘 색상 적용
        holder.goalNameTextView.setText(item.get(position).getGoalName())                                        // 세부목표 이름 적용

        // 카메라 버튼 클릭 리스너
        holder.startCameraButton.setOnClickListener {
            // Camera Activity로 이동
            var intent = Intent(context, CameraActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {

        try {
            // RecyclerView의 총 개수 반환
            return item.size
        }
        catch(e: Exception) {
            Log.e(TAG, "기록화면 RecyclerView 내의 item 개수 반환 실패(선택된 세부목표가 없음)")
            Log.e(TAG, e.stackTraceToString())
        }

        return 0
    }

    fun addItem(data: TimeRecordGoalItem) {
        // 외부에서 item을 추가시킬 함수
        item.add(data)
    }
}