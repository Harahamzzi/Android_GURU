package com.example.guru_hemjee

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