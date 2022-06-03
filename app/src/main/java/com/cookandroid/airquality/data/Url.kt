package com.cookandroid.airquality.data

object Url {
    /// Request URL 참고
    const val KAKAO_API_BASE_URL = "https://dapi.kakao.com/"

    /// 웹브라우저 작업 시 http 허용하지 않기에 Manifest에서
    /// android:usesCleartextTraffic="true" 와
    /// <uses-permission android:name="android.permission.INTERNET"/> 추가하기
    const val AIR_KOREA_API_BASE_URL = "http://apis.data.go.kr/"


    /// 기상청 단기예보 URL 참고
    const val WEATHER_SERVICE_API_BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

}