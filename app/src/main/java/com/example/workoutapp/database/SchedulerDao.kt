package com.example.workoutapp.database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


@Dao
interface SchedulerDao {

    @Insert
    suspend fun insert(scheduler: Scheduler)
    @Transaction
    @Query("DELETE FROM scheduler")
    suspend fun deleteAllSchedule()

    @Query("SELECT * FROM scheduler")
    fun getAllSchedule(): Flow<List<Scheduler>>

}

@Database(entities = [Scheduler::class], version = 1)
abstract class SchedulerDatabase : RoomDatabase() {
    abstract val schedulerDao: SchedulerDao

    private class SchedulerDatabaseCallback(
            private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val schedulerDao = database.schedulerDao


                    schedulerDao.deleteAllSchedule()



                }
            }
        }


    }

    companion object {
        private lateinit var INSTANCE: SchedulerDatabase

        fun getDatabase(context: Context): SchedulerDatabase{
            synchronized(SchedulerDatabase::class.java){
                if(!::INSTANCE.isInitialized){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        SchedulerDatabase::class.java,
                        "schedules"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE
            }
        }
    }
}
