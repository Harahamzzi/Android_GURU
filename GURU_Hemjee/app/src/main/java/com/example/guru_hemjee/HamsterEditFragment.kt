package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.lang.ArithmeticException

class HamsterEditFragment() : Fragment() {

    //함께한, 이름 관련
    private lateinit var hamsterNameTextView: TextView
    private lateinit var totalSpentTimeTextView: TextView
    lateinit var userName: String

    //변경 관련 버튼들
    private lateinit var myHNameEditImageButton: ImageButton
    private lateinit var myHamsterApplyImageButton: ImageButton

    //인벤토리 관련 요소들
    private lateinit var myHInventoryBgImageView: ImageView
    private lateinit var myHClothImageButton: ImageButton
    private lateinit var myHFurnitureImageButton: ImageButton
    private lateinit var myHWallpaperImageButton: ImageButton

    private lateinit var myHItemList: RecyclerView
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

        //인벤토리 버튼에 따라 인벤토리 변화
        myHClothImageButton = requireView().findViewById(R.id.myHClothImageButton)
        myHFurnitureImageButton = requireView().findViewById(R.id.myHFurnitureImageButton)
        myHWallpaperImageButton = requireView().findViewById(R.id.myHWallPaperImageButton)
        myHInventoryBgImageView = requireView().findViewById(R.id.myHInventoryImageView)

        myHItemList = requireView().findViewById(R.id.myHItemList)

        upDateInventory(currentInventory)

        //사용 중인 아이템 미리 선택하기
        dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
        while(cursor.moveToNext()){
            preselectedItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

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
        myHBGFrameLayout = requireView().findViewById(R.id.myHBGFrameLayout)
        myHClothFrameLayout = requireView().findViewById(R.id.myHClothFrameLayout)
        FunUpDateHamzzi.upDate(requireContext(), myHBGFrameLayout, myHClothFrameLayout, true)

        //적용 버튼
        myHamsterApplyImageButton = requireView().findViewById(R.id.myHamsterApplyImageButton)
        myHamsterApplyImageButton.setOnClickListener {
            //사용 중임을 해제할 리스트
            var deSelectItems = ArrayList<String>()

            //기존에 선택 중이던 아이템을 deselectedItems에 대입
            dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
            while(cursor.moveToNext()){
                var itemName = cursor.getString(cursor.getColumnIndex("item_name"))

                if(!selectedItems.contains(itemName)){
                    deSelectItems.add(itemName)
                }
            }
            cursor.close()
            sqlitedb.close()

            //사용중임 수정(selected, deselct 아이템 갱신)
            sqlitedb = dbManager.writableDatabase
            for(item in selectedItems){
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
            }
            for(item in deSelectItems) {
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
            }
            sqlitedb.close()
            dbManager.close()

            //적용된 아이템 갱신
            preselectedItems.clear()
            preselectedItems.addAll(selectedItems)

            upDateInventory(currentInventory)
            FunUpDateHamzzi.upDate(requireContext(), myHBGFrameLayout, myHClothFrameLayout, true)

            Toast.makeText(requireContext(), "적용 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
        sqlitedb.close()
        dbManager.close()
    }

    //이름 변경 팝업
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
                    sqlitedb.execSQL("UPDATE basic_info_db SET user_name = '${name}' WHERE user_name = '${userName}'")
                }
            }
        })
    }

    //인밴토리 확인
    private fun upDateInventory(name: String) {
        val items = ArrayList<MyHamsterItem>()
        var deselectedItems = ArrayList<String>()
        //어뎁터 생성: itemClick 함수 정의(MyHamsterAdapter 참고)
        val myHamsterAdapter = MyHamsterAdapter(requireContext(), items){ item, isClicked ->
            if(isClicked){
                //같은 유형(옷끼리, 모자끼리, 배경끼리) 겹치지 않게 하기 + click 내용 반영하기
                dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
                sqlitedb = dbManager.readableDatabase
                val cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_bought = 1 AND category = '${item.category}'", null)
                while(cursor.moveToNext()){
                    val tempName = cursor.getString(cursor.getColumnIndex("item_name"))
                    if(selectedItems.contains(tempName) && tempName != item.name) {
                        selectedItems.remove(tempName)
                        deselectedItems.add(tempName)
                    }
                }
                cursor.close()
                sqlitedb.close()
                dbManager.close()

                selectedItems.add(item.name)

                upDateInventory(currentInventory)
            } else {
                selectedItems.remove(item.name)
                deselectedItems.add(item.name)
            }

            //화면 표시
            dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
            sqlitedb = dbManager.writableDatabase
            for(item in selectedItems){
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
            }
            for(item in deselectedItems){
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
            }
            sqlitedb.close()
            dbManager.close()

            deselectedItems.clear()
            FunUpDateHamzzi.upDate(requireContext(), myHBGFrameLayout, myHClothFrameLayout, true)
        }
        myHItemList.adapter = myHamsterAdapter

        //리스트 가져와서 적용하기
        dbManager = DBManager(requireContext(), "hamster_deco_info_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        //사용중인 아이템을 우선 배정
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = '$name' AND is_bought = 1 AND is_using = 1",null)
        while(cursor.moveToNext()){
            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val itemName = cursor.getString(cursor.getColumnIndex("item_name"))
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            items.addAll(listOf(MyHamsterItem(id, itemName, itemCategory, selectedItems.contains(itemName))))

        }

        //이외의 아이템 이후에 배정
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE type = '$name' AND is_bought = 1 AND is_using = 0",null)
        while(cursor.moveToNext()){
            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val itemName = cursor.getString(cursor.getColumnIndex("item_name"))
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            items.addAll(listOf(MyHamsterItem(id, itemName, itemCategory, selectedItems.contains(itemName))))

        }
        myHamsterAdapter.notifyDataSetChanged() // 리스트 갱신
        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

}