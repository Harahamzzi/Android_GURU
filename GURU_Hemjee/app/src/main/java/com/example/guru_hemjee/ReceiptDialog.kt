package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceiptDialog(private val context: Context, private val originalSeed: String, private val reducedSeed: String, val itemNames: ArrayList<String>) {
    private val dialog = Dialog(context)

    //기존 씨앗
    private lateinit var originalSeedTextView: TextView

    //사용 씨앗
    private lateinit var usedSeedsTextView: TextView

    //결과 씨앗
    private lateinit var remnantSeedsTextView: TextView

    //아이템 리스트
    private lateinit var receiptItemRecyclerView: RecyclerView

    fun receiptPop(){
        dialog.show()
        dialog.setContentView(R.layout.popup_receipt)

        //기존 씨앗
        originalSeedTextView = dialog.findViewById(R.id.originalSeedTextView)
        originalSeedTextView.text = originalSeed

        //사용 씨앗
        usedSeedsTextView = dialog.findViewById(R.id.usedSeedsTextView)
        usedSeedsTextView.text = reducedSeed

        //결과 씨앗
        remnantSeedsTextView = dialog.findViewById(R.id.remnantSeedsTextView)
        remnantSeedsTextView.text = (originalSeed.toInt() - reducedSeed.toInt()).toString()

        //아이템 리스트
        receiptItemRecyclerView = dialog.findViewById(R.id.receiptItemRecyclerView)
        val items = ArrayList<ReceiptItem>()
        val receiptAdapter = ReceiptAdapter(context, items)
        receiptItemRecyclerView.adapter = receiptAdapter

        val dbManager = DBManager(context, "hamster_deco_info_db", null, 1)
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
        val buy = dialog.findViewById<ImageButton>(R.id.okBuyImageButton)
        buy.setOnClickListener {
            onClickListener.onClicked(true, remnantSeedsTextView.text.toString().toInt())
            dialog.dismiss()
        }

        //구매 취소
        val cancel = dialog.findViewById<ImageButton>(R.id.receiptCancelImageButton)
        cancel.setOnClickListener {
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