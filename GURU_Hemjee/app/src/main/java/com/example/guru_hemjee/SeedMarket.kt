package com.example.guru_hemjee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

class SeedMarket : Fragment() {

    //구매 버튼
    private lateinit var buyImageButton: ImageButton

    //인벤토리 관련
    private lateinit var bgImageView: ImageView
    private lateinit var clothImageButton: ImageButton
    private lateinit var furnitureImageButton: ImageButton
    private lateinit var wallPaperImageButton: ImageButton

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

        //구매 버튼
        buyImageButton = requireView().findViewById(R.id.buyImageButton)
        buyImageButton.setOnClickListener {
            receiptPopUp()
        }

        //인벤토리 관련
        bgImageView = requireView().findViewById(R.id.marketInventorybgImageView)
        clothImageButton = requireView().findViewById(R.id.marketClothImageButton)
        furnitureImageButton = requireView().findViewById(R.id.marketFurnitureImageButton)
        wallPaperImageButton = requireView().findViewById(R.id.marketWallPaparImageButton)

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
        val dialog = ReceiptDialog(requireContext(), null)
        dialog.receiptPop()

        dialog.setOnClickedListener(object : ReceiptDialog.ButtonClickListener{
            override fun onClicked(isBought: Boolean) {
                if(isBought){
                    //여기에 구매 완료시 필요한 연산

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
            "cloth"-> Toast.makeText(requireContext(),"옷이당",Toast.LENGTH_SHORT).show()
            "furniture" -> Toast.makeText(requireContext(),"가구당",Toast.LENGTH_SHORT).show()
            "wallpaper" -> Toast.makeText(requireContext(),"벽지당",Toast.LENGTH_SHORT).show()
        }
    }
}