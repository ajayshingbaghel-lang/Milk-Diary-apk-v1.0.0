package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AdvanceOrGoodsEntry
import com.example.data.model.AuditLogEntry
import com.example.data.model.DailyMilkEntry
import com.example.data.model.MilkAccount
import com.example.data.model.MonthlyRate
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        MilkAccount::class,
        MonthlyRate::class,
        DailyMilkEntry::class,
        AdvanceOrGoodsEntry::class,
        AuditLogEntry::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MilkDiaryDatabase : RoomDatabase() {

    abstract fun milkDiaryDao(): MilkDiaryDao

    companion object {
        @Volatile
        private var INSTANCE: MilkDiaryDatabase? = null

        fun getDatabase(context: Context): MilkDiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MilkDiaryDatabase::class.java,
                    "milk_diary_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
