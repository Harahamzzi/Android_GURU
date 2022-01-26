package com.example.guru_hemjee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    //시작 버튼
    private lateinit var startButton: Button
    //잠금 수정 버튼
    private lateinit var goalSelectButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //lock 화면 연결
        startButton = requireView().findViewById(R.id.startButton)
        startButton.setOnClickListener {
            showSettingConfirmPopUp()
        }

        //잠금 수정
        goalSelectButton = requireView().findViewById(R.id.goalSelectButton)
        goalSelectButton.setOnClickListener {
            showLockSettingPopUp()
        }
    }

    private fun showSettingConfirmPopUp() {
        val dialog = LockSettingConfirmDialog(requireContext())
        dialog.myDig()

        dialog.setOnClickedListener(object : LockSettingConfirmDialog.ButtonClickListener{
            override fun onClicked(isLock: Boolean) {
                if(isLock){
                    var intent = Intent(requireContext(), LockActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    private fun showLockSettingPopUp() {
        val dialog = LockSettingDialog(requireContext(), null, null)
        dialog.lockSetting()


    }

}