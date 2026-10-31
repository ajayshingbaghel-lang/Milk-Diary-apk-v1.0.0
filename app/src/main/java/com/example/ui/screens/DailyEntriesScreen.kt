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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyMilkEntry
import com.example.data.model.MilkAccount
import com.example.data.repository.MilkDiaryRepository
import com.example.ui.MilkDiaryViewModel
import com.example.ui.components.MilkEntryDialog
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.GoldenButter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DailyEntriesScreen(
    selectedAccount: MilkAccount?,
    selectedYearMonth: String,
    dailyEntries: List<DailyMilkEntry>,
    ratePerLiter: Double,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSaveEntry: (date: String, morning: Double, evening: Double, total: Double, notes: String) -> Unit,
    onDeleteEntry: (DailyMilkEntry) -> Unit,
    onViewEditDetails: (DailyMilkEntry) -> Unit,
    onOpenEditHistoryFolder: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<DailyMilkEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<DailyMilkEntry?>(null) }

    val totalMonthLiters = dailyEntries.sumOf { it.totalQuantity }
    val totalCost = totalMonthLiters * ratePerLiter
    val editedEntriesCount = dailyEntries.count { it.isEdited }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    entryToEdit = null
                    showAddDialog = true
                },
                containerColor = DairyBluePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_milk_entry_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Milk Entry")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Month Header Selector
            Surface(
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
                            text = "खाता: ${selectedAccount?.name ?: "डिफ़ॉल्ट"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }

                    IconButton(onClick = onNextMonth) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month", tint = Color.White)
                    }
                }
            }

            // Summary Bar + Edit History Folder Button
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "कुल दूध (${dailyEntries.size} दिन)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.2f", totalMonthLiters)} L",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DairyBluePrimary
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "दूध मूल्य (भाव ₹${String.format(Locale.US, "%.0f", ratePerLiter)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${String.format(Locale.US, "%.2f", totalCost)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenButter
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(6.dp))

                    // Transparency & Editing History Folder link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenEditHistoryFolder() }
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = GoldenButter, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (editedEntriesCount > 0) "पारदर्शिता: $editedEntriesCount प्रविष्टियां संशोधित हुई हैं" else "पारदर्शिता: कोई संशोधन नहीं (सुरक्षित हिसाब)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                        Text(
                            text = "इतिहास फ़ोल्डर देखें ›",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DairyBluePrimary)
                        )
                    }
                }
            }

            // Entries List
            if (dailyEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "इस महीने में अभी कोई दूध प्रविष्टि नहीं है",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                entryToEdit = null
                                showAddDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("दूध दर्ज करें (Add Milk)")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("daily_entries_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dailyEntries, key = { it.id }) { entry ->
                        DailyEntryItemCard(
                            entry = entry,
                            rate = ratePerLiter,
                            onEdit = {
                                entryToEdit = entry
                                showAddDialog = true
                            },
                            onDelete = {
                                entryToDelete = entry
                            },
                            onViewEditDetails = {
                                onViewEditDetails(entry)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddDialog) {
        MilkEntryDialog(
            initialDate = entryToEdit?.date ?: MilkDiaryRepository.getTodayDate(),
            existingEntry = entryToEdit,
            onDismiss = {
                showAddDialog = false
                entryToEdit = null
            },
            onSave = { date, morning, evening, total, notes ->
                onSaveEntry(date, morning, evening, total, notes)
                showAddDialog = false
                entryToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("प्रविष्टि हटाएं? (Delete Entry)") },
            text = { Text("क्या आप दिनांक ${entryToDelete?.date} की दूध प्रविष्टि को हटाना चाहते हैं? यह संशोधन इतिहास में दर्ज होगा।") },
            confirmButton = {
                Button(
                    onClick = {
                        entryToDelete?.let { onDeleteEntry(it) }
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("हटाएं (Delete)")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { entryToDelete = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun DailyEntryItemCard(
    entry: DailyMilkEntry,
    rate: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewEditDetails: () -> Unit
) {
    val dayCost = entry.totalQuantity * rate

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (entry.isEdited) androidx.compose.foundation.BorderStroke(1.2.dp, GoldenButter.copy(alpha = 0.6f)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // CORNER EDIT MARK IF EDITED (MANDATORY REQUIREMENT)
            if (entry.isEdited) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoldenButter.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenButter)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = GoldenButter, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "संशोधित (EDITED)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = GoldenButter
                                )
                            )
                        }
                    }

                    // View comparison option
                    Row(
                        modifier = Modifier
                            .clickable { onViewEditDetails() }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = DairyBluePrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "बदलाव देखें (View Diff)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DairyBluePrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                Divider(modifier = Modifier.padding(bottom = 8.dp), color = GoldenButter.copy(alpha = 0.2f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Date & Day
                Column(modifier = Modifier.width(90.dp)) {
                    Text(
                        text = formatDisplayDate(entry.date),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = formatDayOfWeek(entry.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (entry.notes.isNotBlank()) {
                        Text(
                            text = entry.notes,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = DairyBluePrimary,
                            maxLines = 1
                        )
                    }
                }

                // Middle: Morning & Evening Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Morning
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldenButter.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LightMode, contentDescription = null, tint = GoldenButter, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${entry.morningQuantity}L",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            )
                        }
                    }

                    // Evening
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DairyBluePrimary.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = DairyBluePrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${entry.eveningQuantity}L",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            )
                        }
                    }
                }

                // Right: Total & Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format(Locale.US, "%.2f", entry.totalQuantity)} L",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DairyBluePrimary
                            )
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%.0f", dayCost)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DairyBluePrimary, modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

private fun formatDisplayDate(dateStr: String): String {
    return try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val date = inFormat.parse(dateStr)
        if (date != null) outFormat.format(date) else dateStr
    } catch (_: Exception) {
        dateStr
    }
}

private fun formatDayOfWeek(dateStr: String): String {
    return try {
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val date = inFormat.parse(dateStr)
        if (date != null) outFormat.format(date) else ""
    } catch (_: Exception) {
        ""
    }
}
