package com.example.adddb
import android.util.Log

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbManager = DBManager(this, "hamster_db", null, 1)
        sqlitedb = dbManager.writableDatabase


        /** basic_info_db 데이터 생성 **/
        sqlitedb.execSQL("INSERT INTO basic_info_db VALUES ('우주 최강 귀요미', 3000, '50:00:00')")


        /** hamster_deco_info 데이터 생성 **/
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg1', 10, 'bg', 'bg', 'bg_bg_10', 'market_bg_10', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg2', 1000, 'bg', 'bg', 'bg_bg_1000', 'market_bg_1000', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_toystory', 1500, 'bg', 'bg', 'bg_bg_1500', 'market_bg_1500', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_beach', 1800, 'bg', 'bg', 'bg_bg_1800', 'market_bg_1800', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_snow', 2100, 'bg', 'bg', 'bg_bg_2100', 'market_bg_2100', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('bg_sunset', 2500, 'bg', 'bg', 'bg_bg_2500', 'market_bg_2500', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_sandcattle', 390, 'furni1', 'furni', 'bg_furni_390', 'market_furni_390', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_plant', 390, 'furni', 'furni2', 'bg_furni_390_2', 'market_furni_390_2', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_pics', 560, 'furni', 'furni3', 'bg_furni_560', 'market_furni_560', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_house', 730, 'furni', 'furni5', 'bg_furni_730_2', 'market_furni_730_2', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_surf1', 730, 'furni', 'furni4', 'bg_furni_730', 'market_furni_730', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_surf2', 790, 'furni', 'furni6', 'bg_furni_790', 'market_furni_790', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_snowman', 920, 'furni', 'right', 'bg_furni_920', 'market_furni_920', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_tree1', 1000, 'furni', 'furni8', 'bg_furni_1000', 'market_furni_1000', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('furni_tree2', 1020, 'furni', 'right', 'bg_furni_1020', 'market_furni_1020', '', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_glasses', 390, 'clo', 'gla', 'bg_clo_390', 'market_clo_390', 'hamster_clo_390', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_cap', 390, 'clo', 'hat', 'bg_clo_390_2', 'market_clo_390_2', 'hamster_clo_390_2', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_workhard', 560, 'clo', 'hat', 'bg_clo_560', 'market_clo_560', 'hamster_clo_560', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_earmuffs', 560, 'clo', 'hat', 'bg_clo_560_2', 'market_clo_560_2', 'hamster_clo_560_2', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_sungla', 730, 'clo', 'hat', 'bg_clo_730', 'market_clo_730', 'hamster_clo_730', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_flower', 730, 'clo', 'hat', 'bg_clo_730_2', 'market_clo_730_2', 'hamster_clo_730_2', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_strap', 420, 'clo', 'clo', 'bg_clo_420', 'market_clo_420', 'hamster_clo_420', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_winter', 710, 'clo', 'clo', 'bg_clo_710', 'market_clo_710', 'hamster_clo_710', 0, 0, 0)")
        sqlitedb.execSQL("INSERT INTO hamster_deco_info_db VALUES('clo_summer', 1000, 'clo', 'clo', 'bg_clo_1000', 'market_clo_1000', 'hamster_clo_1000', 0, 0, 0)")


        /** big_goal_db 데이터 생성 **/
        //  color 값
        var colorOrange: Int = ContextCompat.getColor(this, R.color.Orange)
        var colorSeedBrown: Int = ContextCompat.getColor(this,R.color.SeedBrown)
        var colorGreen: Int = ContextCompat.getColor(this,R.color.Green)
        var colorYellow: Int = ContextCompat.getColor(this,R.color.Yellow)

        sqlitedb.execSQL("INSERT INTO big_goal_db VALUES ('알고리즘 공부하기', $colorOrange, '01:20:00')")
        sqlitedb.execSQL("INSERT INTO big_goal_db VALUES ('매일 운동하기', $colorSeedBrown, '01:00:00')")
        sqlitedb.execSQL("INSERT INTO big_goal_db VALUES ('책 읽기', $colorGreen, '00:30:00')")
        sqlitedb.execSQL("INSERT INTO big_goal_db VALUES ('투자 공부', $colorYellow, '00:50:00')")


        /** detail_goal_db 데이터 생성 **/
        // icon 값
        var ic_computer = R.drawable.ic_outline_computer_24
        var ic_playLesson = R.drawable.ic_outline_play_lesson_24

        var ic_dumble = R.drawable.dumble_icon

        var ic_book = R.drawable.ic_outline_menu_book_24

        var ic_stats = R.drawable.ic_outline_query_stats_24

        sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('하루에 백준 실버 3개 풀기', ${ic_computer}, '알고리즘 공부하기')")
        sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('알고리즘 풀이 강의 듣기', ${ic_playLesson}, '알고리즘 공부하기')")
        sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('줄넘기 100개하기', ${ic_dumble}, '매일 운동하기')")
        sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('스쿼트 15개씩 4세트하기', ${ic_dumble}, '매일 운동하기')")
        sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('하루에 30분 읽기', ${ic_book}, '책 읽기')")
        sqlitedb.execSQL("INSERT INTO detail_goal_db VALUES ('경제 서적 읽기', ${ic_stats}, '투자 공부')")


        /** big_goal_time_report_db 데이터 생성 **/
        // TODO: 총 잠금시간, 색상, 잠금한 날짜 값 넣기
        // 월
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 3600000, ${colorOrange}, '2022-01-31-월 01:56:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 4320000, ${colorSeedBrown}, '2022-01-31-월 05:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-01-31-월 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 3400000, ${colorYellow}, '2022-01-31-월 18:41:20')")

        // 화
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 3712000, ${colorOrange}, '2022-02-01-화 03:00:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 2346030, ${colorSeedBrown}, '2022-02-01-화 10:40:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-02-01-화 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 3000000, ${colorYellow}, '2022-02-01-화 18:41:20')")

        // 수
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 1240000, ${colorOrange}, '2022-02-02-수 02:56:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 5320000, ${colorSeedBrown}, '2022-02-02-수 15:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-02-02-수 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 2000000, ${colorYellow}, '2022-02-02-수 18:41:20')")

        // 목
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 4200000, ${colorOrange}, '2022-02-03-목 01:56:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 4320000, ${colorSeedBrown}, '2022-02-03-목 15:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-02-03-목 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 1400000, ${colorYellow}, '2022-02-03-목 18:41:20')")

        // 금
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 3600000, ${colorOrange}, '2022-02-04-금 01:56:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 4320000, ${colorSeedBrown}, '2022-02-04-금 15:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-02-04-금 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 3200000, ${colorYellow}, '2022-02-04-금 18:41:20')")

        // 토
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 5124000, ${colorOrange}, '2022-02-05-토 01:56:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 4320000, ${colorSeedBrown}, '2022-02-05-토 15:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-02-05-토 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 3000000, ${colorYellow}, '2022-02-05-토 18:41:20')")

        // 일
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 1230000, ${colorOrange}, '2022-02-06-일 01:56:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 2573200, ${colorSeedBrown}, '2022-02-06-일 15:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-02-06-일 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 3400000, ${colorYellow}, '2022-02-06-일 18:41:20')")

        // 월
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('알고리즘 공부하기', 6234500, ${colorOrange}, '2022-02-07-월 01:56:14')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('매일 운동하기', 4320000, ${colorSeedBrown}, '2022-02-07-월 15:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('책 읽기', 1800000, ${colorGreen}, '2022-02-07-월 07:23:20')")
        sqlitedb.execSQL("INSERT INTO big_goal_time_report_db VALUES ('투자 공부', 2800000, ${colorYellow}, '2022-02-07-월 18:41:20')")

        /** detail_goal_time_report_db 데이터 생성 **/
        // TODO: 잠금한 날짜, 색상, 아이콘 파일명 값 넣기
        // 월
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-01-31-월 02:00:14', ${colorOrange}, ${ic_computer}, '20220131_020014.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-01-31-월 02:34:14', ${colorOrange}, ${ic_playLesson}, '20220131_023414.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-01-31-월 05:23:20', ${colorSeedBrown}, ${ic_dumble}, '20220131_052320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-01-31-월 05:33:42', ${colorSeedBrown}, ${ic_dumble}, '20220131_053342.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-01-31-월 07:23:20', ${colorGreen}, ${ic_book}, '20220131_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-01-31-월 18:41:20', ${colorYellow}, ${ic_stats}, '20220131_184120.jpg', '매일 운동하기', 0)")

        // 화
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-02-01-화 03:10:14', ${colorOrange}, ${ic_computer}, '20220201_031014.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-02-01-화 03:32:14', ${colorOrange}, ${ic_playLesson}, '20220201_033214.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-02-01-화 10:52:20', ${colorSeedBrown}, ${ic_dumble}, '20220201_105220.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-02-01-화 11:20:32', ${colorSeedBrown}, ${ic_dumble}, '20220201_112032.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-02-01-화 07:23:20', ${colorGreen}, ${ic_book}, '20220201_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-02-01-화 18:41:20', ${colorYellow}, ${ic_stats}, '20220201_184120.jpg', '매일 운동하기', 0)")

        // 수
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-02-02-수 03:00:14', ${colorOrange}, ${ic_computer}, '20220202_030014.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-02-02-수 03:15:14', ${colorOrange}, ${ic_playLesson}, '20220202_031514.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-02-02-수 15:43:20', ${colorSeedBrown}, ${ic_dumble}, '20220202_154320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-02-02-수 16:23:23', ${colorSeedBrown}, ${ic_dumble}, '20220202_162323.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-02-02-수 07:23:20', ${colorGreen}, ${ic_book}, '20220202_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-02-02-수 18:41:20', ${colorYellow}, ${ic_stats}, '20220202_184120.jpg', '매일 운동하기', 0)")

        // 목
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-02-03-목 02:10:14', ${colorOrange}, ${ic_computer}, '20220203_021014.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-02-03-목 02:32:14', ${colorOrange}, ${ic_playLesson}, '20220203_023214.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-02-03-목 15:43:20', ${colorSeedBrown}, ${ic_dumble}, '20220203_154320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-02-03-목 15:55:20', ${colorSeedBrown}, ${ic_dumble}, '20220203_155520.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-02-03-목 07:23:20', ${colorGreen}, ${ic_book}, '20220203_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-02-03-목 18:41:20', ${colorYellow}, ${ic_stats}, '20220203_184120.jpg', '매일 운동하기', 0)")

        // 금
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-02-04-금 02:20:14', ${colorOrange}, ${ic_computer}, '20220204_022014.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-02-04-금 02:32:14', ${colorOrange}, ${ic_playLesson}, '20220204_023214.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-02-04-금 15:33:20', ${colorSeedBrown}, ${ic_dumble}, '20220204_153320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-02-04-금 15:46:20', ${colorSeedBrown}, ${ic_dumble}, '20220204_154620.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-02-04-금 07:23:20', ${colorGreen}, ${ic_book}, '20220204_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-02-04-금 18:41:20', ${colorYellow}, ${ic_stats}, '20220204_184120.jpg', '매일 운동하기', 0)")

        // 토
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-02-05-토 02:16:14', ${colorOrange}, ${ic_computer}, '20220205_021614.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-02-05-토 02:36:14', ${colorOrange}, ${ic_playLesson}, '20220205_023614.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-02-05-토 15:33:20', ${colorSeedBrown}, ${ic_dumble}, '20220205_153320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-02-05-토 15:53:20', ${colorSeedBrown}, ${ic_dumble}, '20220205_155320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-02-05-토 07:23:20', ${colorGreen}, ${ic_book}, '20220205_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-02-05-토 18:41:20', ${colorYellow}, ${ic_stats}, '20220205_184120.jpg', '매일 운동하기', 0)")

        // 일
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-02-06-일 02:28:14', ${colorOrange}, ${ic_computer}, '20220206_022814.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-02-06-일 02:42:14', ${colorOrange}, ${ic_playLesson}, '20220206_024214.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-02-06-일 15:33:20', ${colorSeedBrown}, ${ic_dumble}, '20220206_153320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-02-06-일 15:53:20', ${colorSeedBrown}, ${ic_dumble}, '20220206_155320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-02-06-일 07:23:20', ${colorGreen}, ${ic_book}, '20220206_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-02-06-일 18:41:20', ${colorYellow}, ${ic_stats}, '20220206_184120.jpg', '매일 운동하기', 0)")

        // 월
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 백준 실버 3개 풀기', '2022-02-07-월 02:23:14', ${colorOrange}, ${ic_computer}, '20220207_022314.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('알고리즘 풀이 강의 듣기', '2022-02-07-월 02:51:14', ${colorOrange}, ${ic_playLesson}, '20220207_025114.jpg', '알고리즘 공부하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('줄넘기 100개하기', '2022-02-07-월 15:33:20', ${colorSeedBrown}, ${ic_dumble}, '20220207_153320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('스쿼트 15개씩 4세트하기', '2022-02-07-월 15:53:20', ${colorSeedBrown}, ${ic_dumble}, '20220207_155320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('하루에 30분 읽기', '2022-02-07-월 07:23:20', ${colorGreen}, ${ic_book}, '20220207_072320.jpg', '매일 운동하기', 0)")
        sqlitedb.execSQL("INSERT INTO detail_goal_time_report_db VALUES ('경제 서적 읽기', '2022-02-07-월 18:41:20', ${colorYellow}, ${ic_stats}, '20220207_184120.jpg', '매일 운동하기', 0)")

        sqlitedb.close()
        dbManager.close()
    }
}