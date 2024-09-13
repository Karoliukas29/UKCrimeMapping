package com.karolisstuff.ukcrimemapping.data.mapper

import com.karolisstuff.ukcrimemapping.data.model.CrimeResponseItem
import com.karolisstuff.ukcrimemapping.domain.model.Crime

fun CrimeResponseItem.toDomain(): Crime {
    return Crime(
        category = this.category,
        latitude = this.location.latitude.toDouble(),
        longitude = this.location.longitude.toDouble(),
        outcome = this.outcomeStatus?.category ?: "Unknown",
        date = this.month,
        street = this.location.street.name
    )
}
