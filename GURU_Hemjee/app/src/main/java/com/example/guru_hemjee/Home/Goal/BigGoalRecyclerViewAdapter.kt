package com.example.guru_hemjee.Home.Goal

import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.MyApplication
import com.example.guru_hemjee.databinding.ContainerGoalItemRecyclerviewBinding

// 아코디언 메뉴 커스텀 리스트뷰의 어댑터
// 파라미터 값 : 대표목표 아이템, 세부목표 어댑터에 보내기 위한 세부목표 아이템
class BigGoalRecyclerViewAdapter():
        RecyclerView.Adapter<BigGoalRecyclerViewAdapter.BigGoalViewHolder>() {

    // 세부목표 어댑터와 연결하기 위한 변수
    private lateinit var detailGoalAdapter: DetailGoalRecyclerViewAdapter

    // 대표목표 리스트
    private lateinit var bigItems: MutableList<BigGoalRecyclerViewItem>

    // 세부목표 리스트
    private lateinit var detailItems: MutableList<DetailGoalRecyclerViewItem>

    // item의 클릭 상태를 저장할 array 객체
    private lateinit var selected: SparseBooleanArray

    // 직전에 클릭했던 item의 position
    private var prePos: Int = -1

    // 클릭 인터페이스 정의
    interface BigGoalRecyclerViewItemClickListener {
        fun onItemClick(bigGoal: BigGoalRecyclerViewItem, position: Int)
        fun onRemoveBigGoal(position: Int)
    }

    // 리스너 객체를 전달받는 함수와 리스너 객체를 저장할 변수
    private lateinit var bItemClickListener: BigGoalRecyclerViewItemClickListener

    // context변수
    val context = MyApplication.applicationContext()

    // 클릭 함수
    fun setBigGoalRecyclerViewItemClickListener(clickListener: BigGoalRecyclerViewItemClickListener) {
        bItemClickListener = clickListener
    }

    // 커스텀한 리사이클러뷰에 있는 요소들을 연결하기
    inner class BigGoalViewHolder(val binding: ContainerGoalItemRecyclerviewBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int, bigGoal: BigGoalRecyclerViewItem) {

                var colorImg = binding.goalItemColorImageVIew // 색상
                var bigGoalText = binding.goalItemBigGoalTextView // 대표목표

                colorImg.setColorFilter(bigGoal.colorImg, PorterDuff.Mode.SRC_IN) // 아이콘 위에 색깔 입히기
                bigGoalText.text = bigGoal.bigGoalText

                // 아이콘 리스트
                val iconList: ArrayList<Int> = bigGoal.iconArray

                // 아이콘 레이아웃 만들기 (아이콘 사이즈, 아이콘의 마진값)
                val iconLayoutParams = LinearLayout.LayoutParams(
                    14,
                    14
                )
                iconLayoutParams.setMargins(0,0,6,0)

                // 레이아웃에 아이콘, 레이아웃 적용
                for (i in 0 until iconList.size) {
                    if (i == 6) {
                        break
                    }
                    val icon = ImageView(context)    // 동적 객체 생성
                    icon.setImageResource(iconList[i])
                    binding.goalItemIconLayout.addView(icon, iconLayoutParams)
                }

                changeVisibility(selected.get(position))
                // 클릭 이벤트
                /*val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemView.setOnClickListener {
                        itemClickListener?.onItemClick()
                    }
                }*/
            }

            // 리사이클러뷰를 접고 펼칠 때의 애니메이션 효과
            private fun changeVisibility(isExpanded: Boolean) {
                var dpValue = 250
                var d = context.resources.displayMetrics.density
                var height = (dpValue * d).toInt()

                var valueAnimator: ValueAnimator =
                    if (isExpanded) ValueAnimator.ofInt(0, height)
                    else ValueAnimator.ofInt(height, 0)

                // 애니메이션이 실행되는 시간, n/1000초
                valueAnimator.duration = 600
                valueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
                    fun onAnimationUpdate(animation: ValueAnimator) {
                        // height값
                        var valueH = animation.animatedValue

                        // imageView의 높이 변경
                        binding.goalItemDetailGoalRecyclerView.layoutParams.height = valueH as Int
                        binding.goalItemDetailGoalRecyclerView.requestLayout()

                        // imageView가 실제로 사라지게 하는 부분
                        binding.goalItemDetailGoalRecyclerView.visibility =
                            if (isExpanded) View.VISIBLE
                            else View.GONE
                    }
                })
                // 애니메이션 시작
                valueAnimator.start()
            }
        }

    // 뷰홀더를 생성해줘야 할 때 호출되는 함수 -> 아이템 뷰 객체를 만들어서 뷰홀더에 줌
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BigGoalViewHolder {
        val binding: ContainerGoalItemRecyclerviewBinding = ContainerGoalItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )

        return BigGoalViewHolder(binding)
    }

    // 뷰홀더에 데이터를 바인딩해줘야 할 때마다 호출되는 함수 => 여러번 호출됨
    override fun onBindViewHolder(holder: BigGoalViewHolder, position: Int) {

        // setupfragment에서 받아온 세부목표 아이템을 세부목표 리사이클러뷰 어댑터로 넘기기
        detailGoalAdapter = DetailGoalRecyclerViewAdapter(detailItems)
        holder.binding.goalItemDetailGoalRecyclerView.adapter = detailGoalAdapter
        holder.bind(position, bigItems[position])

        holder.itemView.setOnClickListener {
            bItemClickListener.onItemClick(bigItems[position], position)
        }
        // 클릭 이벤트
        /*holder.binding.goalItemBigGoalLayout.setOnClickListener(View.OnClickListener {
            fun onClick(view: View) {
                if (selected.get(position)) {
                    // 펼쳐진 item 클릭 시
                    selected.delete(position)
                } else {
                    // 직전에 클릭됐던 item의 클릭상태 초기화
                    selected.delete(prePos)
                    // 클릭한 item의 position 저장
                    selected.put(position, true)
                }

                // 해당 포지션의 변화를 알리기
                if (prePos != -1)
                    notifyItemChanged(prePos)
                notifyItemChanged(position)
                // 클릭된 position 저장
                prePos = position
            }
        })*/

       /*holder.binding.goalItemColorImageVIew.setImageResource(bigItems[position].colorImg)    // 색깔
        holder.binding.goalItemBigGoalTextView.text = bigItems[position].bigGoalText           // 대표목표
        holder.binding.goalItemOpenButton.setImageResource(bigItems[position].narrowImg)       // 화살표 이미지

        // 아이콘 리스트
        val iconList: ArrayList<Int> = bigItems[position].iconArray

        // 아이콘 레이아웃 만들기 (아이콘 사이즈, 아이콘의 마진값)
        val iconLayoutParams = LinearLayout.LayoutParams(
            14,
            14
        )
        iconLayoutParams.setMargins(0,0,6,0)

        // 레이아웃에 아이콘, 레이아웃 적용
        for (i in 0 until 6) {

            var icon = ImageView(context)    // 동적 객체 생성
            icon.setImageResource(iconList[i])
            holder.binding.goalItemIconLayout.addView(icon, iconLayoutParams)

            // lateinit var icon: ImageView    // 동적 객체 생성
            // icon.setImageResource(iconList[i])
            // holder.binding.goalItemIconLayout.addView(icon, iconLayoutParams)
        }*/
    }

    // 어댑터 뷰의 자식 뷰들의 개수 리턴
    override fun getItemCount(): Int = bigItems.size

    // 아이템 더하는 함수
    fun addBigGoalItem(bigGoal: BigGoalRecyclerViewItem) {
        bigItems.add(bigGoal)
        notifyDataSetChanged()
    }

    // 아이템을 제거하는 함수
    fun removeBigGoalItem(position: Int) {
        bigItems.removeAt(position)
        notifyDataSetChanged()
    }
}