package com.example.guru_hemjee

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
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

// 나의 햄찌 관리 페이지
// 햄찌를 관리할 수 있는 Fragment 화면
class HamsterEditFragment() : Fragment() {

    //함께한, 이름 관련
    private lateinit var hamsterNameTextView: TextView
    private lateinit var totalSpentTimeTextView: TextView
    lateinit var hamsterName: String

    //변경 관련 버튼들
    private lateinit var myHNameEditImageButton: ImageButton
    private lateinit var myHamsterApplyImageButton: ImageButton

    //인벤토리 관련 요소들
    private lateinit var myHInventoryBgImageView: ImageView
    private lateinit var myHClothImageButton: ImageButton
    private lateinit var myHFurnitureImageButton: ImageButton
    private lateinit var myHWallpaperImageButton: ImageButton

    private lateinit var myHItemLinearLayout: LinearLayout
    private var selectedItems = ArrayList<String>()
    private var preselectedItems = ArrayList<String>()
    private var currentInventory = "clo"

    //배경(옷, 배경, 가구) 관련
    private lateinit var myHBGFrameLayout: FrameLayout
    private lateinit var myHClothFrameLayout: FrameLayout

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
        hamsterNameTextView = requireView().findViewById(R.id.myHamster_hamsterNameTextView)
        totalSpentTimeTextView = requireView().findViewById(R.id.myHamster_totalSpentTimeTextView)

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
            hamsterNameTextView.text = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
            totalSpentTimeTextView.text = cursor.getString(cursor.getColumnIndex("total_time")).toString()
            hamsterName = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //이름 변경 팝업 연결
        myHNameEditImageButton = requireView().findViewById(R.id.myHamster_nameEditImageButton)
        myHNameEditImageButton.setOnClickListener {
            hNameEditPopUp()
        }

        //인벤토리 버튼에 따라 인벤토리 변화
        myHClothImageButton = requireView().findViewById(R.id.myHamster_clothImageButton)
        myHFurnitureImageButton = requireView().findViewById(R.id.myHamster_furnitureImageButton)
        myHWallpaperImageButton = requireView().findViewById(R.id.myHamster_wallPaperImageButton)
        myHInventoryBgImageView = requireView().findViewById(R.id.myHamster_inventoryImageView)

        myHItemLinearLayout = requireView().findViewById(R.id.myHamster_itemList)

        upDateInventory(currentInventory)

        //사용 중인 아이템 미리 선택하기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
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
        dbManager.close()

        preusingItems.clear()
        selectedItems.addAll(preselectedItems)

        upDateInventory("clo")

        myHClothImageButton.setOnClickListener {
            currentInventory = "clo"
            upDateInventory("clo")
            myHInventoryBgImageView.setImageResource(R.drawable.inventory_cloth)
        }
        myHFurnitureImageButton.setOnClickListener {
            currentInventory = "furni"
            upDateInventory("furni")
            myHInventoryBgImageView.setImageResource(R.drawable.inventory_furniture)
        }
        myHWallpaperImageButton.setOnClickListener {
            currentInventory = "bg"
            upDateInventory("bg")
            myHInventoryBgImageView.setImageResource(R.drawable.inventory_wallpapare)
        }

        //배경(옷, 가구, 배경)
        myHBGFrameLayout = requireView().findViewById(R.id.myHamster_BGFrameLayout)
        myHClothFrameLayout = requireView().findViewById(R.id.myHamster_clothFrameLayout)
        FunUpDateHamzzi.upDate(requireContext(), myHBGFrameLayout, myHClothFrameLayout, true, false)

        //적용 버튼
        myHamsterApplyImageButton = requireView().findViewById(R.id.myHamster_applyImageButton)
        myHamsterApplyImageButton.setOnClickListener {
            //사용 중임을 해제할 리스트
            var deSelectItems = ArrayList<String>()

            //기존에 선택 중이던 아이템을 deselectedItems에 대입
            dbManager = DBManager(requireContext(), "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1 OR is_applied = 1",null)
            while(cursor.moveToNext()){
                var itemName = cursor.getString(cursor.getColumnIndex("item_name"))
                if(!selectedItems.contains(itemName))
                    deSelectItems.add(itemName)
            }
            cursor.close()
            sqlitedb.close()

            //사용중임 수정(selected, deselct 아이템 갱신)
            sqlitedb = dbManager.writableDatabase
            for(item in selectedItems){
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_applied = 1 WHERE item_name = '${item}'")
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
            }
            for(item in deSelectItems) {
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_applied = 0 WHERE item_name = '${item}'")
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
            }
            sqlitedb.close()
            dbManager.close()

            //적용된 아이템 갱신
            preselectedItems.clear()
            preselectedItems.addAll(selectedItems)

            upDateInventory(currentInventory)
            FunUpDateHamzzi.upDate(requireContext(), myHBGFrameLayout, myHClothFrameLayout, true, false)


            val dialog = FinalOKDialog(requireContext(), "적용 완료", "확인", false, R.drawable.popup_applyed, null)
            dialog.alertDialog()

            dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener{
                override fun onClicked(isConfirm: Boolean) {
                    //내용 없음
                }
            })

            Toast.makeText(requireContext(), "적용 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //이름 변경 팝업
    private fun hNameEditPopUp() {
        val dialog = HamsterEditNameDialog(requireContext(), hamsterNameTextView.text.toString())
        dialog.EditName()

        dialog.setOnClickedListener(object : HamsterEditNameDialog.ButtonClickListener{
            override fun onClicked(isChanged: Boolean, name: String?) {
                if(isChanged){
                    //이름 변경 db와 ui에 반영
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    hamsterNameTextView.text = name
                    sqlitedb.execSQL("UPDATE basic_info_db SET hamster_name = '${name}' WHERE hamster_name = '${hamsterName}'")
                }
            }
        })
    }

    //인밴토리 확인
    private fun upDateInventory(name: String) {
        //layout 초기화
        myHItemLinearLayout.removeAllViews()

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        val cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = '$name' AND is_bought = 1",null)

        while(cursor.moveToNext()){
            //동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.container_my_hamster_item, myHItemLinearLayout, false)

            var itemImageView = view.findViewById<ImageView>(R.id.myHItemBgImageView)

            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
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

            view.setOnClickListener {
                //선택 해제할 아이템이 들어감
                var deselectItems = ArrayList<String>()

                val dbManager2 = DBManager(requireContext(), "hamster_db", null, 1)
                var sqlitedb2 = dbManager2.readableDatabase

                //선택 해제 중이라면
                if(!selectedItems.contains(itemName)){
                    itemImageView.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.SeedBrown))

                    //같은 카테고리의 아이템이 선택중이면 선택해제
                    val cursor2: Cursor = sqlitedb2.rawQuery("SELECT * FROM hamster_deco_info_db WHERE category = '${itemCategory}'", null)
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
                    FunUpDateHamzzi.upDate(requireContext(), myHBGFrameLayout, myHClothFrameLayout, true, true)

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
                FunUpDateHamzzi.upDate(requireContext(), myHBGFrameLayout, myHClothFrameLayout, true, true)

                Log.i("item", "${selectedItems}")
            }

            myHItemLinearLayout.addView(view)
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }



}