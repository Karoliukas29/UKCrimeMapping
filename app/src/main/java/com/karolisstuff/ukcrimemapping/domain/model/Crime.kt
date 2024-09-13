package com.karolisstuff.ukcrimemapping.domain.model

data class Crime(
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val outcome: String,
    val date: String,
    val street: String

)