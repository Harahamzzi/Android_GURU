package com.harahamzzi.android.Home.Setting

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harahamzzi.android.R

// 설정 - 도움말 페이지에 쓰이는 ViewPager Adapter
class HelpViewPagerAdapter(context: Context, imageNameList: ArrayList<String>, descriptionList: List<String>): RecyclerView.Adapter<HelpViewPagerAdapter.HelpViewPagerHolder>() {

    private var context = context

    private var imageNameList = imageNameList       // 이미지 파일명 목록
    private var descriptionList = descriptionList   // 설명 텍스트 목록

    inner class HelpViewPagerHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.container_help, parent, false)) {

        val imageView: ImageView = itemView.findViewById(R.id.helpImageView)
        val textView: TextView = itemView.findViewById(R.id.helpTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HelpViewPagerHolder(parent)

    override fun getItemCount(): Int = imageNameList.size

    override fun onBindViewHolder(holder: HelpViewPagerHolder, position: Int) {

        // 이미지 바인딩
        holder.imageView.setImageResource(context.resources.getIdentifier(imageNameList[position], "drawable", context.packageName))

        // 텍스트(설명) 바인딩
        holder.textView.text = descriptionList[position]
    }
}