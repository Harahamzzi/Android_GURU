package com.harahamzzi.android.Home.Report

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.databinding.ContainerDailyReportBinding
import java.util.ArrayList

// 일간 리포트의 리포트 Recycler view 어댑터
class DailyReportListAdapter(private val context: Context, private val itemList: ArrayList<DailyReportFragment.ReportData>)
    : RecyclerView.Adapter<DailyReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyReportViewHolder {
        val binding: ContainerDailyReportBinding = ContainerDailyReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyReportViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: DailyReportViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.count()

}