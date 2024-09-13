package com.karolisstuff.ukcrimemapping.data.repository

import com.karolisstuff.ukcrimemapping.data.mapper.toDomain
import com.karolisstuff.ukcrimemapping.data.remote.api.PoliceApiService
import com.karolisstuff.ukcrimemapping.domain.model.Crime
import com.karolisstuff.ukcrimemapping.domain.repository.CrimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CrimeRepositoryImpl @Inject constructor(
    private val apiService: PoliceApiService
) : CrimeRepository {

    override fun getCrimes(lat: Double, lng: Double, date: String): Flow<List<Crime>> = flow {
        val response = apiService.getCrimes(lat, lng, date)
        val crimeList = response.map { it.toDomain() }
        emit(crimeList)
    }
}
