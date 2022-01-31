package com.example.guru_hemjee

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager

class CameraActivity : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView

    private lateinit var surfaceViewHolder: SurfaceHolder
    private lateinit var imageReader: ImageReader
    private lateinit var cameraDevice: CameraDevice
    private lateinit var previewBuilder: CaptureRequest.Builder
    private lateinit var session: CameraCaptureSession

    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 켜진 상태를 유지
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_camera)

        // view 연결
        surfaceView = findViewById(R.id.cameraSurfaceView)

        initSensor()
        initView()
    }

    // 센서 초기화
    private fun initSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    // 뷰 초기화
    private fun initView() {
        surfaceViewHolder = surfaceView.holder
        surfaceViewHolder.addCallback(object: SurfaceHolder.Callback {
            // 뷰 생성 시점에 호출되는 함수
            override fun surfaceCreated(p0: SurfaceHolder?) {
                TODO("Not yet implemented")
            }

            // 뷰 소멸 시점에 호출되는 함수
            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                TODO("Not yet implemented")
            }

            // 뷰 변동 시점(화면 회전 등)에 호출되는 함수
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }
        })
    }
}