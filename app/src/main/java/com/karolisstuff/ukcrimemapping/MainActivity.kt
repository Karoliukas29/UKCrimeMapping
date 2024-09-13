package com.karolisstuff.ukcrimemapping

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.libraries.places.api.Places
import com.karolisstuff.ukcrimemapping.presentation.screens.MapScreen
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.CrimeViewModel
import com.karolisstuff.ukcrimemapping.utils.ManifestUtils
import com.karolisstuff.ukcrimemapping.presentation.viewmodel.MapViewModel
import com.karolisstuff.ukcrimemapping.ui.theme.UKCrimeMappingTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels() // Continue using Hilt's ViewModel injection
    private val crimeViewModel: CrimeViewModel by viewModels() // Hilt will inject this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the API key from the manifest file
        val apiKey = ManifestUtils.getApiKeyFromManifest(this)
        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(applicationContext, apiKey)
        }

        enableEdgeToEdge()

        setContent {
            UKCrimeMappingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass both ViewModels to MapScreen
                    MapScreen(mapViewModel = mapViewModel, crimeViewModel = crimeViewModel)
                }
            }
        }
    }
}
