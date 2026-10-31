package com.example.data.repository

import com.example.data.local.MilkDiaryDao
import com.example.data.model.AdvanceOrGoodsEntry
import com.example.data.model.AuditLogEntry
import com.example.data.model.DailyMilkEntry
import com.example.data.model.MilkAccount
import com.example.data.model.MonthlyCalculationSummary
import com.example.data.model.MonthlyRate
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MilkDiaryRepository(private val dao: MilkDiaryDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allAccounts: Flow<List<MilkAccount>> = dao.getAllAccounts()

    suspend fun ensureInitialData() {
        // Clear any old sample/dummy data ("Ramesh Gwalla", "Shree Krishna Dairy", etc.)
        val accounts = dao.getAllAccountsList()
        val hasSampleAccount = accounts.any { it.name.contains("Ramesh Gwalla") }
        val currentProfile = dao.getUserProfile().firstOrNull()
        val hasSampleProfile = currentProfile?.dairyName?.contains("Shree Krishna Dairy") == true ||
                currentProfile?.mobileNumber == "9876543210"

        if (hasSampleAccount || hasSampleProfile) {
            resetAllDataToBlank()
            return
        }

        // If no user profile exists, create a fresh blank profile so the user starts clean
        if (currentProfile == null) {
            dao.saveUserProfile(
                UserProfile(
                    id = 1,
                    fullName = "",
                    mobileNumber = "",
                    dairyName = "",
                    address = "",
                    isLoggedIn = false
                )
            )
        }
    }

    suspend fun resetAllDataToBlank() {
        dao.clearAllDailyEntries()
        dao.clearAllAdvanceGoods()
        dao.clearAllMonthlyRates()
        dao.clearAllAuditLogs()
        dao.clearAllAccounts()
        dao.saveUserProfile(
            UserProfile(
                id = 1,
                fullName = "",
                mobileNumber = "",
                dairyName = "",
                address = "",
                isLoggedIn = false
            )
        )
    }

    fun getDailyEntriesForMonth(accountId: Long, yearMonth: String): Flow<List<DailyMilkEntry>> {
        return dao.getDailyEntriesForMonth(accountId, yearMonth)
    }

    fun getMonthlyRate(accountId: Long, yearMonth: String): Flow<MonthlyRate?> {
        return dao.getMonthlyRate(accountId, yearMonth)
    }

    fun getAdvanceGoodsForMonth(accountId: Long, yearMonth: String): Flow<List<AdvanceOrGoodsEntry>> {
        return dao.getAdvanceGoodsForMonth(accountId, yearMonth)
    }

    fun getAllAuditLogs(): Flow<List<AuditLogEntry>> {
        return dao.getAllAuditLogs()
    }

    fun getAuditLogsForAccount(accountId: Long): Flow<List<AuditLogEntry>> {
        return dao.getAuditLogsForAccount(accountId)
    }

    fun getAuditLogsForDate(targetType: String, date: String): Flow<List<AuditLogEntry>> {
        return dao.getAuditLogsForDate(targetType, date)
    }

    fun getMonthlyCalculation(accountId: Long, yearMonth: String, accountName: String, defaultRate: Double): Flow<MonthlyCalculationSummary> {
        val rateFlow = dao.getMonthlyRate(accountId, yearMonth)
        val entriesFlow = dao.getDailyEntriesForMonth(accountId, yearMonth)
        val advanceGoodsFlow = dao.getAdvanceGoodsForMonth(accountId, yearMonth)

        return combine(rateFlow, entriesFlow, advanceGoodsFlow) { rateObj, entries, advGoods ->
            val effectiveRate = rateObj?.ratePerLiter ?: defaultRate
            val daysRecorded = entries.size
            val morningTotal = entries.sumOf { it.morningQuantity }
            val eveningTotal = entries.sumOf { it.eveningQuantity }
            val totalLiters = entries.sumOf { it.totalQuantity }
            val milkCost = totalLiters * effectiveRate

            val goods = advGoods.filter { it.type == "GOODS" }
            val advances = advGoods.filter { it.type == "ADVANCE" }

            val totalGoods = goods.sumOf { it.amount }
            val totalAdvance = advances.sumOf { it.amount }
            // Formula requested by user: doodh ki kimat - advance - saman ke paise = total
            val netPayable = milkCost - totalAdvance - totalGoods

            MonthlyCalculationSummary(
                yearMonth = yearMonth,
                accountId = accountId,
                accountName = accountName,
                ratePerLiter = effectiveRate,
                totalDaysRecorded = daysRecorded,
                totalMorningLiters = morningTotal,
                totalEveningLiters = eveningTotal,
                totalLiters = totalLiters,
                milkCost = milkCost,
                totalGoodsAmount = totalGoods,
                goodsList = goods,
                totalAdvanceAmount = totalAdvance,
                advanceList = advances,
                netTotalPayable = netPayable
            )
        }
    }

    // Save Daily Milk Entry with Audit Logging for Transparency
    suspend fun saveDailyEntryWithAudit(
        entry: DailyMilkEntry,
        accountName: String,
        editReason: String = ""
    ) {
        val existing = dao.getDailyEntryForDateSync(entry.accountId, entry.date)
        if (existing != null) {
            // Check if quantities changed
            val isChanged = existing.morningQuantity != entry.morningQuantity ||
                    existing.eveningQuantity != entry.eveningQuantity ||
                    existing.totalQuantity != entry.totalQuantity

            if (isChanged) {
                val diffLiters = entry.totalQuantity - existing.totalQuantity
                val diffSign = if (diffLiters >= 0) "+" else ""
                val diffStr = "$diffSign${String.format(Locale.US, "%.2f", diffLiters)}L"

                val oldDesc = "सुबह: ${existing.morningQuantity}L + शाम: ${existing.eveningQuantity}L = ${existing.totalQuantity}L"
                val newDesc = "सुबह: ${entry.morningQuantity}L + शाम: ${entry.eveningQuantity}L = ${entry.totalQuantity}L"

                val audit = AuditLogEntry(
                    accountId = entry.accountId,
                    accountName = accountName,
                    targetType = "MILK_ENTRY",
                    targetId = existing.id,
                    dateOrPeriod = entry.date,
                    title = "दूध मात्रा में संशोधन (Milk Quantity Edit)",
                    oldValue = oldDesc,
                    newValue = newDesc,
                    differenceText = diffStr,
                    formattedTime = getFormattedCurrentTime(),
                    reason = editReason.ifBlank { "दूध मात्रा में संशोधन" }
                )
                dao.insertAuditLog(audit)

                val updatedEntry = entry.copy(
                    id = existing.id,
                    isEdited = true,
                    lastEditedAt = System.currentTimeMillis(),
                    editReason = editReason
                )
                dao.insertDailyEntry(updatedEntry)
                return
            }
        }
        dao.insertDailyEntry(entry)
    }

    suspend fun deleteDailyEntryWithAudit(entry: DailyMilkEntry, accountName: String) {
        val audit = AuditLogEntry(
            accountId = entry.accountId,
            accountName = accountName,
            targetType = "DELETE_ENTRY",
            targetId = entry.id,
            dateOrPeriod = entry.date,
            title = "दूध प्रविष्टि हटाई गई (Entry Deleted)",
            oldValue = "सुबह: ${entry.morningQuantity}L + शाम: ${entry.eveningQuantity}L = ${entry.totalQuantity}L",
            newValue = "0.0L (हटाया गया)",
            differenceText = "-${entry.totalQuantity}L",
            formattedTime = getFormattedCurrentTime(),
            reason = "प्रविष्टि हटाई गई"
        )
        dao.insertAuditLog(audit)
        dao.deleteDailyEntry(entry)
    }

    // Save Monthly Rate with Audit Logging
    suspend fun saveMonthlyRateWithAudit(
        accountId: Long,
        accountName: String,
        yearMonth: String,
        newRate: Double
    ) {
        val existing = dao.getMonthlyRateSync(accountId, yearMonth)
        if (existing != null && existing.ratePerLiter != newRate) {
            val diffRate = newRate - existing.ratePerLiter
            val diffSign = if (diffRate >= 0) "+" else ""
            val diffStr = "$diffSign₹${String.format(Locale.US, "%.2f", diffRate)}/L"

            val audit = AuditLogEntry(
                accountId = accountId,
                accountName = accountName,
                targetType = "MONTHLY_RATE",
                targetId = existing.id,
                dateOrPeriod = yearMonth,
                title = "दूध का भाव बदला गया (Rate Updated)",
                oldValue = "पुराना भाव: ₹${String.format(Locale.US, "%.2f", existing.ratePerLiter)} / लीटर",
                newValue = "नया भाव: ₹${String.format(Locale.US, "%.2f", newRate)} / लीटर",
                differenceText = diffStr,
                formattedTime = getFormattedCurrentTime(),
                reason = "मासिक भाव संशोधन"
            )
            dao.insertAuditLog(audit)

            dao.setMonthlyRate(
                MonthlyRate(
                    id = existing.id,
                    accountId = accountId,
                    yearMonth = yearMonth,
                    ratePerLiter = newRate,
                    updatedDate = getTodayDate(),
                    isEdited = true,
                    previousRate = existing.ratePerLiter
                )
            )
        } else {
            dao.setMonthlyRate(
                MonthlyRate(
                    accountId = accountId,
                    yearMonth = yearMonth,
                    ratePerLiter = newRate,
                    updatedDate = getTodayDate()
                )
            )
        }
    }

    // Save Advance or Goods with Audit Logging
    suspend fun saveAdvanceOrGoodsWithAudit(
        entry: AdvanceOrGoodsEntry,
        accountName: String
    ) {
        if (entry.id != 0L) {
            val existing = dao.getAdvanceGoodsByIdSync(entry.id)
            if (existing != null && (existing.amount != entry.amount || existing.title != entry.title)) {
                val diff = entry.amount - existing.amount
                val diffSign = if (diff >= 0) "+" else ""
                val diffStr = "$diffSign₹${String.format(Locale.US, "%.2f", diff)}"

                val audit = AuditLogEntry(
                    accountId = entry.accountId,
                    accountName = accountName,
                    targetType = "ADVANCE_GOODS",
                    targetId = entry.id,
                    dateOrPeriod = entry.date,
                    title = if (entry.type == "ADVANCE") "नकद एडवांस संशोधित (Advance Edit)" else "सामान/वस्तु संशोधित (Goods Edit)",
                    oldValue = "${existing.title}: ₹${String.format(Locale.US, "%.2f", existing.amount)}",
                    newValue = "${entry.title}: ₹${String.format(Locale.US, "%.2f", entry.amount)}",
                    differenceText = diffStr,
                    formattedTime = getFormattedCurrentTime(),
                    reason = "मूल्य/विवरण में बदलाव"
                )
                dao.insertAuditLog(audit)

                dao.insertAdvanceGoods(
                    entry.copy(
                        isEdited = true,
                        previousAmount = existing.amount,
                        lastEditedAt = System.currentTimeMillis()
                    )
                )
                return
            }
        }
        dao.insertAdvanceGoods(entry)
    }

    suspend fun deleteAdvanceOrGoodsWithAudit(entry: AdvanceOrGoodsEntry, accountName: String) {
        val audit = AuditLogEntry(
            accountId = entry.accountId,
            accountName = accountName,
            targetType = "ADVANCE_GOODS",
            targetId = entry.id,
            dateOrPeriod = entry.date,
            title = if (entry.type == "ADVANCE") "एडवांस प्रविष्टि हटाई गई" else "सामान प्रविष्टि हटाई गई",
            oldValue = "${entry.title}: ₹${String.format(Locale.US, "%.2f", entry.amount)}",
            newValue = "₹0.00 (हटाया गया)",
            differenceText = "-₹${String.format(Locale.US, "%.2f", entry.amount)}",
            formattedTime = getFormattedCurrentTime(),
            reason = "प्रविष्टि हटाई गई"
        )
        dao.insertAuditLog(audit)
        dao.deleteAdvanceGoods(entry)
    }

    suspend fun addAccount(name: String, phone: String, address: String, defaultRate: Double, notes: String): Long {
        return dao.insertAccount(
            MilkAccount(
                name = name,
                phone = phone,
                address = address,
                defaultRate = defaultRate,
                notes = notes,
                isDefault = false
            )
        )
    }

    suspend fun updateAccount(account: MilkAccount) {
        dao.updateAccount(account)
    }

    suspend fun deleteAccount(account: MilkAccount) {
        dao.deleteAccount(account)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        dao.saveUserProfile(profile)
    }

    // Backup to JSON string including audit logs
    suspend fun exportBackupJson(): String {
        val root = JSONObject()
        root.put("version", 2)
        root.put("appName", "Milk Diary")
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
        root.put("foundedBy", "Ajay Pal")

        val accountsArray = JSONArray()
        dao.getAllAccountsList().forEach { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("name", a.name)
            obj.put("phone", a.phone)
            obj.put("address", a.address)
            obj.put("defaultRate", a.defaultRate)
            obj.put("notes", a.notes)
            obj.put("isDefault", a.isDefault)
            accountsArray.put(obj)
        }
        root.put("accounts", accountsArray)

        val entriesArray = JSONArray()
        dao.getAllDailyEntriesList().forEach { e ->
            val obj = JSONObject()
            obj.put("accountId", e.accountId)
            obj.put("date", e.date)
            obj.put("morningQuantity", e.morningQuantity)
            obj.put("eveningQuantity", e.eveningQuantity)
            obj.put("totalQuantity", e.totalQuantity)
            obj.put("ratePerLiter", e.ratePerLiter)
            obj.put("notes", e.notes)
            obj.put("isEdited", e.isEdited)
            entriesArray.put(obj)
        }
        root.put("dailyEntries", entriesArray)

        val ratesArray = JSONArray()
        dao.getAllMonthlyRatesList().forEach { r ->
            val obj = JSONObject()
            obj.put("accountId", r.accountId)
            obj.put("yearMonth", r.yearMonth)
            obj.put("ratePerLiter", r.ratePerLiter)
            obj.put("isEdited", r.isEdited)
            ratesArray.put(obj)
        }
        root.put("monthlyRates", ratesArray)

        val advGoodsArray = JSONArray()
        dao.getAllAdvanceGoodsList().forEach { g ->
            val obj = JSONObject()
            obj.put("accountId", g.accountId)
            obj.put("date", g.date)
            obj.put("type", g.type)
            obj.put("title", g.title)
            obj.put("amount", g.amount)
            obj.put("notes", g.notes)
            obj.put("isEdited", g.isEdited)
            advGoodsArray.put(obj)
        }
        root.put("advanceGoods", advGoodsArray)

        val auditArray = JSONArray()
        dao.getAllAuditLogsList().forEach { au ->
            val obj = JSONObject()
            obj.put("accountId", au.accountId)
            obj.put("accountName", au.accountName)
            obj.put("targetType", au.targetType)
            obj.put("dateOrPeriod", au.dateOrPeriod)
            obj.put("title", au.title)
            obj.put("oldValue", au.oldValue)
            obj.put("newValue", au.newValue)
            obj.put("differenceText", au.differenceText)
            obj.put("formattedTime", au.formattedTime)
            obj.put("reason", au.reason)
            auditArray.put(obj)
        }
        root.put("auditLogs", auditArray)

        return root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            if (root.has("accounts")) {
                val accList = mutableListOf<MilkAccount>()
                val arr = root.getJSONArray("accounts")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    accList.add(
                        MilkAccount(
                            id = o.optLong("id", 0),
                            name = o.optString("name", "Account"),
                            phone = o.optString("phone", ""),
                            address = o.optString("address", ""),
                            defaultRate = o.optDouble("defaultRate", 60.0),
                            notes = o.optString("notes", ""),
                            isDefault = o.optBoolean("isDefault", false)
                        )
                    )
                }
                if (accList.isNotEmpty()) {
                    dao.insertAccountsBatch(accList)
                }
            }

            if (root.has("dailyEntries")) {
                val entryList = mutableListOf<DailyMilkEntry>()
                val arr = root.getJSONArray("dailyEntries")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    entryList.add(
                        DailyMilkEntry(
                            accountId = o.optLong("accountId", 1),
                            date = o.optString("date", getTodayDate()),
                            morningQuantity = o.optDouble("morningQuantity", 0.0),
                            eveningQuantity = o.optDouble("eveningQuantity", 0.0),
                            totalQuantity = o.optDouble("totalQuantity", 0.0),
                            ratePerLiter = o.optDouble("ratePerLiter", 60.0),
                            notes = o.optString("notes", ""),
                            isEdited = o.optBoolean("isEdited", false)
                        )
                    )
                }
                if (entryList.isNotEmpty()) {
                    dao.insertDailyEntriesBatch(entryList)
                }
            }

            if (root.has("monthlyRates")) {
                val rateList = mutableListOf<MonthlyRate>()
                val arr = root.getJSONArray("monthlyRates")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    rateList.add(
                        MonthlyRate(
                            accountId = o.optLong("accountId", 1),
                            yearMonth = o.optString("yearMonth", getCurrentYearMonth()),
                            ratePerLiter = o.optDouble("ratePerLiter", 60.0),
                            updatedDate = getTodayDate(),
                            isEdited = o.optBoolean("isEdited", false)
                        )
                    )
                }
                if (rateList.isNotEmpty()) {
                    dao.insertMonthlyRatesBatch(rateList)
                }
            }

            if (root.has("advanceGoods")) {
                val advList = mutableListOf<AdvanceOrGoodsEntry>()
                val arr = root.getJSONArray("advanceGoods")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    advList.add(
                        AdvanceOrGoodsEntry(
                            accountId = o.optLong("accountId", 1),
                            date = o.optString("date", getTodayDate()),
                            type = o.optString("type", "GOODS"),
                            title = o.optString("title", "Item"),
                            amount = o.optDouble("amount", 0.0),
                            notes = o.optString("notes", ""),
                            isEdited = o.optBoolean("isEdited", false)
                        )
                    )
                }
                if (advList.isNotEmpty()) {
                    dao.insertAdvanceGoodsBatch(advList)
                }
            }

            if (root.has("auditLogs")) {
                val auditList = mutableListOf<AuditLogEntry>()
                val arr = root.getJSONArray("auditLogs")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    auditList.add(
                        AuditLogEntry(
                            accountId = o.optLong("accountId", 1),
                            accountName = o.optString("accountName", "Account"),
                            targetType = o.optString("targetType", "MILK_ENTRY"),
                            dateOrPeriod = o.optString("dateOrPeriod", ""),
                            title = o.optString("title", "Edit"),
                            oldValue = o.optString("oldValue", ""),
                            newValue = o.optString("newValue", ""),
                            differenceText = o.optString("differenceText", ""),
                            formattedTime = o.optString("formattedTime", getFormattedCurrentTime()),
                            reason = o.optString("reason", "Manual Edit")
                        )
                    )
                }
                if (auditList.isNotEmpty()) {
                    dao.insertAuditLogsBatch(auditList)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        fun getCurrentYearMonth(): String {
            return SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        }

        fun getTodayDate(): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }

        fun getFormattedCurrentTime(): String {
            return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        }
    }
}
