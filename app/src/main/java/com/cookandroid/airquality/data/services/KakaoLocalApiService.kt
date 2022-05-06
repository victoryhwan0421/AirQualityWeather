package com.cookandroid.airquality.data.services

import com.cookandroid.airquality.BuildConfig
import com.cookandroid.airquality.data.models.tmcoordinates.TmCoordinatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// 인터페이스로 구현
interface KakaoLocalApiService {

    // Headers를 통해서 인증정보 전달, gradle.properties에서 KAKAO_API_KEY키 등록 먼저!
    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    // @GET주소: KAKAO_DEVELOPERS: Request URL 참고
    @GET("/v2/local/geo/transcoord.json?output_coord=TM")
    suspend fun getTmCoordinates(
        @Query("x") longitude: Double,
        @Query("y") latitude: Double
    ): Response<TmCoordinatesResponse>// 여기까지 Kakao_Local_API 정의
}