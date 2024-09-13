package com.karolisstuff.ukcrimemapping.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OutcomeStatus(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("category")
	val category: String
)