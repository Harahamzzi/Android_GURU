package com.example.guru_hemjee.Home.Goal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ContainerDetailGoalItemRecyclerviewBinding

// 아코디언 메뉴의 세부목표 어댑터
class DetailGoalItemAdapter(
    private val detailGoalList: ArrayList<DetailGoalItem>
) : RecyclerView.Adapter<DetailGoalItemAdapter.DetailGoalViewHolder>() {

    // context 변수
    val context = MyApplication.applicationContext()

    // 클릭 리스너
    private lateinit var mItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(view: View, detailGoalItem: DetailGoalItem, pos: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener
    }

    inner class DetailGoalViewHolder(
        private val binding: ContainerDetailGoalItemRecyclerviewBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind (detailGoalItem: DetailGoalItem) {
            val detailIcon = binding.detailGoalItemIconIv
            val detailTitle = binding.detailGoalItemDetailGoalTv
            val detailLayout = binding.detailGoalItemDetailGoalCLayout

            try {
                // 세부목표 icon 색상 저장
                var iconColor = 0
                when (detailGoalItem.color) {
                    "Orange" -> R.color.Orange
                    "BrightYellow" -> R.color.BrightYellow
                    "Yellow" -> iconColor = R.color.Yellow
                    "Apricot" -> iconColor = R.color.Apricot
                    "DarkBrown" -> iconColor = R.color.DarkBrown
                    "SeedBrown" -> iconColor = R.color.SeedBrown
                    "NoteYellow" -> iconColor = R.color.NoteYellow
                    "LightGreen" -> iconColor = R.color.LightGreen
                    "Green" -> iconColor = R.color.Green
                    "LightBlue" -> iconColor = R.color.LightBlue
                    "Blue" -> iconColor = R.color.Blue
                    "Purple" -> iconColor = R.color.Purple
                    "Pink" -> iconColor = R.color.Pink
                }

                // 세부목표 아이콘 이미지 및 색상 적용
                val iconId = DBConvert.iconConvert(detailGoalItem.detailIcon, context)
                detailIcon.setImageResource(iconId)
                detailIcon.setColorFilter(ContextCompat.getColor(context, iconColor))

                // 세부목표 텍스트 적용
                detailTitle.text = detailGoalItem.detailTitle
            } catch (e: Exception) {

            }

            // 세부목표 레이아웃 클릭 이벤트
            detailLayout.setOnClickListener {
                mItemClickListener.onItemClick(detailLayout, detailGoalItem, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailGoalViewHolder {
        val binding: ContainerDetailGoalItemRecyclerviewBinding = ContainerDetailGoalItemRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DetailGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailGoalViewHolder, position: Int) {
        holder.bind(detailGoalList[position])
    }

    // 세부목표 리스트 사이즈 반환
    override fun getItemCount(): Int = detailGoalList.size
}