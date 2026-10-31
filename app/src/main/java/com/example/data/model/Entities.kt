package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val mobileNumber: String = "",
    val dairyName: String = "",
    val photoUri: String? = null,
    val address: String? = null,
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "milk_accounts")
data class MilkAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val defaultRate: Double = 60.0,
    val notes: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "monthly_rates",
    indices = [Index(value = ["accountId", "yearMonth"], unique = true)]
)
data class MonthlyRate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val yearMonth: String, // "YYYY-MM"
    val ratePerLiter: Double,
    val updatedDate: String = "",
    val isEdited: Boolean = false,
    val previousRate: Double = 0.0
)

@Entity(
    tableName = "daily_milk_entries",
    indices = [Index(value = ["accountId", "date"], unique = true)]
)
data class DailyMilkEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val date: String, // "YYYY-MM-DD"
    val morningQuantity: Double = 0.0,
    val eveningQuantity: Double = 0.0,
    val totalQuantity: Double = 0.0,
    val ratePerLiter: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isEdited: Boolean = false,
    val lastEditedAt: Long = 0L,
    val editReason: String = ""
)

@Entity(tableName = "advance_goods_entries")
data class AdvanceOrGoodsEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val date: String, // "YYYY-MM-DD"
    val type: String, // "ADVANCE" or "GOODS"
    val title: String, // e.g. "Cash Advance" or "Ghee 1kg"
    val amount: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isEdited: Boolean = false,
    val previousAmount: Double = 0.0,
    val lastEditedAt: Long = 0L
)

// Audit Log Entity for Security, Transparency, and Edit History
@Entity(tableName = "audit_log_entries")
data class AuditLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val accountName: String,
    val targetType: String, // "MILK_ENTRY", "MONTHLY_RATE", "ADVANCE_GOODS", "DELETE_ENTRY"
    val targetId: Long = 0,
    val dateOrPeriod: String, // e.g. "2026-10-28" or "2026-10"
    val title: String, // e.g. "दूध मात्रा बदली गई", "भाव संशोधित हुआ"
    val oldValue: String, // Description of original value
    val newValue: String, // Description of new edited value
    val differenceText: String, // e.g. "+0.5L" or "₹60.00 ➔ ₹65.00"
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "",
    val reason: String = "मैनुअल संशोधन (Manual Edit)"
)

// Data class for Monthly Bill Summary Calculation
data class MonthlyCalculationSummary(
    val yearMonth: String,
    val accountId: Long,
    val accountName: String,
    val ratePerLiter: Double,
    val totalDaysRecorded: Int,
    val totalMorningLiters: Double,
    val totalEveningLiters: Double,
    val totalLiters: Double,
    val milkCost: Double,
    val totalGoodsAmount: Double,
    val goodsList: List<AdvanceOrGoodsEntry>,
    val totalAdvanceAmount: Double,
    val advanceList: List<AdvanceOrGoodsEntry>,
    val netTotalPayable: Double
)
