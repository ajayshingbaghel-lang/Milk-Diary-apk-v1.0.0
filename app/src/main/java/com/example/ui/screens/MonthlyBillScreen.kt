package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MilkAccount
import com.example.data.model.MonthlyCalculationSummary
import com.example.data.model.UserProfile
import com.example.ui.MilkDiaryViewModel
import com.example.ui.components.BillReceiptModal
import com.example.ui.components.RateUpdateDialog
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.EmeraldFresh
import com.example.ui.theme.GoldenButter
import com.example.ui.theme.RoseAdvance
import java.util.Locale

@Composable
fun MonthlyBillScreen(
    userProfile: UserProfile?,
    selectedAccount: MilkAccount?,
    selectedYearMonth: String,
    monthlySummary: MonthlyCalculationSummary?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onUpdateRate: (Double) -> Unit,
    onShareWhatsApp: () -> Unit,
    onCopyBill: () -> Unit
) {
    var showRateDialog by remember { mutableStateOf(false) }
    var showReceiptModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("monthly_bill_screen")
    ) {
        // Month Navigation
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DairyBluePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev Month", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = MilkDiaryViewModel.formatDisplayMonth(selectedYearMonth),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        text = "खाता: ${selectedAccount?.name ?: ""}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                }

                IconButton(onClick = onNextMonth) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Rate Management Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GoldenButter.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenButter),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "माह का दूध भाव (Monthly Rate)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${String.format(Locale.US, "%.2f", monthlySummary?.ratePerLiter ?: selectedAccount?.defaultRate ?: 60.0)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldenButter
                            )
                        )
                        Text(
                            text = " / लीटर",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showRateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenButter),
                    modifier = Modifier.testTag("edit_monthly_rate_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("भाव बदलें")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Billing Auto Calculation Breakdown
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "मासिक अंतिम गणना (Auto Calculation)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Milk Calculation Row
                CalculationDetailRow(
                    label = "1. कुल दूध (Liters)",
                    formula = "${String.format(Locale.US, "%.2f", monthlySummary?.totalLiters ?: 0.0)} L × ₹${String.format(Locale.US, "%.2f", monthlySummary?.ratePerLiter ?: 60.0)}",
                    amount = "₹${String.format(Locale.US, "%.2f", monthlySummary?.milkCost ?: 0.0)}",
                    amountColor = DairyBluePrimary
                )

                // Breakdown note
                Text(
                    text = "• सुबह: ${String.format(Locale.US, "%.2f", monthlySummary?.totalMorningLiters ?: 0.0)}L | शाम: ${String.format(Locale.US, "%.2f", monthlySummary?.totalEveningLiters ?: 0.0)}L (${monthlySummary?.totalDaysRecorded ?: 0} दिन दर्ज)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )

                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Goods / Items Deduction Row
                CalculationDetailRow(
                    label = "2. सामान कटौती (Goods Deduction) (-)",
                    formula = "${monthlySummary?.goodsList?.size ?: 0} सामान दूध में से कटे",
                    amount = "-₹${String.format(Locale.US, "%.2f", monthlySummary?.totalGoodsAmount ?: 0.0)}",
                    amountColor = RoseAdvance
                )

                monthlySummary?.goodsList?.forEach { g ->
                    Text(
                        text = "• ${g.title}: -₹${String.format(Locale.US, "%.2f", g.amount)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp, bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Advances Deductions Row
                CalculationDetailRow(
                    label = "3. नकद पेशगी एडवांस कटौती (-)",
                    formula = "${monthlySummary?.advanceList?.size ?: 0} बार पेशगी ली गई",
                    amount = "-₹${String.format(Locale.US, "%.2f", monthlySummary?.totalAdvanceAmount ?: 0.0)}",
                    amountColor = RoseAdvance
                )

                monthlySummary?.advanceList?.forEach { a ->
                    Text(
                        text = "• ${a.title} (${a.date}): -₹${String.format(Locale.US, "%.2f", a.amount)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp, bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Formula reminder
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldenButter.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "गणना: दूध कीमत - एडवांस - सामान = कुल शुद्ध देय",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Grand Net Total Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DairyBluePrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "कुल देय राशि",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                            Text(
                                text = "Net Balance Payable",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Text(
                            text = "₹${String.format(Locale.US, "%.2f", monthlySummary?.netTotalPayable ?: 0.0)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldenButter
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp Share & PDF/Receipt Actions
        Button(
            onClick = onShareWhatsApp,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("send_whatsapp_bill_button"),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldFresh)
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "व्हाट्सएप पर बिल भेजें (WhatsApp Bill)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { showReceiptModal = true },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("view_receipt_button")
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("बिल रसीद (PDF)", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onCopyBill,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("copy_bill_button")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("बिल कॉपी करें", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Founder Tagline
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Milk Diary • Founded by Ajay Pal",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

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

    if (showReceiptModal && monthlySummary != null) {
        BillReceiptModal(
            summary = monthlySummary,
            dairyName = userProfile?.dairyName ?: "Milk Diary",
            onDismiss = { showReceiptModal = false },
            onShareWhatsApp = {
                showReceiptModal = false
                onShareWhatsApp()
            }
        )
    }
}

@Composable
private fun CalculationDetailRow(
    label: String,
    formula: String,
    amount: String,
    amountColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(text = formula, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = amountColor)
        )
    }
}
