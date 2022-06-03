package com.cookandroid.airquality.data.services


import com.cookandroid.airquality.BuildConfig
import com.cookandroid.airquality.data.models.ModelWeather.WEATHER

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastApiService {

    /// Retrofit에서 Service는 API를 정의하는 인터페이스를 의미.
    /// 어떤 형태와 방식으로 통신할지 어노테이션과 파라미터 지정.
    /// Service는 기본적으로 Call<> 객체를 반환
    /// 클라이언트는 Retrofit을 통해 call<String>을 받게
    /// Call Back URL: http://~~go.kr/ 이하 주소를 @GET()내부에 입력
//    @GET("1360000/VilageFcstInfoService_2.0/getVilageFcst" +
//                "?serviceKey=${BuildConfig.WEATHER_SERVICE_KEY}"+
//                "&returnType=json")
    @GET("getUltraSrtFcst" + "?serviceKey=${BuildConfig.WEATHER_SERVICE_KEY}")

    fun getWeather(
        @Query("numOfRows") num_of_rows: Int, // 한페이 경과 수
        @Query("pageNo") page_no: Int,        // 페이지 번호
        @Query("dataType")data_type: String,  // 응답 자료 형식
        @Query("base_date") base_date:String, // 발표 일자
        @Query("base_time") base_time:String, // 발표 시
        @Query("nx") nx:Int,                  // 예보지점 x좌표
        @Query("ny") ny:Int                   // 예보지점 y좌표
    ): Call<WEATHER>
}