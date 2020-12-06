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

class ModelCurrency(application: Application, lifecycleOwner: LifecycleOwner) : AndroidViewModel(application) {

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
//        REFRESH LIST ONCE SINCE THE CURRENCY LIST DOESN'T OFTEN CHANGE
        database.currencyDao.getAll().observe(lifecycleOwner, {
            it?.let {list->
                val noOfAddedFlags = list.filter { it.flag.isNotEmpty() }
                if (list.size<10||noOfAddedFlags.size < 10) refreshCurrency()//refresh when both currency list and flags are available
            }
        })

        _queryString.value = Event("")
    }
//REFRESH CURRENCY LIST
    fun refreshCurrency(){
        viewModelScope.launch {
            coursesRepo.getCurrency()
        }
    }

    //currency list feedback from repo
    val currency: LiveData<List<Currency>> = coursesRepo.getCurrencies()
    //error or network status feedback
    val feedBack = coursesRepo.feedBack

    //INPUT QUERY SEARCH VALUE
    val queryString: LiveData<Event<String>> get() = _queryString
    fun setSearchQuery(queryString: String=""){
        _queryString.value = Event(queryString)
    }
    fun currency(qString: String=""):LiveData<List<Currency>>{
        return coursesRepo.getCurrencies(qString)
    }




    //CURRENT CURRENCY SELECTED FROM THE DIALOG FRAGMENT SELECTION
    val curCurrency: LiveData<Event<Currency>> get() = _curCurrency
    private val _curCurrency = MutableLiveData<Event<Currency>>()
    fun setCurSymbol(data: Currency) {
        _curCurrency.value = Event(data)
    }






    //FACTORY TO SHIP IN DEFAULT "APP" VALUE
    class Factory(private val app: Application, val lifecycleOwner: LifecycleOwner) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelCurrency::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelCurrency(app, lifecycleOwner) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
