package com.karolisstuff.ukcrimemapping.domain.repository


import com.karolisstuff.ukcrimemapping.domain.model.Crime
import kotlinx.coroutines.flow.Flow

interface CrimeRepository {
    fun getCrimes(lat: Double, lng: Double, date: String): Flow<List<Crime>>
}
