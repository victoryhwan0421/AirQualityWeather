package com.cookandroid.airquality.data.services

import com.cookandroid.airquality.BuildConfig
import com.cookandroid.airquality.data.models.airquality.AirQualityResponse
import com.cookandroid.airquality.data.models.monitoringstations.MonitoringStationsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {

    /// Call Back URL: http://~~go.kr/ 이하 주소를 @GET()내부에 입력
    @GET("B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
    "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}"+
    "&returnType=json")
    suspend fun getNearByMonitoringStation(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MonitoringStationsResponse>

    /// Call Back URL: http://~~go.kr/ 이하 주소를 @GET()내부에 입력
    @GET("B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"+
            "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
            "&returnType=json" +
            "&dataTerm=DAILY" +
            "&ver=1.3")
    suspend fun getRealtimeAirQualities(
        @Query("stationName") stationName: String
    ): Response<AirQualityResponse>
}