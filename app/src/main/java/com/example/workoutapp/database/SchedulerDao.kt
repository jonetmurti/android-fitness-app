package com.example.workoutapp.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



@Dao
interface SchedulerDao {

    @Insert
    suspend fun insert(scheduler: Scheduler) : Long
    @Transaction
    @Query("DELETE FROM scheduler")
    suspend fun deleteAllSchedule()

    @Query("SELECT trainingType FROM scheduler WHERE id = :id ")
    fun getTrainingType(id : Int) : String

    @Query("SELECT targetKm FROM scheduler WHERE id = :id")
    fun getTargetCycling(id: Int) : Int

    @Query("SELECT targetStep FROM scheduler WHERE id = :id")
    fun getTargetWalking(id: Int) : Int



}

@Database(entities = [Scheduler::class], version = 2)
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
