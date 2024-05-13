package com.mehmetalan.weather.models

import com.google.gson.annotations.SerializedName

data class DailyForecasts(
    @SerializedName("DailyForecasts")
    val dailyForecast: List<DailyForecast>,

)