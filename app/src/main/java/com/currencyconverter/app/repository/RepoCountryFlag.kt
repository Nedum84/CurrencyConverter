package com.currencyconverter.app.repository

import android.util.Log
import com.currencyconverter.app.model.Currency
import com.currencyconverter.app.room.DatabaseRoom
import com.currencyconverter.app.network.RetrofitConstant
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


class RepoCountryFlag(private val database: DatabaseRoom, val currencyList: MutableList<Currency>) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getFlag()
        }
    }

    suspend fun getFlag(){
        val courseService = RetrofitConstant.retrofit_CountryFlag
            .create(FlagService::class.java)
            .getFlags(fields = "flag;currencies;alpha2Code")


            try {
                courseService.enqueue(object  : Callback<JsonElement> {
                    override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                        if (!response.isSuccessful){
                            print("network_error")
                        }else{
                            val newCurrencyList = mutableListOf<Currency>()

                            try {
                                val respArray = response.body()!!.asJsonArray
                                for(i in 0 until respArray.size()){
                                    val obj = respArray[i].asJsonObject
                                    val flag = obj["flag"].asString
                                    val alpha2Code = obj["alpha2Code"].asString
                                    val currencySymbolObj = obj["currencies"].asJsonArray[0].asJsonObject
                                    if (!currencySymbolObj.keySet().contains("code")) continue;

                                    val currencySymbol= currencySymbolObj["code"].asString.toUpperCase(Locale.ROOT)
                                    val currency = currencyList.filter { it.symbol==currencySymbol }
                                    if (currency.isNotEmpty()){
                                        val row = currency[0]
//                                        row.flag = flag
                                        row.flag = alpha2Code
                                        newCurrencyList.add(row)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            //update the Flag list in the DB
                            updateFlagToDb(newCurrencyList)

                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                print("network_error")
            }
    }

    fun updateFlagToDb(currencyList: MutableList<Currency>){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.currencyDao.upSert(currencyList)
            } catch (e: Exception) {e.printStackTrace()}
        }
    }
}

interface FlagService {

    @GET("all")
    fun getFlags(@Query("fields") fields:String): Call<JsonElement>
}
