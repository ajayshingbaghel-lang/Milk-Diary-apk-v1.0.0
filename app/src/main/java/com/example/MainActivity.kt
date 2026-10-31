package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.MilkDiaryViewModel
import com.example.ui.components.AuditDetailModal
import com.example.ui.components.FounderVersionDialog
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.AdvanceGoodsScreen
import com.example.ui.screens.AuthMobileScreen
import com.example.ui.screens.AuthOtpScreen
import com.example.ui.screens.BackupScreen
import com.example.ui.screens.DailyEntriesScreen
import com.example.ui.screens.EditHistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MonthlyBillScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.GoldenButter
import com.example.ui.theme.MilkDiaryTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MilkDiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MilkDiaryTheme {
                MilkDiaryApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilkDiaryApp(viewModel: MilkDiaryViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val selectedAccount by viewModel.selectedAccount.collectAsStateWithLifecycle()
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()
    val monthlySummary by viewModel.monthlySummary.collectAsStateWithLifecycle()
    val dailyEntries by viewModel.dailyEntries.collectAsStateWithLifecycle()
    val advanceGoodsList by viewModel.advanceGoodsList.collectAsStateWithLifecycle()
    val backupStatus by viewModel.backupStatus.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val selectedAuditDetail by viewModel.selectedAuditDetail.collectAsStateWithLifecycle()

    val mobileInput by viewModel.mobileInput.collectAsStateWithLifecycle()
    val otpInput by viewModel.otpInput.collectAsStateWithLifecycle()
    val generatedOtp by viewModel.generatedOtp.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    var showAccountDropdown by remember { mutableStateOf(false) }
    var showFounderDialog by remember { mutableStateOf(false) }

    // Handle system back navigation to return to Home screen
    BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.AUTH_LOGIN) {
        if (currentScreen == AppScreen.AUTH_OTP) {
            viewModel.navigateTo(AppScreen.AUTH_LOGIN)
        } else {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    // Full screen login / OTP flows if not on authenticated screens
    if (currentScreen == AppScreen.AUTH_LOGIN) {
        AuthMobileScreen(
            mobileInput = mobileInput,
            errorMessage = authError,
            onMobileChange = { viewModel.updateMobileInput(it) },
            onSendOtp = { viewModel.sendOtp() },
            onSkipForNow = { viewModel.navigateTo(AppScreen.HOME) }
        )
        return
    }

    if (currentScreen == AppScreen.AUTH_OTP) {
        AuthOtpScreen(
            mobileNumber = mobileInput.ifEmpty { userProfile?.mobileNumber ?: "9876543210" },
            otpInput = otpInput,
            generatedOtp = generatedOtp,
            errorMessage = authError,
            onOtpChange = { viewModel.updateOtpInput(it) },
            onAutoFill = { viewModel.autoFillOtp() },
            onVerifyOtp = { viewModel.verifyOtp() },
            onChangeNumber = { viewModel.navigateTo(AppScreen.AUTH_LOGIN) }
        )
        return
    }

    // Main App Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showFounderDialog = true }
                            .testTag("top_bar_founder_title")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(1.dp, GoldenButter, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_milk_logo),
                                contentDescription = "Milk Diary Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Milk Diary",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = GoldenButter,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = "Founded by Ajay Pal",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentScreen != AppScreen.HOME) {
                        IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    // Account Switcher Pill in Top Bar
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clickable { showAccountDropdown = true }
                                .padding(end = 4.dp)
                                .testTag("top_bar_account_selector")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedAccount?.name?.take(10) ?: "खाता",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showAccountDropdown,
                            onDismissRequest = { showAccountDropdown = false }
                        ) {
                            accounts.forEach { acc ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = acc.name + if (acc.id == selectedAccount?.id) " ✓" else "",
                                            fontWeight = if (acc.id == selectedAccount?.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.selectAccount(acc)
                                        showAccountDropdown = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("+ नया खाता जोड़ें", color = DairyBluePrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    showAccountDropdown = false
                                    viewModel.navigateTo(AppScreen.ACCOUNTS)
                                }
                            )
                        }
                    }

                    // Editing History & Transparency Folder icon
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.EDIT_HISTORY) },
                        modifier = Modifier.testTag("top_bar_edit_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Editing History",
                            tint = if (auditLogs.isNotEmpty()) GoldenButter else Color.White
                        )
                    }

                    // Backup icon
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.BACKUP) }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Backup",
                            tint = Color.White
                        )
                    }

                    // Profile icon
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.PROFILE) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DairyBluePrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("होम") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DairyBluePrimary,
                        selectedTextColor = DairyBluePrimary,
                        indicatorColor = DairyBluePrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.DAILY_ENTRIES,
                    onClick = { viewModel.navigateTo(AppScreen.DAILY_ENTRIES) },
                    icon = { Icon(Icons.Default.LocalDrink, contentDescription = "Daily Milk") },
                    label = { Text("रोज़ का दूध") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DairyBluePrimary,
                        selectedTextColor = DairyBluePrimary,
                        indicatorColor = DairyBluePrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_daily")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADVANCE_GOODS,
                    onClick = { viewModel.navigateTo(AppScreen.ADVANCE_GOODS) },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Goods") },
                    label = { Text("सामान/एडवांस") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DairyBluePrimary,
                        selectedTextColor = DairyBluePrimary,
                        indicatorColor = DairyBluePrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_goods")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.MONTHLY_BILL,
                    onClick = { viewModel.navigateTo(AppScreen.MONTHLY_BILL) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Bill") },
                    label = { Text("मासिक बिल") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DairyBluePrimary,
                        selectedTextColor = DairyBluePrimary,
                        indicatorColor = DairyBluePrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_bill")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.EDIT_HISTORY,
                    onClick = { viewModel.navigateTo(AppScreen.EDIT_HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("इतिहास") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DairyBluePrimary,
                        selectedTextColor = DairyBluePrimary,
                        indicatorColor = DairyBluePrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_history")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    userProfile = userProfile,
                    accounts = accounts,
                    selectedAccount = selectedAccount,
                    selectedYearMonth = selectedYearMonth,
                    monthlySummary = monthlySummary,
                    dailyEntries = dailyEntries,
                    auditLogsCount = auditLogs.size,
                    onSelectAccount = { viewModel.selectAccount(it) },
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onUpdateRate = { viewModel.updateMonthlyRate(it) },
                    onSaveMilkEntry = { date, m, e, t, n -> viewModel.addOrUpdateMilkEntry(date, m, e, t, n) },
                    onNavigate = { viewModel.navigateTo(it) },
                    onShareWhatsApp = { viewModel.shareBillOnWhatsApp(context) },
                    onOpenFounderConsole = { showFounderDialog = true }
                )

                AppScreen.DAILY_ENTRIES -> DailyEntriesScreen(
                    selectedAccount = selectedAccount,
                    selectedYearMonth = selectedYearMonth,
                    dailyEntries = dailyEntries,
                    ratePerLiter = monthlySummary?.ratePerLiter ?: selectedAccount?.defaultRate ?: 60.0,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onSaveEntry = { date, m, e, t, n -> viewModel.addOrUpdateMilkEntry(date, m, e, t, n) },
                    onDeleteEntry = { viewModel.deleteMilkEntry(it) },
                    onViewEditDetails = { viewModel.openAuditDetailForDate(it.date) },
                    onOpenEditHistoryFolder = { viewModel.navigateTo(AppScreen.EDIT_HISTORY) }
                )

                AppScreen.ADVANCE_GOODS -> AdvanceGoodsScreen(
                    selectedAccount = selectedAccount,
                    selectedYearMonth = selectedYearMonth,
                    items = advanceGoodsList,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onSaveItem = { id, date, type, title, amt, notes -> viewModel.addAdvanceOrGoods(id, date, type, title, amt, notes) },
                    onDeleteItem = { viewModel.deleteAdvanceOrGoods(it) },
                    onViewEditDetails = { viewModel.openAuditDetailForDate(it.date) },
                    onOpenEditHistoryFolder = { viewModel.navigateTo(AppScreen.EDIT_HISTORY) }
                )

                AppScreen.MONTHLY_BILL -> MonthlyBillScreen(
                    userProfile = userProfile,
                    selectedAccount = selectedAccount,
                    selectedYearMonth = selectedYearMonth,
                    monthlySummary = monthlySummary,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onUpdateRate = { viewModel.updateMonthlyRate(it) },
                    onShareWhatsApp = { viewModel.shareBillOnWhatsApp(context) },
                    onCopyBill = { viewModel.copyBillText(context) }
                )

                AppScreen.EDIT_HISTORY -> EditHistoryScreen(
                    auditLogs = auditLogs,
                    onBack = { viewModel.navigateTo(AppScreen.HOME) },
                    onViewDetail = { viewModel.openAuditDetail(it) }
                )

                AppScreen.ACCOUNTS -> AccountsScreen(
                    accounts = accounts,
                    selectedAccount = selectedAccount,
                    onSelectAccount = { viewModel.selectAccount(it) },
                    onAddNewAccount = { n, p, a, r, note -> viewModel.addNewAccount(n, p, a, r, note) },
                    onUpdateAccount = { viewModel.updateAccount(it) },
                    onDeleteAccount = { viewModel.deleteAccount(it) }
                )

                AppScreen.PROFILE -> ProfileScreen(
                    userProfile = userProfile,
                    onSaveProfile = { name, dairy, phone, addr, photo ->
                        viewModel.saveUserProfile(name, dairy, phone, addr, photo)
                    },
                    onLogout = { viewModel.logout() },
                    onOpenFounderConsole = { showFounderDialog = true },
                    onResetToBlank = { viewModel.resetAllDataToBlank() }
                )

                AppScreen.BACKUP -> BackupScreen(
                    backupStatus = backupStatus,
                    onExportBackup = { viewModel.exportBackup(it) },
                    onRestoreBackup = { viewModel.restoreBackup(it) },
                    onClearStatus = { viewModel.clearBackupStatus() },
                    onResetToBlank = { viewModel.resetAllDataToBlank() }
                )

                AppScreen.AUTH_LOGIN, AppScreen.AUTH_OTP -> {
                    // Handled above in early returns
                }
            }
        }
    }

    // Modal to view exact Old vs New modification diff (Transparency)
    if (selectedAuditDetail != null) {
        AuditDetailModal(
            audit = selectedAuditDetail!!,
            onDismiss = { viewModel.closeAuditDetail() }
        )
    }

    // Founder Version & Technical Update Hub Console
    if (showFounderDialog) {
        FounderVersionDialog(
            onDismiss = { showFounderDialog = false },
            onResetToBlank = { viewModel.resetAllDataToBlank() }
        )
    }
}
