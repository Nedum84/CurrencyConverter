package com.currencyconverter.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.currencyconverter.app.repository.RepoCurrency
import com.currencyconverter.app.room.DatabaseRoom
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = DatabaseRoom.getDatabaseInstance(applicationContext)

        val getSymbols = RepoCurrency(database)

        return try {
            getSymbols.getCurrency()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }



    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

}
