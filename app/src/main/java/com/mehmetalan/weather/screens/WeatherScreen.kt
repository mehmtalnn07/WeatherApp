package com.mehmetalan.weather.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.ArrowDropDown
import androidx.compose.material.icons.sharp.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mehmetalan.weather.R
import com.mehmetalan.weather.models.BaseModels
import com.mehmetalan.weather.viewmodels.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WeatherScreen(
    navController: NavController,
    locationKey: String,
    locationName: String,
    country: String,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val dailyForecast by weatherViewModel.dailyForecast.collectAsState()
    val hourlyForecast by weatherViewModel.hourlyForecast.collectAsState()
    
    LaunchedEffect(
        Unit
    ) {
        weatherViewModel.getDailyForecast(locationKey)
        weatherViewModel.getHourlyForecast(locationKey)

    }
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = locationName,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = Color.Black
                        )
                        Text(
                            text = country,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(route = "home")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back Button",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        },
        containerColor = Color.Transparent
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            Column (
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp,)
                ) {
                    AnimatedVisibility(
                        visible = hourlyForecast is BaseModels.Success
                    ) {
                        val data = hourlyForecast as BaseModels.Success
                        val temp = data.data.first().temperature
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${temp.value}°C",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 80.sp,
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = hourlyForecast is BaseModels.Loading
                    ) {
                        Loading()
                    }
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.daily_forecast),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                    )
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )
                    AnimatedVisibility(
                        visible = dailyForecast is BaseModels.Success
                    ) {
                        val data = dailyForecast as BaseModels.Success
                        LazyColumn (
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(data.data.dailyForecast) { forecast ->
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(shape = RoundedCornerShape(16.dp))
                                        .padding(start = 16.dp, end = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${SimpleDateFormat("EEEE", Locale("tr","TR")).format(Date(forecast.epochDate*1000))}",
                                    )
                                    Row{
                                        Icon(
                                            Icons.Sharp.ArrowDropDown,
                                            contentDescription = null,
                                            tint = Color.Green
                                        )
                                        Text(
                                            text = "${forecast.temperature.min.value}°C",
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .width(6.dp)
                                        )
                                        Icon(
                                            Icons.Sharp.KeyboardArrowUp,
                                            contentDescription = null,
                                            tint = Color.Red
                                        )
                                        Text(
                                            text = "${forecast.temperature.max.value}°C",
                                        )
                                    }
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(70.dp),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("https://developer.accuweather.com/sites/default/files/${forecast.day.icon.fixIcon()}-s.png")
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )
                    AnimatedVisibility(
                        visible = hourlyForecast is BaseModels.Loading
                    ) {
                        Loading()
                    }
                    Text(
                        text = stringResource(id = R.string.hourly_forecast),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                    )
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )
                    AnimatedVisibility(
                        visible = hourlyForecast is BaseModels.Success
                    ) {
                        val data = hourlyForecast as BaseModels.Success
                        LazyRow (
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(data.data){ forecast ->
                                Column (
                                    modifier = Modifier
                                        .size(100.dp, 140.dp)
                                        .clip(shape = RoundedCornerShape(16.dp))
                                        .background(color = colorResource(id = R.color.green)),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = SimpleDateFormat("H a").format(Date(forecast.epochDateTime*1000)),
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(8.dp)
                                    )
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(70.dp),
                                        model = ImageRequest.Builder(LocalContext.current).data("https://developer.accuweather.com/sites/default/files/${forecast.weatherIcon.fixIcon()}-s.png").build(),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = null
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(8.dp)
                                    )
                                    Text(
                                        text = forecast.temperature.value.toString() + "°C",
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = hourlyForecast is BaseModels.Loading
                    ) {
                        Loading()
                    }
                }
            }
        }
    }
}

@Composable
fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White
        )
    }
}

fun Int.fixIcon(): String {
    return if (this > 9){
        toString()
    } else{
        "0${this}"
    }
}