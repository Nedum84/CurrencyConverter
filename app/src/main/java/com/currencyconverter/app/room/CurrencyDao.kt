package com.currencyconverter.app.room


import androidx.lifecycle.LiveData
import androidx.room.*
import com.currencyconverter.app.model.Currency
import com.currencyconverter.app.room.TableNames.Companion.TABLE_CURRENCY

/**
 * Defines methods for using the SleepNight class with Room.
 */
@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<Currency>)//upsert -> Insert and/or update data to database


    @Query("SELECT * from ${TABLE_CURRENCY} WHERE symbol = :symbol")
    fun getById(symbol: String): Currency?


    @Query("SELECT * FROM $TABLE_CURRENCY ORDER BY country DESC")
    fun getAll(): LiveData<List<Currency>>

    @Query("SELECT * FROM $TABLE_CURRENCY WHERE country LIKE :searchString OR  symbol LIKE :searchString  ORDER BY country DESC")
    fun getAll(searchString: String): LiveData<List<Currency>>

    @Query("DELETE FROM $TABLE_CURRENCY WHERE symbol !=:id ")
    fun deleteNotMySchool(id: Int)
}

