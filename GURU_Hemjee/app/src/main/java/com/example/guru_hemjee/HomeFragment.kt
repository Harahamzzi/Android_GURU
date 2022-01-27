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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.button.MaterialButton
import org.w3c.dom.Text

class HomeFragment : Fragment() {

    //시작 버튼
    private lateinit var startButton: Button
    //잠금 수정 버튼
    private lateinit var goalSelectButton: MaterialButton

    //잠금 시간 안내
    private var time = "00:00:00"
    private lateinit var goalTime: TextView

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

        //잠금 시간 안내
        goalTime = requireView().findViewById(R.id.goalTime)
        goalTime.text = time.split(':')[0]+"시 " + time.split(':')[1]+"분 " +time.split(':')[2]+"초"

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

    //잠금 시작
    private fun showSettingConfirmPopUp() {
        val dialog = LockSettingConfirmDialog(requireContext(),time)
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

    //잠금 설정
    private fun showLockSettingPopUp() {
        val dialog = LockSettingDialog(requireContext(), goalSelectButton.text.toString(), time)
        dialog.lockSetting()

        dialog.setOnClickedListener(object : LockSettingDialog.ButtonClickListener{
            override fun onClicked(isChanged: Boolean, bigGoalTitle: String?, getTime: String) {
                if(isChanged){
                    goalSelectButton.text = bigGoalTitle
                    time = getTime
                    var timeArray = time.split(':')
                    goalTime.text = timeArray[0] + "시 " + timeArray[1]+"분 " + timeArray[2]+"초"
                }
            }
        })
    }

}