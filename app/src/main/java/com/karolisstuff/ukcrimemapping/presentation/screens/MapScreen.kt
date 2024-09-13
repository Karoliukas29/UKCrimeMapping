package com.karolisstuff.ukcrimemapping.presentation.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import com.karolisstuff.ukcrimemapping.domain.model.Crime
import com.karolisstuff.ukcrimemapping.presentation.components.CrimeDetailsDialog
import com.karolisstuff.ukcrimemapping.presentation.components.SearchBar
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.MapViewModel
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.CrimeViewModel
import com.karolisstuff.ukcrimemapping.utils.addJitterToCoordinates
import timber.log.Timber


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

    // States to control the dialog visibility and selected crime
    var showDialog by remember { mutableStateOf(false) }
    var selectedCrime by remember { mutableStateOf<Crime?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val existingCoordinates = mutableSetOf<Pair<Double, Double>>() // Set to track used coordinates


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
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                mapViewModel.fetchUserLocation(context, fusedLocationClient)
            }

            else -> {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // ** Trigger Crime Fetching on User Location and move camera **
    var isCameraMovedToUserLocation by remember { mutableStateOf(false) }

    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            crimeViewModel.fetchUserLocation(location.latitude, location.longitude)

            // Move the camera to user's location only once
            if (!isCameraMovedToUserLocation) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)
                )
                isCameraMovedToUserLocation = true
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
                    val (adjustedLat, adjustedLng) = addJitterToCoordinates(
                        crime.latitude,
                        crime.longitude,
                        index,
                        existingCoordinates
                    )
                    Marker(
                        state = MarkerState(position = LatLng(adjustedLat, adjustedLng)),
                        title = crime.category,
                        snippet = "Click for details", // Short description

                        // On clicking the info window, show detailed info in a dialog
                        onInfoWindowClick = {
                            selectedCrime = crime // Set the selected crime
                            showDialog = true // Show the dialog
                        }
                    )
                }
            }
        }
    }

    // Display the dialog with crime details
    if (showDialog && selectedCrime != null) {
        CrimeDetailsDialog(
            crime = selectedCrime!!,
            onDismiss = { showDialog = false }
        )
    }
}
