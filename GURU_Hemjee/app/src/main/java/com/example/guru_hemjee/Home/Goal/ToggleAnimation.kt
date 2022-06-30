package com.example.guru_hemjee.Home.Goal

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

// 대표목표 아코디언 메뉴를 클릭했을 경우, 열고 닫고 하는 전환 효과
class ToggleAnimation {

    companion object {
        fun toggleArrow(view: View, isExpanded: Boolean): Boolean {

            // 열려있다면 닫고, 닫혀있다면 열기
            if (isExpanded) {
                view.animate().setDuration(200).rotation(180f)
                return true
            }
            else {
                view.animate().setDuration(200).rotation(0f)
                return false
            }
        }

        // 메뉴 닫힌 상태 -> 열린 상태 전환
        fun expand(view: View) {
            val animation = expandAction(view)
            view.startAnimation(animation)
        }

        private fun expandAction(view: View): Animation {
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val actualHeight = view.measuredHeight

            view.layoutParams.height = 0
            view.visibility = View.VISIBLE

            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {

                    view.layoutParams.height =
                        if (interpolatedTime == 1f) // 애니메이션 동작이 끝났을 경우
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        else
                            (actualHeight * interpolatedTime).toInt()

                    view.requestLayout()
                }
            }

            animation.duration = (actualHeight / view.context.resources.displayMetrics.density).toLong()

            view.startAnimation(animation)

            return animation
        }

        // 메뉴 열린 상태 -> 닫힌 상태 전환
        fun collapse(view: View) {
            val actualHeight = view.measuredHeight
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {

                    if (interpolatedTime == 1f) // 애니메이션 동작이 끝났을 경우
                        view.visibility = View.GONE
                    else {
                        view.layoutParams.height = (actualHeight - (actualHeight * interpolatedTime)).toInt()
                        view.requestLayout()
                    }
                }
            }

            animation.duration = (actualHeight / view.context.resources.displayMetrics.density).toLong()
            view.startAnimation(animation)
        }
    }
}