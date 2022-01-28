package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

class HamsterEditFragment() : Fragment() {

    //함께한, 이름 관련
    private lateinit var hamsterNameTextView: TextView
    private lateinit var totalSpentTimeTextView: TextView
    lateinit var userName: String

    //변경 관련 버튼들
    private lateinit var myHNameEditImageButton: ImageButton
    private lateinit var myHamsterApplyImageButton: ImageButton

    //인벤토리 관련 요소들
    private lateinit var myHInventorybgImageView: ImageView
    private lateinit var myHClothImageButton: ImageButton
    private lateinit var myHFurnitureImageButton: ImageButton
    private lateinit var myHWallpaperImageButton: ImageButton

    private lateinit var myHItemList: RecyclerView

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hamster_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //이름, 함께한 시간
        hamsterNameTextView = requireView().findViewById(R.id.hamsterNameTextView)
        totalSpentTimeTextView = requireView().findViewById(R.id.totalSpentTimeTextView)

        dbManager = DBManager(requireContext(), "basic_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
            hamsterNameTextView.text = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
            totalSpentTimeTextView.text = cursor.getString(cursor.getColumnIndex("total_time")).toString()
            userName = cursor.getString(cursor.getColumnIndex("user_name")).toString()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //이름 변경 팝업 연결
        myHNameEditImageButton = requireView().findViewById(R.id.myHNameEditImageButton)
        myHNameEditImageButton.setOnClickListener {
            hNameEditPopUp()
        }

        //적용 버튼
        myHamsterApplyImageButton = requireView().findViewById(R.id.myHamsterApplyImageButton)
        myHamsterApplyImageButton.setOnClickListener {
            hChangeImagePopUp()
        }

        //인벤토리 버튼에 따라 인벤토리 변화
        myHClothImageButton = requireView().findViewById(R.id.myHClothImageButton)
        myHFurnitureImageButton = requireView().findViewById(R.id.myHFurnitureImageButton)
        myHWallpaperImageButton = requireView().findViewById(R.id.myHWallPaperImageButton)
        myHInventorybgImageView = requireView().findViewById(R.id.myHInventoryImageView)

        myHItemList = requireView().findViewById(R.id.myHItemList)

        upDateInventory("cloth")

        myHClothImageButton.setOnClickListener {
            upDateInventory("cloth")
            myHInventorybgImageView.setImageResource(R.drawable.inventory_cloth)
        }
        myHFurnitureImageButton.setOnClickListener {
            upDateInventory("furniture")
            myHInventorybgImageView.setImageResource(R.drawable.inventory_furniture)
        }
        myHWallpaperImageButton.setOnClickListener {
            upDateInventory("wallpaper")
            myHInventorybgImageView.setImageResource(R.drawable.inventory_wallpapare)
        }
    }

    private fun hNameEditPopUp() {
        val dialog = HamsterEditNameDialog(requireContext(), hamsterNameTextView.text.toString())
        dialog.EditName()

        dialog.setOnClickedListener(object : HamsterEditNameDialog.ButtonClickListener{
            override fun onClicked(isChanged: Boolean, name: String?) {
                if(isChanged){
                    //이름 변경 db와 ui에 반영
                    dbManager = DBManager(requireContext(), "basic_info_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    hamsterNameTextView.text = name
                    sqlitedb.execSQL("UPDATE basic_info_db SET hamster_name = '"+hamsterNameTextView.text+
                            "' WHERE user_name = '"+userName+"'")
                }
            }
        })
    }

    private fun hChangeImagePopUp() {
        val dialog = FinalOK(requireContext(), "적용 완료", "확인", false)
        dialog.alertDialog()

        //햄찌 데코 반영
        dialog.setOnClickedListener(object : FinalOK.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                //생략
            }
        })
    }

    private fun upDateInventory(name: String) {
        //인벤토리 변환
        when(name){
            "cloth"-> {
                var items = ArrayList<MarketItem>()
                val marketItemAdapter = MarketItemAdapter(requireContext(), items)
                myHItemList.adapter = marketItemAdapter

                dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
                sqlitedb = dbManager.readableDatabase
                var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'clo'",null)

                var num: Int = 0
                while(cursor.moveToNext()){
                    var marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                    var price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                    var id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

                    items.addAll(listOf(MarketItem(id, price)))

                    marketItemAdapter.notifyDataSetChanged() // 리스트 갱신
                    num++
                    Log.d("현재 num 값", num.toString())
                }
                cursor.close()
                sqlitedb.close()
                dbManager.close()

            }
            "furniture" -> {
                var items = ArrayList<MarketItem>()
                val marketItemAdapter = MarketItemAdapter(requireContext(), items)
                myHItemList.adapter = marketItemAdapter

                dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
                sqlitedb = dbManager.readableDatabase
                var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'furni'",null)

                var num: Int = 0
                while(cursor.moveToNext()){
                    var marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                    var price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                    var id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

                    items.addAll(listOf(MarketItem(id, price)))

                    marketItemAdapter.notifyDataSetChanged() // 리스트 갱신
                    num++
                    Log.d("현재 num 값", num.toString())
                }
                cursor.close()
                sqlitedb.close()
                dbManager.close()

            }
            "wallpaper" -> {
                var items = ArrayList<MarketItem>()
                val marketItemAdapter = MarketItemAdapter(requireContext(), items)
                myHItemList.adapter = marketItemAdapter

                dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
                sqlitedb = dbManager.readableDatabase
                var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = 'bg'",null)

                var num: Int = 0
                while(cursor.moveToNext()){
                    var marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                    var price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                    var id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

                    items.addAll(listOf(MarketItem(id, price)))

                    marketItemAdapter.notifyDataSetChanged() // 리스트 갱신
                    num++
                    Log.d("현재 num 값", num.toString())
                }
                cursor.close()
                sqlitedb.close()
                dbManager.close()

            }
        }
    }
}