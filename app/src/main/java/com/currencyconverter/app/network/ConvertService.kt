package com.currencyconverter.app.network

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*


interface ConvertService {
    //CONVERT IS NOT WORKING FOR FREE FIXER ACCOUNT. SO, I HAVE TO USE LATEST API ENDPOINT & CONVERT IT MYSELF

//    @GET("convert")
//    fun convertAsync(
//        @Query("access_key") access_key: String,
//        @Query("from") from: String,
//        @Query("to") to: String,
//        @Query("amount") amount: String
//    ): Call<JsonElement>


    @GET("latest")
    fun convertAsync(
        @Query("access_key") access_key: String,
        @Query("symbols") symbols: String,
    ): Call<JsonElement>
}




