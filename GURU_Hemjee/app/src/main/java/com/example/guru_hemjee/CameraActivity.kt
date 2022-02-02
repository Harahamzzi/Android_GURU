package com.example.guru_hemjee

import android.content.ContentValues.TAG
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")    // 사용하지 말아야 할 메소드 관련 경고 억제
class CameraActivity : AppCompatActivity() {

    // 촬영한 사진을 띄울 이미지 뷰
    private lateinit var imagePreview: ImageView

    // 저장 버튼
    private lateinit var saveButton: ImageButton

    // 카메라 관련..필요한 변수
    private lateinit var currentPhotoPath: String
    private val REQUEST_TAKE_PHOTO = 1

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 켜진 상태를 유지
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_camera)

        // 위젯 연결
        imagePreview = findViewById(R.id.imagePreview)
        saveButton = findViewById(R.id.saveImageButton)
        saveButton.setOnClickListener {
            saveImage() // 이미지 저장
        }

        // 카메라 실행
        captureCamera()
    }

    // 카메라 기능을 실행하는 메소드
    private fun captureCamera() {
        var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 인텐트를 처리할 카메라 액티비티가 있는지 확인
        if(takePictureIntent.resolveActivity(packageManager) != null)
        {
            // 촬영한 사진을 저장할 파일 생성
            var photoFile: File? = null

            try {
                // 임시로 사용할 파일이므로 경로는 캐시 폴더로 함
                var tempDir: File = cacheDir

                // 임시 촬영 파일 세팅
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName: String = timeStamp + "_"

                var tempImage = File.createTempFile(
                    imageFileName,  // 파일 이름
                    ".jpg", // 파일 형식
                    tempDir         // 경로
                )

                // ACTION_VIEW 인텐트를 사용할 경로(임시파일의 경로)
                currentPhotoPath = tempImage.absolutePath
                photoFile = tempImage
            }
            catch(e: IOException)
            {
                Log.e(TAG, "파일 생성 오류")
            }

            // 파일이 정상적으로 생성되었다면 계속 진행
            if(photoFile != null)
            {
                // Uri 가져오기
                var photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    packageName + ".fileprovider",
                    photoFile
                )

                // 인텐트에 Uri 담기
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                // 인텐트 실행
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    // 카메라 촬영할 때 실행되는 함수(Request 내용 받아오기)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            // 사진 촬영 후
            when(requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    if(resultCode == RESULT_OK) {
                        var file = File(currentPhotoPath)
                        var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))

                        if(bitmap != null)
                        {
                            var ei: ExifInterface = ExifInterface(currentPhotoPath)
                            var orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED)

                            var rotatedBitmap: Bitmap? = null
                            when(orientation) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> {
                                    rotatedBitmap = rotateImage(bitmap, 90f)
                                }
                                ExifInterface.ORIENTATION_ROTATE_180 -> {
                                    rotatedBitmap = rotateImage(bitmap, 180f)
                                }
                                ExifInterface.ORIENTATION_ROTATE_270 -> {
                                    rotatedBitmap = rotateImage(bitmap, 270f)
                                }
                                else -> rotatedBitmap = bitmap
                            }

                            // Rotate한 bitmap을 ImageView에 저장
                            imagePreview.setImageBitmap(rotatedBitmap)
                        }
                    }
                }
            }
        }
        catch(e: Exception)
        {
            Log.e(TAG, "onActivityResult 오류")
        }
    }

    // 카메라에 맞게 이미지 로테이션
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(angle)

        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    // 이미지를 저장하는 메소드(api 29 미만..?)
    private fun saveImage() {
        try {
            // 저장할 파일 경로
            var storageDir: File = File(filesDir.toString() + "/picture")
            // 만일 폴더가 없다면 생성하기
            if(!storageDir.exists())
                storageDir.mkdirs()

            var fileName: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"

            // FIXME: DB 데이터 보고 기존에 있으면 삭제
            // 해당 파일이 기존에 있었다면 삭제
            var file = File(storageDir, fileName)
            var deleted: Boolean = file.delete()
            Log.i (TAG, "파일 삭제 체크: " + deleted)

            var output: FileOutputStream? = null

            try {
                output = FileOutputStream(file)
                var drawable: BitmapDrawable = imagePreview.drawable as BitmapDrawable
                var bitmap: Bitmap = drawable.bitmap
                // 해상도에 맞추어 Compress
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output)
            }
            catch(e: FileNotFoundException)
            {
                e.printStackTrace()
            }
            finally {
                try {
                    assert(output != null)
                    output?.close()
                }
                catch(e: IOException)
                {
                    e.printStackTrace()
                }
            }

            Log.i (TAG, "사진 저장 성공")

            // 선택한 세부 목표 이름 가져오기
            var goalName = intent.getStringExtra("detailGoalName")
            // 현재 날짜 가져오기
            var lockDate = Date(System.currentTimeMillis()) // 현재 시간을 Date형으로 가져옴

            // FIXME: 해당 대표 목표의 세부 목표를 가져오는 방법..잘 모르겠음
            // 세부 목표 리포트 DB 가져오기
            dbManager = DBManager(this, "detail_goal_time_report_db", null, 1)
            sqlitedb = dbManager.writableDatabase
            // 날짜 넣기
            sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET lock_date = '$lockDate' WHERE detail_goal_name = '$goalName' AND is_active = 1")
            // 파일명 넣기
            sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET photo_name = '$fileName' WHERE detail_goal_name = '$goalName' AND is_active = 1")

            sqlitedb.close()
            dbManager.close()

            // FIXME: 목표 달성 팝업창으로 바꾸기..?
            // 목표 달성 팝업창
            finalPopup("목표 달성!", "확인", false)
        }
        catch(e: Exception)
        {
            Log.e(TAG, "사진 저장 실패")
        }
    }

    // 마지막 팝업 창(목표 달성!)
    private fun finalPopup(title: String, okString: String, isNeedDrawable: Boolean) {
        val dialog = FinalOKDialog(this,title, okString, isNeedDrawable, R.drawable.popup_goal, "좋아! 끝까지 가보는거다 햄찌!\n해바라기 씨를 위해!")
        dialog.alertDialog()

        dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    // 현재 액티비티 닫기
                    finish()

//                    var intent = Intent(this@CameraActivity, LockActivity::class.java)
//                    intent.putExtra("id", intent.getIntExtra("id", 0))  // 받았던 id 다시 넘기기
//                    startActivity(intent)
                }
            }
        })
    }

    //    // 이미지 저장 메소드(api 29 이상..?)
//    private fun saveImage() {
//        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"
//
//        // ContentValues는 ContentResolver가 처리할 수 있는 값을 저장해 둘 목적으로 사용됨
//        val contentValues = ContentValues()
//        contentValues.apply {
//            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave")    // 경로 설정
//            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)             // 파일 이름을 put
//            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
//            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)    // 저장소 독점 설정
//        }
//
//        // 이미지를 저장할 uri
//        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        try {
//            if(uri != null)
//            {   // write 모드로 file을 open한다
//                val image = contentResolver.openFileDescriptor(uri, "w", null)
//
//                if(image != null)
//                {   // preview에 올려놨던 사진 bitmap으로 가져오기
//                    var drawable: BitmapDrawable = imagePreview.drawable as BitmapDrawable
//                    var bitmap: Bitmap = drawable.bitmap
//
//                    val fos = FileOutputStream(image.fileDescriptor)
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos)
//                    fos.close()
//
//                    contentValues.clear()
//                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)    // 저장소 독점을 해제
//                    contentResolver.update(uri, contentValues, null, null)
//
//                    // 잠금화면으로 다시 이동
//                    var intent = Intent(this, LockActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//        }
//        catch(e: FileNotFoundException) {
//            Log.e("오류태그", "FileNotFoundException")
//            e.printStackTrace()
//        }
//        catch(e: IOException)
//        {
//            Log.e("오류태그", "IOException")
//            e.printStackTrace()
//        }
//        catch(e: Exception)
//        {
//            Log.e("오류태그", "이미지 저장 메소드 Exception")
//            e.printStackTrace()
//        }
//    }
}