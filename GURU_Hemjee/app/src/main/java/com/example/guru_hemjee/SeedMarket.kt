package com.example.guru_hemjee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast

class SeedMarket : Fragment() {

    lateinit var buyImageButton: ImageButton

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

        buyImageButton = requireView().findViewById(R.id.buyImageButton)
        buyImageButton.setOnClickListener {
            receiptPopUp()
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

}