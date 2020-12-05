package com.currencyconverter.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.currencyconverter.app.model.Currency
import com.currencyconverter.app.utils.Event
import com.currencyconverter.app.repository.RepoCurrency
import com.currencyconverter.app.room.DatabaseRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ModelCurrency(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val coursesRepo = RepoCurrency(database)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    private val _queryString = MutableLiveData<Event<String>>()
    init {
        viewModelScope.launch {
            coursesRepo.getCurrency()
        }

        _queryString.value = Event("")
    }

    val currency: LiveData<List<Currency>> = coursesRepo.courses()
    val feedBack = coursesRepo.feedBack

    val queryString: LiveData<Event<String>> get() = _queryString

    fun setSearchQuery(queryString: String=""){
        _queryString.value = Event(queryString)
    }
    fun currency(qString: String=""):LiveData<List<Currency>>{
        return coursesRepo.courses(qString)
    }


    fun refreshCurrency(){
        viewModelScope.launch {
            coursesRepo.getCurrency()
        }
    }



    //Current Symbol
    val curCurrency: LiveData<Event<Currency>> get() = _curCurrency
    private val _curCurrency = MutableLiveData<Event<Currency>>()
    fun setCurSymbol(data: Currency) {
        _curCurrency.value = Event(data)
    }





    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelCurrency::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelCurrency(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
