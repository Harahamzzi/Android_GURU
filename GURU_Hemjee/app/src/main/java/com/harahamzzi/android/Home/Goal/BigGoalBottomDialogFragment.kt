package com.harahamzzi.android.Home.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.harahamzzi.android.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// 아코디언 메뉴에서 대표목표를 롱클릭하면 띄워지는 바텀 시트 다이얼로그
class BigGoalBottomDialogFragment(val itemLongClick: (Int) -> Unit, private val bigGoalItem: BigGoalItem) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.container_big_goal_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 바텀 시트 제목 설정
        view.findViewById<TextView>(R.id.container_bigGoal_title_tv).text = bigGoalItem.title

        // 수정 버튼을 클릭했을 경우
        view.findViewById<LinearLayout>(R.id.container_bigGoal_edit_LLayout).setOnClickListener {
            itemLongClick(0)
            dialog?.dismiss()
        }

        // 삭제 버튼을 클릭했을 경우
        view.findViewById<LinearLayout>(R.id.container_bigGoal_delete_LLayout).setOnClickListener {
            itemLongClick(1)
            dialog?.dismiss()
        }

        // 세부목표 추가 버튼을 클릭했을 경우
        view.findViewById<LinearLayout>(R.id.container_bigGoal_add_LLayout).setOnClickListener {
            itemLongClick(2)
            dialog?.dismiss()
        }

        // 목표 완료 버튼을 클릭했을 경우
        view.findViewById<LinearLayout>(R.id.container_bigGoal_complete_LLayout).setOnClickListener {
            itemLongClick(3)
            dialog?.dismiss()
        }
    }
}