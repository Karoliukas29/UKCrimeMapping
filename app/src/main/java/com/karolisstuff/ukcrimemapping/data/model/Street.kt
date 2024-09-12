package com.karolisstuff.ukcrimemapping.data.model

import com.google.gson.annotations.SerializedName

data class Street(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int
)