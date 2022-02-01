package com.example.guru_hemjee

import android.content.ContentValues.TAG
import android.hardware.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.util.Log
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files.exists
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")    // 사용하지 말아야 할 메소드 관련 경고 억제
class CameraActivity : AppCompatActivity() {

    private var camera: Camera? = null
    private var preview: CameraPreview? = null

    private lateinit var captureButton: ImageButton // 촬영 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 켜진 상태를 유지
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_camera)

        // 카메라 인스턴스 생성
        camera = getCameraInstance()

        // 카메라 preview 생성
        preview = camera?.let {
            CameraPreview(this, it)
        }

        // preview 뷰의 content를 현재 액티비티로 설정..?
        preview?.also {
            val previewContainer: FrameLayout = findViewById(R.id.cameraPreviewContainer)
            previewContainer.addView(it)    // camera preview 넣기(띄우기)
        }

        // 촬영 버튼 연결 및 리스너 설정
        captureButton = findViewById(R.id.captureImageButton)
        captureButton.setOnClickListener {
            camera?.takePicture(null, null, mPicture)
        }
    }

    override fun onPause() {
        super.onPause()

        // 카메라 인스턴스 해제
        camera?.release()
        camera = null
      }

    // 카메라 인스턴스를 가져와 카메라 액세스를 요청하는 함수
    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open()   // 후면 카메라부터 액세스
        }
        catch (e: Exception) {
            null
        }
    }

    // 카메라에서 수신한 이미지를 저장하기 위한 콜백 인터페이스
    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile: File = getOutputMediaFile() ?: run {
            Log.d(TAG, ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        }
    }

    // 이미지를 저장하기 위한 파일 경로를 생성하는 함수
    private fun getOutputMediaFileUri(): Uri {
        return Uri.fromFile(getOutputMediaFile())
    }

    // 이미지를 저장하기 위한 파일을 생성하는 함수
    private fun getOutputMediaFile(): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(
            // 사진 저장을 위한 기본, 공유 및 권장 위치를 반환함
            // 사용자가 앱을 제거해도 이 위치에 저장된 미디어 파일은 삭제되지 않음
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp"
        )
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory")
                    return null
                }
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        return File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
    }
}