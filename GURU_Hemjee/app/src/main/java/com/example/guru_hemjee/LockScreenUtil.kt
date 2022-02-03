package com.example.guru_hemjee

import android.content.Intent

object LockScreenUtil {

    // 잠금 서비스 활성화
    fun active() {
        MyApplication.applicationContext()?.run {
            startForegroundService(Intent(this, LockScreenService::class.java))
        }
    }

    // 잠금 서비스 비활성화
    fun deActive() {
        MyApplication.applicationContext()?.run {
            stopService(Intent(this, LockScreenService::class.java))
        }
    }
}