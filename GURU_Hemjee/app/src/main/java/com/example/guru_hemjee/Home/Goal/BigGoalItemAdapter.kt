package com.example.guru_hemjee.Home.Goal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.ContainerGoalItemRecyclerviewBinding

// 아코디언 메뉴 커스텀 리스트뷰의 어댑터
// 파라미터 값 : 대표목표 아이템, 세부목표 어댑터에 보내기 위한 세부목표 아이템
class BigGoalItemAdapter(
    private val bigGoalList: ArrayList<BigGoalItem>
): RecyclerView.Adapter<BigGoalItemAdapter.BigGoalViewHolder>() {

    // context 변수
    val context = MyApplication.applicationContext()

    // 세부목표 어댑터 변수
    private lateinit var detailGoalAdapter: DetailGoalItemAdapter

    // 세부목표 리스트 변수
    private var inDetailGoalList = ArrayList<DetailGoalItem>()

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
                var iconColor: Int = 0
                when (bigGoalItem.color) {
                    "Orange" -> color.setColorFilter(ContextCompat.getColor(context, R.color.Orange))
                    "BrightYellow" -> color.setColorFilter(ContextCompat.getColor(context, R.color.BrightYellow))
                    "Yellow" -> color.setColorFilter(ContextCompat.getColor(context, R.color.Yellow))
                    "Apricot" -> color.setColorFilter(ContextCompat.getColor(context, R.color.Apricot))
                    "DarkBrown" -> color.setColorFilter(ContextCompat.getColor(context, R.color.DarkBrown))
                    "SeedBrown" -> color.setColorFilter(ContextCompat.getColor(context, R.color.SeedBrown))
                    "NoteYellow" -> color.setColorFilter(ContextCompat.getColor(context, R.color.NoteYellow))
                    "LightGreen" -> color.setColorFilter(ContextCompat.getColor(context, R.color.LightGreen))
                    "Green" -> color.setColorFilter(ContextCompat.getColor(context, R.color.Green))
                    "LightBlue" -> color.setColorFilter(ContextCompat.getColor(context, R.color.LightBlue))
                    "Blue" -> color.setColorFilter(ContextCompat.getColor(context, R.color.Blue))
                    "Purple" -> color.setColorFilter(ContextCompat.getColor(context, R.color.Purple))
                    "Pink" -> color.setColorFilter(ContextCompat.getColor(context, R.color.Pink))
                }

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
                            moreIcon.setColorFilter(ContextCompat.getColor(context, iconColor))
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
                        icon.setColorFilter(ContextCompat.getColor(context, iconColor))

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

        // 세부목표 어댑터 연결 및 세부목표 데이터 전송
        holder.binding.goalItemDetailGoalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        detailGoalAdapter = DetailGoalItemAdapter(inDetailGoalList)
        holder.binding.goalItemDetailGoalRecyclerView.adapter = detailGoalAdapter
        holder.bind(bigGoalList[position])
    }

    // 대표목표 리스트 사이즈 반환
    override fun getItemCount(): Int = bigGoalList.size

    // 아이템 더하는 함수
    fun addBigGoalItem(bigGoal: BigGoalItem) {
        bigGoalList.add(bigGoal)
        notifyDataSetChanged()
    }

    // 아이템을 제거하는 함수
    fun removeBigGoalItem(position: Int) {
        bigGoalList.removeAt(position)
        notifyDataSetChanged()
    }
}