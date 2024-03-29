package com.harahamzzi.android.Home.TimeRecord

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.core.content.FileProvider
import com.harahamzzi.android.DBManager
import com.harahamzzi.android.FinalOKDialog
import com.harahamzzi.android.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

// 홈 화면의 시작 버튼(HomeFragment) -> 잠금 화면(LockActivity) -> 세부 목표 클릭시 -> 카메라 화면
// 카메라 어플을 실행시키고 사진을 저장해 목표 달성 인증을 기록하는 Activity 화면
class CameraActivity : AppCompatActivity() {

    // Log 태그
    private var TAG = "CameraActivity"

    // 카메라 관련..필요한 변수
    private lateinit var currentPhotoPath: String
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var photoURI: Uri

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    private lateinit var goalName: String   // 세부목표 이름

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // 세부목표 이름 가져오기
        goalName = intent.getStringExtra("detailGoalName")!!

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // 상태바 숨기기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) // API 30이상
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else
        {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN )
        }

        // 화면 켜진 상태를 유지
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale("ko", "KR")).format(Date(System.currentTimeMillis()))
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
                e.printStackTrace()
            }

            // 파일이 정상적으로 생성되었다면 계속 진행
            if(photoFile != null)
            {
                // Uri 가져오기
                photoURI = FileProvider.getUriForFile(
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
                            saveImage(rotatedBitmap)
                        }
                    }
                    else if(resultCode == RESULT_CANCELED)
                    {
                        finish()    // 취소 버튼을 눌렀을 때 바로 기록화면으로 이동하게 함
                    }
                }

               else -> {
                   finish() // (폰) 뒤로가기를 눌렀을 때 바로 기록화면으로 이동하게 함
               }
            }
        }
        catch(e: Exception)
        {
            Log.e(TAG, "onActivityResult 오류")
            e.printStackTrace()
        }
    }

    // 카메라에 맞게 이미지 로테이션
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(angle)

        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    // 이미지를 저장하는 메소드
    private fun saveImage(img: Bitmap) {

        lateinit var fileName: String

        try {
            // 저장할 파일 경로
            var storageDir: File = File(filesDir.toString() + "/picture")
            // 만일 폴더가 없다면 생성하기
            if(!storageDir.exists())
                storageDir.mkdirs()

            fileName = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"

            // 해당 파일이 기존에 있었다면 삭제
            var file = File(storageDir, fileName)
            var deleted: Boolean = file.delete()
            Log.i (TAG, "파일 삭제 체크: " + deleted)

            var output: FileOutputStream? = null

            try {
                output = FileOutputStream(file)

                // 해상도에 맞추어 Compress
                img.compress(Bitmap.CompressFormat.JPEG, 70, output)
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
        }
        catch(e: Exception)
        {
            Log.e(TAG, "사진 저장 실패")
            e.printStackTrace()
        }

        // DB 데이터 업데이트
        updateDetailGoalDB(fileName)

        // 목표 달성 팝업창
        finalPopup("10개 획득", "닫기", true)
    }

    // 세부목표 관련 DB 데이터 업데이트
    @SuppressLint("Range")
    private fun updateDetailGoalDB(fileName: String) {
        try {
            // 현재 날짜 가져오기(한국 시간 기준)
            var lockDate = SimpleDateFormat("yyyy-MM-dd-E HH:mm:ss").format(Date(System.currentTimeMillis()))


            /** 세부 목표 리포트 DB 가져오기: 날짜 & 파일명 & 완료 체크 업데이트 **/

            dbManager = DBManager(this, "hamster_db", null, 1)
            sqlitedb = dbManager.writableDatabase

            // 날짜 넣기
            sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET lock_date = '$lockDate' " +
                    "WHERE detail_goal_name = '$goalName' AND is_active = 1")
            // 파일명 넣기
            sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET photo_name = '$fileName' " +
                    "WHERE detail_goal_name = '$goalName' AND is_active = 1")
            // 완료 체크
            sqlitedb.execSQL("UPDATE detail_goal_time_report_db SET is_complete = 1 " +
                    "WHERE detail_goal_name = '$goalName' AND is_active = 1")

            sqlitedb.close()
            dbManager.close()


            /** 세부 목표 DB 가져오기: 완료 횟수 업데이트 **/

            dbManager = DBManager(this, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '$goalName'", null)

            var count = 0

            if (cursor.moveToNext())
            {
                count = cursor.getInt(cursor.getColumnIndex("count")) + 1
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            dbManager = DBManager(this, "hamster_db", null, 1)
            sqlitedb = dbManager.writableDatabase

            sqlitedb.execSQL("UPDATE detail_goal_db SET count = $count WHERE detail_goal_name = '$goalName'")

            sqlitedb.close()
            dbManager.close()

            Log.i(TAG, "DB 데이터 업데이트 완료")
        }
        catch (e: Exception) {
            Log.e(TAG, "세부목표 DB 데이터 업데이트 실패")
            e.printStackTrace()
        }
    }

    // 마지막 팝업 창(목표 달성!)
    private fun finalPopup(title: String, okString: String, isNeedDrawable: Boolean) {
        val dialog = FinalOKDialog(this, title, okString, isNeedDrawable, R.drawable.complete_hamzzi)
        dialog.alertDialog()

        dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener {
            @SuppressLint("Range")
            override fun onClicked(isConfirm: Boolean) {
                if(isConfirm){
                    /** 목표 달성시 수행할 작업 **/

                    // 현재 씨앗 개수 가져오기
                    dbManager = DBManager(this@CameraActivity, "hamster_db", null, 1)
                    sqlitedb = dbManager.readableDatabase
                    var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)

                    var seedPoint = 0

                    if (cursor.moveToNext())
                    {
                        seedPoint = cursor.getInt(cursor.getColumnIndex("seed")) + 10
                    }

                    cursor.close()
                    sqlitedb.close()
                    dbManager.close()

                    // 획득한 씨앗 갱신
                    dbManager = DBManager(this@CameraActivity, "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    sqlitedb.execSQL("UPDATE basic_info_db SET seed = $seedPoint")
                    sqlitedb.close()
                    dbManager.close()

                    // 현재 액티비티 닫기
                    finish()
                }
            }
        })
    }
}