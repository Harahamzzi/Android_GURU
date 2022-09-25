package com.example.guru_hemjee.Home.MyHamsterManage

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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.guru_hemjee.*
import com.example.guru_hemjee.Home.Store.StoreItem
import com.example.guru_hemjee.Home.Store.StoreListAdapter
import com.example.guru_hemjee.databinding.FragmentHamsterEditBinding

// 나의 햄찌 관리 페이지
// 햄찌를 관리할 수 있는 Fragment 화면
class HamsterEditFragment: Fragment() {

    private var mBinding: FragmentHamsterEditBinding? = null
    private val binding get() = mBinding!!

    // 아이템 리스트 관련
    private var selectedItems = ArrayList<String>() //선택 중인 아이템 리스트
    private var appliedItems = ArrayList<String>() // 햄스터가 입고 있는 아이템 리스트
    private var currentInventory = "all" //현제 인밴토리 위치

    // DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHamsterEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 햄찌 이름 반영
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if (cursor.moveToNext()) {
            binding.myHamsterHamsterNameTextView.text = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 이름 변경 팝업 이벤트
        binding.myHamsterNameEditImageButton.setOnClickListener {
            hNameEditPopUp()
        }

        // 적용 버튼 클릭 이벤트
        binding.myHamsterApplyButton.setOnClickListener {
            // 사용 중임을 해제할 리스트
            val deSelectItems = ArrayList<String>()

            // 기존에 입고 있던 아이템을 deselectedItems에 대입
            dbManager = DBManager(requireContext(), "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_applied = 1",null)
            while (cursor.moveToNext()) {
                val itemName = cursor.getString(cursor.getColumnIndex("item_name"))
                if (!selectedItems.contains(itemName))
                    deSelectItems.add(itemName)
            }
            cursor.close()
            sqlitedb.close()

            // 아이템 선택 여부 수정(selected, deselct 아이템 갱신)
            sqlitedb = dbManager.writableDatabase
            for (item in selectedItems) {
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_applied = 1 WHERE item_name = '${item}'")
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
            }
            for (item in deSelectItems) {
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_applied = 0 WHERE item_name = '${item}'")
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
            }
            sqlitedb.close()
            dbManager.close()

            // 적용된 아이템 갱신
            appliedItems.clear()
            appliedItems.addAll(selectedItems)

            // 인벤토리, 아이템 리스트 업데이트
            upDateInventory(currentInventory)
            FunUpDateHamzzi.updateBackground(requireContext(), binding.myHamsterBGFrameLayout, true)
            FunUpDateHamzzi.updateCloth(requireContext(), binding.myHamsterClothFrameLayout, binding.myHamsterBottomClothFrameLayout, binding.myHamsterCapeFrameLayout, true)

            // 적용 완료 팝업 이벤트
            val dialog = FinalOKDialog(requireContext(), "적용 완료", "확인",
                false, R.drawable.popup_applyed, null)
            dialog.alertDialog()
            dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener {
                override fun onClicked(isConfirm: Boolean) {
                    //내용 없음
                }
            })
        }

        // All 버튼 클릭 이벤트
        binding.myHamsterAllButton.setOnClickListener {
            currentInventory = "all"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.myHamsterAllButton.setTextColor(Color.WHITE)
            binding.myHamsterAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
            binding.myHamsterClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
        }

        // 옷 카테고리 버튼 클릭 이벤트
        binding.myHamsterClothImageButton.setOnClickListener {
            currentInventory = "clo"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.myHamsterAllButton.setTextColor(Color.BLACK)
            binding.myHamsterAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
            binding.myHamsterFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
        }

        // 가구 카테고리 버튼 클릭 이벤트
        binding.myHamsterFurnitureImageButton.setOnClickListener {
            currentInventory = "furni"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.myHamsterAllButton.setTextColor(Color.BLACK)
            binding.myHamsterAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
            binding.myHamsterWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
        }

        // 벽지 카테고리 버튼 클릭 이벤트
        binding.myHamsterWallPaperImageButton.setOnClickListener {
            currentInventory = "bg"
            upDateInventory(currentInventory)

            // 버튼 색상 변경하기
            binding.myHamsterAllButton.setTextColor(Color.BLACK)
            binding.myHamsterAllButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterClothImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterClothImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterFurnitureImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Black))
            binding.myHamsterFurnitureImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterWallPaperImageButton.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.White))
            binding.myHamsterWallPaperImageButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.DarkBrown))
        }

        // 현재 햄스터가 입고 있는 아이템 이름 저장
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
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

        sqlitedb = dbManager.writableDatabase
        // 사용자가 가지고 있는 아이템 중에서, 현재 햄스터가 입고 있지 않은 아이템을 is_using = 0 으로 만들기
        for (item in bringItems) {
            if (!appliedItems.contains(item))
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
        }
        // 햄스터가 입고 있는 아이템 중에서, 가지고 있지 않거나 입고 있지 않은 아이템 is_using = 1
        for (item in appliedItems) {
            if (!bringItems.contains(item))
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
        }
        sqlitedb.close()
        dbManager.close()

        // 뷰에 배경 및 옷 반영
        FunUpDateHamzzi.updateBackground(requireContext(), binding.myHamsterBGFrameLayout, true)
        FunUpDateHamzzi.updateCloth(requireContext(), binding.myHamsterClothFrameLayout, binding.myHamsterBottomClothFrameLayout, binding.myHamsterCapeFrameLayout, true)

        bringItems.clear()
        selectedItems.addAll(appliedItems) // 현재 사용자가 입고 있는 아이템 목록 추가

        // 인벤토리 화면 출력
        upDateInventory(currentInventory)
    }

    // 이름 변경 팝업 이벤트
    private fun hNameEditPopUp() {
        val dialog = HamsterEditNameDialog(requireContext(), binding.myHamsterHamsterNameTextView.text.toString())
        dialog.editName()

        dialog.setOnClickedListener(object : HamsterEditNameDialog.ButtonClickListener {
            override fun onClicked(isChanged: Boolean, name: String?) {
                if (isChanged) {
                    // 이름 변경 db와 ui에 반영
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    sqlitedb.execSQL("UPDATE basic_info_db SET hamster_name = '${name}' WHERE" +
                            " hamster_name = '${binding.myHamsterHamsterNameTextView.text}'")
                    binding.myHamsterHamsterNameTextView.text = name

                    Toast.makeText(requireContext(), "이름을 변경했습니다", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // 인벤토리 업데이트
    @SuppressLint("Range")
    private fun upDateInventory(currentInventoryName: String) {
        // 리스트 초기화
        binding.myHamsterItemRv.removeAllViews()

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        val boughtItemList = ArrayList<StoreItem>() // 구매한 아이템 뷰 리스트
        val cursor: Cursor =
            if (currentInventoryName == "all") {
                sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_bought = 1", null)
            } else {
                sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE " +
                            "type = '${currentInventoryName}' AND is_bought = 1", null
                )
            }

        while (cursor.moveToNext()) {
            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            // 리스트에 아이템 추가 (사용자가 가지고 있는 아이템 목록)
            boughtItemList.add(StoreItem(itemName, itemCategory, 0, id, false, false))
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 어댑터 생성 및 연결
        val hamsterItemListAdapter = StoreListAdapter(requireContext(), boughtItemList)
        binding.myHamsterItemRv.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.myHamsterItemRv.adapter = hamsterItemListAdapter

        // 기존에 선택되어 있는 아이템들 선택 배경으로 설정
        for (i in 0 until selectedItems.size) {
            hamsterItemListAdapter.changeItemBG(selectedItems[i], true)
        }

        // 아이템 클릭 이벤트
        hamsterItemListAdapter.onItemClickListener = { position ->
            // db
            val dbManager2 = DBManager(requireContext(), "hamster_db", null, 1)
            var sqlitedb2 = dbManager2.readableDatabase

            // 값 찾기
            val itemName = hamsterItemListAdapter.getItemList(position).itemName
            val itemCategory = hamsterItemListAdapter.getItemList(position).category

            // 햄찌가 시착중이지 않은 아이템 목록
            val deselectItems = ArrayList<String>()

            // 새로운 아이템을 클릭했다면
            if (!selectedItems.contains(itemName)) {
                Log.d("market", "새로운 아이템 클릭!")

                // 선택 배경으로 변경
                hamsterItemListAdapter.changeItemBG(itemName, true)

                // 같은 카테고리의 아이템이 선택중이면 선택 해제
                val cursor2: Cursor = sqlitedb2.rawQuery(
                    "SELECT * FROM hamster_deco_info_db " +
                            "WHERE category = '${itemCategory}'", null
                )
                while (cursor2.moveToNext()) {
                    val tempName = cursor2.getString(cursor2.getColumnIndex("item_name"))

                    if (selectedItems.contains(tempName) && tempName != itemName) {
                        selectedItems.remove(tempName) // 기존에 선택중이었던 아이템을 리스트 및 db 값 변경
                        deselectItems.add(tempName)
                        hamsterItemListAdapter.changeItemBG(tempName, false)
                    }
                }
                cursor2.close()

                selectedItems.add(itemName)
                //upDateInventory(currentInventory)
                Log.d("market :: inventory", "인벤토리 함수 실행")

                FunUpDateHamzzi.updateBackground(requireContext(), binding.myHamsterBGFrameLayout, true) // 뷰에 이미지 적용
                FunUpDateHamzzi.updateCloth(requireContext(), binding.myHamsterClothFrameLayout, binding.myHamsterBottomClothFrameLayout, binding.myHamsterCapeFrameLayout, true)
            }
            // 선택 중인 아이템을 클릭했다면(햄스터가 시착중인 아이템을 클릭했다면)
            else {
                Log.d("market", "선택 중인 아이템 클릭!")

                // 만약 선택한 아이템이 배경이라면
                if (itemCategory == "bg") {
                    // default 배경 선택되도록 하기
                    val defaultBG = "bg0"
                    selectedItems.add(defaultBG)
                    hamsterItemListAdapter.changeItemBG(defaultBG, true)
                }
                // 만약 선택한 아이템이 기존 배경이 아니라면
                if (itemName != "bg0") {
                    // 선택 해제된 배경으로 변경
                    hamsterItemListAdapter.changeItemBG(itemName, false)

                    // 선택된 아이템 리스트에서 해당 아이템 제거
                    selectedItems.remove(itemName)
                    deselectItems.add(itemName)
                }
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
            FunUpDateHamzzi.updateBackground(requireContext(), binding.myHamsterBGFrameLayout, true) // 뷰에 이미지 적용
            FunUpDateHamzzi.updateCloth(requireContext(), binding.myHamsterClothFrameLayout, binding.myHamsterBottomClothFrameLayout, binding.myHamsterCapeFrameLayout, true)
        }

        // 이전 코드
        /*while(cursor.moveToNext()){
            //동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.container_my_hamster_item, myHamster_itemList, false)

            var myHItemBgImageView = view.findViewById<ImageView>(R.id.myHItemBgImageView)

            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            //배경 설정
            myHItemBgImageView.setImageResource(id)
            if(selectedItems.contains(itemName)){
                myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.SeedBrown))
            } else {
                myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00756557"))
            }

            view.setOnClickListener {
                //선택 해제할 아이템이 들어감
                var deselectItems = ArrayList<String>()

                val dbManager2 = DBManager(requireContext(), "hamster_db", null, 1)
                var sqlitedb2 = dbManager2.readableDatabase

                //선택 해제 중이라면
                if(!selectedItems.contains(itemName)){
                    myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(resources.getColor(
                        R.color.SeedBrown
                    ))

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

                    //인벤토리, 화면 업데이트
                    upDateInventory(currentInventory)
                    FunUpDateHamzzi.updateBackground(requireContext(), binding.myHamsterBGFrameLayout, true, true)
                    FunUpDateHamzzi.updateCloth(requireContext(), binding.myHamsterClothFrameLayout, binding.myHamsterBottomClothFrameLayout, false)

                }
                //이미 선택중이라면
                else {
                    myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00756557"))
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
                FunUpDateHamzzi.updateBackground(requireContext(), binding.myHamsterBGFrameLayout, true, true)
                FunUpDateHamzzi.updateCloth(requireContext(), binding.myHamsterClothFrameLayout, binding.myHamsterBottomClothFrameLayout, false)
            }

            myHamster_itemList.addView(view)
        }*/
    }
}