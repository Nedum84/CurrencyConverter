package com.currencyconverter.app.network

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*


interface CurrencyHistoryService {

    @GET("timeseries")
    fun getHistoryAsync(
        @Query("access_key") access_key: String,
        @Query("start_date") start_date: String,
        @Query("end_date") end_date : String,
    ): Call<JsonElement>
}




