package com.karolisstuff.ukcrimemapping.data.model

data class CrimeResponseItem(
	val locationSubtype: String,
	val outcomeStatus: OutcomeStatus,
	val persistentId: String,
	val month: String,
	val context: String,
	val location: Location,
	val id: Int,
	val category: String,
	val locationType: String
)
