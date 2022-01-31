package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class DetailGoalSetupFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // 세부목표 리스트가 들어갈 리니어 레이아웃
    lateinit var detailGoalListLayout : LinearLayout
    // 전체 레이아웃
    // lateinit var detailGoalSetupLayout : ConstraintLayout

    // 세부 목표
    lateinit var bigGoalTextView : TextView // 대표목표
    lateinit var bigGoalColorImageView : ImageView // 색상(원형 아이콘)
    lateinit var plusDetailGoalBtn : ImageButton // + 아이콘(세부목표 추가)
    lateinit var editBigGoalBtn : ImageButton // 연필 아이콘(대표목표 수정)
    lateinit var completeBtn : androidx.appcompat.widget.AppCompatButton // 확인버튼

    // 배열
    var iconList = ArrayList<ImageButton>() // 아이콘
    var edtdetailGoalList = ArrayList<EditText>() // 세부목표
    var textdetailGoalList = ArrayList<TextView>()
    var detailGoalList = ArrayList<String>()
    var sebuMenuList = ArrayList<ImageButton>() // 세부메뉴

    private lateinit var str_biggoal : String // 대표목표
    private var integer_color : Int = 0 // 대표목표 색상

    var mainActivity : MainActivity? = null // 메인 액티비티 변수

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_detail_goal_setup, container, false)

        detailGoalListLayout = view.findViewById(R.id.detailGoalListLayout)
        // detailGoalSetupLayout = view.findViewById(R.id.detailGoalSetupLayout)
        bigGoalTextView = view.findViewById(R.id.bigGoalTextView)
        bigGoalColorImageView = view.findViewById(R.id.bigGoalColorImageView)
        plusDetailGoalBtn = view.findViewById(R.id.plusDetailGoalBtn)
        editBigGoalBtn = view.findViewById(R.id.editBigGoalBtn)
        completeBtn = view.findViewById(R.id.completeBtn)

        // SetupFragment에서 넘어온 값(대표 목표) 받기
        if (arguments != null)
        {
            // 받아온 값 넣기
            str_biggoal = requireArguments().getString("bundle_biggoal").toString() // 대표목표

            // 대표목표 DB
            dbManager = DBManager(context, "big_goal_db", null, 1)
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
            bigGoalTextView.text = str_biggoal
            Log.d("색상", integer_color.toString())
            bigGoalColorImageView.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN)
        }

        /** 만약에 이전에 저장해둔 값이 있다면 리스트 가져와서 레이아웃에 반영하기 **/
        dbManager = DBManager(context, "detail_goal_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_db WHERE big_goal_name = '"+str_biggoal+"';", null)

        var num : Int = 0 // 리스트 개수
        while(cursor.moveToNext()) {
            // 대표목표에 해당하는 커서에 있는 값들 가져오기
            var str_detail = cursor.getString(cursor.getColumnIndex("detail_goal_name")).toString()
            var int_icon = cursor.getInt(cursor.getColumnIndex("icon"))

            // 동적 뷰 생성
            var view = layoutInflater.inflate(R.layout.layout_detail_goal_text, detailGoalListLayout, false)

            // 아이콘과 EditText, 레이아웃 객체 동적 생성
            var detailGoalIconBtn : ImageButton = view.findViewById(R.id.detailGoalIconBtn)
            var detailGoalTextView : TextView = view.findViewById(R.id.detailGoalTextView)
            var sebuMenuBtn : ImageButton = view.findViewById(R.id.sebuMenuBtn)

            // 값 넣기
            detailGoalIconBtn.setImageResource(int_icon)
            detailGoalIconBtn.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN)
            detailGoalIconBtn.setTag(int_icon)
            detailGoalTextView.text = str_detail
            sebuMenuBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_sebumenu))

            // 리니어 레이아웃에 객체 추가
            iconList.add(detailGoalIconBtn)
            textdetailGoalList.add(detailGoalTextView)
            detailGoalList.add(detailGoalTextView.text.toString())
            sebuMenuList.add(sebuMenuBtn)

            detailGoalListLayout.addView(view)
            num++
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // var isEditing : Boolean = false // 텍스트뷰, 에디트텍스트뷰의 상태를 점검

        // +버튼을 눌렀을 경우
        plusDetailGoalBtn.setOnClickListener {
            // 동적 뷰 생성
            var view = layoutInflater.inflate(R.layout.layout_detail_goal, detailGoalListLayout, false)
            // isEditing = true
            // Log.d("+버튼을 눌렀을 경우 ", isEditing.toString())

            // 아이콘과 EditText, 레이아웃 객체 동적 생성
            var detailGoalIconBtn : ImageButton = view.findViewById<ImageButton>(R.id.detailGoalIconBtn)
            var detailGoalEditText : EditText = view.findViewById<EditText>(R.id.detailGoalEditText)
            var sebuMenuBtn : ImageButton = view.findViewById<ImageButton>(R.id.sebuMenuBtn)

            // 초기값
            detailGoalIconBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_outline_menu_book_24))
            detailGoalIconBtn.setColorFilter(integer_color, PorterDuff.Mode.SRC_IN)
            detailGoalIconBtn.setTag(R.drawable.ic_outline_menu_book_24)
            sebuMenuBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_sebumenu))

            // 아이콘을 클릭했을 경우
            detailGoalIconBtn.setOnClickListener {

            }

            // 세부메뉴 버튼을 클릭했을 경우
            sebuMenuBtn.setOnClickListener {

            }

            // 리니어 레이아웃에 객체 추가
            iconList.add(detailGoalIconBtn)
            edtdetailGoalList.add(detailGoalEditText)
            detailGoalList.add(detailGoalEditText.text.toString())
            sebuMenuList.add(sebuMenuBtn)

            detailGoalListLayout.addView(view)

            num++ // 추가한 목표 개수 증가

            // 각각의 객체에 대한 layout param을 만들기
            /*/val iconLayoutParams = LinearLayout.LayoutParams( // 아이콘의 레이아웃
                    28,
                    28
            )
            val editLayoutParams = LinearLayout.LayoutParams( // editText의 레이아웃
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // 각각의 레이아웃에 속성 값 넣기
            iconLayoutParams.setMargins(0,0,7,0)
            editLayoutParams.weight = 1F

            // 레이아웃 적용
            detailGoalIconBtn.layoutParams = iconLayoutParams
            detailGoalEditText.layoutParams = editLayoutParams

            // 각각의 객체에 속성 값 넣기
            detailGoalIconBtn.background = null
            detailGoalIconBtn.setColorFilter(R.color.Apricot, PorterDuff.Mode.SRC_IN) // TODO : 대표 목표 색깔 넣기
            detailGoalIconBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_outline_menu_book_24))
            detailGoalEditText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.Yellow) // 밑줄샥
            detailGoalEditText.setTextSize(Dimension.SP, 18.0F) // sp단위
            detailGoalEditText.inputType = 0x00000001 // text타입
            detailGoalEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.Black))
            detailGoalEditText.typeface = resources.getFont(R.font.noto_sans)

            // 레이아웃에 객체 추가
            detailGoalListLayout.addView(detailGoalIconBtn)
            detailGoalListLayout.addView(detailGoalEditText)*/
        }

        // editText가 아니라 다른 곳을 클릭할 경우 방법(1)
        /*detailGoalSetupLayout.setOnClickListener {
            // 키보드 숨기기
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
            // textView상태로 바꾸기
            isEditing = false
            Log.d("레이아웃 리스너안에 ", isEditing.toString())
        }*/

        // EditText <-> TextView 전환
        /*if (isEditing) { // editText 상태일 경우
            Log.d("if문안에", isEditing.toString())
            detailGoalTextView.visibility = View.INVISIBLE
            detailGoalEditText.visibility = View.VISIBLE
            detailGoalEditText.text = detailGoalTextView.text as Editable?
        }
        else if (!isEditing) { // textview 상태일 경우
            Log.d("else if문안에", isEditing.toString())
            detailGoalEditText.visibility = View.INVISIBLE
            detailGoalTextView.visibility = View.VISIBLE
            detailGoalTextView.text = detailGoalEditText.text.toString()
        }*/

        /*detailGoalSetupLayout.setOnClickListener{
            Log.d("키보드 내림", "키보드내리라고")
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(detailGoalEditText.windowToken, 0)
        }*/

        // 확인버튼을 눌렀다면
        completeBtn.setOnClickListener {
            // DB에 데이터 쓰기(세부 목표)
            dbManager = DBManager(context, "detail_goal_db", null, 1)
            sqlitedb = dbManager.writableDatabase

            // 새롭게 추가한 목표를 db에 저장
            var i : Int = 0
            while (i < edtdetailGoalList.size) {
                var detailGoal: String = edtdetailGoalList[i].text.toString()
                var iconName : Int = iconList[i].getTag() as Int
                Log.d("세부목표: ", i.toString() + "번째" + detailGoal)
                Log.d("아이콘이름: ", i.toString() + "번째" + iconName)

                sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('" + detailGoal + "', '" + iconName + "', '" + str_biggoal + "')")
                ++i
            }

            sqlitedb.close()
            Toast.makeText(context, "목표가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            goSetUp()
        }

        return view
    }

    // setupfragment로 화면을 전환하는 함수
    fun goSetUp() {
        mainActivity?.supportFragmentManager
                ?.beginTransaction()
                //?.remove(this)
                ?.replace(R.id.fragment_main, SetupFragment())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.commit()
    }
}