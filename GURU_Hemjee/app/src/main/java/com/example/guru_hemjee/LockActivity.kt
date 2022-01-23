package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar

class LockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
    }

}