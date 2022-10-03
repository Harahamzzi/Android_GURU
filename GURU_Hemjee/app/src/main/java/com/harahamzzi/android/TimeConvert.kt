package com.harahamzzi.android

import java.math.BigInteger

// 각 시간값 관련 변환 클래스
class TimeConvert {

    /*시간 설정 변환 클래스
    *
    * 1. 형식에 맞춘 문자열로 반환해 주는 함수
    *   인자: 시간, 분, 초 정보가 담긴 String
    *   사용법: TimeConvert.timeStrConvert(String, String, String)
    *
    * 2. 밀리초(ms) -> hh:mm:ss 변환 함수
    *   인자: 밀리초 단위의 시간 데이터(int)
    *   사용법: TimeConvert.msToTimeConvert(ms: Int)
    *
    * 3. hh:mm:ss -> 밀리초(ms) 변환 함수
    *   인자: hh:mm:ss 형태의 String
    *   사용법: TimeConvert.timeToMsConvert(time: String)
    *
    * */

    companion object {

        private var time: String = "00:00:00"

        // 1. 시간값을 각각 받아 hh:mm:ss 형식에 맞춘 문자열로 반환하는 함수
        fun timeStrConvert(hour: String?, min: String?, sec: String?): String {
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
                    time += "0"+sec

            }
            return time
        }

        // 2. 밀리초(ms) -> hh:mm:ss 변환 함수
        fun msToTimeConvert(ms: BigInteger): String {

            var bigInt1000 = 1000.toBigInteger()
            var bigInt60 = 60.toBigInteger()
            var bigInt24 = 24.toBigInteger()

            var hour = ms / bigInt1000 / bigInt60 / bigInt60 % bigInt24
            var min = ms / bigInt1000 / bigInt60 % bigInt60
            var sec = ms / bigInt1000 % bigInt60

            return timeStrConvert(hour.toString(), min.toString(), sec.toString())
        }

        // 3. hh:mm:ss -> 밀리초(ms) 변환 함수
        fun timeToMsConvert(time: String): BigInteger {

            var hour: BigInteger = time.split(':')[0].toBigInteger()
            var min: BigInteger = time.split(':')[1].toBigInteger()
            var sec: BigInteger = time.split(':')[2].toBigInteger()

            var bigInt1000 = 1000.toBigInteger()
            var bigInt60 = 60.toBigInteger()

            var result: BigInteger = (hour * bigInt1000 * bigInt60 * bigInt60) + (min * bigInt1000 * bigInt60) + (sec * bigInt1000)

            return result
        }
    }

}