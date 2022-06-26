package com.example.guru_hemjee.Home.Goal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ContainerDetailGoalItemRecyclerviewBinding

class DetailGoalRecyclerViewAdapter(private val detailItems: MutableList<DetailGoalRecyclerViewItem>):
    RecyclerView.Adapter<DetailGoalRecyclerViewAdapter.DetailGoalViewHolder>() {

    // 클릭 리스너
    interface DetailGoalRecyclerViewItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    // 클릭 리스너
    private var itemClickListener: DetailGoalRecyclerViewItemClickListener? = null

    // 클릭 함수
    fun setDetailGoalRecyclerViewItemClickListener(clickListener: DetailGoalRecyclerViewItemClickListener) {
        itemClickListener = clickListener
    }

    class DetailGoalViewHolder(val binding: ContainerDetailGoalItemRecyclerviewBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(detailGoal: DetailGoalRecyclerViewItem) {
            val iconImg = binding.detailGoalItemIcon
            val detailGoalText = binding.detailGoalItemDetailGoalTextView

            iconImg.setImageResource(detailGoal.IconImg)
            detailGoalText.text = detailGoal.detailGoalText
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailGoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.container_detail_goal_item_recyclerview, parent, false)

        return DetailGoalViewHolder(ContainerDetailGoalItemRecyclerviewBinding.bind(view))
    }

    override fun onBindViewHolder(
        holder: DetailGoalViewHolder,
        position: Int
    ) {
        holder.bind(detailItems[position])
    }

    override fun getItemCount(): Int = detailItems.size

}