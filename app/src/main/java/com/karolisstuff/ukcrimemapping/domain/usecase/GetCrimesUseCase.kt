package com.karolisstuff.ukcrimemapping.domain.usecase

import com.karolisstuff.ukcrimemapping.domain.model.Crime
import com.karolisstuff.ukcrimemapping.domain.repository.CrimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetCrimesUseCase @Inject constructor(
    private val repository: CrimeRepository
) {
    operator fun invoke(lat: Double, lng: Double): Flow<List<Crime>> = flow {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

        val currentDate = LocalDate.now()

        val datesToFetch = listOf(
            currentDate.format(formatter),
            currentDate.minusMonths(1).format(formatter),
            currentDate.minusMonths(2).format(formatter),
            currentDate.minusMonths(3).format(formatter),
            currentDate.minusMonths(4).format(formatter),
            currentDate.minusMonths(5).format(formatter),
            currentDate.minusMonths(6).format(formatter),


            )

        val crimesForAllMonths = mutableListOf<Crime>()
        for (date in datesToFetch) {
            repository.getCrimes(lat, lng, date).collect { crimes ->
                crimesForAllMonths.addAll(crimes)
                println("Crimes for $date: ${crimes.size}")

            }
        }
        println("------------Total crimes collected: ${crimesForAllMonths.size}")

        emit(crimesForAllMonths)
    }
}
