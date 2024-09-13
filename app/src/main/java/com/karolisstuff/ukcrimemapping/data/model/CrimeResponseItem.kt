package com.karolisstuff.ukcrimemapping.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrimeResponseItem(
	@SerialName("category") val category: String,
	@SerialName("location_type") val locationType: String,
	@SerialName("location") val location: Location,
	@SerialName("context") val context: String,
	@SerialName("outcome_status") val outcomeStatus: OutcomeStatus?,
	@SerialName("persistent_id") val persistentId: String,
	@SerialName("id") val id: Int,
	@SerialName("location_subtype") val locationSubtype: String,
	@SerialName("month") val month: String
)
