package com.example.guru_hemjee

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.os.IBinder
import androidx.core.app.NotificationCompat

// 잠금을 실행 했을 때의 잠금 서비스를 구동하기 위한 서비스 클래스
class LockScreenService : Service() {

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null) {
                when(intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
//                        val newIntent = Intent(context, LockActivity::class.java)
//                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        // 잠금화면이 보이는 중에 앱 서랍에서 안 보이도록 하기 위함
//                        newIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//
//                        startActivity(newIntent)
                    }
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val CHANNEL_ID = "channel_1"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "알림 표시를 해제해주세요!", NotificationManager.IMPORTANCE_DEFAULT)
        // 채널 생성
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID).build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, filter)

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}