package com.mehmetalan.weather.models

sealed class BaseModels <out T> {
    data class Success<T>(val data:T):BaseModels<T>()
    data class Error(val error: String):BaseModels<Nothing>()
    object Loading: BaseModels<Nothing>()

}