package com.example.guru_hemjee.Home.Fame

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.databinding.ContainerFameItemRvBinding

// 명예의 전당 RV 어댑터
class FameListAdapter(private val fameGoalList: ArrayList<FameItem>, val context: Context) : RecyclerView.Adapter<FameViewHolder>() {

    var onFameItemClickListener: ((Int) -> Unit)? = null // 대표목표 클릭

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FameViewHolder {
        val binding: ContainerFameItemRvBinding = ContainerFameItemRvBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FameViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: FameViewHolder, position: Int) {
        holder.bind(fameGoalList[position])

        // 대표목표 클릭 이벤트
        holder.binding.containerFameTopLLayout.setOnClickListener {
            onFameItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = fameGoalList.size
}