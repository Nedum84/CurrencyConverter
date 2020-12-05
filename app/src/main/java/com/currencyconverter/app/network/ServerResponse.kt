package com.currencyconverter.app.network

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject

class ServerResponse {
    @SerializedName("success")
    val success: Boolean? = null
    @SerializedName("symbols")
    val symbols: JsonObject? = null//or you can use JsonElement(com.google.gson.JsonElement)
}