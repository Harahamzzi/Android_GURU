package com.example.guru_hemjee.Home.Goal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBConvert
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ContainerGoalItemRecyclerviewBinding

// 아코디언 메뉴 커스텀 리스트뷰의 어댑터
// 파라미터 값 : 대표목표 아이템, 세부목표 어댑터에 보내기 위한 세부목표 아이템
class BigGoalItemAdapter: RecyclerView.Adapter<BigGoalItemAdapter.BigGoalViewHolder>() {

    // context 변수
    val context = MyApplication.applicationContext()

    // 대표목표 리스트
    private var bigGoalList = ArrayList<BigGoalItem>()

    // 세부목표 어댑터 변수
    private lateinit var detailGoalAdapter: DetailGoalItemAdapter

    // 세부목표 리스트 변수
    private var inDetailGoalList = ArrayList<DetailGoalItem>()

    // 롱클릭 리스너
    private lateinit var mItemLongClickListener: OnItemLongClickListener

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, bigGoalItem: BigGoalItem, pos: Int)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mItemLongClickListener = listener
    }

    // 커스텀한 리사이클러뷰에 있는 요소들을 연결하기
    inner class BigGoalViewHolder(
        val binding: ContainerGoalItemRecyclerviewBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(bigGoalItem: BigGoalItem) {

                val color = binding.goalItemColorIv
                val title = binding.goalItemBigGoalTv
                val iconsLayout = binding.goalItemIconLLayout
                val openBtn = binding.goalItemOpenBtn
                val bigGoalLayout = binding.goalItemBigGoalCLayout
                val detailGoalLayout = binding.goalItemDetailGoalLLayout

                // 대표목표 색상값을 color값(int)으로 저장
                DBConvert.colorConvert(color, bigGoalItem.color, context)

                try {
                    // 아이콘 리스트
                    var iconList: ArrayList<String>? = bigGoalItem.iconArray

                    // 아이콘 레이아웃 만들기 (아이콘 사이즈, 아이콘의 마진값)
                    val iconLayoutParams = LinearLayout.LayoutParams(
                        48,
                        48
                    )
                    iconLayoutParams.setMargins(0,0,32,0)

                    // 아이콘 레이아웃에 아이콘&아이콘 색상&레이아웃 세팅 적용
                    for (i in 0 until iconList!!.size) {
                        // 아이콘이 6개 이상일 경우, ... 아이콘 보이기
                        if (i == 6) {
                            val moreIcon = ImageView(context)
                            moreIcon.setImageResource(R.drawable.ic_more_goals)
                            DBConvert.colorConvert(moreIcon, bigGoalItem.color, context)
                            val moreIconLayoutParams = LinearLayout.LayoutParams(
                                48,
                                48
                            )
                            moreIconLayoutParams.setMargins(-8, 0, 0, 0)
                            moreIcon.layoutParams = moreIconLayoutParams
                            iconsLayout.addView(moreIcon)
                            break
                        }
                        val iconId = context.resources.getIdentifier(iconList[i], "drawable", context.packageName)
                        val icon = ImageView(context) // 동적 이미지 객체 생성
                        icon.setImageResource(iconId)
                        icon.layoutParams = iconLayoutParams
                        DBConvert.colorConvert(icon, bigGoalItem.color, context)

                        iconsLayout.addView(icon)
                    }

                    title.text = bigGoalItem.title // 대표목표 텍스트 적용
                    inDetailGoalList = bigGoalItem.detailGoalList!! // 세부목표 리스트 적용

                } catch (e: Exception) {
                    // 대표목표 색상 적용
                    title.text = bigGoalItem.title // 대표목표 텍스트 적용
                }


                // 대표목표 박스 클릭 이벤트
                bigGoalLayout.setOnClickListener {
                    val show = toggleLayout(!bigGoalItem.isExpanded, openBtn, detailGoalLayout)
                    bigGoalItem.isExpanded = show
                }

                // 대표목표 박스 롱 클릭 이벤트
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    bigGoalLayout.setOnLongClickListener {
                        mItemLongClickListener?.onItemLongClick(bigGoalLayout, bigGoalItem, pos)
                        return@setOnLongClickListener true
                    }
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
        }

    // 뷰홀더를 생성해줘야 할 때 호출되는 함수 -> 아이템 뷰 객체를 만들어서 뷰홀더에 줌
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BigGoalViewHolder {
        val binding: ContainerGoalItemRecyclerviewBinding = ContainerGoalItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )

        return BigGoalViewHolder(binding)
    }

    // 뷰홀더에 데이터를 바인딩해줘야 할 때마다 호출되는 함수 => 여러번 호출됨
    override fun onBindViewHolder(holder: BigGoalViewHolder, position: Int) {
        holder.bind(bigGoalList[position])

        // 세부목표 어댑터 연결 및 세부목표 데이터 전송
        holder.binding.goalItemDetailGoalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        detailGoalAdapter = DetailGoalItemAdapter(inDetailGoalList)
        holder.binding.goalItemDetailGoalRecyclerView.adapter = detailGoalAdapter
    }

    // 대표목표 리스트 사이즈 반환
    override fun getItemCount(): Int = bigGoalList.size

    // 아이템 더하는 함수
    fun addBigGoalItem(bigGoalItem: BigGoalItem) {
        bigGoalList.add(bigGoalItem)
        notifyItemChanged(itemCount - 1)
    }

    // 아이템을 제거하는 함수
    @SuppressLint("NotifyDataSetChanged")
    fun removeBigGoalItem(bigGoalItem: BigGoalItem) {
        bigGoalList.remove(bigGoalItem)
        notifyDataSetChanged()
    }

    // 아이템의 위치에 따른 대표목표 view를 반환하는 함수
    fun getItemTitle(index: Int): String {
        return bigGoalList[index].title
    }

    // 아이템 수정하는 함수
    fun setModifyItem(position: Int, title: String, color: String) {
        bigGoalList[position].title = title
        bigGoalList[position].color = color
        notifyItemChanged(position)
    }

    // dataloading함수에서 쓰이는 아이템 재설정 함수
    fun setItem(position: Int, iconList: ArrayList<String>, detailList: ArrayList<DetailGoalItem>) {
        bigGoalList[position].iconArray = iconList
        bigGoalList[position].detailGoalList = detailList
        notifyItemChanged(position)
    }

    // 아이템의 위치에 따른 세부목표 view를 반환하는 함수
    fun getDetailItemTitle(bigIndex: Int, detailIndex: Int): String {
        return bigGoalList[bigIndex].detailGoalList!![detailIndex].detailTitle
    }

    // 세부목표 추가하는 함수
    fun addDetailGoalItem(bigPos: Int, detailGoalItem: DetailGoalItem) {
        bigGoalList[bigPos].detailGoalList!!.add(detailGoalItem)
        notifyItemChanged(bigPos)
    }

    // 세부목표 수정하는 함수
    fun setModifyDetailItem(bigPos: Int, detailPos: Int, title: String, icon: String) {
        bigGoalList[bigPos].detailGoalList!![detailPos].detailTitle = title
        bigGoalList[bigPos].detailGoalList!![detailPos].detailIcon = icon
        notifyItemChanged(bigPos)
    }
}