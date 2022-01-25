package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.ImageButton

class AvailableAppsDialog(context: Context) {
    private val dialog = Dialog(context)

    fun availableApps(){
        dialog.show()
        dialog.setContentView(R.layout.popup_available_apps)

    }
}