package com.example.workoutapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Dao
interface TrackerDao{
    @Insert
    suspend fun insert(cycling: Cycling)

    @Insert
    suspend fun insert(track: CyclingTrack)

    @Transaction
    @Query("DELETE FROM cycling")
    suspend fun deleteAllCycling()

    @Transaction
    @Query("SELECT * FROM cycling ORDER BY timeStart DESC")
    fun getRecentCycling(): LiveData<CyclingAndTrack>
}

@Database(entities = [Cycling::class, CyclingTrack::class], version = 2)
abstract class TrainingDatabase : RoomDatabase() {
    abstract val trackerDao: TrackerDao

    private class TrainingDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val trackerDao = database.trackerDao

                    trackerDao.deleteAllCycling()

//                    var word = Word("Hello")
//                    wordDao.insert(word)
//                    word = Word("World!")
//                    wordDao.insert(word)
//
//                    word = Word("TODO!")
//                    wordDao.insert(word)
                }
            }
        }
    }

    companion object {
        private lateinit var INSTANCE: TrainingDatabase

        fun getDatabase(context: Context): TrainingDatabase{
            synchronized(TrainingDatabase::class.java){
                if(!::INSTANCE.isInitialized){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        TrainingDatabase::class.java,
                        "training"
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE
            }
        }
    }
}