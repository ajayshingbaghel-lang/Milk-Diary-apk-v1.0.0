package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MilkDiaryDatabase
import com.example.data.model.AdvanceOrGoodsEntry
import com.example.data.model.AuditLogEntry
import com.example.data.model.DailyMilkEntry
import com.example.data.model.MilkAccount
import com.example.data.model.MonthlyCalculationSummary
import com.example.data.model.UserProfile
import com.example.data.repository.MilkDiaryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppScreen {
    HOME,
    DAILY_ENTRIES,
    ADVANCE_GOODS,
    MONTHLY_BILL,
    ACCOUNTS,
    PROFILE,
    BACKUP,
    EDIT_HISTORY, // Folder for All Editing History & Transparency Log
    AUTH_LOGIN,
    AUTH_OTP
}

@OptIn(ExperimentalCoroutinesApi::class)
class MilkDiaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MilkDiaryRepository

    init {
        val database = MilkDiaryDatabase.getDatabase(application)
        repository = MilkDiaryRepository(database.milkDiaryDao())
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val accounts: StateFlow<List<MilkAccount>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAccount = MutableStateFlow<MilkAccount?>(null)
    val selectedAccount: StateFlow<MilkAccount?> = _selectedAccount.asStateFlow()

    private val _selectedYearMonth = MutableStateFlow(MilkDiaryRepository.getCurrentYearMonth())
    val selectedYearMonth: StateFlow<String> = _selectedYearMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(MilkDiaryRepository.getTodayDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Audit logs for Editing History Folder & Transparency
    val auditLogs: StateFlow<List<AuditLogEntry>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected audit log for detail view modal
    private val _selectedAuditDetail = MutableStateFlow<AuditLogEntry?>(null)
    val selectedAuditDetail: StateFlow<AuditLogEntry?> = _selectedAuditDetail.asStateFlow()

    // Auth state
    private val _mobileInput = MutableStateFlow("")
    val mobileInput: StateFlow<String> = _mobileInput.asStateFlow()

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput.asStateFlow()

    private val _generatedOtp = MutableStateFlow("741852")
    val generatedOtp: StateFlow<String> = _generatedOtp.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _backupStatus = MutableStateFlow<String?>(null)
    val backupStatus: StateFlow<String?> = _backupStatus.asStateFlow()

    init {
        // Automatically select the first account when accounts load
        viewModelScope.launch {
            accounts.collect { list ->
                if (_selectedAccount.value == null && list.isNotEmpty()) {
                    _selectedAccount.value = list.firstOrNull { it.isDefault } ?: list.first()
                } else if (_selectedAccount.value != null && list.none { it.id == _selectedAccount.value?.id }) {
                    _selectedAccount.value = list.firstOrNull()
                }
            }
        }
    }

    // Daily entries for current selected account & month
    val dailyEntries: StateFlow<List<DailyMilkEntry>> = combine(
        selectedAccount,
        selectedYearMonth
    ) { account, ym ->
        account to ym
    }.flatMapLatest { (account, ym) ->
        if (account != null) repository.getDailyEntriesForMonth(account.id, ym)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Advance & Goods for current selected account & month
    val advanceGoodsList: StateFlow<List<AdvanceOrGoodsEntry>> = combine(
        selectedAccount,
        selectedYearMonth
    ) { account, ym ->
        account to ym
    }.flatMapLatest { (account, ym) ->
        if (account != null) repository.getAdvanceGoodsForMonth(account.id, ym)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Calculation Summary
    val monthlySummary: StateFlow<MonthlyCalculationSummary?> = combine(
        selectedAccount,
        selectedYearMonth
    ) { account, ym ->
        account to ym
    }.flatMapLatest { (account, ym) ->
        if (account != null) {
            repository.getMonthlyCalculation(account.id, ym, account.name, account.defaultRate)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectAccount(account: MilkAccount) {
        _selectedAccount.value = account
    }

    fun selectYearMonth(yearMonth: String) {
        _selectedYearMonth.value = yearMonth
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun openAuditDetail(audit: AuditLogEntry) {
        _selectedAuditDetail.value = audit
    }

    fun openAuditDetailForDate(date: String) {
        val found = auditLogs.value.firstOrNull { it.dateOrPeriod == date }
        if (found != null) {
            _selectedAuditDetail.value = found
        } else {
            _selectedAuditDetail.value = AuditLogEntry(
                accountId = _selectedAccount.value?.id ?: 0,
                accountName = _selectedAccount.value?.name ?: "खाता",
                targetType = "MILK_ENTRY",
                dateOrPeriod = date,
                title = "संशोधित प्रविष्टि (Modified Entry)",
                oldValue = "पिछली प्रविष्टि संशोधित की गई है",
                newValue = "ताज़ा प्रविष्टि सक्रिय है",
                differenceText = "संशोधित",
                formattedTime = MilkDiaryRepository.getFormattedCurrentTime(),
                reason = "दूध मात्रा में संशोधन"
            )
        }
    }

    fun closeAuditDetail() {
        _selectedAuditDetail.value = null
    }

    fun previousMonth() {
        val current = _selectedYearMonth.value
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
            val d = sdf.parse(current)
            val cal = Calendar.getInstance()
            if (d != null) cal.time = d
            cal.add(Calendar.MONTH, -1)
            _selectedYearMonth.value = sdf.format(cal.time)
        } catch (_: Exception) {}
    }

    fun nextMonth() {
        val current = _selectedYearMonth.value
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
            val d = sdf.parse(current)
            val cal = Calendar.getInstance()
            if (d != null) cal.time = d
            cal.add(Calendar.MONTH, 1)
            _selectedYearMonth.value = sdf.format(cal.time)
        } catch (_: Exception) {}
    }

    // Milk Entry operations with Audit Logging for Transparency
    fun addOrUpdateMilkEntry(
        date: String,
        morning: Double,
        evening: Double,
        total: Double,
        notes: String = "",
        editReason: String = ""
    ) {
        val account = _selectedAccount.value ?: return
        viewModelScope.launch {
            val currentRate = monthlySummary.value?.ratePerLiter ?: account.defaultRate
            repository.saveDailyEntryWithAudit(
                DailyMilkEntry(
                    accountId = account.id,
                    date = date,
                    morningQuantity = morning,
                    eveningQuantity = evening,
                    totalQuantity = total,
                    ratePerLiter = currentRate,
                    notes = notes
                ),
                accountName = account.name,
                editReason = editReason
            )
        }
    }

    fun deleteMilkEntry(entry: DailyMilkEntry) {
        val account = _selectedAccount.value ?: return
        viewModelScope.launch {
            repository.deleteDailyEntryWithAudit(entry, account.name)
        }
    }

    // Rate operations with Audit Logging
    fun updateMonthlyRate(rate: Double) {
        val account = _selectedAccount.value ?: return
        val ym = _selectedYearMonth.value
        viewModelScope.launch {
            repository.saveMonthlyRateWithAudit(account.id, account.name, ym, rate)
        }
    }

    // Advance and Goods operations with Audit Logging
    fun addAdvanceOrGoods(
        id: Long = 0L,
        date: String,
        type: String, // "ADVANCE" or "GOODS"
        title: String,
        amount: Double,
        notes: String = ""
    ) {
        val account = _selectedAccount.value ?: return
        viewModelScope.launch {
            repository.saveAdvanceOrGoodsWithAudit(
                AdvanceOrGoodsEntry(
                    id = id,
                    accountId = account.id,
                    date = date,
                    type = type,
                    title = title,
                    amount = amount,
                    notes = notes
                ),
                accountName = account.name
            )
        }
    }

    fun deleteAdvanceOrGoods(entry: AdvanceOrGoodsEntry) {
        val account = _selectedAccount.value ?: return
        viewModelScope.launch {
            repository.deleteAdvanceOrGoodsWithAudit(entry, account.name)
        }
    }

    // Accounts management
    fun addNewAccount(name: String, phone: String, address: String, defaultRate: Double, notes: String) {
        viewModelScope.launch {
            val newId = repository.addAccount(name, phone, address, defaultRate, notes)
            val ym = _selectedYearMonth.value
            repository.saveMonthlyRateWithAudit(newId, name, ym, defaultRate)
        }
    }

    fun updateAccount(account: MilkAccount) {
        viewModelScope.launch {
            repository.updateAccount(account)
            if (_selectedAccount.value?.id == account.id) {
                _selectedAccount.value = account
            }
        }
    }

    fun deleteAccount(account: MilkAccount) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    // Profile & Auth
    fun updateMobileInput(mobile: String) {
        _mobileInput.value = mobile.filter { it.isDigit() }.take(10)
        _authError.value = null
    }

    fun updateOtpInput(otp: String) {
        _otpInput.value = otp.filter { it.isDigit() }.take(6)
        _authError.value = null
    }

    fun sendOtp() {
        if (_mobileInput.value.length < 10) {
            _authError.value = "कृपया 10 अंकों का मान्य मोबाइल नंबर दर्ज करें"
            return
        }
        val random6Digit = (100000..999999).random().toString()
        _generatedOtp.value = random6Digit
        _otpInput.value = ""
        _authError.value = null
        _currentScreen.value = AppScreen.AUTH_OTP
    }

    fun autoFillOtp() {
        _otpInput.value = _generatedOtp.value
    }

    fun verifyOtp() {
        if (_otpInput.value != _generatedOtp.value) {
            _authError.value = "गलत OTP कोड! कृपया दोबारा जांचें।"
            return
        }
        viewModelScope.launch {
            val existing = userProfile.value
            val updated = (existing ?: UserProfile()).copy(
                mobileNumber = _mobileInput.value,
                isLoggedIn = true
            )
            repository.saveUserProfile(updated)
            _authError.value = null
            _currentScreen.value = AppScreen.HOME
        }
    }

    fun saveUserProfile(name: String, dairyName: String, phone: String, address: String, photoUri: String?) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.saveUserProfile(
                current.copy(
                    fullName = name,
                    dairyName = dairyName,
                    mobileNumber = phone,
                    address = address,
                    photoUri = photoUri,
                    isLoggedIn = true
                )
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.saveUserProfile(current.copy(isLoggedIn = false))
            _mobileInput.value = ""
            _otpInput.value = ""
            _currentScreen.value = AppScreen.AUTH_LOGIN
        }
    }

    // Backup & Restore
    fun exportBackup(context: Context) {
        viewModelScope.launch {
            try {
                val json = repository.exportBackupJson()
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, json)
                    putExtra(Intent.EXTRA_SUBJECT, "Milk Diary Backup - Ajay Pal")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Backup Save / Share")
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(shareIntent)
                _backupStatus.value = "बैकअप सफलतापूर्वक तैयार हो गया है!"
            } catch (e: Exception) {
                _backupStatus.value = "बैकअप विफल: ${e.localizedMessage}"
            }
        }
    }

    fun restoreBackup(jsonText: String) {
        viewModelScope.launch {
            val success = repository.restoreBackupJson(jsonText)
            _backupStatus.value = if (success) {
                "बैकअप डेटा सफलतापूर्वक रीस्टोर हो गया!"
            } else {
                "बैकअप फ़ाइल अमान्य है या रीस्टोर नहीं हो सका।"
            }
        }
    }

    fun clearBackupStatus() {
        _backupStatus.value = null
    }

    fun resetAllDataToBlank() {
        viewModelScope.launch {
            repository.resetAllDataToBlank()
            _selectedAccount.value = null
            _backupStatus.value = "ऐप डेटा पूरी तरह खाली (Blank) कर दिया गया है!"
        }
    }

    // WhatsApp Bill Sharing
    fun shareBillOnWhatsApp(context: Context) {
        val summary = monthlySummary.value ?: return
        val text = buildWhatsAppBillMessage(summary)

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_SUBJECT, "दूध का बिल - Milk Diary")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Share Bill / बिल भेजें"))
        }
    }

    fun copyBillText(context: Context): String {
        val summary = monthlySummary.value ?: return ""
        val text = buildWhatsAppBillMessage(summary)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Milk Bill", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "बिल क्लिपबोर्ड में कॉपी हो गया!", Toast.LENGTH_SHORT).show()
        return text
    }

    private fun buildWhatsAppBillMessage(summary: MonthlyCalculationSummary): String {
        val profile = userProfile.value
        val sb = StringBuilder()
        sb.append("🥛 *MILK DIARY (दूध का हिसाब)* 🥛\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        if (!profile?.dairyName.isNullOrBlank()) {
            sb.append("🏠 *${profile?.dairyName}*\n")
        }
        sb.append("👤 *दूधवाला / खाता:* ${summary.accountName}\n")
        sb.append("📅 *माह (Month):* ${formatDisplayMonth(summary.yearMonth)}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n\n")

        sb.append("📊 *दूध का हिसाब (Milk Details):*\n")
        sb.append("• कुल दिन (Recorded Days): ${summary.totalDaysRecorded} दिन\n")
        sb.append("• सुबह का दूध (Morning): ${String.format(Locale.US, "%.2f", summary.totalMorningLiters)} L\n")
        sb.append("• शाम का दूध (Evening): ${String.format(Locale.US, "%.2f", summary.totalEveningLiters)} L\n")
        sb.append("• *कुल दूध (Total Liters):* ${String.format(Locale.US, "%.2f", summary.totalLiters)} L\n")
        sb.append("• भाव/दर (Rate): ₹${String.format(Locale.US, "%.2f", summary.ratePerLiter)} / L\n")
        sb.append("💰 *दूध की कुल राशि:* ₹${String.format(Locale.US, "%.2f", summary.milkCost)}\n\n")

        if (summary.goodsList.isNotEmpty()) {
            sb.append("🛍️ *सामान कटौती (Goods Deductions):*\n")
            summary.goodsList.forEach { g ->
                sb.append("• ${g.title}: -₹${String.format(Locale.US, "%.2f", g.amount)}${if (g.isEdited) " (संशोधित)" else ""}\n")
            }
            sb.append("➖ *कुल सामान कटौती:* -₹${String.format(Locale.US, "%.2f", summary.totalGoodsAmount)}\n\n")
        }

        if (summary.advanceList.isNotEmpty()) {
            sb.append("💵 *नकद पेशगी कटौती (Advance Deductions):*\n")
            summary.advanceList.forEach { a ->
                sb.append("• ${a.title} (${a.date}): -₹${String.format(Locale.US, "%.2f", a.amount)}${if (a.isEdited) " (संशोधित)" else ""}\n")
            }
            sb.append("➖ *कुल एडवांस कटौती:* -₹${String.format(Locale.US, "%.2f", summary.totalAdvanceAmount)}\n\n")
        }

        sb.append("📋 *गणना (Calculation):*\n")
        sb.append("दूध कीमत (₹${String.format(Locale.US, "%.2f", summary.milkCost)}) - एडवांस (₹${String.format(Locale.US, "%.2f", summary.totalAdvanceAmount)}) - सामान (₹${String.format(Locale.US, "%.2f", summary.totalGoodsAmount)})\n\n")

        sb.append("════════════════════\n")
        sb.append("✅ *अंतिम देय राशि (Net Balance): ₹${String.format(Locale.US, "%.2f", summary.netTotalPayable)}*\n")
        sb.append("════════════════════\n\n")
        sb.append("🔒 *पूर्ण पारदर्शिता व ऑडिट इतिहास सुरक्षित*\n")
        sb.append("_दूध डायरी ऐप द्वारा तैयार (Milk Diary)_\n")
        sb.append("*Founded by Ajay Pal* 🌟\n")

        return sb.toString()
    }

    companion object {
        fun formatDisplayMonth(yearMonth: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM", Locale.US)
                val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(yearMonth)
                if (date != null) outputFormat.format(date) else yearMonth
            } catch (_: Exception) {
                yearMonth
            }
        }
    }
}
