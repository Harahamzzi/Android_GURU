package com.harahamzzi.android.Home.Album

//// 나의 성취 앨범(AlbumMainActivity) -> 목표 or 카테고리 -> 해당 폴더 클릭시 나온다.
//// 또는 홈 앨범(HomeAlbumFragment) -> 목표 or 카테고리 폴더를 클릭시 나온다.
//// 대표 목표 앨범 폴더, 카테고리 앨범 폴더를 클릭했을 때 나오는 전체 사진 Fragment 화면
//class SelectAlbumFragment : Fragment() {
//
//    // 위젯
//    private lateinit var categoryIconImageView: ImageView   // 카테고리 아이콘
//    private lateinit var iconImageView: ImageView           // 대표 목표 색상 아이콘
//    private lateinit var nameTextView: TextView             // 대표 목표 이름
//
//    private lateinit var preButton: ImageButton             // 이전 페이지로 가는 버튼
//    private lateinit var nextButton: ImageButton            // 다음 페이지로 가는 버튼
//
//    private lateinit var pictureGridLayout: GridLayout      // 사진이 보여질 레이아웃
//
//    //DB 관련
//    private lateinit var dbManager: DBManager
//    private lateinit var sqlitedb: SQLiteDatabase
//
//    // 번들을 통해 전달받은 값
//    private lateinit var flag: String       // 목표/카테고리 구별 플래그
//    private lateinit var goalName: String   // 대표 목표 이름
//    private var categoryIcon: Int = 0       // 카테고리 아이콘 값
//
//    // 대표 목표 이름 목록
//    private var goalNameList = ArrayList<String>()
//    // 카테고리 목록
//    private var categoryIconList = ArrayList<Int>()
//
//    // (폰) 뒤로가기 처리 콜백
//    private lateinit var callback: OnBackPressedCallback
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//        // (폰) 뒤로가기를 눌렀을 때의 동작
//        callback = object: OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//
////                // Spinner 보이기
////                var spinner: Spinner = requireActivity().findViewById(R.id.albumMenuSpinner)
////                spinner.visibility = View.VISIBLE
//
//                var transaction = requireActivity().supportFragmentManager.beginTransaction()
//
//                if(flag == "GOAL")
//                {   // 목표 앨범으로 이동하도록 함
//                    transaction.replace(R.id.fragment_main, GoalAlbumFragment())
//                }
//                else if(flag == "CATEGORY")
//                {   // 카테고리 앨범으로 이동하도록 함
//                    transaction.replace(R.id.fragment_main, CategoryAlbumFragment())
//                }
//
//                transaction.commit()
//            }
//        }
//        // 콜백 추가
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//
//        // 콜백 제거
//        callback.remove()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_select_album, container, false)
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        // 위젯 연결
//        categoryIconImageView = requireView().findViewById(R.id.albumSelect_categoryIconImageView)
//        iconImageView = requireView().findViewById(R.id.albumSelect__iconImageView)
//        nameTextView = requireView().findViewById(R.id.albumSelect_nameTextView)
//
//        preButton = requireView().findViewById(R.id.albumSelect_prevButton)
//        nextButton = requireView().findViewById(R.id.albumSelect_nextButton)
//
//        pictureGridLayout = requireView().findViewById(R.id.albumSelect_GridLayout)
//
//        // 대표 목표 리스트 세팅
//        setGoalNameList()
//
//        // 아이콘 값 리스트 세팅
//        setCategoryIconList()
//
//        // 다른 fragment로부터 온 값 전달받기
//        if(arguments != null)
//        {
//            flag = requireArguments().getString("flag").toString()
//
//            // 플래그(목표/카테고리) 판단
//            // 목표 관련 앨범
//            if(flag == "GOAL")
//            {
//                // 위젯 나타내기
//                iconImageView.visibility = View.VISIBLE
//                nameTextView.visibility = View.VISIBLE
//
//                // 카테고리 아이콘 위젯 숨기기
//                categoryIconImageView.visibility = View.GONE
//
//                goalName = requireArguments().getString("goalName").toString()
//                nameTextView.text = goalName
//
//                // 해당 대표 목표의 전체 사진 띄우기
//                viewSelectedGoalAlbum()
//
//                // 이전 버튼 클릭 리스너 설정
//                preButton.setOnClickListener {
//                    // 현재 몇 번째에 있는지 세팅
//                    var position: Int = goalNameList.indexOf(nameTextView.text.toString())
//
//                    // 현재 첫 번째 페이지에 있다면
//                    if(position == 0)
//                    {
//                        Toast.makeText(requireActivity().applicationContext,"더이상 이동할 수 없습니다.",Toast.LENGTH_SHORT).show()
//                    }
//                    // 그렇지 않다면 페이지 정상 이동
//                    else
//                    {   // 이전 페이지로 이동 설정
//                        goalName = goalNameList.get(position - 1)
//                        nameTextView.text = goalName
//                        viewSelectedGoalAlbum()
//                    }
//                }
//
//                // 다음 버튼 클릭 리스너 설정
//                nextButton.setOnClickListener {
//                    // 현재 몇 번째에 있는지 세팅
//                    var position: Int = goalNameList.indexOf(nameTextView.text.toString())
//
//                    // 현재 마지막 페이지에 있다면
//                    if(position == goalNameList.lastIndex)
//                    {
//                        Toast.makeText(requireActivity().applicationContext,"더이상 이동할 수 없습니다.",Toast.LENGTH_SHORT).show()
//                    }
//                    // 그렇지 않다면 페이지 정상 이동
//                    else
//                    {   // 다음 페이지로 이동 설정
//                        goalName = goalNameList.get(position + 1)
//                        nameTextView.text = goalName
//                        viewSelectedGoalAlbum()
//                    }
//                }
//            }
//            // 카테고리 관련 앨범
//            else if(flag == "CATEGORY")
//            {
//                // 위젯 숨기기
//                iconImageView.visibility = View.INVISIBLE
//                nameTextView.visibility = View.GONE
//
//                // 카테고리 아이콘 위젯 나타내기
//                categoryIconImageView.visibility = View.VISIBLE
//
//                // 카테고리 아이콘 적용
//                categoryIcon = requireArguments().getInt("icon")
//                categoryIconImageView.setImageResource(categoryIcon)
//
//                // 해당 카테고리의 전체 사진 띄우기
//                viewSelectedCategoryAlbum()
//
//                // 이전 버튼 클릭 리스너 설정
//                preButton.setOnClickListener {
//                    // 현재 몇 번째에 있는지 세팅
//                    var position: Int = categoryIconList.indexOf(categoryIcon)
//
//                    // 현재 첫 번째 페이지에 있다면
//                    if(position == 0)
//                    {
//                        Toast.makeText(requireActivity().applicationContext,"더이상 이동할 수 없습니다.",Toast.LENGTH_SHORT).show()
//                    }
//                    // 그렇지 않다면 페이지 정상 이동
//                    else
//                    {   // 이전 페이지로 이동 설정
//                        categoryIcon = categoryIconList.get(position - 1)
//                        categoryIconImageView.setImageResource(categoryIcon)
//                        viewSelectedCategoryAlbum()
//                    }
//                }
//
//                // 다음 버튼 클릭 리스너 설정
//                nextButton.setOnClickListener {
//                    // 현재 몇 번째에 있는지 세팅
//                    var position: Int = categoryIconList.indexOf(categoryIcon)
//
//                    // 현재 마지막 페이지에 있다면
//                    if(position == categoryIconList.lastIndex)
//                    {
//                        Toast.makeText(requireActivity().applicationContext,"더이상 이동할 수 없습니다.",Toast.LENGTH_SHORT).show()
//                    }
//                    // 그렇지 않다면 페이지 정상 이동
//                    else
//                    {   // 다음 페이지로 이동 설정
//                        categoryIcon = categoryIconList.get(position + 1)
//                        categoryIconImageView.setImageResource(categoryIcon)
//                        viewSelectedCategoryAlbum()
//                    }
//                }
//            }
//            else
//            {
//                Log.e("오류태그", "값을 전달받지 못했습니다!")
//            }
//        }
//    }
//
//    // 대표 목표 목록을 세팅하는 함수
//    private fun setGoalNameList() {
//
//        /** 대표 목표 이름 추가 **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 대표 목표 리포트 DB 열기
//        var cursor: Cursor = sqlitedb.rawQuery("SELECT DISTINCT big_goal_name FROM big_goal_time_report_db", null)
//
//        while(cursor.moveToNext())
//        {   // 리스트에 대표 목표 이름 추가
//            goalNameList.add(cursor.getString(cursor.getColumnIndex("big_goal_name")).toString())
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 현재 사진이 없는 대표 목표의 이름 제거 **/
//
//        var removeCount = 0                 // 삭제한 횟수
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        for(index in goalNameList.indices)
//        {
//            // 세부 목표 리포트 DB 열기
//            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db"
//                    + " WHERE big_goal_name = '${goalNameList.get(index - removeCount)}'", null)
//
//            // 만일 해당 대표 목표에서 저장된 사진이 없다면
//            if(!cursor.moveToNext())
//            {
//                // 해당 대표 목표 이름 삭제
//                goalNameList.removeAt(index - removeCount)
//
//                // 삭제한 횟수 증가
//                removeCount++
//            }
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//    }
//
//    // 카테고리 아이콘 목록을 세팅하는 함수
//    private fun setCategoryIconList() {
//
//        /** 카테고리 아이콘 추가 **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 세부 목표 리포트 DB 열기 - 아이콘 값만 가져옴(중복 데이터 제외)
//        var cursor: Cursor = sqlitedb.rawQuery("SELECT DISTINCT icon FROM detail_goal_time_report_db", null)
//
//        while(cursor.moveToNext())
//        {   // 리스트에 아이콘 값 추가
//            categoryIconList.add(cursor.getInt(cursor.getColumnIndex("icon")))
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 현재 사진이 없는 카테고리의 이름 제거 **/
//
//        var removeCount = 0                         // 삭제한 횟수
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        for(index in categoryIconList.indices)
//        {
//            // 세부 목표 리포트 DB 열기
//            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db "
//                    + "WHERE icon = ${categoryIconList.get(index - removeCount)}", null)
//
//            // 만일 해당 카테고리에서 저장된 사진이 없다면
//            if(!cursor.moveToNext())
//            {
//                // 해당 아이콘 삭제
//                categoryIconList.removeAt(index - removeCount)
//
//                // 삭제한 횟수 증가
//                removeCount++
//            }
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//    }
//
//    // 해당 대표 목표의 전체 사진들을 보여주는 함수
//    private fun viewSelectedGoalAlbum() {
//        // 레이아웃의 모든 뷰 제거
//        pictureGridLayout.removeAllViews()
//
//        /** 아이콘 색상 불러와서 적용하기 **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 대표 목표 리포트 DB 열기
//        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db WHERE big_goal_name = '$goalName'", null)
//
//        if(cursor.moveToNext())
//        {
//            // 아이콘 색상을 대표 목표 색상으로 변경
//            iconImageView.setColorFilter(cursor.getInt(cursor.getColumnIndex("color")), PorterDuff.Mode.SRC_IN)
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        /** 사진 불러오고 생성하기 **/
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 세부 목표 리포트 DB 열기 (해당하는 대표 목표의 세부 목표 관련 데이터들만 가져옴)
//        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db"
//                + " WHERE big_goal_name = '$goalName'", null)
//
//        cursor.moveToLast()    // 가장 최근 데이터를 가져오기 위해 커서를 마지막으로 이동
//        cursor.moveToNext()    // 한 단계 앞으로(빈 곳을 가리키도록 함)
//
//        // 세부 목표 리포트에서 파일명 가져와서 이미지 추가하기
//        while(cursor.moveToPrevious())
//        {
//            var detailGoalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
//            var date: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
//            var color = cursor.getInt(cursor.getColumnIndex("color"))
//            var icon = cursor.getInt(cursor.getColumnIndex("icon"))
//            var bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name"))
//
//            //빈 값 처리
//            if(detailGoalName == null || icon == null || bigGoalName == null ||
//                color == null || date == null){
//                Log.i("사진 저장 오류", "${detailGoalName}, ${date}, ${color}, ${icon}, ${bigGoalName}")
//                continue
//            }
//
//            var path = requireContext().filesDir.toString() + "/picture/"
//            path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()
//
//            try {
//                // imageView 생성
//                var imageView: ImageView = ImageView(requireContext())
//
//                var imageViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                imageViewParams.gravity = Gravity.CENTER
//
//                imageView.layoutParams = imageViewParams
//
//                var bitmap: Bitmap = BitmapFactory.decodeFile(path)
//                // 이미지 배율 크기 작업 - 360x360 크기로 재설정함
//                var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 360, 360, true)
//                imageView.setImageBitmap(reScaledBitmap)
//
//                //사진에 팝업 연결
//                imageView.setOnClickListener {
//                    val dialog = PhotoDialog(requireContext(), path, icon, detailGoalName, bigGoalName, date.split(" ")[0], color)
//                    dialog.photoPopUp()
//                }
//
//                // 레이아웃에 이미지 뷰 넣기
//                pictureGridLayout.addView(imageView)
//            }
//            catch(e: Exception) {
//                Log.e("오류태그", "대표 목표별 사진 로드/세팅 실패 => 강제 탈출 \n${e.printStackTrace()}")
//                break
//            }
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//    }
//
//    // 해당 카테고리의 전체 사진들을 보여주는 함수
//    private fun viewSelectedCategoryAlbum() {
//        // 레이아웃의 모든 뷰 제거
//        pictureGridLayout.removeAllViews()
//
//        // DB 불러오기
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        // 세부 목표 리포트 DB 열기 - 해당 아이콘의 데이터만 가져옴
//        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE icon = $categoryIcon", null)
//
//        cursor.moveToLast()    // 가장 최근 데이터를 가져오기 위해 커서를 마지막으로 이동
//        cursor.moveToNext()    // 한 단계 앞으로(빈 곳을 가리키도록 함)
//
//        // 세부 목표 리포트에서 파일명 가져와서 이미지 추가하기
//        while(cursor.moveToPrevious())
//        {
//            var detailGoalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
//            var date: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
//            var color = cursor.getInt(cursor.getColumnIndex("color"))
//            var icon = cursor.getInt(cursor.getColumnIndex("icon"))
//            var bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name"))
//
//            //빈 값 처리
//            if(detailGoalName == null || icon == null || bigGoalName == null ||
//                color == null || date == null){
//                Log.i("사진 저장 오류", "${detailGoalName}, ${date}, ${color}, ${icon}, ${bigGoalName}")
//                continue
//            }
//
//            var path = requireContext().filesDir.toString() + "/picture/"
//            path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()
//
//            try {
//                // imageView 생성
//                var imageView: ImageView = ImageView(requireContext())
//
//                var imageViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                imageViewParams.gravity = Gravity.CENTER
//
//                imageView.layoutParams = imageViewParams
//
//                var bitmap: Bitmap = BitmapFactory.decodeFile(path)
//                // 이미지 배율 크기 작업 - 360x360 크기로 재설정함
//                var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 360, 360, true)
//                imageView.setImageBitmap(reScaledBitmap)
//
//                //사진에 팝업 연결
//                imageView.setOnClickListener {
//                    val dialog = PhotoDialog(requireContext(), path, icon, detailGoalName, bigGoalName, date.split(" ")[0], color)
//                    dialog.photoPopUp()
//                }
//
//                // 레이아웃에 이미지 뷰 넣기
//                pictureGridLayout.addView(imageView)
//            }
//            catch(e: Exception) {
//                Log.e("오류태그", "카테고리별 사진 로드/세팅 실패 => 강제 탈출 \n${e.printStackTrace()}")
//                break
//            }
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//    }
//}