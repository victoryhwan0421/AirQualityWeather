package com.cookandroid.airquality.data.models.monitoringstations


import com.google.gson.annotations.SerializedName

data class Body(
    // 이름을 변경해도 gson에서는 Serialized된 "items"로 인식함!
    @SerializedName("items")
    val monitoringStations: List<MonitoringStation>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)