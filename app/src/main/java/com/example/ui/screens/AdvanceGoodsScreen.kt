package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
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
import com.example.data.model.AdvanceOrGoodsEntry
import com.example.data.model.MilkAccount
import com.example.data.repository.MilkDiaryRepository
import com.example.ui.MilkDiaryViewModel
import com.example.ui.components.AdvanceGoodsDialog
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.EmeraldFresh
import com.example.ui.theme.GoldenButter
import com.example.ui.theme.RoseAdvance
import java.util.Locale

@Composable
fun AdvanceGoodsScreen(
    selectedAccount: MilkAccount?,
    selectedYearMonth: String,
    items: List<AdvanceOrGoodsEntry>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSaveItem: (id: Long, date: String, type: String, title: String, amount: Double, notes: String) -> Unit,
    onDeleteItem: (AdvanceOrGoodsEntry) -> Unit,
    onViewEditDetails: (AdvanceOrGoodsEntry) -> Unit,
    onOpenEditHistoryFolder: () -> Unit
) {
    var filterType by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<AdvanceOrGoodsEntry?>(null) }
    var itemToDelete by remember { mutableStateOf<AdvanceOrGoodsEntry?>(null) }

    val totalAdvance = items.filter { it.type == "ADVANCE" }.sumOf { it.amount }
    val totalGoods = items.filter { it.type == "GOODS" }.sumOf { it.amount }
    val editedCount = items.count { it.isEdited }

    val filteredList = when (filterType) {
        "ADVANCE" -> items.filter { it.type == "ADVANCE" }
        "GOODS" -> items.filter { it.type == "GOODS" }
        else -> items
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    itemToEdit = null
                    showAddDialog = true
                },
                containerColor = EmeraldFresh,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_advance_goods_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Advance or Goods")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Month Header
            Surface(
                color = EmeraldFresh,
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "सामान व पेशगी ट्रैकिंग",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "${MilkDiaryViewModel.formatDisplayMonth(selectedYearMonth)} • ${selectedAccount?.name ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                        )
                    }

                    IconButton(onClick = onNextMonth) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
                    }
                }
            }

            // Summary Totals + Edit History transparency link
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("कुल सामान कटौती (-)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "-₹${String.format(Locale.US, "%.2f", totalGoods)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoseAdvance)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("कुल नकद एडवांस कटौती (-)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "-₹${String.format(Locale.US, "%.2f", totalAdvance)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoseAdvance)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(6.dp))

                    // Transparency Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenEditHistoryFolder() }
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = GoldenButter, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (editedCount > 0) "पारदर्शिता: $editedCount सामान/पेशगी संशोधित" else "सुरक्षित ऑडिट: कोई बदलाव नहीं",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "इतिहास फ़ोल्डर देखें ›",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldFresh)
                        )
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == "ALL",
                    onClick = { filterType = "ALL" },
                    label = { Text("सभी (${items.size})") }
                )
                FilterChip(
                    selected = filterType == "ADVANCE",
                    onClick = { filterType = "ADVANCE" },
                    label = { Text("💵 नकद एडवांस") }
                )
                FilterChip(
                    selected = filterType == "GOODS",
                    onClick = { filterType = "GOODS" },
                    label = { Text("🛍️ सामान / उत्पाद") }
                )
            }

            // List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "इस माह में कोई सामान या एडवांस दर्ज नहीं है",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                itemToEdit = null
                                showAddDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldFresh)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("प्रविष्टि जोड़ें (Add Entry)")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("advance_goods_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        AdvanceGoodsItemCard(
                            item = item,
                            onEdit = {
                                itemToEdit = item
                                showAddDialog = true
                            },
                            onDelete = { itemToDelete = item },
                            onViewEditDetails = { onViewEditDetails(item) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AdvanceGoodsDialog(
            initialDate = itemToEdit?.date ?: MilkDiaryRepository.getTodayDate(),
            onDismiss = {
                showAddDialog = false
                itemToEdit = null
            },
            onSave = { date, type, title, amount, notes ->
                onSaveItem(itemToEdit?.id ?: 0L, date, type, title, amount, notes)
                showAddDialog = false
                itemToEdit = null
            }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("हटाने की पुष्टि करें?") },
            text = { Text("क्या आप '${itemToDelete?.title}' (₹${itemToDelete?.amount}) को हटाना चाहते हैं? यह संपादन इतिहास में दर्ज रहेगा।") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { onDeleteItem(it) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("हटाएं")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToDelete = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun AdvanceGoodsItemCard(
    item: AdvanceOrGoodsEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewEditDetails: () -> Unit
) {
    val isAdvance = item.type == "ADVANCE"
    val accentColor = if (isAdvance) RoseAdvance else EmeraldFresh

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (item.isEdited) androidx.compose.foundation.BorderStroke(1.2.dp, GoldenButter.copy(alpha = 0.6f)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // CORNER EDIT MARK IF EDITED (MANDATORY REQUIREMENT)
            if (item.isEdited) {
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

                    Row(
                        modifier = Modifier
                            .clickable { onViewEditDetails() }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = EmeraldFresh, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "बदलाव देखें (View Diff)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldFresh,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAdvance) Icons.Default.AttachMoney else Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${item.date} • ${if (isAdvance) "नकद एडवांस" else "सामान / उत्पाद"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (item.notes.isNotBlank()) {
                            Text(
                                text = item.notes,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "-₹${String.format(Locale.US, "%.2f", item.amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoseAdvance
                        )
                    )

                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = DairyBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
