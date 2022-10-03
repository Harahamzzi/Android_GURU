package com.harahamzzi.android.Home.Goal

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.MyApplication
import com.harahamzzi.android.databinding.ContainerGoalItemRecyclerviewBinding

// 아코디언 메뉴 RV 어댑터
class BigGoalItemAdapter(
    private val bigGoalList: ArrayList<BigGoalItem>
    ): RecyclerView.Adapter<BigGoalViewHolder>() {

    val context = MyApplication.applicationContext()

    var onBigGoalItemLongClickListener: ((Int) -> Unit)? = null     // 대표목표 롱클릭
    var onDetailGoalItemClickListener: ((Int, Int) -> Unit)? = null // 세부목표 클릭

    private lateinit var detailGoalItemAdapter: DetailGoalItemAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BigGoalViewHolder {
        val binding: ContainerGoalItemRecyclerviewBinding = ContainerGoalItemRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return BigGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BigGoalViewHolder, position: Int) {
        holder.bind(bigGoalList[position])

        for (i in 0 until (bigGoalList[position].detailGoalList.size)) {
            Log.i("onbindviewholder", bigGoalList[position].detailGoalList[i].toString())
        }

        detailGoalItemAdapter = DetailGoalItemAdapter(bigGoalList[position].detailGoalList, onDetailGoalItemClickListener, position)

        holder.detailGoalRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.detailGoalRV.adapter = detailGoalItemAdapter

        // 대표목표 박스 클릭 이벤트
        holder.binding.goalItemBigGoalCLayout.setOnClickListener {
            val show = toggleLayout(!bigGoalList[position].isExpanded, holder.binding.goalItemOpenBtn, holder.binding.goalItemDetailGoalLLayout)
            bigGoalList[position].isExpanded = show
        }

        // 대표목표 박스 롱클릭 이벤트
        holder.binding.goalItemBigGoalCLayout.setOnLongClickListener {
            onBigGoalItemLongClickListener?.invoke(position)
            return@setOnLongClickListener true
        }
    }

    // isExpanded의 여부에 따라서 화살표 회전 및 하위 메뉴 펼치기
    private fun toggleLayout(isExpanded: Boolean, view: View, detailGoalLayout: LinearLayout): Boolean {
        ToggleAnimation.toggleArrow(view, isExpanded)

        if (isExpanded) {
            ToggleAnimation.expand(detailGoalLayout)
        }
        else {
            ToggleAnimation.collapse(detailGoalLayout)
        }
        return isExpanded
    }

    override fun getItemCount(): Int = bigGoalList.size

    // 대표목표 아이템 반환
    fun getBigGoalItem(position: Int): BigGoalItem {
        return bigGoalList[position]
    }

    // 대표목표 아이템 추가
    @SuppressLint("NotifyDataSetChanged")
    fun addBigGoalItem(bigGoalItem: BigGoalItem) {
        bigGoalList.add(bigGoalItem)
        notifyDataSetChanged()
    }

    // 대표목표 아이템 수정
    @SuppressLint("NotifyDataSetChanged")
    fun modifyBigGoalItem(title: String, color: String, initTitle: String) {
        for (position in 0 until itemCount) {
            if (bigGoalList[position].title == initTitle) {
                bigGoalList[position].title = title
                bigGoalList[position].color = color

                for (index in 0 until bigGoalList[position].detailGoalList.size) {
                    bigGoalList[position].detailGoalList[index].color = color
                }

                notifyItemChanged(position)
                detailGoalItemAdapter.notifyDataSetChanged()
                break
            }
        }
    }

    // 대표목표 아이템 삭제
    @SuppressLint("NotifyDataSetChanged")
    fun removeBigGoalItem(bigGoalItem: BigGoalItem) {
        bigGoalList.remove(bigGoalItem)
        notifyDataSetChanged()
    }

    // 세부목표 아이템 추가
    @SuppressLint("NotifyDataSetChanged")
    fun addDetailGoalItem(detailTitle: String, icon: String, color: String, bigGoalTitle: String) {
        for (position in 0 until itemCount) {
            if (bigGoalList[position].title == bigGoalTitle) {
                Log.d("FIND ERROR", "icon $icon color $color title $detailTitle initBigGoal $bigGoalTitle position $position")
                bigGoalList[position].detailGoalList.add(DetailGoalItem(detailTitle, icon, color))
                bigGoalList[position].iconArray.add(icon)
                Log.i("세부목표 리스트 ", "$position" + bigGoalList[position].detailGoalList)
                Log.i("세부목표 아이콘 리스트 ", "$position" + bigGoalList[position].iconArray)
                notifyItemChanged(position)
                detailGoalItemAdapter.notifyDataSetChanged()
                break
            }
        }
    }

    // 세부목표 아이템 수정
    fun modifyDetailGoalItem(title: String, icon: String, color: String, initTitle: String, initBigGoal: String) {
        for (position in 0 until itemCount) {
            if (bigGoalList[position].title == initBigGoal) {
                for (index in 0 until bigGoalList[position].detailGoalList.size) {
                    if (bigGoalList[position].detailGoalList[index].detailTitle == initTitle) {
                        bigGoalList[position].detailGoalList[index].detailTitle = title
                        bigGoalList[position].detailGoalList[index].detailIcon = icon
                        bigGoalList[position].detailGoalList[index].color = color
                        bigGoalList[position].iconArray[index] = icon

                        notifyItemChanged(position)
                        detailGoalItemAdapter.notifyItemChanged(index)
                        break
                    }
                }
            }
        }
    }

    // 세부목표 아이템 삭제
    @SuppressLint("NotifyDataSetChanged")
    fun removeDetailGoalItem(bigGoalItem: BigGoalItem, detailGoalItem: DetailGoalItem) {
        for (position in 0 until itemCount) {
            if (bigGoalList[position].title == bigGoalItem.title) {
                for (index in 0 until bigGoalList[position].detailGoalList.size) {
                    if (bigGoalList[position].detailGoalList[index].detailTitle == detailGoalItem.detailTitle) {
                        bigGoalList[position].detailGoalList.remove(detailGoalItem)
                        bigGoalList[position].iconArray.removeAt(index)

                        notifyItemChanged(position)
                        detailGoalItemAdapter.notifyDataSetChanged()
                        break
                    }
                }
            }
        }
    }
}