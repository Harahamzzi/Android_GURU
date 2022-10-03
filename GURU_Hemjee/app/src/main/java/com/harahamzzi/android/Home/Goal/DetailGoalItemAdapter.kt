package com.harahamzzi.android.Home.Goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.databinding.ContainerDetailGoalItemRecyclerviewBinding

// 아코디언 메뉴의 세부목표 어댑터
class DetailGoalItemAdapter(
    private val detailGoalList: ArrayList<DetailGoalItem>?,
    private val onDetailGoalClick: ((Int, Int) -> Unit)?,
    private val bigGoalPosition: Int
) : RecyclerView.Adapter<DetailGoalViewHolder>() {

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
        holder.bind(detailGoalList!![position])

        // 세부목표 레이아웃 클릭 이벤트
        holder.binding.detailGoalItemDetailGoalCLayout.setOnClickListener {
            onDetailGoalClick?.invoke(position, bigGoalPosition)
        }
    }

    override fun getItemCount(): Int = detailGoalList?.size ?: 0
}