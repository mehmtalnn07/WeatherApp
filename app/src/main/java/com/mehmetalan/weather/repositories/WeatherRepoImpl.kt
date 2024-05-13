package com.mehmetalan.weather.repositories

import com.mehmetalan.weather.models.BaseModels
import com.mehmetalan.weather.models.DailyForecasts
import com.mehmetalan.weather.models.HourlyForecast
import com.mehmetalan.weather.models.Location
import com.mehmetalan.weather.network.Api
import retrofit2.Response

class WeatherRepoImpl(private val api:Api): WeatherRepo {
    override suspend fun searchLocation(query: String): BaseModels<List<Location>> {
        return request {
            api.searchLocation(query = query)
        }
    }

    override suspend fun getCurrentLocation(geoPosition: String): BaseModels<Location> {
        return request {
            api.getCurrentLocation(geoPosition = geoPosition)
        }
    }

    override suspend fun getDailyForecasts(locationKey: String): BaseModels<DailyForecasts> {
        return request {
            api.getDailyForecasts(locationKey = locationKey)
        }
    }

    override suspend fun getHourlyForecasts(locationKey: String): BaseModels<List<HourlyForecast>> {
        return request {
            api.getHourlyForecasts(locationKey = locationKey)
        }
    }
}
suspend fun<T> request(request: suspend  ()->Response<T>):BaseModels<T> {
    try {
        request().also {
            return if (it.isSuccessful) {
                BaseModels.Success(it.body()!!)
            } else {
                BaseModels.Error(it.errorBody()?.string().toString())
            }
        }
    } catch (e: Exception) {
        return BaseModels.Error(e.message.toString())
    }
}