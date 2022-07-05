package com.example.guru_hemjee.Home.Goal

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.guru_hemjee.*
import com.example.guru_hemjee.Home.MainActivity

// 홈(MainActivity) -> SubMainActivity -> 목표/잠금 시간 설정(Setup) -> DetailGoalSetupFragment
// 세부 목표 확인 및 추가/삭제, 수정할 수 있는 Fragment 화면
class DetailGoalSetupFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    // 세부목표 리스트가 들어갈 리니어 레이아웃
    private lateinit var goalDetail_detailGoalListLayout: LinearLayout

    // 세부 목표
    private lateinit var goalDetail_bigGoalTextView: TextView // 대표목표
    private lateinit var goalDetail_bigGoalColorImageView: ImageView // 색상(원형 아이콘)
    private lateinit var goalDetail_plusDetailGoalButton: ImageButton // + 아이콘(세부목표 추가)
    private lateinit var goalDetail_editBigGoalButton: ImageButton // 연필 아이콘(대표목표 수정)
    private lateinit var goalDetail_storeButton: AppCompatButton // 확인버튼
    private lateinit var goalDetail_backButton: AppCompatButton // 취소버튼

    // 배열
    private var iconList = ArrayList<ImageButton>() // 아이콘
    private var textdetailGoalList = ArrayList<TextView>() // 목표 리스트
    private var bigGoalList = ArrayList<String>() // 대표 목표 리스트
    private var sebuMenuList = ArrayList<ImageButton>() // 세부메뉴

    private var origin = 0

    private lateinit var str_biggoal : String // 대표목표
    private var integer_color : Int = 0 // 대표목표 색상

    private var mainActivity : MainActivity? = null // 메인 액티비티 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()

        mainActivity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_detail_goal_setup, container, false)

        goalDetail_detailGoalListLayout = view.findViewById(R.id.goalDetail_detailGoalListLayout)
        goalDetail_bigGoalTextView = view.findViewById(R.id.goalDetail_bigGoalTextView)
        goalDetail_bigGoalColorImageView = view.findViewById(R.id.goalDetail_bigGoalColorImageView)
        goalDetail_plusDetailGoalButton = view.findViewById(R.id.goalDetail_plusDetailGoalButton)
        goalDetail_editBigGoalButton = view.findViewById(R.id.goalDetail_editBigGoalButton)
        goalDetail_storeButton = view.findViewById(R.id.goalDetail_storeButton)
        goalDetail_backButton = view.findViewById(R.id.goalDetail_backButton)

        // SetupFragment 또는 BigGoalModifyFragment에서 넘어온 (대표 목표)값 받기
        if (arguments != null)
        {
            // 받아온 값 넣기
            str_biggoal = requireArguments().getString("bundle_biggoal").toString() // 대표목표

            // 대표목표 DB
            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 대표목표 찾기
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '"+str_biggoal+"';", null)

            // 색상 넣기
            if(cursor.moveToNext())
                integer_color = cursor.getInt(cursor.getColumnIndex("color"))

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            // 위젯에 반영하기
            goalDetail_bigGoalTextView.text = str_biggoal
            goalDetail_bigGoalColorImageView.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN)
        }

        /** 만약에 이전에 저장해둔 값이 있다면 리스트 가져와서 레이아웃에 반영하기 **/
        dbManager = DBManager(context, "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '"+goalDetail_bigGoalTextView.text.toString()+"';", null)
        var num : Int = 0 // 리스트 개수
        while (cursor.moveToNext()) {
            // 대표목표에 해당하는 커서에 있는 값들 가져오기
            var str_detail = cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString()
            var int_icon = cursor.getInt(cursor.getColumnIndex("icon"))

            // 동적 뷰 생성
            var view = layoutInflater.inflate(R.layout.container_detail_goal_text, goalDetail_detailGoalListLayout, false)

            // 아이콘과 EditText, 레이아웃 객체 동적 생성
            var detailGoalIconBtn : ImageButton = view.findViewById(R.id.detailGoalIconBtn)
            var detailGoalTextView : TextView = view.findViewById(R.id.detailGoalTextView)
            var sebuMenuBtn : ImageButton = view.findViewById(R.id.sebuMenuBtn)

            // 값 넣기
            detailGoalIconBtn.setImageResource(int_icon)
            detailGoalIconBtn.scaleType = ImageView.ScaleType.FIT_CENTER
            detailGoalIconBtn.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN)
            detailGoalIconBtn.setTag(int_icon)

            //아이콘 변경
            detailGoalIconBtn.setOnClickListener {
                showIconPopUp(integer_color, detailGoalIconBtn)
            }

            //세부 목표 수정
            sebuMenuBtn.setOnClickListener {
                showDetailEditPopUp(detailGoalTextView, textdetailGoalList.indexOf(detailGoalTextView))
            }

            detailGoalTextView.text = str_detail
            sebuMenuBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_sebumenu
            ))

            // 리니어 레이아웃에 객체 추가
            iconList.add(detailGoalIconBtn)
            textdetailGoalList.add(detailGoalTextView)
            bigGoalList.add(str_biggoal)
            sebuMenuList.add(sebuMenuBtn)

            goalDetail_detailGoalListLayout.addView(view)
            num++
            origin++
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 연필 아이콘을 클릭했을 경우
        goalDetail_editBigGoalButton.setOnClickListener {
            // 대표목표 수정 화면으로 이동
            val transaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val modifyFragment = BigGoalModifyFragment()
            val bundle = Bundle()
            bundle.putString("bundle_biggoal_2", str_biggoal)

            modifyFragment.setArguments(bundle)
            transaction.replace(R.id.fragment_main, modifyFragment)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.addToBackStack("ModifyGoal")
            transaction.commit()
        }

        // +버튼을 눌렀을 경우
        goalDetail_plusDetailGoalButton.setOnClickListener {
            // 동적 뷰 생성
            var view = layoutInflater.inflate(R.layout.container_goal_detail_goal, goalDetail_detailGoalListLayout, false)

            // 아이콘과 EditText, 레이아웃 객체 동적 생성
            var detailGoalIconBtn : ImageButton = view.findViewById<ImageButton>(R.id.detailGoalIconBtn)
            var detailGoalEditText : EditText = view.findViewById<EditText>(R.id.detailGoalEditText)

            // 초기값
            detailGoalIconBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_book_24
            ))
            detailGoalIconBtn.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN)
            detailGoalIconBtn.scaleType = ImageView.ScaleType.FIT_CENTER
            detailGoalIconBtn.setTag(R.drawable.ic_book_24)

            // 아이콘을 클릭했을 경우
            detailGoalIconBtn.setOnClickListener {
                showIconPopUp(integer_color, detailGoalIconBtn)
            }

            // 리니어 레이아웃에 객체 추가
            iconList.add(detailGoalIconBtn)
            textdetailGoalList.add(detailGoalEditText)
            bigGoalList.add(str_biggoal)

            goalDetail_detailGoalListLayout.addView(view)

            num++ // 추가한 목표 개수 증가
        }

        // 확인버튼을 눌렀다면
        goalDetail_storeButton.setOnClickListener {
            // DB에 데이터 쓰기(세부 목표)
            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("DELETE FROM detail_goal_db WHERE big_goal_name = '$str_biggoal'")
            sqlitedb.close()
            dbManager.close()

            // 새롭게 추가한 목표를 db에 저장
            var isValid = true
            var i : Int = 0
            while(i < iconList.size){
                var detailGoal: String = textdetailGoalList[i].text.toString()
                var iconName : Int = iconList[i].getTag() as Int
                var bigGoal: String = bigGoalList[i]

                for(item in textdetailGoalList){
                    if(item.text.toString() == detailGoal && item != textdetailGoalList[i]){
                        isValid = false
                        Toast.makeText(context, "중복되는 목표가 입력되었습니다!", Toast.LENGTH_SHORT).show()
                        break
                    }
                }

                dbManager = DBManager(context, "hamster_db", null, 1)
                sqlitedb = dbManager.readableDatabase
                cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE detail_goal_name = '${detailGoal}'",null)
                if (cursor.moveToNext()) {
                    isValid = false
                    Toast.makeText(context, "중복되는 목표가 있습니다!(${detailGoal})", Toast.LENGTH_SHORT).show()
                }
                cursor.close()
                sqlitedb.close()
                dbManager.close()

                if(!isValid)
                    break;

                i++
            }

            dbManager = DBManager(context, "hamster_db", null, 1)
            sqlitedb = dbManager.writableDatabase

            i=0
            while (i < iconList.size && isValid) {
                var detailGoal: String = textdetailGoalList[i].text.toString()
                var iconName : Int = iconList[i].getTag() as Int
                var bigGoal: String = bigGoalList[i]

                // 공백 데이터 제외하고 저장
                if(detailGoal != "")
                    sqlitedb.execSQL("INSERT OR IGNORE INTO detail_goal_db VALUES ('" + detailGoal + "', '" + iconName + "', '" + bigGoal + "')")
                ++i
            }

            sqlitedb.close()
            dbManager.close()
            if(isValid){
                goSetUp()
                Toast.makeText(context, "목표가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 취소 버튼을 눌렀다면
        goalDetail_backButton.setOnClickListener {
            goSetUp() // 이전화면으로 돌아가기
            Toast.makeText(context, "저장이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    // setupfragment로 화면을 전환하는 함수
    private fun goSetUp() {
        mainActivity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_main, SetupFragment())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                ?.commit()
        requireActivity().supportFragmentManager.popBackStack("DetailGoal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showIconPopUp(color: Int, iconButton: ImageButton) {
        val dialog = IconChangeDialog(requireContext(), color, iconButton.getTag() as Int)
        dialog.iconPopUp()

        dialog.setOnClickedListener(object : IconChangeDialog.ButtonClickListener {
            override fun onClick(isChanged: Boolean, changedIcon: Int) {
                if (isChanged) {
                    iconButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), changedIcon))
                    iconButton.setTag(changedIcon)
                }
            }
        })
    }

    private fun showDetailEditPopUp(detailNameTextView: TextView, num: Int){
        val dialog = DetailGoalEditDialog(requireContext(), detailNameTextView.text.toString(), bigGoalList[num])
        dialog.detailGoalEditPopUp()

        dialog.setOnClickedListener(object : DetailGoalEditDialog.ButtonClickListener {
            override fun onClicked(
                isDeleted: Boolean,
                goalName: String,
                bigGoalName: String
            ) {
                if(isDeleted){
                    iconList[num].setColorFilter(R.color.Gray, PorterDuff.Mode.SRC_IN)
                    detailNameTextView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.Gray)))
                    sebuMenuList[num].isClickable = false
                    iconList.removeAt(num)
                    textdetailGoalList.removeAt(num)
                    bigGoalList.removeAt(num)
                    sebuMenuList.removeAt(num)
                } else {
                    dbManager = DBManager(context, "hamster_db", null, 1)
                    sqlitedb = dbManager.readableDatabase
                    var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '$bigGoalName'",null)
                    if(cursor.moveToNext()){
                        iconList[num].setColorFilter(cursor.getInt(cursor.getColumnIndex("color")), PorterDuff.Mode.SRC_IN)
                    }
                    cursor.close()
                    sqlitedb.close()
                    dbManager.close()

                    detailNameTextView.text = goalName
                    bigGoalList[num] = bigGoalName
                }
            }
        })
    }
}