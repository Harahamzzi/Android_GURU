package com.harahamzzi.android.Home.Report

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.databinding.ContainerWeeklyReportBinding
import java.util.ArrayList

// 주간 리포트의 리포트 Recycler view 어댑터
class WeeklyReportListAdapter(private val context: Context, private val itemList: ArrayList<WeeklyReportFragment.ReportData>)
    : RecyclerView.Adapter<WeeklyReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyReportViewHolder {
        val binding: ContainerWeeklyReportBinding = ContainerWeeklyReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeeklyReportViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: WeeklyReportViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.count()
}