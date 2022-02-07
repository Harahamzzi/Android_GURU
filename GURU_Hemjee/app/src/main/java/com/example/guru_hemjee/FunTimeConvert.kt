package com.example.guru_hemjee

// 각 시간값(문자열)을 받아 변환하여 문자열로 반환하는 변환 클래스
class FunTimeConvert {

    /*시간 설정 변환 클래스
    * 인자: 시간, 분, 초 정보가 담긴 String
    * 사용법: FunTimeConvert.timeConvert(String, String, String)
    * */

    companion object {
        private var time: String = "00:00:00"
        fun timeConvert(hour: String?, min: String?, sec: String?): String{
            //시간 설정
            if(hour.isNullOrBlank()){
                time = "00:"
            } else {
                //00:00:00 형식을 맞추기 위한 부분
                if(hour.toInt()/10 >= 1)
                    time = hour + ":"
                else
                    time = "0" + hour +":"
            }

            //분 설정
            if(min.isNullOrBlank()){
                time += "00:"
            } else {
                //00:00:00 형식을 맞추기 위한 부분
                if(min.toInt()/10 >= 1)
                    time += min + ":"
                else
                    time += "0"+min+":"

            }

            //초 설정
            if(sec.isNullOrBlank()){
                time += "00"
            } else {
                //00:00:00 형식을 맞추기 위한 부분
                if(sec.toInt()/10 >= 1)
                    time += sec
                else
                    time += "0"+sec+":"

            }
            return time
        }
    }

}