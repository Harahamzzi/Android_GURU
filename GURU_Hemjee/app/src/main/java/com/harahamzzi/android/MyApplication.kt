package com.harahamzzi.android

import android.app.Application
import android.content.Context

// 어디서든 applicationContext를 불러올 수 있게 하는 클래스 및 메소드
class MyApplication : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication

        // 어디서든 MyApplication.applicationContext()로 application context에 접근할 수 있게 함
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}