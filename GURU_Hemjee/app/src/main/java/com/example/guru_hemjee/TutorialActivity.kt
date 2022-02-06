package com.example.guru_hemjee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar

class TutorialActivity : AppCompatActivity() {

    //튜토리얼 초기 화면
    private lateinit var startImageView: ImageView

    //이름 입력 칸, 튜토리얼 끝내기
    private lateinit var nameEditTextView: EditText
    private lateinit var backButton: Button

    //튜토리얼 안내 이미지
    private var imageButton = ArrayList<ImageButton>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        // 액션바 숨기기
        var actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        //초기화면 1.5초 후 종료하기
        startImageView = findViewById(R.id.startImageView)
        Handler().postDelayed({
            startImageView.visibility = View.GONE
        }, 1500L)


        for(i in 1..20){
            imageButton.add(findViewById(resources.getIdentifier("tutorial${i}ImageButton", "id", packageName)))
            imageButton[i-1].setOnClickListener {
                it.visibility = View.INVISIBLE
            }
        }

        nameEditTextView = findViewById(R.id.tutorialHamsterNameEditText)
        backButton = findViewById(R.id.goBackButton)
        backButton.setOnClickListener {
            var dbManager = DBManager(this, "hamster_db", null, 1)
            var sqlitedb = dbManager.writableDatabase

            sqlitedb.execSQL("INSERT INTO basic_info_db VALUES('${nameEditTextView.text.toString()}', 0, '00:00:00')")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg1', 10, 'bg', 'bg', 'bg_bg_10', 'market_bg_10', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg2', 1000, 'bg', 'bg', 'bg_bg_1000', 'market_bg_1000', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_toystory', 1500, 'bg', 'bg', 'bg_bg_1500', 'market_bg_1500', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_beach', 1800, 'bg', 'bg', 'bg_bg_1800', 'market_bg_1800', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_snow', 2100, 'bg', 'bg', 'bg_bg_2100', 'market_bg_2100', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_sunset', 2500, 'bg', 'bg', 'bg_bg_2500', 'market_bg_2500', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sandcattle', 390, 'furni1', 'furni', 'bg_furni_390', 'market_furni_390', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant', 390, 'furni', 'furni2', 'bg_furni_390_2', 'market_furni_390_2', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_pics', 560, 'furni', 'furni3', 'bg_furni_560', 'market_furni_560', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_house', 730, 'furni', 'furni5', 'bg_furni_730_2', 'market_furni_730_2', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_surf1', 730, 'furni', 'furni4', 'bg_furni_730', 'market_furni_730', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_surf2', 790, 'furni', 'furni6', 'bg_furni_790', 'market_furni_790', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_snowman', 920, 'furni', 'right', 'bg_furni_920', 'market_furni_920', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_tree1', 1000, 'furni', 'furni8', 'bg_furni_1000', 'market_furni_1000', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_tree2', 1020, 'furni', 'right', 'bg_furni_1020', 'market_furni_1020', '', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_glasses', 390, 'clo', 'gla', 'bg_clo_390', 'market_clo_390', 'hamster_clo_390', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap', 390, 'clo', 'hat', 'bg_clo_390_2', 'market_clo_390_2', 'hamster_clo_390_2', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_workhard', 560, 'clo', 'hat', 'bg_clo_560', 'market_clo_560', 'hamster_clo_560', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs', 560, 'clo', 'hat', 'bg_clo_560_2', 'market_clo_560_2', 'hamster_clo_560_2', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sungla', 730, 'clo', 'hat', 'bg_clo_730', 'market_clo_730', 'hamster_clo_730', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_flower', 730, 'clo', 'hat', 'bg_clo_730_2', 'market_clo_730_2', 'hamster_clo_730_2', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_strap', 420, 'clo', 'clo', 'bg_clo_420', 'market_clo_420', 'hamster_clo_420', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_winter', 710, 'clo', 'clo', 'bg_clo_710', 'market_clo_710', 'hamster_clo_710', 0, 0, 0)")
            sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_summer', 1000, 'clo', 'clo', 'bg_clo_1000', 'market_clo_1000', 'hamster_clo_1000', 0, 0, 0)")

            dbManager.close()
            sqlitedb.close()

            finish()
        }
    }
}