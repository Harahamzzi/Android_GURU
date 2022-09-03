package com.example.guru_hemjee.Home.Store

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.guru_hemjee.*
import com.example.guru_hemjee.databinding.FragmentSeedMarketBinding

//씨앗 상점 페이지
//아이템 구매 Fragment 화면
class SeedMarketFragment : Fragment() {

    private var mBinding: FragmentSeedMarketBinding? = null
    private val binding get() = mBinding!!

    //구매 관련
    private var appliedItems = ArrayList<String>() // 햄스터가 입고 있는 아이템 목록
    private var selectedItems = ArrayList<String>() //선택한 아이템 리스트
    private var newPrice = 0 // 구매할 가격

    //인벤토리 리스트 관련
    private var currentInventory: String = "all"

    // 사용 예정 가격 arrayList
    private var priceArrayList = ArrayList<Pair<String, Int>>()

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase
    private lateinit var cursor: Cursor
    private lateinit var hamsterName: String // 햄스터 이름

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSeedMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 기본 정보 가져오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if (cursor.moveToNext()) {
            binding.marketSeedTextView.text = cursor.getString(cursor.getColumnIndex("seed")).toString() // 현재 씨앗 개수
            hamsterName = cursor.getString(cursor.getColumnIndex("hamster_name")).toString() // 햄스터 이름
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 초기화 버튼 클릭 이벤트
        binding.marketRefreshImageButton.setOnClickListener {

        }

        //구매 버튼 클릭 이벤트
        binding.marketBuyButton.setOnClickListener {
            // 사용 예정 씨앗이 보유한 씨앗보다 많다면
            if (newPrice > binding.marketSeedTextView.text.toString().toInt()){
                val dialog = FinalOKDialog(requireContext(), "해바라기 씨 부족!", "확인",
                    false, R.drawable.popup_low_balance, null)
                dialog.alertDialog()
                dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener{
                    override fun onClicked(isConfirm: Boolean) {
                        //내용 없음
                    }
                })
            }
            // 보유한 씨앗이 많다면
            else {
                receiptPopUp() // 영수증 팝업 출력
            }
        }

        // 인벤토리 화면 출력
        upDateInventory(currentInventory)

        // All 버튼 클릭 이벤트
        binding.marketAllButton.setOnClickListener {
            currentInventory = "all"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.marketAllButton.setTextColor(Color.WHITE)
            binding.marketAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
            binding.marketClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
        }

        // 옷 카테고리 버튼 클릭 이벤트
        binding.marketClothImageButton.setOnClickListener {
            currentInventory = "clo"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.marketAllButton.setTextColor(Color.BLACK)
            binding.marketAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
            binding.marketFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
        }

        // 가구 카테고리 버튼 클릭 이벤트
        binding.marketFurnitureImageButton.setOnClickListener {
            currentInventory = "furni"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.marketAllButton.setTextColor(Color.BLACK)
            binding.marketAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
            binding.marketWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
        }

        // 벽지 카테고리 버튼 클릭 이벤트
        binding.marketWallPaperImageButton.setOnClickListener {
            currentInventory = "bg"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.marketAllButton.setTextColor(Color.BLACK)
            binding.marketAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.marketFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.marketWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
        }

        // 아이템들 찾기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 현재 햄스터가 입고 있는 아이템 이름 저장
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_applied = 1",null)
        while (cursor.moveToNext()) {
            appliedItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }
        cursor.close()

        // 사용자가 가지고 있는 아이템 이름 저장
        val bringItems = ArrayList<String>()
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
        while (cursor.moveToNext()) {
            bringItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }
        cursor.close()
        sqlitedb.close()

        /*cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db",null)
        while (cursor.moveToNext()) {
            val int_isApplied = cursor.getInt(cursor.getColumnIndex("is_applied"))
            val int_isUsing = cursor.getInt(cursor.getColumnIndex("is_using"))
            val str_itemName = cursor.getString(cursor.getColumnIndex("item_name"))

            // 현재 햄스터가 입고 있는 아이템 이름 저장
            if (int_isApplied == 1) {
                appliedItems.add(str_itemName)
            }
            // 사용자가 가지고 있는 아이템 이름 저장
            if (int_isUsing == 1) {
                bringItems.add(str_itemName)
            }
        }
        cursor.close()
        sqlitedb.close()
        */

        sqlitedb = dbManager.writableDatabase
        // 사용자가 가지고 있는 아이템 중에서, 현재 햄스터가 입고 있지 않은 아이템을 is_using = 0 으로 만들기
        for (item in bringItems) {
            if (!appliedItems.contains(item))
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
        }
        // 햄스터가 입고 있는 아이템 중에서, 가지고 있지 않거나 입고 있지 않은 아이템 is_using = 1 ????? 왜 이렇게 하는지 의도 파악X
        for (item in appliedItems) {
            if (!bringItems.contains(item))
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
        }
        sqlitedb.close()
        dbManager.close()

        // 배경 및 옷 설정
        FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true)
        FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)

        bringItems.clear()
        selectedItems.addAll(appliedItems) // 현재 사용자가 입고 있는 아이템 목록 추가
    }

    // 영수증 팝업
    private fun receiptPopUp() {
        // 사려는 아이템만 영수증 다이얼로그에 넘기기
        val buyItems = ArrayList<String>()
        for (item in selectedItems) {
            if (!appliedItems.contains(item)) {
                buyItems.add(item)
            }
        }

        // 다이얼 로그에서 넘기기
        val dialog = ReceiptDialog(requireContext(), binding.marketSeedTextView.text.toString(),
            newPrice, buyItems)
        dialog.receiptPop()

        dialog.setOnClickedListener(object : ReceiptDialog.ButtonClickListener {
            override fun onClicked(isBought: Boolean, seed: Int?) {
                if (isBought) {
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    // 구매완료시, bought = 1로 변경 및 아이템 배열에서 삭제
                    for (item in selectedItems) {
                        sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_bought = '1' WHERE item_name = '$item'")
                        for (i in 0 until priceArrayList.size){
                            if (priceArrayList[i].first == item){
                                priceArrayList.removeAt(i)
                                break
                            }
                        }
                    }
                    sqlitedb.close()
                    dbManager.close()

                    // 인벤토리 초기화
                    upDateInventory(currentInventory)

                    // 씨앗 개수 업데이트
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE basic_info_db SET seed = '${seed.toString()}' WHERE hamster_name = '${hamsterName}'")
                    sqlitedb.close()
                    dbManager.close()

                    binding.marketSeedPriceTextView.text = "0"

                    binding.marketSeedTextView.text = seed.toString()

                    // 구매 확인 완료 팝업 띄우기
                    val dialog = FinalOKDialog(requireContext(),"구매 확인", "확인",
                        false, R.drawable.popup_confirm_buy, null)
                    dialog.alertDialog()

                    dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener {
                        override fun onClicked(isConfirm: Boolean) {
                            // 내용 없음
                        }
                    })
                } else {
                    // 구매를 취소했다면 화면 초기화
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase

                    for (item in selectedItems) {
                        sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
                    }
                    for (item in appliedItems) {
                        sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
                    }
                    selectedItems.clear()
                    selectedItems.addAll(appliedItems)
                    sqlitedb.close()
                    dbManager.close()

                    binding.marketSeedPriceTextView.text = "0"
                    upDateInventory(currentInventory)

                    FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true)
                    FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)
                }
            }
        })
    }

    //인밴토리 업데이트
    @SuppressLint("Range")
    private fun upDateInventory(currentInventoryName: String) {
        // 리스트 초기화
        binding.marketItemRv.removeAllViews()

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        val noBoughtItemList = ArrayList<StoreItem>() // 아이템 뷰 리스트
        // cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db", null)

        cursor =
            if (currentInventoryName == "all") {
                sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE " +
                    "is_bought = 0",null)
            }
            else {
                sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE " +
                    "type = '${currentInventoryName}' AND is_bought = 0",null)
        }

        while (cursor.moveToNext()) {

           /*val isBought = cursor.getInt(cursor.getColumnIndex("is_bought")) // 0
           val type = cursor.getString(cursor.getColumnIndex("type")).toString() // currentInventoryName
           var marketPic: String
           var price = 0
           var itemName = ""
           var itemCategory = ""
           var id = 0*/

            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
            val itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            /*// 유형에 따라 아이템 목록 가져오기
           // 모든 아이템 목록 찾기
           if (currentInventoryName == "all") {
               if (isBought == 0) {
                   marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                   price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                   itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
                   itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
                   id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

                   // 리스트에 아이템 추가 (사용자가 아직 구입하지 않은 아이템 목록)
                   noBoughtItemList.add(StoreItem(itemName, itemCategory, price, id, false))
               }
           }
           // 카테고리에 따른 아이템 목록 찾기
           else {
               if (type == currentInventoryName && isBought == 0) {
                   marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                   price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                   itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
                   itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
                   id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

                   // 리스트에 아이템 추가 (사용자가 아직 구입하지 않은 아이템 목록)
                   noBoughtItemList.add(StoreItem(itemName, itemCategory, price, id, false))
               }
           }*/

            // 리스트에 아이템 추가 (사용자가 아직 구입하지 않은 아이템 목록)
            noBoughtItemList.add(StoreItem(itemName, itemCategory, price, id, false))

            // 가격 및 아이템 이름 정보 추가
            priceArrayList.add(Pair(itemName, price))
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()
        
        // 어댑터 생성 및 연결
        val storeItemListAdapter = StoreListAdapter(requireContext(), noBoughtItemList)
        binding.marketItemRv.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.marketItemRv.adapter = storeItemListAdapter

        // 아이템 클릭 이벤트
        storeItemListAdapter.onStoreItemClickListener = { position ->

            // db
            val dbManager2 = DBManager(requireContext(), "hamster_db", null, 1)
            var sqlitedb2 = dbManager2.readableDatabase

            // 값 찾기
            val itemName = storeItemListAdapter.getItemList(position).itemName
            val itemCategory = storeItemListAdapter.getItemList(position).category

            // 햄찌가 시착중이지 않은 아이템 목록
            val deselectItems = ArrayList<String>()

            // 선택 중인 아이템을 클릭했다면(햄스터가 시착중인 아이템을 클릭했다면)
            if (!selectedItems.contains(itemName)) {
                Log.d("market", "선택 중인 아이템 클릭!")

                // 선택 해제된 배경으로 변경
                storeItemListAdapter.changeItemBG(itemName, true)

                // 같은 카테고리의 아이템이 선택중이면 선택 해제
                val cursor2: Cursor = sqlitedb2.rawQuery("SELECT * FROM hamster_deco_info_db " +
                        "WHERE category = '${itemCategory}'", null)
                while (cursor2.moveToNext()) {
                    val tempName = cursor2.getString(cursor2.getColumnIndex("item_name"))

                    if (selectedItems.contains(tempName) && tempName != itemName) {
                        selectedItems.remove(tempName) // 기존에 선택중이었던 아이템을 리스트 및 db 값 변경
                        deselectItems.add(tempName)
                    }
                }
                cursor2.close()

                selectedItems.add(itemName)
                upDateInventory(currentInventory)

                FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true) // 뷰에 이미지 적용
                FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)

            }
            // 선택안 한 아이템을 클릭했다면
            else {
                Log.d("market", "새로운 아이템 클릭!")

                // 선택된 배경으로 변경
                storeItemListAdapter.changeItemBG(itemName, false)

                // 선택된 아이템 리스트에서 해당 아이템 제거
                selectedItems.remove(itemName)
                deselectItems.add(itemName)
            }
            sqlitedb2.close()

            // 인벤토리에 반영
            sqlitedb2 = dbManager.writableDatabase
            for (item in selectedItems) {
                sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
            }
            for (item in deselectItems) {
                sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
            }
            sqlitedb2.close()
            dbManager2.close()

            deselectItems.clear()
            priceReset() // 가격 초기화
            FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true) // 뷰에 이미지 적용
            FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)

            /** **/

            /*// db
            val dbManager2 = DBManager(requireContext(), "hamster_db", null, 1)
            val sqlitedb2 = dbManager2.writableDatabase

            // 값 찾기
            val itemName = storeItemListAdapter.getItemList(position).itemName
            val itemCategory = storeItemListAdapter.getItemList(position).category
            val storeItem = storeItemListAdapter.getItemList(position)

            // 햄찌가 시착중이지 않은 아이템 목록
            val deselectItems = ArrayList<String>()

            // 선택 중인 아이템을 클릭했다면(햄스터가 시착중인 아이템을 클릭했다면)
            if (storeItem.isSelected) {
                Log.d("market", "선택 중인 아이템 클릭!")

                // 선택 해제된 배경으로 변경
                storeItemListAdapter.changeItemBG(position, false)
                // 선택된 아이템 리스트에서 해당 아이템 제거
                selectedItems.remove(itemName)
                deselectItems.add(itemName)
                //sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${itemName}'")
            }
            // 선택이 안 된 아이템을 클릭했다면(햄스터가 안 입고 있는 아이템을 클릭했다면)
            else {
                Log.d("market", "선택 안된 아이템 클릭!")
                // 같은 카테고리의 아이템이 선택 중이면 선택해제
                for (i in 0 until storeItemListAdapter.itemCount) {
                    Log.d("market", "아이템 개수 : " + storeItemListAdapter.itemCount.toString())
                    // 동일 아이템이 아니고, 선택된 아이템에 포함되어 있다면(전혀 다른 아이템을 선택했다면)
                    val deleteItem = storeItemListAdapter.getItemList(i).itemName
                    if (itemName != deleteItem && selectedItems.contains(itemName)) {
                        if (itemCategory == storeItemListAdapter.getItemList(i).category)
                        {
                            // 기존에 선택중이었던 아이템을 리스트 및 db 값 변경
                            selectedItems.remove(deleteItem)
                            //sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${deleteItem}'")
                            deselectItems.add(deleteItem)
                            break
                        }
                    }
                }
                // 선택된 아이템 리스트 및 db에 추가
                Log.d("market", "아이템 추가 : $itemName")
                selectedItems.add(itemName)
                upDateInventory(currentInventory)
                //sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${itemName}'")
                // 선택된 배경으로 변경
                storeItemListAdapter.changeItemBG(position, true)
            }

            deselectItems.clear()
            priceReset() // 가격 초기화
            FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true) // 뷰에 이미지 적용
            FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)

            sqlitedb2.close()
            dbManager2.close()*/
            /***************/
        /*// 선택 해제한 아이템 리스트
               val deselectItems = ArrayList<String>()
               // 값 찾기
               val itemName = storeItemListAdapter.getItemList(position).itemName
               val itemCategory = storeItemListAdapter.getItemList(position).category
               // 선택이 안 된 아이템을 클릭했다면
               if (!selectedItems.contains(itemName)) {
                   // 선택된 배경으로 변경
                   storeItemListAdapter.changeItemBG(storeItemListAdapter.getItemList(position), true)
                   // 같은 카테고리의 아이템이 선택 중이면 선택해제
                   while (cursor.moveToNext()) {
                       val tempName = cursor.getString(cursor.getColumnIndex("item_name"))
                       val tempCategory = cursor.getString(cursor.getColumnIndex("category"))
                       if (selectedItems.contains(tempName) && tempName != itemName) {
                           if (itemCategory == tempCategory) {
                               selectedItems.remove(tempName)
                               deselectItems.add(tempName)
                           }
                       }
                   }
                   selectedItems.add(itemName)
                   upDateInventory(currentInventory)
                   FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true)
                   FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)
               }
               // 선택된 아이템을 클릭했다면
               else {
                   // 선택 해제된 배경으로 변경
                   storeItemListAdapter.changeItemBG(storeItemListAdapter.getItemList(position), false)
                   selectedItems.remove(itemName)
                   deselectItems.add(itemName)
               }
               // 화면 표시
               for (item in selectedItems) {
                   sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
               }
               for (item in deselectItems) {
                   sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
               }
               deselectItems.clear()
               priceReset() // 가격 초기화
               FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true) // 뷰에 이미지 적용
               FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)*/
        }

        /********************/


        /*// 리스트 초기화
        binding.marketItemRv.removeAllViews()

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        /*// 유형에 따라 아이템 목록 가져오기
        cursor = // 모든 아이템 목록 찾기
            if (currentInventoryName == "all") {
            sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE " +
                    "is_bought = 0",null)
            }
            // 카테고리에 따른 아이템 목록 찾기
            else {
            sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE " +
                    "type = '${currentInventoryName}' AND is_bought = 0",null)
        }*/

        var rowIdx = 0
        val viewList = ArrayList<View>() // 아이템 뷰 리스트
        val sqlitedb2 = dbManager.writableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db", null)
        while (cursor.moveToNext()) {

            val isBought = cursor.getInt(cursor.getColumnIndex("is_bought")) // 0
            val type = cursor.getString(cursor.getColumnIndex("type")).toString() // currentInventoryName
            var marketPic: String
            var price = 0
            var itemName = ""
            var itemCategory = ""
            var id = 0

            // 유형에 따라 아이템 목록 가져오기
            // 모든 아이템 목록 찾기
            if (currentInventoryName == "all") {
                if (isBought == 0) {
                    marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                    price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                    itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
                    itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
                    id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)
                }
            }
            // 카테고리에 따른 아이템 목록 찾기
            else {
                if (type == currentInventoryName && isBought == 0) {
                    marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                    price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                    itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
                    itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
                    id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)
                }
            }

            // 동적 뷰 생성
            val view: View = layoutInflater.inflate(R.layout.container_market_item, binding.marketItemTableLayout, false)

            val itemIv = view.findViewById<ImageView>(R.id.container_market_item_iv)
            val priceTv = view.findViewById<TextView>(R.id.container_market_item_tv)

            // 아이템 이미지 넣기
            itemIv.setImageResource(id)
            if (selectedItems.contains(itemName)) {
                // 햄스터가 착용하고 있는 아이템 이라면
                itemIv.setBackgroundResource(R.drawable.solid_market_selected_box)
            } else {
                // 햄스터가 안 착용하고 있는 아이템 이라면
                itemIv.setBackgroundResource(R.drawable.solid_market_unselected_box)
            }
            priceTv.text = price.toString() // 가격 정보 넣기

            // 아이템 클릭 이벤트
            view.setOnClickListener {
                // 선택 해제한 아이템 리스트
                val deselectItems = ArrayList<String>()

                //가격 받아오기
                var price = priceTv.text.toString().toInt()

                // 선택이 안 된 아이템을 클릭했다면
                if (!selectedItems.contains(itemName)) {
                    itemIv.setBackgroundResource(R.drawable.solid_market_selected_box)

                    // 같은 카테고리의 아이템이 선택 중이면 선택해제
                    while (cursor.moveToNext()) {
                        val tempName = cursor.getString(cursor.getColumnIndex("item_name"))
                        val tempCategory = cursor.getString(cursor.getColumnIndex("category"))

                        if (selectedItems.contains(tempName) && tempName != itemName) {
                            if (itemCategory == tempCategory) {
                                selectedItems.remove(tempName)
                                deselectItems.add(tempName)
                            }
                        }
                    }

                    selectedItems.add(itemName)

                    upDateInventory(currentInventory)
                    FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true)
                    FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)
                }
                // 선택된 아이템을 클릭했다면
                else {
                    itemIv.setBackgroundResource(R.drawable.solid_market_unselected_box)
                    selectedItems.remove(itemName)
                    deselectItems.add(itemName)
                }

                // 화면 표시
                for (item in selectedItems) {
                    sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
                }
                for (item in deselectItems) {
                    sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
                }

                deselectItems.clear()
                priceReset()
                FunUpDateHamzzi.updateBackground(requireContext(), binding.marketBGFrameLayout, true, true)
                FunUpDateHamzzi.updateCloth(requireContext(), binding.marketClothFrameLayout, true, true)
            }
            priceArrayList.add(Pair(itemName, price))

            viewList.add(view)
            ++rowIdx

            binding.marketItemTableLayout.addView(view)

            /*if (rowIdx == 4) {
                // 동적 row 생성 및 뷰 추가
                val tableRow = TableRow(requireContext())
                tableRow.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                for (i in 0 until 4) {
                    tableRow.addView(viewList[i])
                }
                binding.marketItemTableLayout.addView(tableRow)
                rowIdx = 0
            }*/
        }
        cursor.close()
        sqlitedb2.close()
        sqlitedb.close()
        dbManager.close()*/
    }

    // db 모음
    /*private fun runDB(sqlitedb2: SQLiteDatabase, itemName: String, deleteItem: String, itemName2: String) {

        // 선택중인 아이템을 클릭했을 때, 해당 아이템 제거
        if (itemName != "null") {

        }
        // 기존에 선택중이던 아이템이 있을 때, 새로운 아이템을 클릭했다면 기존 아이템 선택 해제
        else if (deleteItem != "null") {

        }
        // 기존에 선택중이던 아이템이 있을 때, 새로운 아이템을 클릭했다면 새로운 아이템 선택 ON
        if (itemName2 != "null") {

        }
    }*/

    // 가격 조정 함수
    @SuppressLint("SetTextI18n")
    private fun priceReset() {
        newPrice = 0 // 선택한 아이템들을 더한 가격

        // 선택된 아이템의 가격만 추가
        for (item in selectedItems) {
            for (i in 0 until priceArrayList.size) {
                if (priceArrayList[i].first == item) {
                    newPrice += priceArrayList[i].second
                    break
                }
            }
        }

        // 사용 예정 씨앗 개수 변경
        if (newPrice.toString().length > 4) {
            binding.marketSeedPriceTextView.text = "9999+"
        }
        else {
            binding.marketSeedPriceTextView.text = newPrice.toString()
        }

        // 사용 예정 씨앗 관리 (보유 씨앗 갯수를 초과하면 붉은색으로)
        if (newPrice > binding.marketSeedTextView.text.toString().toInt()){
            binding.marketSeedPriceTextView.setTextColor(Color.RED)
        }
        else {
            binding.marketSeedPriceTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Black))
        }
    }
}