package com.harahamzzi.android.Home.TimeRecord

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.DBConvert
import com.harahamzzi.android.R

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

        // 아직 완료하지 못한 목표라면
        if (!(item.get(position).getIsComplete()))
        {
            // 카메라 버튼 클릭 리스너
            holder.startCameraButton.setOnClickListener {
                // Camera Activity로 이동
                var intent = Intent(context, CameraActivity::class.java)

                try {
                    intent.putExtra("detailGoalName", item.get(position).getGoalName())
                }
                catch (e: Exception) {
                    Log.e(TAG, "세부 목표 이름 intent 담기 실패")
                    Log.e(TAG, e.stackTraceToString())
                }

                context.startActivity(intent)
            }
        }
        // 완료했다면
        else
        {
            // 카메라 버튼 없애기
            holder.startCameraButton.visibility = View.GONE
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

    fun clearAllItem() {
        if (itemCount > 0)
        {
            for (i in 0 until itemCount)
            {
                item.removeFirst()
            }

            notifyItemRangeRemoved(0, itemCount)
        }
    }

    fun addItem(data: TimeRecordGoalItem) {
        // 외부에서 item을 추가시킬 함수
        item.add(data)
    }
}