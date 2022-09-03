package com.example.guru_hemjee.Home.Store

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.R

//씨앗 상점의 영수증 팝업
class ReceiptDialog(private val context: Context, private val originalSeed: String, private val reducedSeed: Int, val itemNames: ArrayList<String>) {
    private val dialog = Dialog(context)

    //기존 씨앗
    private lateinit var pop_originalSeedTextView: TextView

    //사용 씨앗
    private lateinit var pop_usedSeedsTextView: TextView

    //결과 씨앗
    private lateinit var pop_remnantSeedsTextView: TextView

    //아이템 리스트
    private lateinit var pop_receiptItemRecyclerView: RecyclerView

    @SuppressLint("NotifyDataSetChanged", "Range")
    fun receiptPop(){
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 모서리 둥글게
        dialog.setContentView(R.layout.popup_receipt)

        //기존 씨앗
        pop_originalSeedTextView = dialog.findViewById(R.id.pop_originalSeedTextView)
        pop_originalSeedTextView.text = originalSeed

        //사용 씨앗
        pop_usedSeedsTextView = dialog.findViewById(R.id.pop_usedSeedsTextView)
        pop_usedSeedsTextView.text = reducedSeed.toString()

        //결과 씨앗
        pop_remnantSeedsTextView = dialog.findViewById(R.id.pop_remnantSeedsTextView)
        pop_remnantSeedsTextView.text = (originalSeed.toInt() - reducedSeed).toString()

        //아이템 리스트
        pop_receiptItemRecyclerView = dialog.findViewById(R.id.pop_receiptItemRecyclerView)
        val items = ArrayList<ReceiptItem>()
        val receiptAdapter = ReceiptAdapter(context, items)
        pop_receiptItemRecyclerView.adapter = receiptAdapter

        //아이템 리스트에 동적으로 연결
        val dbManager = DBManager(context, "hamster_db", null, 1)
        val sqlitedb: SQLiteDatabase = dbManager.readableDatabase
        val cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_bought = 0",null)
        while(cursor.moveToNext()){
            if(itemNames.contains(cursor.getString(cursor.getColumnIndex("item_name")))){
                val itemPic = cursor.getString(cursor.getColumnIndex("market_pic"))
                val price = cursor.getString(cursor.getColumnIndex("price")).toString().toInt()
                val type = cursor.getString(cursor.getColumnIndex("type")).toString()
                val picId = context.resources.getIdentifier(itemPic, "drawable", "com.example.guru_hemjee")

                items.addAll(listOf(ReceiptItem(picId, price, type)))
            }

            receiptAdapter.notifyDataSetChanged()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //구매 확인
        val pop_okBuyImageButton = dialog.findViewById<ImageButton>(R.id.pop_okBuyImageButton)
        pop_okBuyImageButton.setOnClickListener {
            onClickListener.onClicked(true, pop_remnantSeedsTextView.text.toString().toInt())
            dialog.dismiss()
        }

        //구매 취소
        val pop_receiptCancelImageButton = dialog.findViewById<ImageButton>(R.id.pop_receiptCancelImageButton)
        pop_receiptCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }
    }


    interface ButtonClickListener {
        fun onClicked(isBought: Boolean, seed: Int?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}