package com.harahamzzi.android.Home.Fame

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.databinding.ContainerFameItemRvBinding

// 명예의 전당 RV 어댑터
class FameListAdapter(val context: Context) : RecyclerView.Adapter<FameViewHolder>() {

    var onFameItemClickListener: ((Int) -> Unit)? = null // 대표목표 클릭
    var onFameItemLongClickListener: ((Int) -> Unit)? = null // 대표목표 롱클릭

    private var fameGoalList = ArrayList<FameItem>() // 리스트

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FameViewHolder {
        val binding: ContainerFameItemRvBinding = ContainerFameItemRvBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FameViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: FameViewHolder, position: Int) {
        var safePosition = holder.adapterPosition
        holder.bind(fameGoalList[safePosition])

        // 대표목표 클릭 이벤트
        holder.binding.containerFameTopLLayout.setOnClickListener {
            onFameItemClickListener?.invoke(safePosition)
        }

        // 대표목표 롱클릭 이벤트
        holder.binding.containerFameTopLLayout.setOnLongClickListener {
            onFameItemLongClickListener?.invoke(safePosition)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = fameGoalList.size

    // 아이템 삭제
    @SuppressLint("NotifyDataSetChanged")
    fun removeGoalItem(fameItem: FameItem, position: Int) {
        fameGoalList.remove(fameItem)
        notifyItemRemoved(position)
    }

    // 아이템 추가
    fun addGoalItem(fameItem: FameItem) {
        fameGoalList.add(fameItem)
        notifyItemChanged(fameGoalList.size)
    }
}