package com.karolisstuff.ukcrimemapping.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karolisstuff.ukcrimemapping.domain.model.Crime
import com.karolisstuff.ukcrimemapping.domain.usecase.GetCrimesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // This annotation is required for Hilt to generate the ViewModel
class CrimeViewModel @Inject constructor(
    private val getCrimesUseCase: GetCrimesUseCase // Ensure this use case is correctly provided by Hilt
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _crimes = MutableStateFlow<List<Crime>>(emptyList())
    val crimes: StateFlow<List<Crime>> = _crimes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchUserLocation(lat: Double, lng: Double) {
        _userLocation.value = Pair(lat, lng)
        fetchCrimes(lat, lng)
    }

    private fun fetchCrimes(lat: Double, lng: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            getCrimesUseCase(lat, lng).collect { crimes ->
                _crimes.value = crimes
                _isLoading.value = false
            }
        }
    }
}
