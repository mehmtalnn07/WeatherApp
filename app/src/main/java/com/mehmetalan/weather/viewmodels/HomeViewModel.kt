package com.mehmetalan.weather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetalan.weather.models.BaseModels
import com.mehmetalan.weather.models.Location
import com.mehmetalan.weather.repositories.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel: ViewModel(), KoinComponent {

    val repo: WeatherRepo by inject()
    private val _locations: MutableStateFlow<BaseModels<List<Location>>?> = MutableStateFlow(null)
    val location = _locations.asStateFlow()

    private val _currentLocation: MutableStateFlow<BaseModels<Location>?> = MutableStateFlow(null)
    val currentLocation = _currentLocation.asStateFlow()

    fun searchLocation(query: String) {
        viewModelScope.launch {
            _locations.update { BaseModels.Loading }
            repo.searchLocation(query).also {data ->
                _locations.update { data }
            }
        }
    }

    fun getCurrentLocation(geoPosition: String) {
        viewModelScope.launch {
            _currentLocation.update { BaseModels.Loading }
            repo.getCurrentLocation(geoPosition).also {data ->
                _currentLocation.update { data }
            }
        }
    }

}