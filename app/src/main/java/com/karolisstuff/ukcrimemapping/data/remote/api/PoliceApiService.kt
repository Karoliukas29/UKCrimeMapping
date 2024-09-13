package com.karolisstuff.ukcrimemapping.data.remote.api

import com.karolisstuff.ukcrimemapping.data.model.CrimeResponseItem
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import android.util.Log
import io.ktor.client.statement.bodyAsText

class PoliceApiService(private val client: HttpClient) {

    suspend fun getCrimes(lat: Double, lng: Double, date: String): List<CrimeResponseItem> {
        return try {
            Log.d("PoliceApiService", "+++++ getCrimes called with lat=$lat, lng=$lng, date=$date")

            // Make the API request
            val response = client.get("https://data.police.uk/api/crimes-at-location") {
                parameter("lat", lat)
                parameter("lng", lng)
                parameter("date", date)
            }

            Log.d("PoliceApiService", "API Response status: ${response.status}")

            if (response.status.isSuccess()) {
                // Log raw response body for debugging
                val rawResponse = response.bodyAsText()
                Log.d("PoliceApiService", "Raw API Response: $rawResponse")

                // Parse the response as a list of CrimeResponseItem
                val crimeResponse: List<CrimeResponseItem> = response.body() // Correct deserialization
                Log.d("PoliceApiService", "Crimes fetched successfully: ${crimeResponse.size} crimes")

                crimeResponse // Return the list of crimes
            } else {
                Log.e("PoliceApiService", "Failed to fetch crimes: ${response.status}, Body: ${response.bodyAsText()}")
                emptyList() // Return an empty list if the response is not successful
            }

        } catch (e: Exception) {
            // Log the exception for debugging
            Log.e("PoliceApiService", "Error fetching crimes", e)
            emptyList() // Return an empty list if an error occurs
        }
    }
}
