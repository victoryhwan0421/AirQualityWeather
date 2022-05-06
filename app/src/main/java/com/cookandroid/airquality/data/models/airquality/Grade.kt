package com.cookandroid.airquality.data.models.airquality

import androidx.annotation.ColorRes
import com.google.gson.annotations.SerializedName
import com.cookandroid.airquality.R

// gson의 경우 @SerializedName()으로 각각의 값을 지정하면 자동으로 enum으로 매핑됨
// 이모지 단축기 window키 + ; (윈도우os)
// 색상값 https://material.io/resources/color/#!/?view.left=0&view.right=0 참고하여
// res->values -> colors.xml 에 추가하기
enum class Grade(
    val label: String,
    val emoji:String,
    @ColorRes val colorsResId: Int
    ) {
    @SerializedName("1")
    GOOD("좋음","😊", R.color.blue),

    @SerializedName("2")
    NORMAL("보통", "😢", R.color.green),

    @SerializedName("3")
    BAD("나쁨", "😢", R.color.yellow),

    @SerializedName("4")
    AWFUL("매우 나쁨", "🤢", R.color.red),

    // gson에서 위의 경우를 제외한 값이 들어올 경우 null로 배정됨
    UNKNOWN("미측정", "🤔", R.color.gray);

    override fun toString(): String {
        return "$label $emoji"
    }
}