package com.mehmetalan.weather.models

import com.google.gson.annotations.SerializedName

data class HourlyForecast(
    @SerializedName("Data")
    val date: String,
    @SerializedName("EpochDateTime")
    val epochDateTime: Long,
    @SerializedName("WeatherIcon")
    val weatherIcon: Int,
    @SerializedName("IconPhrase")
    val iconPhrase: String,
    @SerializedName("HasPrecipitation")
    val hasPrecipitation: Boolean,
    @SerializedName("IsDailylight")
    val isDailylight: Boolean,
    @SerializedName("Temperature")
    val temperature: Value
)