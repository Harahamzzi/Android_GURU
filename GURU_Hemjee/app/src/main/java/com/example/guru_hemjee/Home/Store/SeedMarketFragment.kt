package com.example.guru_hemjee.Home.Store

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.guru_hemjee.*
import com.example.guru_hemjee.databinding.FragmentSeedMarketBinding

//씨앗 상점 페이지
//아이템 구매 Fragment 화면
class SeedMarketFragment : Fragment() {

    private var binding: FragmentSeedMarketBinding? = null

    //씨앗 관련
    private lateinit var market_SeedTextView: TextView
    private lateinit var market_toBeUsedSeedTextView: TextView

    //구매 관련
    private lateinit var market_buyImageButton: ImageButton
    private var preselectedItems = ArrayList<String>()//이미 적용되어 있던 아이템 리스트
    private var selectedItems = ArrayList<String>() //선택한 아이템 리스트

    //인벤토리 배경 관련
    private lateinit var market_inventorybgImageView: ImageView //인벤토리 배경
    private lateinit var market_ClothImageButton: ImageButton //옷 버튼
    private lateinit var market_FurnitureImageButton: ImageButton//기구 버튼
    private lateinit var market_WallPaparImageButton: ImageButton//배경 버튼

    //인벤토리 리스트 관련
    private lateinit var market_ItemList: LinearLayout
    private var currentInventory: String = "clo"

    //가격 arrayList
    private var priceArrayList = ArrayList<Pair<String, Int>>()

    //햄찌 장식(배경) 관련
    private lateinit var market_BGFrameLayout: FrameLayout
    private lateinit var market_ClothFrameLayout: FrameLayout

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

        binding = FragmentSeedMarketBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding!!.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //씨앗 관련
        //market_SeedTextView = requireView().findViewById(R.id.market_SeedTextView)
        binding?.marketSeedTextView!!.text = "hello"
        //market_toBeUsedSeedTextView = requireView().findViewById(R.id.market_toBeUsedSeedTextView)

        //기본 정보 가져오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
            market_SeedTextView.text = cursor.getString(cursor.getColumnIndex("seed")).toString()
            hamsterName = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //구매 버튼
       // market_buyImageButton = requireView().findViewById(R.id.market_buyImageButton)
       // market_buyImageButton.setOnClickListener {
       //     if(market_toBeUsedSeedTextView.text.toString().toInt() > market_SeedTextView.text.toString().toInt()){
       //         val dialog = FinalOKDialog(requireContext(), "해바라기 씨 부족!", "확인",
       //             false, R.drawable.popup_low_balance, null)
       //         dialog.alertDialog()
//
       //         dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener{
       //             override fun onClicked(isConfirm: Boolean) {
       //                 //내용 없음
       //             }
       //         })
       //     } else {
       //         receiptPopUp()
       //     }
       // }
//
       // //인벤토리 배경 관련
       // market_inventorybgImageView = requireView().findViewById(R.id.market_inventorybgImageView)
       // market_ClothImageButton = requireView().findViewById(R.id.market_ClothImageButton)
       // market_FurnitureImageButton = requireView().findViewById(R.id.market_FurnitureImageButton)
       // market_WallPaparImageButton = requireView().findViewById(R.id.market_WallPaparImageButton)
//
       // //인벤토리 리스트 뷰
       // market_ItemList = requireView().findViewById(R.id.market_ItemList)
//
       // //인벤토리 초기 화면
       // //화면 초기화
       // upDateInventory(currentInventory)
//
       // market_ClothImageButton.setOnClickListener {
       //     currentInventory = "clo"
       //     upDateInventory("clo")
       //     market_inventorybgImageView.setImageResource(R.drawable.inventory_cloth)
       // }
       // market_FurnitureImageButton.setOnClickListener {
       //     currentInventory = "furni"
       //     upDateInventory("furni")
       //     market_inventorybgImageView.setImageResource(R.drawable.inventory_furniture)
       // }
       // market_WallPaparImageButton.setOnClickListener {
       //     currentInventory = "bg"
       //     upDateInventory("bg")
       //     market_inventorybgImageView.setImageResource(R.drawable.inventory_wallpapare)
       // }
//
       // //햄찌 선처리(미리 적용된 것들 처리
       // dbManager = DBManager(requireContext(), "hamster_db", null, 1)
       // sqlitedb = dbManager.readableDatabase
       // cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_applied = 1",null)
       // while(cursor.moveToNext()){
       //     preselectedItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
       // }
       // cursor.close()
       // var preusingItems = ArrayList<String>()
       // cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
       // while(cursor.moveToNext()){
       //     preusingItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
       // }
       // cursor.close()
       // sqlitedb.close()
//
       // sqlitedb = dbManager.writableDatabase
       // for(item in preusingItems){
       //     if(!preselectedItems.contains(item))
       //         sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
       // }
       // for(item in preselectedItems){
       //     if(!preusingItems.contains(item))
       //         sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
       // }
       // sqlitedb.close()
       // dbManager.close()
//
       // //햄찌(배경) 업데이트
       // market_BGFrameLayout = requireView().findViewById(R.id.market_BGFrameLayout)
       // market_ClothFrameLayout = requireView().findViewById(R.id.market_ClothFrameLayout)
       // //햄찌 배경 설정 함수(FunUpDateHamzzi 참고)
       // FunUpDateHamzzi.upDate(requireContext(), market_BGFrameLayout, market_ClothFrameLayout, true, true)
//
       // preusingItems.clear()
       // selectedItems.addAll(preselectedItems)
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
        val dialog = ReceiptDialog(requireContext(), market_SeedTextView.text.toString(),
            market_toBeUsedSeedTextView.text.toString(), buyItems)
        dialog.receiptPop()

        dialog.setOnClickedListener(object : ReceiptDialog.ButtonClickListener {
            override fun onClicked(isBought: Boolean, seed: Int?) {
                if (isBought) {
                    //여기에 구매 완료시 필요한 연산
                    //인벤토리 관련
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    for (item in selectedItems) {
                        sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_bought = '1' WHERE item_name = '$item'")
                        for(i in 0 until priceArrayList.size){
                            if(priceArrayList[i].first == item){
                                priceArrayList.removeAt(i)
                                break
                            }
                        }
                    }
                    sqlitedb.close()
                    dbManager.close()

                    upDateInventory(currentInventory)

                    //씨앗 관련
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE basic_info_db SET seed = '${seed.toString()}' WHERE hamster_name = '${hamsterName}'")
                    sqlitedb.close()
                    dbManager.close()

                    market_toBeUsedSeedTextView.text = "0"

                    market_SeedTextView.text = seed.toString()

                    //구매 확인 완료 팝업
                    val dialog = FinalOKDialog(requireContext(),"구매 확인", "확인",
                        false, R.drawable.popup_confirm_buy, null)
                    dialog.alertDialog()

                    dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener {
                        override fun onClicked(isConfirm: Boolean) {
                            //내용 없음
                        }
                    })
                } else {
                    //화면 초기화
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
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

                    market_toBeUsedSeedTextView.text = "0"
                    upDateInventory(currentInventory)

                    FunUpDateHamzzi.upDate(
                        requireContext(), market_BGFrameLayout,
                        market_ClothFrameLayout, true, true
                    )
                }
            }
        })
    }

    //인밴토리 업데이트
    @SuppressLint("Range")
    private fun upDateInventory(name: String) {
        //layout 초기화
        market_ItemList.removeAllViews()

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        //유형에 따라 list 가져오기
        val cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE " +
                "type = '${name}' AND is_bought = 0",null)

        while(cursor.moveToNext()){
            //동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.container_market_item, market_ItemList, false)

            var itemImageView = view.findViewById<ImageView>(R.id.marketItemBgImageView)
            var priceTextView = view.findViewById<TextView>(R.id.marketItemSeedTextView)

            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
            val itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            //배경 설정
            itemImageView.setImageResource(id)
            if(selectedItems.contains(itemName)){
                itemImageView.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.SeedBrown))
            } else {
                itemImageView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00756557"))
            }
            priceTextView.text = price.toString()

            view.setOnClickListener {
                //선택 해제할 아이템이 들어감
                var deselectItems = ArrayList<String>()

                //가격 받아오기
                var price = priceTextView.text.toString().toInt()

                val dbManager2 = DBManager(requireContext(), "hamster_db", null, 1)
                var sqlitedb2 = dbManager2.readableDatabase

                //선택 해제 중이라면
                if(!selectedItems.contains(itemName)){
                    itemImageView.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.SeedBrown))

                    //같은 카테고리의 아이템이 선택중이면 선택해제
                    val cursor2: Cursor = sqlitedb2.rawQuery("SELECT * FROM hamster_deco_info_db " +
                            "WHERE category = '${itemCategory}'", null)
                    while(cursor2.moveToNext()){
                        val tempName = cursor2.getString(cursor2.getColumnIndex("item_name"))

                        if(selectedItems.contains(tempName) && tempName != itemName) {
                            selectedItems.remove(tempName)
                            deselectItems.add(tempName)
                        }
                    }
                    cursor2.close()

                    selectedItems.add(itemName)

                    upDateInventory(currentInventory)
                    FunUpDateHamzzi.upDate(
                        requireContext(), market_BGFrameLayout,
                        market_ClothFrameLayout, true, true
                    )

                }
                //선택중이라면
                else {
                    itemImageView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00756557"))
                    selectedItems.remove(itemName)
                    deselectItems.add(itemName)
                }
                sqlitedb2.close()

                //화면 표시
                sqlitedb2 = dbManager.writableDatabase
                for(item in selectedItems){
                    sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
                }
                for(item in deselectItems){
                    sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
                }
                sqlitedb2.close()
                dbManager2.close()

                deselectItems.clear()
                priceReset()
                FunUpDateHamzzi.upDate(
                    requireContext(), market_BGFrameLayout,
                    market_ClothFrameLayout, true, true
                )
            }
            priceArrayList.add(Pair(itemName, price))

            market_ItemList.addView(view)
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    //가격 조정
    private fun priceReset(){
        var newPrice = 0

        //선택된 아이템의 가격만 추가
        for(item in selectedItems) {
            for(i in 0 until priceArrayList.size){
                if(priceArrayList[i].first == item ){
                    newPrice+=priceArrayList[i].second
                    break;
                }
            }
        }

        //씨앗
        market_toBeUsedSeedTextView.text = newPrice.toString()

        //사용 예정 씨앗 관리(보유 씨앗 갯수를 초과하면 붉은색으로)
        if(newPrice > market_SeedTextView.text.toString().toInt()){
            market_toBeUsedSeedTextView.setTextColor(Color.RED)
        } else {
            market_toBeUsedSeedTextView.setTextColor(resources.getColor(R.color.Black))
        }

    }

}