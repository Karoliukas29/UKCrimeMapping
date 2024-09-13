package com.karolisstuff.ukcrimemapping.presentation.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.karolisstuff.ukcrimemapping.presentation.components.SearchBar
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.MapViewModel
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.CrimeViewModel
import timber.log.Timber


// Function to add a slight jitter to the coordinates to prevent marker overlap
fun addJitterToCoordinates(lat: Double, lng: Double, index: Int): Pair<Double, Double> {
    val jitter = 0.0009 * (index % 5) // Adjust the multiplier for better spacing
    return Pair(lat + jitter, lng + jitter)
}

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    crimeViewModel: CrimeViewModel
) {
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current

    // Observe user location from MapViewModel
    val userLocation by mapViewModel.userLocation

    // Observe selected location from MapViewModel
    val selectedLocation by mapViewModel.selectedLocation

    // Observe crimes from CrimeViewModel
    val crimes by crimeViewModel.crimes.collectAsState()
    val isLoading by crimeViewModel.isLoading.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Handle location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mapViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            Timber.e("Location permission was denied by the user.")
        }
    }

    // Request location permission on launch
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                mapViewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // ** Trigger Crime Fetching on User Location **
    var isCameraMoved by remember { mutableStateOf(false) }
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            crimeViewModel.fetchUserLocation(location.latitude, location.longitude)

            if (!isCameraMoved) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(location, 15f)
                )
                isCameraMoved = true
            }
        }
    }

    // Update the camera position when a new location is selected from the SearchBar
    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f)
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(18.dp))

        // Search bar to update selected location
        SearchBar(
            onPlaceSelected = { place ->
                mapViewModel.selectLocation(place, context, crimeViewModel)
            }
        )

        // Show loading indicator while fetching crimes
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Display the Google Map with user location and crime markers
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true
                )
            ) {
                // Show crime markers on the map, applying jitter to avoid overlapping markers
                crimes.forEachIndexed { index, crime ->
                    val (adjustedLat, adjustedLng) = addJitterToCoordinates(crime.latitude, crime.longitude, index)
                    Marker(
                        state = MarkerState(position = LatLng(adjustedLat, adjustedLng)),
                        title = crime.category,
                        snippet = crime.outcome
                    )
                }
            }
        }
    }
}


