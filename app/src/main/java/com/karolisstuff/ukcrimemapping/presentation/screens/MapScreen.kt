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
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.karolisstuff.ukcrimemapping.presentation.components.SearchBar
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.MapViewModel
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.CrimeViewModel
import timber.log.Timber

@Composable
fun MapScreen(
    mapViewModel: MapViewModel, // Continue using MapViewModel for user and selected locations
    crimeViewModel: CrimeViewModel // Add CrimeViewModel to fetch and display crimes
) {
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current

    // Observe user location from MapViewModel
    val userLocation by mapViewModel.userLocation

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

    // When userLocation is updated, trigger CrimeViewModel to fetch crimes
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            crimeViewModel.fetchUserLocation(location.latitude, location.longitude)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(18.dp))

        // Search bar to update selected location
        SearchBar(
            onPlaceSelected = { place ->
                mapViewModel.selectLocation(place, context)
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
                cameraPositionState = cameraPositionState
            ) {
                // Show the user's location marker
                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Your Location"
                    )
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 10f)
                }

                // Show crime markers on the map
                crimes.forEach { crime ->
                    Marker(
                        state = MarkerState(position = com.google.android.gms.maps.model.LatLng(crime.latitude, crime.longitude)),
                        title = crime.category,
                        snippet = crime.outcome
                    )
                }
            }
        }
    }
}
