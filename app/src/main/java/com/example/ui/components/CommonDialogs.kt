package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DailyMilkEntry
import com.example.data.model.MilkAccount
import com.example.data.model.MonthlyCalculationSummary
import com.example.ui.MilkDiaryViewModel
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.EmeraldFresh
import com.example.ui.theme.GoldenButter
import com.example.ui.theme.RoseAdvance
import java.util.Locale

// 1. Rate Update Dialog
@Composable
fun RateUpdateDialog(
    currentRate: Double,
    yearMonth: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var rateInput by remember { mutableStateOf(if (currentRate > 0) currentRate.toString() else "60.0") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CurrencyRupee,
                    contentDescription = null,
                    tint = GoldenButter
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "दूध का भाव सेट करें (Set Rate)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "माह: ${MilkDiaryViewModel.formatDisplayMonth(yearMonth)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "इस महीने का प्रति लीटर भाव (₹/लीटर) दर्ज करें:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rateInput,
                    onValueChange = {
                        rateInput = it
                        errorText = null
                    },
                    label = { Text("भाव प्रति लीटर (Rate ₹/L)") },
                    leadingIcon = {
                        Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = DairyBluePrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rate_input_field")
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "त्वरित चयन (Quick Select):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("55.0", "60.0", "65.0", "70.0").forEach { preset ->
                        FilterChip(
                            selected = rateInput == preset,
                            onClick = { rateInput = preset },
                            label = { Text("₹$preset", fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = rateInput.toDoubleOrNull()
                    if (parsed != null && parsed > 0) {
                        onConfirm(parsed)
                    } else {
                        errorText = "कृपया मान्य भाव दर्ज करें"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                modifier = Modifier.testTag("save_rate_button")
            ) {
                Text("भाव सुरक्षित करें (Save)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("रद्द करें (Cancel)")
            }
        }
    )
}

// 2. Milk Entry Dialog (Add/Edit)
@Composable
fun MilkEntryDialog(
    initialDate: String,
    existingEntry: DailyMilkEntry?,
    onDismiss: () -> Unit,
    onSave: (date: String, morning: Double, evening: Double, total: Double, notes: String) -> Unit
) {
    var dateText by remember { mutableStateOf(existingEntry?.date ?: initialDate) }
    var morningText by remember { mutableStateOf(existingEntry?.morningQuantity?.takeIf { it > 0 }?.toString() ?: "0.0") }
    var eveningText by remember { mutableStateOf(existingEntry?.eveningQuantity?.takeIf { it > 0 }?.toString() ?: "0.0") }
    var notesText by remember { mutableStateOf(existingEntry?.notes ?: "") }

    val morningVal = morningText.toDoubleOrNull() ?: 0.0
    val eveningVal = eveningText.toDoubleOrNull() ?: 0.0
    val totalVal = morningVal + eveningVal

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = null,
                    tint = DairyBluePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingEntry != null) "दूध की प्रविष्टि बदलें (Edit Entry)" else "दूध की प्रविष्टि जोड़ें (Add Milk)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Date field
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("दिनांक (Date YYYY-MM-DD)") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = DairyBluePrimary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("milk_entry_date_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Morning Milk
                OutlinedTextField(
                    value = morningText,
                    onValueChange = { morningText = it },
                    label = { Text("सुबह का दूध (Morning L)") },
                    trailingIcon = { Text("लीटर (L) ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("morning_milk_input")
                )

                // Quick buttons for morning
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("1.0", "1.5", "2.0", "2.5").forEach { preset ->
                        FilterChip(
                            selected = morningText == preset,
                            onClick = { morningText = preset },
                            label = { Text("${preset}L", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Evening Milk
                OutlinedTextField(
                    value = eveningText,
                    onValueChange = { eveningText = it },
                    label = { Text("शाम का दूध (Evening L)") },
                    trailingIcon = { Text("लीटर (L) ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("evening_milk_input")
                )

                // Quick buttons for evening
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("0.0", "1.0", "1.5", "2.0").forEach { preset ->
                        FilterChip(
                            selected = eveningText == preset,
                            onClick = { eveningText = preset },
                            label = { Text("${preset}L", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Total calculated banner
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "कुल दूध (Total):",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.2f", totalVal)} लीटर (L)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DairyBluePrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("टिप्पणी / नोट (Notes - Optional)") },
                    placeholder = { Text("जैसे: शादी के लिए 1L अतिरिक्त") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("milk_entry_notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(dateText, morningVal, eveningVal, totalVal, notesText.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                modifier = Modifier.testTag("save_milk_entry_button")
            ) {
                Text("सेव करें (Save)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("रद्द करें (Cancel)")
            }
        }
    )
}

// 3. Advance & Goods Dialog
@Composable
fun AdvanceGoodsDialog(
    initialDate: String,
    onDismiss: () -> Unit,
    onSave: (date: String, type: String, title: String, amount: Double, notes: String) -> Unit
) {
    var selectedType by remember { mutableStateOf("ADVANCE") } // "ADVANCE" or "GOODS"
    var dateText by remember { mutableStateOf(initialDate) }
    var titleText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (selectedType == "ADVANCE") "नकद पेशगी जोड़ें (Add Advance)" else "सामान / उत्पाद जोड़ें (Add Goods)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Tab to switch between Advance & Goods
                TabRow(
                    selectedTabIndex = if (selectedType == "ADVANCE") 0 else 1,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedType == "ADVANCE",
                        onClick = {
                            selectedType = "ADVANCE"
                            if (titleText.isEmpty() || titleText.startsWith("Desi") || titleText.startsWith("Pashu")) {
                                titleText = "नकद पेशगी (Cash Advance)"
                            }
                        },
                        text = { Text("💵 नकद एडवांस", fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedType == "GOODS",
                        onClick = {
                            selectedType = "GOODS"
                            if (titleText.isEmpty() || titleText.contains("Advance")) {
                                titleText = "देसी घी (Desi Ghee) 1kg"
                            }
                        },
                        text = { Text("🛍️ सामान/वस्तु", fontSize = 13.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("दिनांक (Date YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Title / Item Name
                OutlinedTextField(
                    value = titleText,
                    onValueChange = {
                        titleText = it
                        errorText = null
                    },
                    label = {
                        Text(if (selectedType == "ADVANCE") "विवरण (e.g. Cash Advance)" else "सामान का नाम (Item Name)")
                    },
                    leadingIcon = {
                        Icon(
                            if (selectedType == "ADVANCE") Icons.Default.AttachMoney else Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = if (selectedType == "ADVANCE") RoseAdvance else EmeraldFresh
                        )
                    },
                    placeholder = {
                        Text(if (selectedType == "ADVANCE") "नकद एडवांस" else "जैसे: 1kg देसी घी, पशु आहार, पनीर")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_title_input")
                )

                // Quick item presets for Goods
                if (selectedType == "GOODS") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "लोकप्रिय सामान (Quick Items):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("देसी घी 1kg", "पशु आहार / खली", "पनीर 500g").forEach { preset ->
                            FilterChip(
                                selected = titleText.contains(preset),
                                onClick = {
                                    titleText = preset
                                    if (preset == "देसी घी 1kg") amountText = "650"
                                    if (preset == "पशु आहार / खली") amountText = "450"
                                    if (preset == "पनीर 500g") amountText = "200"
                                },
                                label = { Text(preset, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorText = null
                    },
                    label = { Text("रुपया / कीमत (Amount ₹)") },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = GoldenButter) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_amount_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("टिप्पणी (Notes - Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    if (titleText.isBlank()) {
                        errorText = "कृपया विवरण या सामान का नाम लिखें"
                    } else if (amt == null || amt <= 0) {
                        errorText = "कृपया मान्य राशि दर्ज करें"
                    } else {
                        onSave(dateText, selectedType, titleText.trim(), amt, notesText.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "ADVANCE") RoseAdvance else EmeraldFresh
                ),
                modifier = Modifier.testTag("save_advance_goods_button")
            ) {
                Text("सुरक्षित करें (Save)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("रद्द करें (Cancel)")
            }
        }
    )
}

// 4. Add/Edit Account Dialog
@Composable
fun AccountDialog(
    existingAccount: MilkAccount? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, address: String, defaultRate: Double, notes: String) -> Unit
) {
    var nameText by remember { mutableStateOf(existingAccount?.name ?: "") }
    var phoneText by remember { mutableStateOf(existingAccount?.phone ?: "") }
    var addressText by remember { mutableStateOf(existingAccount?.address ?: "") }
    var rateText by remember { mutableStateOf(existingAccount?.defaultRate?.toString() ?: "60.0") }
    var notesText by remember { mutableStateOf(existingAccount?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = DairyBluePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingAccount != null) "दूधवाला खाता संपादित करें" else "नया दूधवाला / खाता जोड़ें",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        errorText = null
                    },
                    label = { Text("दूधवाले का नाम (Vendor / Account Name)*") },
                    placeholder = { Text("जैसे: रामू ग्वाला / शर्मा डेयरी") },
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phoneText,
                    onValueChange = { phoneText = it },
                    label = { Text("मोबाइल नंबर (Mobile No)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it },
                    label = { Text("डिफ़ॉल्ट भाव प्रति लीटर (Default Rate ₹/L)") },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = addressText,
                    onValueChange = { addressText = it },
                    label = { Text("पता / क्षेत्र (Address)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("टिप्पणी / विवरण (Notes)") },
                    placeholder = { Text("जैसे: गाय का दूध, सुबह 7 बजे") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameText.isBlank()) {
                        errorText = "कृपया दूधवाले का नाम लिखें"
                    } else {
                        val rate = rateText.toDoubleOrNull() ?: 60.0
                        onSave(nameText.trim(), phoneText.trim(), addressText.trim(), rate, notesText.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                modifier = Modifier.testTag("save_account_button")
            ) {
                Text("सुरक्षित करें (Save)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("रद्द करें (Cancel)")
            }
        }
    )
}

// 5. Bill Receipt Printable Modal
@Composable
fun BillReceiptModal(
    summary: MonthlyCalculationSummary,
    dairyName: String,
    onDismiss: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("bill_receipt_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Receipt Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = dairyName.ifEmpty { "Milk Diary" },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DairyBluePrimary
                            )
                        )
                        Text(
                            text = "दूध का मासिक हिसाब (Monthly Statement)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                // Account & Period
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("दूधवाला / खाता:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(summary.accountName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("माह (Month):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(MilkDiaryViewModel.formatDisplayMonth(summary.yearMonth), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Table breakdown
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        ReceiptRow("रिकॉर्डेड दिन (Recorded Days)", "${summary.totalDaysRecorded} दिन")
                        ReceiptRow("सुबह का दूध (Morning)", "${String.format(Locale.US, "%.2f", summary.totalMorningLiters)} L")
                        ReceiptRow("शाम का दूध (Evening)", "${String.format(Locale.US, "%.2f", summary.totalEveningLiters)} L")
                        ReceiptRow("कुल दूध (Total Milk)", "${String.format(Locale.US, "%.2f", summary.totalLiters)} L", isBold = true)
                        ReceiptRow("भाव (Rate per Liter)", "₹${String.format(Locale.US, "%.2f", summary.ratePerLiter)} / L")
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        ReceiptRow("दूध की कुल राशि (Milk Cost)", "₹${String.format(Locale.US, "%.2f", summary.milkCost)}", isBold = true, color = DairyBluePrimary)
                    }
                }

                if (summary.goodsList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("सामान कटौती (Goods Deductions):", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    summary.goodsList.forEach { g ->
                        ReceiptRow("• ${g.title}", "-₹${String.format(Locale.US, "%.2f", g.amount)}")
                    }
                    ReceiptRow("कुल सामान कटौती (-)", "-₹${String.format(Locale.US, "%.2f", summary.totalGoodsAmount)}", isBold = true, color = RoseAdvance)
                }

                if (summary.advanceList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("नकद पेशगी (Advance Deductions):", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    summary.advanceList.forEach { a ->
                        ReceiptRow("• ${a.title} (${a.date})", "-₹${String.format(Locale.US, "%.2f", a.amount)}")
                    }
                    ReceiptRow("कुल एडवांस (-)", "-₹${String.format(Locale.US, "%.2f", summary.totalAdvanceAmount)}", isBold = true, color = RoseAdvance)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Grand Total Box
                Surface(
                    color = DairyBluePrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "अंतिम देय राशि\n(Net Payable):",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%.2f", summary.netTotalPayable)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldenButter
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Founder Branding inside Receipt
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Milk Diary App",
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "Founded by Ajay Pal",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DairyBluePrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Button(
                    onClick = onShareWhatsApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("receipt_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldFresh)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("व्हाट्सएप पर बिल भेजें (Share WhatsApp)")
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    title: String,
    value: String,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                color = color
            )
        )
    }
}


// 6. Audit Detail / Transparency View Modal
@Composable
fun AuditDetailModal(
    audit: com.example.data.model.AuditLogEntry,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("audit_detail_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldenButter.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = GoldenButter,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "संशोधन विवरण (Edit History)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "पूर्ण पारदर्शिता व सही हिसाब हेतु",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Account & Date Info
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("खाता (Account):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(audit.accountName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("दिनांक / माह (Date):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(audit.dateOrPeriod, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        if (audit.formattedTime.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("संशोधन का समय (Time):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(audit.formattedTime, style = MaterialTheme.typography.labelSmall.copy(color = DairyBluePrimary, fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = audit.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DairyBluePrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Old Value Card (लाल/न्यूट्रल बॉर्डर में पुराना मान)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF1F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDA4AF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✕ पुराना लिखा हुआ (Previous Value):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBE123C)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = audit.oldValue,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF881337)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. New Value Card (हरे रंग में नया बदला हुआ मान)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✓ नया संशोधित मान (New Edited Value):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = audit.newValue,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF14532D)
                            )
                        )
                    }
                }

                if (audit.differenceText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldenButter.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("हिसाब में कुल अंतर (Difference):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                            Text(
                                audit.differenceText,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldenButter
                                )
                            )
                        }
                    }
                }

                if (audit.reason.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "संशोधन का कारण (Reason): ${audit.reason}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary)
                ) {
                    Text("समझ गया / बंद करें (Close)")
                }
            }
        }
    }
}
