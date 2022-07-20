package com.example.guru_hemjee.Home.Fame

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R
import com.example.guru_hemjee.databinding.FragmentFameBinding

class FameFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor

    private var mBinding: FragmentFameBinding? = null // binding변수
    private val binding get() = mBinding!!

    override fun onDestroy() {
        // binding class 인스턴트 참조 정리
        mBinding = null

        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentFameBinding.inflate(inflater, container, false)

        return binding.root
    }
}