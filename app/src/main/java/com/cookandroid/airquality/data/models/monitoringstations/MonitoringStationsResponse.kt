package com.cookandroid.airquality.data.models.monitoringstations


import com.google.gson.annotations.SerializedName

data class MonitoringStationsResponse(
    @SerializedName("response")
    val response: Response?
)
