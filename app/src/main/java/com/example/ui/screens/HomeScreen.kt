package com.example.ui.screens

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DailyMilkEntry
import com.example.data.model.MilkAccount
import com.example.data.model.MonthlyCalculationSummary
import com.example.data.model.UserProfile
import com.example.data.repository.MilkDiaryRepository
import com.example.ui.AppScreen
import com.example.ui.MilkDiaryViewModel
import com.example.ui.components.FounderHeaderBanner
import com.example.ui.components.MilkEntryDialog
import com.example.ui.components.RateUpdateDialog
import com.example.ui.theme.DairyBlueLight
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.EmeraldFresh
import com.example.ui.theme.GoldenButter
import com.example.ui.theme.RoseAdvance
import java.util.Locale

@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    accounts: List<MilkAccount>,
    selectedAccount: MilkAccount?,
    selectedYearMonth: String,
    monthlySummary: MonthlyCalculationSummary?,
    dailyEntries: List<DailyMilkEntry>,
    auditLogsCount: Int = 0,
    onSelectAccount: (MilkAccount) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onUpdateRate: (Double) -> Unit,
    onSaveMilkEntry: (date: String, morning: Double, evening: Double, total: Double, notes: String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onShareWhatsApp: () -> Unit,
    onOpenFounderConsole: () -> Unit = {}
) {
    val context = LocalContext.current
    var showRateDialog by remember { mutableStateOf(false) }
    var showMilkEntryDialog by remember { mutableStateOf(false) }

    val todayStr = remember { MilkDiaryRepository.getTodayDate() }
    val todayEntry = dailyEntries.firstOrNull { it.date == todayStr }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("home_screen_container")
    ) {
        // 1. Mandatory Founder Banner with Logo and "Founded by Ajay Pal"
        FounderHeaderBanner(onClick = onOpenFounderConsole)

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Scenic Farm Illustration Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_dairy_hero),
                    contentDescription = "Fresh Dairy Meadow",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = userProfile?.dairyName?.takeIf { it.isNotBlank() } ?: "Milk Diary",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "दूध का सटीक व आसान डिजिटल हिसाब",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Multi-Account Switcher (Doodhwale Accounts)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = DairyBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "खाता / दूधवाला (Account):",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "सभी खाते (${accounts.size}) ›",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DairyBluePrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { onNavigate(AppScreen.ACCOUNTS) }
                            .testTag("manage_accounts_link")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (accounts.isEmpty()) {
                    OutlinedButton(
                        onClick = { onNavigate(AppScreen.ACCOUNTS) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("add_first_account_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = DairyBluePrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ अपना पहला दूधवाला / खाता जोड़ें", fontWeight = FontWeight.Bold, color = DairyBluePrimary)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        accounts.take(3).forEach { acc ->
                            val isSelected = acc.id == selectedAccount?.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectAccount(acc) },
                                label = {
                                    Text(
                                        acc.name,
                                        maxLines = 1,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("account_chip_${acc.id}")
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Month Selector Bar with Rate Button
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DairyBluePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("current_month_display")
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = GoldenButter,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = MilkDiaryViewModel.formatDisplayMonth(selectedYearMonth),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Rate Pill & Quick Update
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = GoldenButter.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenButter),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (accounts.isEmpty()) onNavigate(AppScreen.ACCOUNTS)
                    else showRateDialog = true
                }
                .testTag("rate_banner_clickable")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = GoldenButter, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "इस माह का भाव: ₹${String.format(Locale.US, "%.2f", monthlySummary?.ratePerLiter ?: 60.0)} / लीटर",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldenButter
                ) {
                    Text(
                        text = "भाव बदलें",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Monthly Auto Calculation Dashboard Summary
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "मासिक कुल हिसाब (Auto Calculation)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${monthlySummary?.totalDaysRecorded ?: 0} दिन",
                        style = MaterialTheme.typography.labelSmall.copy(color = DairyBluePrimary, fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "कुल दूध (Liters)",
                        value = "${String.format(Locale.US, "%.1f", monthlySummary?.totalLiters ?: 0.0)} L",
                        subtitle = "सुबह + शाम",
                        color = DairyBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "दूध का रुपया",
                        value = "₹${String.format(Locale.US, "%.0f", monthlySummary?.milkCost ?: 0.0)}",
                        subtitle = "Liters × Rate",
                        color = DairyBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "सामान कटौती (-)",
                        value = "-₹${String.format(Locale.US, "%.0f", monthlySummary?.totalGoodsAmount ?: 0.0)}",
                        subtitle = "दूध में से कटौती",
                        color = RoseAdvance,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "एडवांस कटौती (-)",
                        value = "-₹${String.format(Locale.US, "%.0f", monthlySummary?.totalAdvanceAmount ?: 0.0)}",
                        subtitle = "पेशगी नकद कटौती",
                        color = RoseAdvance,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Final Net Payable Highlight
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "अंतिम देय राशि (Net Balance)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "₹${String.format(Locale.US, "%.2f", monthlySummary?.netTotalPayable ?: 0.0)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DairyBluePrimary
                                )
                            )
                        }

                        Button(
                            onClick = onShareWhatsApp,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldFresh),
                            modifier = Modifier.testTag("home_whatsapp_share_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("व्हाट्सएप बिल")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Today's Quick Milk Entry Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalDrink, contentDescription = null, tint = DairyBluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "आज का दूध (Today's Entry)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = todayStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (todayEntry != null && todayEntry.totalQuantity > 0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "सुबह: ${todayEntry.morningQuantity}L | शाम: ${todayEntry.eveningQuantity}L",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "कुल दर्ज: ${todayEntry.totalQuantity} लीटर",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DairyBluePrimary
                                    )
                                )
                            }
                            IconButton(onClick = { showMilkEntryDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Today", tint = DairyBluePrimary)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "आज का दूध अभी दर्ज नहीं किया गया है",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (accounts.isEmpty()) onNavigate(AppScreen.ACCOUNTS)
                            else showMilkEntryDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_today_milk_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("आज का दूध दर्ज करें (Add Today's Milk)")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7. Quick Navigation Action Grid
        Text(
            text = "शीघ्र सेवाएं (Quick Actions):",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                icon = Icons.Default.ReceiptLong,
                title = "मासिक बिल\n(Bill)",
                color = DairyBluePrimary,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppScreen.MONTHLY_BILL) }
            )
            ActionTile(
                icon = Icons.Default.ShoppingBag,
                title = "सामान/एडवांस\n(Goods)",
                color = EmeraldFresh,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppScreen.ADVANCE_GOODS) }
            )
            ActionTile(
                icon = Icons.Default.History,
                title = "रोज़ का दूध\n(Calendar)",
                color = GoldenButter,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppScreen.DAILY_ENTRIES) }
            )
            ActionTile(
                icon = Icons.Default.Save,
                title = "बैकअप डेटा\n(Backup)",
                color = Color(0xFF6366F1),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppScreen.BACKUP) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 8. Transparency & Editing History Folder Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = GoldenButter.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldenButter.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(AppScreen.EDIT_HISTORY) }
                .testTag("home_edit_history_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GoldenButter.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GoldenButter,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "संपादन इतिहास फ़ोल्डर",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldenButter
                            ) {
                                Text(
                                    text = "$auditLogsCount बदलाव",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "पारदर्शिता हेतु हर संशोधन (दूध, भाव, नकद) का सुरक्षित रिकॉर्ड",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "खोलें ›",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = DairyBluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Dialogs
    if (showRateDialog) {
        RateUpdateDialog(
            currentRate = monthlySummary?.ratePerLiter ?: selectedAccount?.defaultRate ?: 60.0,
            yearMonth = selectedYearMonth,
            onDismiss = { showRateDialog = false },
            onConfirm = { newRate ->
                onUpdateRate(newRate)
                showRateDialog = false
            }
        )
    }

    if (showMilkEntryDialog) {
        MilkEntryDialog(
            initialDate = todayStr,
            existingEntry = todayEntry,
            onDismiss = { showMilkEntryDialog = false },
            onSave = { date, morning, evening, total, notes ->
                onSaveMilkEntry(date, morning, evening, total, notes)
                showMilkEntryDialog = false
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ),
                maxLines = 2
            )
        }
    }
}
