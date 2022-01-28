package com.example.guru_hemjee

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class SeedMarket : Fragment() {

    //씨앗 관련
    private lateinit var marketSeedTextView: TextView

    //구매 버튼
    private lateinit var buyImageButton: ImageButton

    //인벤토리 배경 관련
    private lateinit var bgImageView: ImageView //인벤토리 배경
    private lateinit var clothImageButton: ImageButton //옷 버튼
    private lateinit var furnitureImageButton: ImageButton//기구 버튼
    private lateinit var wallPaperImageButton: ImageButton//배경 버튼

    //인벤토리 리스트 관련
    private lateinit var marketListView: RecyclerView

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seed_market, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //씨앗 관련
        marketSeedTextView = requireView().findViewById(R.id.marketSeedTextView)

        dbManager = DBManager(requireContext(), "basic_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
             marketSeedTextView.text = cursor.getString(cursor.getColumnIndex("seed")).toString()
            userName = cursor.getString(cursor.getColumnIndex("user_name")).toString()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //구매 버튼
        buyImageButton = requireView().findViewById(R.id.buyImageButton)
        buyImageButton.setOnClickListener {
            receiptPopUp()
        }

        //인벤토리 배경 관련
        bgImageView = requireView().findViewById(R.id.marketInventorybgImageView)
        clothImageButton = requireView().findViewById(R.id.marketClothImageButton)
        furnitureImageButton = requireView().findViewById(R.id.marketFurnitureImageButton)
        wallPaperImageButton = requireView().findViewById(R.id.marketWallPaparImageButton)

        //인벤토리 리스트 뷰
        marketListView = requireView().findViewById(R.id.marketItemList)

        //인벤토리 초기 화면
        upDateInventory("cloth")

        clothImageButton.setOnClickListener {
            upDateInventory("cloth")
            bgImageView.setImageResource(R.drawable.inventory_cloth)
        }
        furnitureImageButton.setOnClickListener {
            upDateInventory("furniture")
            bgImageView.setImageResource(R.drawable.inventory_furniture)
        }
        wallPaperImageButton.setOnClickListener {
            upDateInventory("wallpaper")
            bgImageView.setImageResource(R.drawable.inventory_wallpapare)
        }
    }

    private fun receiptPopUp() {
        val dialog = ReceiptDialog(requireContext(), marketSeedTextView.text.toString().toInt(),null)
        dialog.receiptPop()

        dialog.setOnClickedListener(object : ReceiptDialog.ButtonClickListener{
            override fun onClicked(isBought: Boolean, seed: Int?) {
                if(isBought){
                    //여기에 구매 완료시 필요한 연산
                    dbManager = DBManager(requireContext(), "basic_info_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE basic_info_db SET seed = '"+seed.toString()+
                            "' WHERE user_name = '"+userName+"'")
                    sqlitedb.close()
                    dbManager.close()

                    marketSeedTextView.text = seed.toString()

                    //구매 확인 완료 팝업
                    finalOK("구매 완료", "확인", false)
                }
            }
        })
    }

    private fun finalOK(title: String, okString: String, isNeedDrawable: Boolean) {
        val dialog = FinalOK(requireContext(),title, okString, isNeedDrawable)
        dialog.alertDialog()

        dialog.setOnClickedListener(object : FinalOK.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                //내용 없음
            }
        })
    }

    private fun upDateInventory(name: String) {
        //인벤토리 변환
        when(name){
            //옷일 때
            "cloth"-> {
                var items = ArrayList<MarketItem>()
                val marketItemAdapter = MarketItemAdapter(requireContext(), items)
                marketListView.adapter = marketItemAdapter

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
                marketListView.adapter = marketItemAdapter

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
                marketListView.adapter = marketItemAdapter

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