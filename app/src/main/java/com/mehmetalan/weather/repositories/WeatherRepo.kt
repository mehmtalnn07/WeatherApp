package com.mehmetalan.weather.repositories

import com.mehmetalan.weather.models.BaseModels
import com.mehmetalan.weather.models.DailyForecasts
import com.mehmetalan.weather.models.HourlyForecast
import com.mehmetalan.weather.models.Location

interface WeatherRepo {
    suspend fun searchLocation(query: String):BaseModels<List<Location>>
    suspend fun getDailyForecasts(locationKey: String):BaseModels<DailyForecasts>
    suspend fun getHourlyForecasts(locationKey: String):BaseModels<List<HourlyForecast>>
    suspend fun getCurrentLocation(geoPosition: String): BaseModels<Location>
}