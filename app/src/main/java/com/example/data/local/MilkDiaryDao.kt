package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdvanceOrGoodsEntry
import com.example.data.model.AuditLogEntry
import com.example.data.model.DailyMilkEntry
import com.example.data.model.MilkAccount
import com.example.data.model.MonthlyRate
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkDiaryDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // Milk Accounts
    @Query("SELECT * FROM milk_accounts ORDER BY isDefault DESC, name ASC")
    fun getAllAccounts(): Flow<List<MilkAccount>>

    @Query("SELECT * FROM milk_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Long): MilkAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: MilkAccount): Long

    @Update
    suspend fun updateAccount(account: MilkAccount)

    @Delete
    suspend fun deleteAccount(account: MilkAccount)

    // Monthly Rates
    @Query("SELECT * FROM monthly_rates WHERE accountId = :accountId AND yearMonth = :yearMonth LIMIT 1")
    fun getMonthlyRate(accountId: Long, yearMonth: String): Flow<MonthlyRate?>

    @Query("SELECT * FROM monthly_rates WHERE accountId = :accountId AND yearMonth = :yearMonth LIMIT 1")
    suspend fun getMonthlyRateSync(accountId: Long, yearMonth: String): MonthlyRate?

    @Query("SELECT * FROM monthly_rates WHERE accountId = :accountId ORDER BY yearMonth DESC")
    fun getAllMonthlyRates(accountId: Long): Flow<List<MonthlyRate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMonthlyRate(rate: MonthlyRate)

    // Daily Milk Entries
    @Query("SELECT * FROM daily_milk_entries WHERE accountId = :accountId AND date LIKE :monthPrefix || '%' ORDER BY date ASC")
    fun getDailyEntriesForMonth(accountId: Long, monthPrefix: String): Flow<List<DailyMilkEntry>>

    @Query("SELECT * FROM daily_milk_entries WHERE accountId = :accountId AND date = :date LIMIT 1")
    fun getDailyEntryForDate(accountId: Long, date: String): Flow<DailyMilkEntry?>

    @Query("SELECT * FROM daily_milk_entries WHERE accountId = :accountId AND date = :date LIMIT 1")
    suspend fun getDailyEntryForDateSync(accountId: Long, date: String): DailyMilkEntry?

    @Query("SELECT * FROM daily_milk_entries WHERE accountId = :accountId ORDER BY date DESC")
    fun getAllDailyEntries(accountId: Long): Flow<List<DailyMilkEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyEntry(entry: DailyMilkEntry)

    @Delete
    suspend fun deleteDailyEntry(entry: DailyMilkEntry)

    // Advance & Goods Entries
    @Query("SELECT * FROM advance_goods_entries WHERE accountId = :accountId AND date LIKE :monthPrefix || '%' ORDER BY date DESC")
    fun getAdvanceGoodsForMonth(accountId: Long, monthPrefix: String): Flow<List<AdvanceOrGoodsEntry>>

    @Query("SELECT * FROM advance_goods_entries WHERE accountId = :accountId ORDER BY date DESC")
    fun getAllAdvanceGoods(accountId: Long): Flow<List<AdvanceOrGoodsEntry>>

    @Query("SELECT * FROM advance_goods_entries WHERE id = :id LIMIT 1")
    suspend fun getAdvanceGoodsByIdSync(id: Long): AdvanceOrGoodsEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvanceGoods(entry: AdvanceOrGoodsEntry)

    @Delete
    suspend fun deleteAdvanceGoods(entry: AdvanceOrGoodsEntry)

    // Audit Log & Editing History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(entry: AuditLogEntry)

    @Query("SELECT * FROM audit_log_entries ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntry>>

    @Query("SELECT * FROM audit_log_entries WHERE accountId = :accountId ORDER BY timestamp DESC")
    fun getAuditLogsForAccount(accountId: Long): Flow<List<AuditLogEntry>>

    @Query("SELECT * FROM audit_log_entries WHERE targetType = :targetType AND dateOrPeriod = :dateOrPeriod ORDER BY timestamp DESC")
    fun getAuditLogsForDate(targetType: String, dateOrPeriod: String): Flow<List<AuditLogEntry>>

    @Query("SELECT * FROM audit_log_entries WHERE targetType = :targetType AND dateOrPeriod = :dateOrPeriod ORDER BY timestamp DESC")
    suspend fun getAuditLogsForDateSync(targetType: String, dateOrPeriod: String): List<AuditLogEntry>

    // Backup & Restore
    @Query("SELECT * FROM milk_accounts")
    suspend fun getAllAccountsList(): List<MilkAccount>

    @Query("SELECT * FROM daily_milk_entries")
    suspend fun getAllDailyEntriesList(): List<DailyMilkEntry>

    @Query("SELECT * FROM monthly_rates")
    suspend fun getAllMonthlyRatesList(): List<MonthlyRate>

    @Query("SELECT * FROM advance_goods_entries")
    suspend fun getAllAdvanceGoodsList(): List<AdvanceOrGoodsEntry>

    @Query("SELECT * FROM audit_log_entries")
    suspend fun getAllAuditLogsList(): List<AuditLogEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountsBatch(accounts: List<MilkAccount>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyEntriesBatch(entries: List<DailyMilkEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyRatesBatch(rates: List<MonthlyRate>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvanceGoodsBatch(entries: List<AdvanceOrGoodsEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogsBatch(entries: List<AuditLogEntry>)

    @Query("DELETE FROM daily_milk_entries WHERE accountId = :accountId")
    suspend fun clearEntriesForAccount(accountId: Long)

    // Clear all data for blank app reset
    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    @Query("DELETE FROM milk_accounts")
    suspend fun clearAllAccounts()

    @Query("DELETE FROM monthly_rates")
    suspend fun clearAllMonthlyRates()

    @Query("DELETE FROM daily_milk_entries")
    suspend fun clearAllDailyEntries()

    @Query("DELETE FROM advance_goods_entries")
    suspend fun clearAllAdvanceGoods()

    @Query("DELETE FROM audit_log_entries")
    suspend fun clearAllAuditLogs()
}
