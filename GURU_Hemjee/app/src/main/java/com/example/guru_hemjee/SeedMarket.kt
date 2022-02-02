package com.example.guru_hemjee

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class SeedMarket : Fragment() {

    //씨앗 관련
    private lateinit var marketSeedTextView: TextView
    private lateinit var marketReducedSeedTextView: TextView

    //구매 관련
    private lateinit var buyImageButton: ImageButton
    private var preselectedItems = ArrayList<String>()//이미 적용되어 있던 아이템 리스트
    private var selectedItems = ArrayList<String>() //선택한 아이템 리스트

    //인벤토리 배경 관련
    private lateinit var bgImageView: ImageView //인벤토리 배경
    private lateinit var clothImageButton: ImageButton //옷 버튼
    private lateinit var furnitureImageButton: ImageButton//기구 버튼
    private lateinit var wallPaperImageButton: ImageButton//배경 버튼

    //인벤토리 리스트 관련
    private lateinit var marketListView: RecyclerView
    private var currentInventory: String = "clo"

    //햄찌 장식(배경) 관련
    private lateinit var marketBGFrameLayout: FrameLayout
    private lateinit var marketClothFrameLayout: FrameLayout

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var hamsterName: String

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
        marketReducedSeedTextView = requireView().findViewById(R.id.toBeUsedSeedTextView)

        //기본 정보 가져오기
        dbManager = DBManager(requireContext(), "basic_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
            marketSeedTextView.text = cursor.getString(cursor.getColumnIndex("seed")).toString()
            hamsterName = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //구매 버튼
        buyImageButton = requireView().findViewById(R.id.buyImageButton)
        buyImageButton.setOnClickListener {
            if(marketReducedSeedTextView.text.toString().toInt() > marketSeedTextView.text.toString().toInt()){
                Toast.makeText(requireContext(), "해바라기 씨가 부족합니다!", Toast.LENGTH_SHORT).show()
            } else {
                receiptPopUp()
            }
        }

        //인벤토리 배경 관련
        bgImageView = requireView().findViewById(R.id.marketInventorybgImageView)
        clothImageButton = requireView().findViewById(R.id.marketClothImageButton)
        furnitureImageButton = requireView().findViewById(R.id.marketFurnitureImageButton)
        wallPaperImageButton = requireView().findViewById(R.id.marketWallPaparImageButton)

        //인벤토리 리스트 뷰
        marketListView = requireView().findViewById(R.id.marketItemList)

        //인벤토리 초기 화면
        //화면 초기화
        upDateInventory(currentInventory)

        clothImageButton.setOnClickListener {
            currentInventory = "clo"
            upDateInventory("clo")
            bgImageView.setImageResource(R.drawable.inventory_cloth)
        }
        furnitureImageButton.setOnClickListener {
            currentInventory = "furni"
            upDateInventory("furni")
            bgImageView.setImageResource(R.drawable.inventory_furniture)
        }
        wallPaperImageButton.setOnClickListener {
            currentInventory = "bg"
            upDateInventory("bg")
            bgImageView.setImageResource(R.drawable.inventory_wallpapare)
        }

        //햄찌 선처리
        dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_applied = 1",null)
        while(cursor.moveToNext()){
            preselectedItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }
        cursor.close()
        var preusingItems = ArrayList<String>()
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
        while(cursor.moveToNext()){
            preusingItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }
        cursor.close()
        sqlitedb.close()

        sqlitedb = dbManager.writableDatabase
        for(item in preusingItems){
            if(!preselectedItems.contains(item))
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
        }
        for(item in preselectedItems){
            if(!preusingItems.contains(item))
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
        }
        sqlitedb.close()
        dbManager.close()

        //햄찌(배경) 업데이트
        marketBGFrameLayout = requireView().findViewById(R.id.marketBGFrameLayout)
        marketClothFrameLayout = requireView().findViewById(R.id.marketClothFrameLayout)
        //햄찌 배경 설정 함수(FunUpDateHamzzi 참고)
        FunUpDateHamzzi.upDate(requireContext(), marketBGFrameLayout, marketClothFrameLayout, true, true)

        preusingItems.clear()
        selectedItems.addAll(preselectedItems)
    }


    //영수증 팝업
    private fun receiptPopUp() {
        //사려는 아이템만 영수증 다이얼로그에 넘기기
        var buyItems = ArrayList<String>()
        for(item in selectedItems){
            if(!preselectedItems.contains(item)){
                buyItems.add(item)
            }
        }

        //다이얼 로그에서 넘기기
        val dialog = ReceiptDialog(requireContext(), marketSeedTextView.text.toString(), marketReducedSeedTextView.text.toString(), buyItems)
        dialog.receiptPop()

        dialog.setOnClickedListener(object : ReceiptDialog.ButtonClickListener {
            override fun onClicked(isBought: Boolean, seed: Int?) {
                if (isBought) {
                    //여기에 구매 완료시 필요한 연산
                    //인벤토리 관련
                    dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    for (item in selectedItems) {
                        sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_bought = '1' WHERE item_name = '$item'")
                    }
                    selectedItems.clear()
                    sqlitedb.close()
                    dbManager.close()

                    upDateInventory(currentInventory)

                    //씨앗 관련
                    dbManager = DBManager(requireContext(), "basic_info_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE basic_info_db SET seed = '${seed.toString()}' WHERE hamster_name = '${hamsterName}'")
                    sqlitedb.close()
                    dbManager.close()

                    marketReducedSeedTextView.text = "0"


                    marketSeedTextView.text = seed.toString()

                    //구매 확인 완료 팝업
                    val dialog = FinalOKDialog(requireContext(),"구매 확인", "확인", false, R.drawable.popup_confirm_buy, null)
                    dialog.alertDialog()

                    dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener{
                        override fun onClicked(isConfirm: Boolean) {
                            //내용 없음
                        }
                    })
                } else {
                    //화면 초기화
                    dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    for(item in selectedItems){
                        sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
                    }
                    for(item in preselectedItems){
                        sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
                    }
                    selectedItems.clear()
                    selectedItems.addAll(preselectedItems)
                    sqlitedb.close()
                    dbManager.close()

                    marketReducedSeedTextView.text = "0"
                    upDateInventory(currentInventory)

                    FunUpDateHamzzi.upDate(requireContext(), marketBGFrameLayout, marketClothFrameLayout, true, true)
                }
            }
        })
    }

    //인밴토리 업데이트
    private fun upDateInventory(name: String) {
        val items = ArrayList<MarketItem>()
        //어뎁터 생성: itemClick 함수 정의(MarketItemAdapter 참고)
        val marketItemAdapter = MarketItemAdapter(requireContext(), items) { item, isClicked ->
            var deselectItems = ArrayList<String>()
            if(isClicked){
                //같은 유형(옷끼리, 모자끼리, 배경끼리) 겹치지 않게 하기
                dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
                sqlitedb = dbManager.readableDatabase
                val cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE category = '${item.category}'", null)
                while(cursor.moveToNext()){
                    val tempName = cursor.getString(cursor.getColumnIndex("item_name"))
                    val tempPrice = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()

                    if(selectedItems.contains(tempName) && tempName != item.name) {
                        selectedItems.remove(tempName)
                        deselectItems.add(tempName)
                        if(!preselectedItems.contains(tempName))
                            marketReducedSeedTextView.text = (marketReducedSeedTextView.text.toString().toInt() - tempPrice).toString()
                    }
                }
                cursor.close()
                sqlitedb.close()
                dbManager.close()

                selectedItems.add(item.name)
                //사용 예정 씨앗 관리(씨앗 +-, 구매 버튼 비활성화)
                marketReducedSeedTextView.text = (marketReducedSeedTextView.text.toString().toInt() + item.price).toString()
                if(marketReducedSeedTextView.text.toString().toInt() > marketSeedTextView.text.toString().toInt()){
                    marketReducedSeedTextView.setTextColor(Color.RED)
                } else {
                    marketReducedSeedTextView.setTextColor(Color.parseColor("#2E2925"))
                }

                upDateInventory(currentInventory)
                FunUpDateHamzzi.upDate(requireContext(), marketBGFrameLayout, marketClothFrameLayout, true, true)
            }
            //요소 클릭 종료
            else {
                selectedItems.remove(item.name)
                deselectItems.add(item.name)
                //사용 예정 씨앗 관리(씨앗 +-, 구매 버튼 비활성화)
                marketReducedSeedTextView.text = (marketReducedSeedTextView.text.toString().toInt() - item.price).toString()
                if(marketReducedSeedTextView.text.toString().toInt() > marketSeedTextView.text.toString().toInt()){
                    marketReducedSeedTextView.setTextColor(Color.RED)
                } else {
                    marketReducedSeedTextView.setTextColor(Color.parseColor("#2E2925"))
                }
            }

            //화면 표시
            dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
            sqlitedb = dbManager.writableDatabase
            for(item in selectedItems){
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
            }
            for(item in deselectItems){
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
            }
            sqlitedb.close()
            dbManager.close()

            deselectItems.clear()
            FunUpDateHamzzi.upDate(requireContext(), marketBGFrameLayout, marketClothFrameLayout, true, true)
        }
        marketListView.adapter = marketItemAdapter

        dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        //유형에 따라 list 가져오기
        val cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = '${name}' AND is_bought = 0",null)

        while(cursor.moveToNext()){
            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
            val itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            items.addAll(listOf(MarketItem(id, price, itemName, itemCategory, selectedItems.contains(itemName))))

            marketItemAdapter.notifyDataSetChanged() // 리스트 갱신
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

}