package com.example.guru_hemjee.Home.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.guru_hemjee.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailGoalBottomDialogFragment(
    val itemClick: (Int) -> Unit,
    private val bigGoalItem: BigGoalItem,
    private val detailGoalItem: DetailGoalItem): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.container_detail_goal_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 바텀 시트 제목 설정
        view.findViewById<TextView>(R.id.container_detailGoal_title_tv).text = detailGoalItem.detailTitle

        // 수정 버튼을 클릭했을 경우
        view.findViewById<LinearLayout>(R.id.container_detailGoal_edit_LLayout).setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }

        // 삭제 버튼을 클릭했을 경우
        view.findViewById<LinearLayout>(R.id.container_detailGoal_delete_LLayout).setOnClickListener {
            itemClick(1)
            dialog?.dismiss()
        }
    }
}