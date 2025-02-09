package com.karolisstuff.ukcrimemapping.data.remote.api

import com.karolisstuff.ukcrimemapping.data.model.CrimeResponseItem
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*


class PoliceApiService(private val client: HttpClient) {

    suspend fun getCrimes(lat: Double, lng: Double, date: String): List<CrimeResponseItem> {
        return try {
            // Make the API request
            val response = client.get("https://data.police.uk/api/crimes-at-location") {
                parameter("lat", lat)
                parameter("lng", lng)
                parameter("date", date)
            }
            if (response.status.isSuccess()) {
                // Parse the response as a list of CrimeResponseItem
                val crimeResponse: List<CrimeResponseItem> = response.body()
                crimeResponse // Return the list of crimes
            } else {
                emptyList() // Return an empty list if the response is not successful
            }
        } catch (e: Exception) {
            emptyList() // Return an empty list if an error occurs
        }
    }
}
