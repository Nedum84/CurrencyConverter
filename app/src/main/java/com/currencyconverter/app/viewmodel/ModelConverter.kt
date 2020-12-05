package com.currencyconverter.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.currencyconverter.app.model.Convert
import com.currencyconverter.app.model.Currency
import com.currencyconverter.app.network.ConvertService
import com.currencyconverter.app.network.CurrencyHistoryService
import com.currencyconverter.app.network.RetrofitConstant
import com.currencyconverter.app.utils.Event
import com.currencyconverter.app.network.ServerResponse
import com.currencyconverter.app.room.DatabaseRoom
import com.currencyconverter.app.utils.UrlHolder.ACCESS_KEY
import com.currencyconverter.app.utils.ClassAlertDialog
import com.google.gson.JsonElement
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModelConverter(application: Application) : AndroidViewModel(application) {
    val app = application

    private val database = DatabaseRoom.getDatabaseInstance(app)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val fromAmount: LiveData<String> get() = _fromAmount
    private val _fromAmount = MutableLiveData<String>()
    fun setFromAmount(data: String) {
        _fromAmount.value = data
    }

    val toAmount: LiveData<String> get() = _toAmount
    private val _toAmount = MutableLiveData<String>()
    fun setToAmount(data: String) {
        _toAmount.value = data
    }


    val fromCurrency: LiveData<Currency> get() = _fromCurrency
    private val _fromCurrency = MutableLiveData<Currency>()
    fun setFromCurrency(data: Currency) {
        _fromCurrency.value = data
    }

    val toCurrency: LiveData<Currency> get() = _toCurrency
    private val _toCurrency = MutableLiveData<Currency>()
    fun setToCurrency(data: Currency) {
        _toCurrency.value = data
    }


    init {
        setFromCurrency(Currency("USD", "United States Dollar"))
        setToCurrency(Currency("NGN", "Nigerian Naira"))
    }


    fun convert() {
//        getRateHistory()
        val convert = Convert(fromCurrency.value!!.symbol, toCurrency.value!!.symbol, fromAmount.value!!.toFloat())
        val convertService = RetrofitConstant.retrofit
            .create(ConvertService::class.java)
//            .convertAsync(from = convert.from, to = convert.to, amount = convert.amount, access_key = ACCESS_KEY)//access restricted
            .convertAsync(symbols = "${convert.from}, ${convert.to}", access_key = ACCESS_KEY)

        convertService.enqueue(object : Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                ClassAlertDialog(app).toast("Network error, try again"); t.printStackTrace()
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (!response.isSuccessful) {
                    ClassAlertDialog(app).toast("Network error")
                } else {
                    val resp = response.body()
                    val obj = resp?.asJsonObject!!
                    val success = obj["success"].asBoolean
                    if (success) {
                        val rates = obj["rates"]
                        val from = rates.asJsonObject[convert.from].asFloat
                        val to = rates.asJsonObject[convert.to].asFloat

                        val rate = (to/from)*convert.amount
                        setToAmount(rate.toString())
                    }


                }
            }
        })
    }

    fun getRateHistory() {
        val convertService = RetrofitConstant.retrofit
            .create(CurrencyHistoryService::class.java)
            .getHistoryAsync(start_date = "2020-05-01", end_date = "2020-05-03", access_key = ACCESS_KEY,)

        convertService.enqueue(object : Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                ClassAlertDialog(app).toast("Network error, try again"); t.printStackTrace()
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (!response.isSuccessful) {
                    ClassAlertDialog(app).toast("Network error")
                } else {
                    val resp = response.body()
                    print(resp.toString());

                }
            }
        })
    }


    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelConverter::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelConverter(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
