package com.example.guru_hemjee.Home.Store

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.databinding.ContainerMarketItemBinding

// 씨앗 상점 및 나의 햄찌 관리 화면의 RV 어댑터
class StoreListAdapter(val context: Context, private val itemList: ArrayList<StoreItem>): RecyclerView.Adapter<StoreViewHolder>() {

    var onItemClickListener: ((Int) -> Unit)? = null // 아이템 클릭

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding: ContainerMarketItemBinding = ContainerMarketItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(itemList[position])

        // 아이템 클릭 이벤트
        holder.binding.containerMarketItemCLayout.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = itemList.size

    // 아이템 찾기
    fun getItemList(position: Int): StoreItem {
        return itemList[position]
    }

    // 아이템의 배경값 바꾸기
    fun changeItemBG(itemName: String, isChange: Boolean) {
        // 햄스터가 착용 중인 아이템이라면 isSelected = true, 아니라면 false
        for (i in 0 until itemList.size) {
            if (itemList[i].itemName == itemName) {
                itemList[i].isSelected = isChange
                notifyItemChanged(i)
                break
            }
        }
    }
}