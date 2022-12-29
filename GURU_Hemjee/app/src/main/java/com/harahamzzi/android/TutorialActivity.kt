package com.harahamzzi.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.harahamzzi.android.databinding.ActivityTutorialBinding

// 앱을 처음 실행했을 때 보여주는 튜토리얼 Activity 화면
class TutorialActivity: AppCompatActivity() {

    private var mBinding: ActivityTutorialBinding? = null
    private val binding get() = mBinding!!

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액션바 숨기기
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // 초기화면 1.5초 후 종료하기
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tutorialStartImageView.visibility = View.GONE
        }, 1500L)

        // 튜토리얼 이미지 차례로 띄우기
        val imageButton = ArrayList<ImageButton>()
        for (i in 1..6) {
            imageButton.add(findViewById(resources.getIdentifier("tutorial_${i}ImageButton", "id", packageName)))
            imageButton[i-1].setOnClickListener {
                it.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.tutorialGoBackButton.setOnClickListener {
            if (binding.tutorialHamsterNameEditText.text.toString().isBlank()) {
                Toast.makeText(this, "이름을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                val dbManager = DBManager(this, "hamster_db", null, 1)
                val sqlitedb = dbManager.writableDatabase

                sqlitedb.execSQL("INSERT INTO basic_info_db VALUES('${binding.tutorialHamsterNameEditText.text}', 0, '00:00:00')")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg0', 10, 'bg', 'bg', 'bg_bg_10', 'market_bg_10', 1, 1, 1)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg1', 300, 'bg', 'bg', 'bg_bg_300', 'market_bg_300', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_airport', 500, 'bg', 'bg', 'bg_bg_500', 'market_bg_500', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_line', 700, 'bg', 'bg', 'bg_bg_700', 'market_bg_700', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_desert', 900, 'bg', 'bg', 'bg_bg_900', 'market_bg_900', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_sunset', 1100, 'bg', 'bg', 'bg_bg_1100', 'market_bg_1100', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_sun', 1400, 'bg', 'bg', 'bg_bg_1400', 'market_bg_1400', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_darkyellow', 190, 'furni', 'furni1', 'bg_furni_190_darkyellow', 'market_furni_190_darkyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_green', 190, 'furni', 'furni1', 'bg_furni_190_green', 'market_furni_190_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_lightyellow', 190, 'furni', 'furni1', 'bg_furni_190_lightyellow', 'market_furni_190_lightyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_orange', 190, 'furni', 'furni1', 'bg_furni_190_orange', 'market_furni_190_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_darkyellow', 260, 'furni', 'furni2', 'bg_furni_260_darkyellow', 'market_furni_260_darkyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_green', 260, 'furni', 'furni2', 'bg_furni_260_green', 'market_furni_260_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_lightyellow', 260, 'furni', 'furni2', 'bg_furni_260_lightyellow', 'market_furni_260_lightyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_orange', 260, 'furni', 'furni2', 'bg_furni_260_orange', 'market_furni_260_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_blue', 430, 'furni', 'furni3', 'bg_furni_430_blue', 'market_furni_430_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_darkpurple', 430, 'furni', 'furni3', 'bg_furni_430_darkpurple', 'market_furni_430_darkpurple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_green', 430, 'furni', 'furni3', 'bg_furni_430_green', 'market_furni_430_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_purple', 430, 'furni', 'furni3', 'bg_furni_430_purple', 'market_furni_430_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_chair_blue', 490, 'furni', 'furni4', 'bg_furni_490_blue', 'market_furni_490_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_chair_green', 490, 'furni', 'furni4', 'bg_furni_490_green', 'market_furni_490_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_chair_yellow', 490, 'furni', 'furni4', 'bg_furni_490_yellow', 'market_furni_490_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_mirror_brown', 620, 'furni', 'furni5', 'bg_furni_620_brown', 'market_furni_620_brown', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_mirror_gray', 620, 'furni', 'furni5', 'bg_furni_620_gray', 'market_furni_620_gray', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_mirror_red', 620, 'furni', 'furni5', 'bg_furni_620_red', 'market_furni_620_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_shelf_green', 700, 'furni', 'furni6', 'bg_furni_700_green', 'market_furni_700_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_shelf_purple', 700, 'furni', 'furni6', 'bg_furni_700_purple', 'market_furni_700_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_shelf_yellow', 700, 'furni', 'furni6', 'bg_furni_700_yellow', 'market_furni_700_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sofa_green', 720, 'furni', 'front_furni', 'bg_furni_720_green', 'market_furni_720_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sofa_pink', 720, 'furni', 'front_furni', 'bg_furni_720_pink', 'market_furni_720_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sofa_red', 720, 'furni', 'front_furni', 'bg_furni_720_red', 'market_furni_720_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_black', 190, 'clo', 'hat', 'bg_clo_hair_190_black', 'market_clo_hair_190_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_blue', 190, 'clo', 'hat', 'bg_clo_hair_190_blue', 'market_clo_hair_190_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_red', 190, 'clo', 'hat', 'bg_clo_hair_190_red', 'market_clo_hair_190_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_white', 190, 'clo', 'hat', 'bg_clo_hair_190_white', 'market_clo_hair_190_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_band_green', 190, 'clo', 'hat', 'bg_clo_hair_190_green', 'market_clo_hair_190_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_band_pink', 190, 'clo', 'hat', 'bg_clo_hair_190_pink', 'market_clo_hair_190_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_band_white', 190, 'clo', 'hat', 'bg_clo_hair_190_white2', 'market_clo_hair_190_white2', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_blue', 360, 'clo', 'hat', 'bg_clo_hair_360_blue', 'market_clo_hair_360_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_orange', 360, 'clo', 'hat', 'bg_clo_hair_360_orange', 'market_clo_hair_360_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_purple', 360, 'clo', 'hat', 'bg_clo_hair_360_purple', 'market_clo_hair_360_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_white', 360, 'clo', 'hat', 'bg_clo_hair_360_white', 'market_clo_hair_360_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_flower_blue', 530, 'clo', 'hat', 'bg_clo_hair_530_blue', 'market_clo_hair_530_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_flower_pink', 530, 'clo', 'hat', 'bg_clo_hair_530_pink', 'market_clo_hair_530_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sunglass_green', 530, 'clo', 'hat', 'bg_clo_hair_530_green', 'market_clo_hair_530_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sunglass_purple', 530, 'clo', 'hat', 'bg_clo_hair_530_purple', 'market_clo_hair_530_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sunglass_yellow', 530, 'clo', 'hat', 'bg_clo_hair_530_yellow', 'market_clo_hair_530_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_black', 220, 'clo', 'upper', 'bg_clo_upper_220_black', 'market_clo_upper_220_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_green', 220, 'clo', 'upper', 'bg_clo_upper_220_green', 'market_clo_upper_220_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_red', 220, 'clo', 'upper', 'bg_clo_upper_220_red', 'market_clo_upper_220_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_white', 220, 'clo', 'upper', 'bg_clo_upper_220_white', 'market_clo_upper_220_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_yellow', 220, 'clo', 'upper', 'bg_clo_upper_220_yellow', 'market_clo_upper_220_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_black', 360, 'clo', 'upper', 'bg_clo_upper_360_black', 'market_clo_upper_360_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_green', 360, 'clo', 'upper', 'bg_clo_upper_360_green', 'market_clo_upper_360_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_shirt_blue', 510, 'clo', 'upper', 'bg_clo_upper_510_blue', 'market_clo_upper_510_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_shirt_pink', 510, 'clo', 'upper', 'bg_clo_upper_510_pink', 'market_clo_upper_510_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_vest_blue', 550, 'clo', 'upper', 'bg_clo_upper_550_blue', 'market_clo_upper_550_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_vest_purple', 550, 'clo', 'upper', 'bg_clo_upper_550_purple', 'market_clo_upper_550_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_green', 600, 'clo', 'upper', 'bg_clo_upper_600_green', 'market_clo_upper_600_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_orange', 600, 'clo', 'upper', 'bg_clo_upper_600_orange', 'market_clo_upper_600_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_pink', 600, 'clo', 'upper', 'bg_clo_upper_600_pink', 'market_clo_upper_600_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_red', 600, 'clo', 'upper', 'bg_clo_upper_600_red', 'market_clo_upper_600_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_black', 190, 'clo', 'bottom', 'bg_clo_bottom_190_black', 'market_clo_bottom_190_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_blue', 190, 'clo', 'bottom', 'bg_clo_bottom_190_blue', 'market_clo_bottom_190_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_purple', 190, 'clo', 'bottom', 'bg_clo_bottom_190_purple', 'market_clo_bottom_190_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_white', 190, 'clo', 'bottom', 'bg_clo_bottom_190_white', 'market_clo_bottom_190_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_bottom_black', 220, 'clo', 'bottom', 'bg_clo_bottom_220_black', 'market_clo_bottom_220_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_bottom_pink', 220, 'clo', 'bottom', 'bg_clo_bottom_220_pink', 'market_clo_bottom_220_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_bottom_green', 220, 'clo', 'bottom', 'bg_clo_bottom_220_green', 'market_clo_bottom_220_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_skirt_blue', 220, 'clo', 'bottom', 'bg_clo_bottom_220_blue', 'market_clo_bottom_220_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_skirt_purple', 220, 'clo', 'bottom', 'bg_clo_bottom_220_purple', 'market_clo_bottom_220_purple', 0, 0, 0)")

                dbManager.close()
                sqlitedb.close()

                finish()
            }
        }
    }
}