package com.karolisstuff.ukcrimemapping.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable

data class Location(

	@field:SerializedName("street")
	val street: Street,

	@field:SerializedName("latitude")
	val latitude: String,

	@field:SerializedName("longitude")
	val longitude: String
)