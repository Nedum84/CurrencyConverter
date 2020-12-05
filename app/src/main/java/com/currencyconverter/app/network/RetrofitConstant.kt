package com.currencyconverter.app.network

import com.currencyconverter.app.utils.UrlHolder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConstant {


    companion object{

        private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        private val client: OkHttpClient =  OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(UrlHolder.URL_BASE)
            .client(client)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofit_CountryFlag: Retrofit = Retrofit.Builder()
            .baseUrl(UrlHolder.URL_BASE_COUNTRY_FLAG)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
