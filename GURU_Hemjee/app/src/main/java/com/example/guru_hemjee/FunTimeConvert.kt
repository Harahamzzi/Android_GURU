package com.example.guru_hemjee

// 각 시간값(문자열)을 받아 변환하여 문자열로 반환하는 변환 클래스
class FunTimeConvert {

    companion object {
        private var time: String = "00:00:00"
        fun timeConvert(hour: String?, min: String?, sec: String?): String{
            if(hour.isNullOrBlank()){
                time = "00:"
            } else {
                if(hour.toInt()/10 >= 1)
                    time = hour + ":"
                else
                    time = "0" + hour +":"
            }

            if(min.isNullOrBlank()){
                time += "00:"
            } else {
                if(min.toInt()/10 >= 1)
                    time += min + ":"
                else
                    time += "0"+min+":"

            }

            if(sec.isNullOrBlank()){
                time += "00"
            } else {
                if(sec.toInt()/10 >= 1)
                    time += sec
                else
                    time += "0"+sec+":"

            }
            return time
        }
    }

}