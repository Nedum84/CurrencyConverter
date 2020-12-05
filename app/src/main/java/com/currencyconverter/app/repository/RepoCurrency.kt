package com.currencyconverter.app.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.currencyconverter.app.model.Currency
import com.currencyconverter.app.room.DatabaseRoom
import com.currencyconverter.app.network.RetrofitConstant
import com.currencyconverter.app.network.CurrencyService
import com.currencyconverter.app.network.ServerResponse
import com.currencyconverter.app.utils.ClassAlertDialog
import com.currencyconverter.app.utils.UrlHolder.ACCESS_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoCurrency(private val database: DatabaseRoom) {


//    val symbols : LiveData<List<Symbols>> = database.symbolsDao.getAll()
    fun courses(searchQuery: String?=""):LiveData<List<Currency>>{

        return if (searchQuery.isNullOrEmpty())
            database.currencyDao.getAll()
        else
            database.currencyDao.getAll("%$searchQuery%")
    }

    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun getCurrency(){
        val courseService = RetrofitConstant.retrofit
            .create(CurrencyService::class.java)
            .getSymbolsAsync(access_key = ACCESS_KEY)


            try {
//                val listResult = courseService.await()
//                database.currencyDao.upSert(listResult)

                courseService.enqueue(object  : Callback<ServerResponse> {
                    override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                        _feedBack.postValue("network_error"); t.printStackTrace()}

                    override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                        if (!response.isSuccessful){
                            _feedBack.postValue("network_error")
                        }else{
                            val resp = response.body()

                            if(resp?.success as Boolean){
                                val currency = mutableListOf<Currency>()
                                val symbols = resp.symbols!!
                                for (key in symbols.keySet()) {
                                    val row = Currency(
                                        symbol = key,
                                        country = symbols[key].asString,
                                        flag = ""
                                    )

                                    currency.add(row)
                                }
                                addCurrencyToDb(currency)
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
    }

    fun addCurrencyToDb(currency: MutableList<Currency>){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.currencyDao.upSert(currency)
            } catch (e: Exception) {e.printStackTrace()}
        }
    }
}
