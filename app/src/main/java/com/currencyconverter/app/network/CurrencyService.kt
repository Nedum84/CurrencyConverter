package com.currencyconverter.app.network

import com.currencyconverter.app.model.Currency
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*


interface CurrencyService {

//    @GET("symbols")
//    fun getSymbolsAsync(@Query("access_key") access_key: String,): Deferred<List<Currency>>
    @GET("symbols")
    fun getSymbolsAsync(@Query("access_key") access_key: String,): Call<ServerResponse>
}




