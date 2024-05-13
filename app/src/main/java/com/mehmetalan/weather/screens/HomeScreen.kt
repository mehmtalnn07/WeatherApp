package com.mehmetalan.weather.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowDropDown
import androidx.compose.material.icons.sharp.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
import com.mehmetalan.weather.R
import com.mehmetalan.weather.models.BaseModels
import com.mehmetalan.weather.viewmodels.HomeViewModel
import com.mehmetalan.weather.viewmodels.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentLocation by homeViewModel.currentLocation.collectAsState()
    var currentLocationKey by remember { mutableStateOf("") }
    val weatherViewModel: WeatherViewModel = viewModel()
    val dailyForecast by weatherViewModel.dailyForecast.collectAsState()
    val hourlyForecast by weatherViewModel.hourlyForecast.collectAsState()
    val locations by homeViewModel.location.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("weather_app", Context.MODE_PRIVATE)
    var hasLocationPermission by remember { mutableStateOf(sharedPreferences.getBoolean("location_permission", false)) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {isGranted ->
        hasLocationPermission = isGranted
    }
    LaunchedEffect(Unit) {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            hasLocationPermission = true
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            coroutineScope.launch {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                try {
                    val location = fusedLocationClient.lastLocation.await()
                    if (location != null) {
                        val geoPosition = "${location.latitude},${location.longitude}"
                        homeViewModel.getCurrentLocation(geoPosition = geoPosition)
                    } else {
                        Toast.makeText(context, "Konum bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Konum alınamadı", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        sharedPreferences.edit().putBoolean("location_permission", hasLocationPermission).apply()
    }

    val (city, setCity) = remember {
        mutableStateOf("")
    }

    LaunchedEffect(city) {
        delay(500)
        if (city.isNotEmpty()) {
            homeViewModel.searchLocation(city)
        }
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { setCity(it) },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.textfield_place_holder),
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 17.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Black,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Black,
                            focusedContainerColor = Color.Transparent,
                            unfocusedPlaceholderColor = Color.Black,
                            focusedPlaceholderColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(50.dp),
                        shape = RoundedCornerShape(32.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .height(70.dp)
            )
        },
        containerColor = Color.Transparent
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp,)
                ) {
                    Text(
                        text = stringResource(id = R.string.welcome_app),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(top = 5.dp)
                    )
                    Text(
                        text = "${SimpleDateFormat("EEEE, d MMMM HH:mm", Locale("tr", "TR")).format(Date(System.currentTimeMillis()))}"
                    )
                    Spacer(
                        modifier = Modifier
                            .height(20.dp)
                    )
                    AnimatedVisibility(
                        visible = locations is BaseModels.Success,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.search_cities)
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(10.dp)
                            )
                            if (city != "") {
                                when (val data = locations) {
                                    is BaseModels.Success -> {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            items(data.data) { location ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(50.dp)
                                                        .clip(shape = RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            navController.navigate(route = "weather/${location.key}/${location.englishName}/${location.country.englishName}")
                                                        }
                                                        .padding(8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = location.englishName,
                                                            fontWeight = FontWeight.ExtraBold
                                                        )
                                                        Text(
                                                            text = location.country.englishName,
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    else -> {

                                    }
                                }
                            }

                        }
                    }
                    AnimatedVisibility(
                        visible = locations is BaseModels.Loading,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                    )
                    if (city == "") {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AnimatedVisibility(
                                    visible = currentLocation is BaseModels.Success,
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut()
                                ) {
                                    when (val data = currentLocation) {
                                        is BaseModels.Success -> {
                                            currentLocationKey = data.data.key
                                            LaunchedEffect(
                                                Unit
                                            ) {
                                                weatherViewModel.getDailyForecast(currentLocationKey)
                                                weatherViewModel.getHourlyForecast(currentLocationKey)
                                            }
                                            Text(
                                                text = data.data.englishName,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 50.sp
                                            )
                                        }

                                        is BaseModels.Loading -> {
                                            CircularProgressIndicator()
                                        }

                                        else -> {
                                            Text("Konum alınamadı")
                                        }
                                    }
                                }
                            }
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
                                        fontSize = 80.sp
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = hourlyForecast is BaseModels.Loading
                            ) {
                                AnimatedLoading()
                            }
                            Text(
                                text = stringResource(id = R.string.daily_forecast),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(5.dp)
                            )
                            AnimatedVisibility(
                                visible = dailyForecast is BaseModels.Success
                            ) {
                                val data = dailyForecast as BaseModels.Success
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(data.data.dailyForecast) { forecast ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .clip(shape = RoundedCornerShape(4.dp))
                                                .padding(start = 16.dp, end = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${SimpleDateFormat("EEEE", Locale("tr", "TR")).format(Date(forecast.epochDate * 1000))}",
                                            )
                                            Row {
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
                            AnimatedVisibility(
                                visible = hourlyForecast is BaseModels.Loading
                            ) {
                                AnimatedLoading()
                            }
                            Text(
                                text = stringResource(id = R.string.hourly_forecast),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(5.dp)
                            )
                            AnimatedVisibility(
                                visible = hourlyForecast is BaseModels.Success
                            ) {
                                val data = hourlyForecast as BaseModels.Success
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(data.data) { forecast ->
                                        Column(
                                            modifier = Modifier
                                                .size(100.dp, 140.dp)
                                                .clip(shape = RoundedCornerShape(16.dp))
                                                .background(color = colorResource(id = R.color.green)),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = SimpleDateFormat("H a").format(Date(forecast.epochDateTime * 1000)),
                                            )
                                            Spacer(
                                                modifier = Modifier
                                                    .height(8.dp)
                                            )
                                            AsyncImage(
                                                modifier = Modifier
                                                    .size(70.dp),
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data("https://developer.accuweather.com/sites/default/files/${forecast.weatherIcon.fixIcon()}-s.png")
                                                    .build(),
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
                                AnimatedLoading()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedLoading() {
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
        onDispose {
        }
    }
    if (isLoading) {
        Loading()
    }
}