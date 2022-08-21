package com.example.guru_hemjee.Home.Fame

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.databinding.ContainerFameDetailItemRvBinding

// 명예의 전당 세부목표 RV 어댑터
class FameDetailListAdapter(private val fameDetailGoalList: ArrayList<FameDetailItem>, val context: Context) : RecyclerView.Adapter<FameDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FameDetailViewHolder {
        val binding: ContainerFameDetailItemRvBinding = ContainerFameDetailItemRvBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FameDetailViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: FameDetailViewHolder, position: Int) {
        holder.bind(fameDetailGoalList[position])
    }

    override fun getItemCount(): Int = fameDetailGoalList.size
}