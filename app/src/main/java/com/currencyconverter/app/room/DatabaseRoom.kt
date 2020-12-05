package com.currencyconverter.app.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.currencyconverter.app.model.Currency
import com.currencyconverter.app.room.TableNames.Companion.DATABASE_NAME


@Database(entities = [Currency::class], version = 1, exportSchema = false)
abstract class DatabaseRoom : RoomDatabase() {

    /**
     * Connects the database to the DAO.
     */
    abstract val currencyDao: CurrencyDao

    companion object {

        @Volatile
        private var INSTANCE: DatabaseRoom? = null

        fun getDatabaseInstance(context: Context): DatabaseRoom {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {

                var instance = INSTANCE
                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseRoom::class.java,
                        "$DATABASE_NAME"
                    )

                        .fallbackToDestructiveMigration()
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }
                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}
