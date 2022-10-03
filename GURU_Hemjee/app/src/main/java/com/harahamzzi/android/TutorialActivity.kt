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
        for (i in 1..20) {
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
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg1', 1000, 'bg', 'bg', 'bg_bg_1000', 'market_bg_1000', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_airport', 1500, 'bg', 'bg', 'bg_bg_1500', 'market_bg_1500', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_line', 1800, 'bg', 'bg', 'bg_bg_1800', 'market_bg_1800', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_desert', 2100, 'bg', 'bg', 'bg_bg_2100', 'market_bg_2100', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_sunset', 2500, 'bg', 'bg', 'bg_bg_2500', 'market_bg_2500', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_sun', 2800, 'bg', 'bg', 'bg_bg_2800', 'market_bg_2800', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_darkyellow', 390, 'furni', 'furni1', 'bg_furni_390_darkyellow', 'market_furni_390_darkyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_green', 390, 'furni', 'furni1', 'bg_furni_390_green', 'market_furni_390_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_lightyellow', 390, 'furni', 'furni1', 'bg_furni_390_lightyellow', 'market_furni_390_lightyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_lamp_orange', 390, 'furni', 'furni1', 'bg_furni_390_orange', 'market_furni_390_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_darkyellow', 560, 'furni', 'furni2', 'bg_furni_560_darkyellow', 'market_furni_560_darkyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_green', 560, 'furni', 'furni2', 'bg_furni_560_green', 'market_furni_560_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_lightyellow', 560, 'furni', 'furni2', 'bg_furni_560_lightyellow', 'market_furni_560_lightyellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_biglamp_orange', 560, 'furni', 'furni2', 'bg_furni_560_orange', 'market_furni_560_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_blue', 730, 'furni', 'furni3', 'bg_furni_730_blue', 'market_furni_730_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_darkpurple', 730, 'furni', 'furni3', 'bg_furni_730_darkpurple', 'market_furni_730_darkpurple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_green', 730, 'furni', 'furni3', 'bg_furni_730_green', 'market_furni_730_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant_purple', 730, 'furni', 'furni3', 'bg_furni_730_purple', 'market_furni_730_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_chair_blue', 790, 'furni', 'furni4', 'bg_furni_790_blue', 'market_furni_790_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_chair_green', 790, 'furni', 'furni4', 'bg_furni_790_green', 'market_furni_790_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_chair_yellow', 790, 'furni', 'furni4', 'bg_furni_790_yellow', 'market_furni_790_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_mirror_brown', 920, 'furni', 'furni5', 'bg_furni_920_brown', 'market_furni_920_brown', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_mirror_gray', 920, 'furni', 'furni5', 'bg_furni_920_gray', 'market_furni_920_gray', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_mirror_red', 920, 'furni', 'furni5', 'bg_furni_920_red', 'market_furni_920_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_shelf_green', 1000, 'furni', 'furni6', 'bg_furni_1000_green', 'market_furni_1000_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_shelf_purple', 1000, 'furni', 'furni6', 'bg_furni_1000_purple', 'market_furni_1000_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_shelf_yellow', 1000, 'furni', 'furni6', 'bg_furni_1000_yellow', 'market_furni_1000_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sofa_green', 1020, 'furni', 'front_furni', 'bg_furni_1020_green', 'market_furni_1020_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sofa_pink', 1020, 'furni', 'front_furni', 'bg_furni_1020_pink', 'market_furni_1020_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sofa_red', 1020, 'furni', 'front_furni', 'bg_furni_1020_red', 'market_furni_1020_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_black', 390, 'clo', 'hat', 'bg_clo_hair_390_black', 'market_clo_hair_390_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_blue', 390, 'clo', 'hat', 'bg_clo_hair_390_blue', 'market_clo_hair_390_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_red', 390, 'clo', 'hat', 'bg_clo_hair_390_red', 'market_clo_hair_390_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap_white', 390, 'clo', 'hat', 'bg_clo_hair_390_white', 'market_clo_hair_390_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_band_green', 390, 'clo', 'hat', 'bg_clo_hair_390_green', 'market_clo_hair_390_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_band_pink', 390, 'clo', 'hat', 'bg_clo_hair_390_pink', 'market_clo_hair_390_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_band_white', 390, 'clo', 'hat', 'bg_clo_hair_390_white2', 'market_clo_hair_390_white2', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_blue', 560, 'clo', 'hat', 'bg_clo_hair_560_blue', 'market_clo_hair_560_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_orange', 560, 'clo', 'hat', 'bg_clo_hair_560_orange', 'market_clo_hair_560_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_purple', 560, 'clo', 'hat', 'bg_clo_hair_560_purple', 'market_clo_hair_560_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs_white', 560, 'clo', 'hat', 'bg_clo_hair_560_white', 'market_clo_hair_560_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_flower_blue', 730, 'clo', 'hat', 'bg_clo_hair_730_blue', 'market_clo_hair_730_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_flower_pink', 730, 'clo', 'hat', 'bg_clo_hair_730_pink', 'market_clo_hair_730_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sunglass_green', 730, 'clo', 'hat', 'bg_clo_hair_730_green', 'market_clo_hair_730_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sunglass_purple', 730, 'clo', 'hat', 'bg_clo_hair_730_purple', 'market_clo_hair_730_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sunglass_yellow', 730, 'clo', 'hat', 'bg_clo_hair_730_yellow', 'market_clo_hair_730_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_black', 420, 'clo', 'upper', 'bg_clo_upper_420_black', 'market_clo_upper_420_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_green', 420, 'clo', 'upper', 'bg_clo_upper_420_green', 'market_clo_upper_420_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_red', 420, 'clo', 'upper', 'bg_clo_upper_420_red', 'market_clo_upper_420_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_white', 420, 'clo', 'upper', 'bg_clo_upper_420_white', 'market_clo_upper_420_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_yellow', 420, 'clo', 'upper', 'bg_clo_upper_420_yellow', 'market_clo_upper_420_yellow', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_black', 560, 'clo', 'upper', 'bg_clo_upper_560_black', 'market_clo_upper_560_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_green', 560, 'clo', 'upper', 'bg_clo_upper_560_green', 'market_clo_upper_560_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_shirt_blue', 710, 'clo', 'upper', 'bg_clo_upper_710_blue', 'market_clo_upper_710_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_shirt_pink', 710, 'clo', 'upper', 'bg_clo_upper_710_pink', 'market_clo_upper_710_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_vest_blue', 780, 'clo', 'upper', 'bg_clo_upper_780_blue', 'market_clo_upper_780_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_vest_purple', 780, 'clo', 'upper', 'bg_clo_upper_780_purple', 'market_clo_upper_780_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_green', 1000, 'clo', 'upper', 'bg_clo_upper_1000_green', 'market_clo_upper_1000_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_orange', 1000, 'clo', 'upper', 'bg_clo_upper_1000_orange', 'market_clo_upper_1000_orange', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_pink', 1000, 'clo', 'upper', 'bg_clo_upper_1000_pink', 'market_clo_upper_1000_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cape_red', 1000, 'clo', 'upper', 'bg_clo_upper_1000_red', 'market_clo_upper_1000_red', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_black', 390, 'clo', 'bottom', 'bg_clo_bottom_390_black', 'market_clo_bottom_390_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_blue', 390, 'clo', 'bottom', 'bg_clo_bottom_390_blue', 'market_clo_bottom_390_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_purple', 390, 'clo', 'bottom', 'bg_clo_bottom_390_purple', 'market_clo_bottom_390_purple', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_running_bottom_white', 390, 'clo', 'bottom', 'bg_clo_bottom_390_white', 'market_clo_bottom_390_white', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_bottom_black', 420, 'clo', 'bottom', 'bg_clo_bottom_420_black', 'market_clo_bottom_420_black', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_bottom_pink', 420, 'clo', 'bottom', 'bg_clo_bottom_420_pink', 'market_clo_bottom_420_pink', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_train_running_bottom_green', 420, 'clo', 'bottom', 'bg_clo_bottom_420_green', 'market_clo_bottom_420_green', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_skirt_blue', 420, 'clo', 'bottom', 'bg_clo_bottom_420_blue', 'market_clo_bottom_420_blue', 0, 0, 0)")
                sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_skirt_purple', 420, 'clo', 'bottom', 'bg_clo_bottom_420_purple', 'market_clo_bottom_420_purple', 0, 0, 0)")

                dbManager.close()
                sqlitedb.close()

                finish()
            }
        }
    }
}