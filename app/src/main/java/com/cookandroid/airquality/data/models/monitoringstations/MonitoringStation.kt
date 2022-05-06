package com.cookandroid.airquality.data.models.monitoringstations


import com.google.gson.annotations.SerializedName

// Item = Station
data class MonitoringStation(
    @SerializedName("addr")
    val addr: String?,
    @SerializedName("stationName")
    val stationName: String?,
    @SerializedName("tm")
    val tm: Double?
)