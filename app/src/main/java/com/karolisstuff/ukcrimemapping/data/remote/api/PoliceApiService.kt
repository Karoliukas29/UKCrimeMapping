package com.karolisstuff.ukcrimemapping.data.remote.api

import io.ktor.client.request.*
import javax.inject.Inject
import com.karolisstuff.ukcrimemapping.data.model.CrimeResponseItem
import io.ktor.client.call.body

class PoliceApiService @Inject constructor() {

    suspend fun getCrimes(lat: Double, lng: Double, date: String): List<CrimeResponseItem> {
        return KtorClient.client.get("https://data.police.uk/api/crimes-at-location") {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("date", date)
        }.body()
    }
}
