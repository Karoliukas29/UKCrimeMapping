package com.karolisstuff.ukcrimemapping.utils

import kotlin.math.*

fun distanceBetween(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    // Calculate the distance between two latitude/longitude points using the Haversine formula
    val earthRadius = 6371e3 // Radius of Earth in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

// Adjust coordinates with jitter and prevent overlap
fun addJitterToCoordinates(
    lat: Double,
    lng: Double,
    index: Int,
    existingCoordinates: MutableSet<Pair<Double, Double>>
): Pair<Double, Double> {
    val baseJitter = 0.0005 // Base jitter value
    var jitterDistance = baseJitter * ((index % 5) + 1) // Vary jitter based on index
    val maxAttempts = 10 // Maximum number of attempts to prevent overlap

    var adjustedLat = lat
    var adjustedLng = lng
    var attempt = 0

    // Apply jitter and check if it overlaps with existing coordinates
    while (attempt < maxAttempts) {
        val angle = 2 * PI * (index + attempt) / 5 // Change angle per attempt
        adjustedLat = lat + (jitterDistance * sin(angle))
        adjustedLng = lng + (jitterDistance * cos(angle))

        // Check if the adjusted coordinates are too close to existing coordinates
        var isOverlapping = false
        for ((existingLat, existingLng) in existingCoordinates) {
            val distance = distanceBetween(adjustedLat, adjustedLng, existingLat, existingLng)
            if (distance < 15) { // 15 meters distance threshold to consider overlapping
                isOverlapping = true
                break
            }
        }

        if (!isOverlapping) {
            break // Stop if no overlap
        }

        attempt++
        jitterDistance += baseJitter // Increase jitter distance with each attempt
    }

    // Add the new coordinates to the set
    existingCoordinates.add(Pair(adjustedLat, adjustedLng))

    return Pair(adjustedLat, adjustedLng)
}
