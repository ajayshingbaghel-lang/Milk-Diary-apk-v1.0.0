package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.EmeraldFresh
import com.example.ui.theme.GoldenButter

@Composable
fun BackupScreen(
    backupStatus: String?,
    onExportBackup: (Context) -> Unit,
    onRestoreBackup: (String) -> Unit,
    onClearStatus: () -> Unit,
    onResetToBlank: () -> Unit = {}
) {
    val context = LocalContext.current
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var restoreJsonInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("backup_screen_container")
    ) {
        // Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DairyBluePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = GoldenButter,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "डेटा बैकअप व स्टोरेज (Data Backup)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "आपके सभी दूध हिसाब, खाते, भाव व प्रविष्टियों को सुरक्षित रखें",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status banner if any
        if (backupStatus != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = backupStatus,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = onClearStatus,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("ठीक है", fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Export Backup Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = DairyBluePrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "1. बैकअप फाइल तैयार करें (Export)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "अपने सभी खातों, दूध प्रविष्टियों, सामान व पेशगी के डेटा का बैकअप JSON फॉर्मेट में बनाकर व्हाट्सएप, गूगल ड्राइव या ईमेल पर भेजें।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onExportBackup(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("export_backup_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("बैकअप बनाएं व शेयर करें (Create Backup)")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Restore Backup Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = EmeraldFresh)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "2. बैकअप रीस्टोर करें (Restore)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "यदि आपने पहले कोई बैकअप लिया था, तो बैकअप कोड पेस्ट करके सारा हिसाब वापस ला सकते हैं।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { showRestoreDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("restore_backup_button")
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("बैकअप डेटा रीस्टोर करें (Restore)")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History Storage Info
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = GoldenButter, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ऑफलाइन सुरक्षित स्टोरेज (Safe Local Storage)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "आपका डेटा बिना इंटरनेट के भी सुरक्षित आपके फोन में सेव रहता है। हर माह का पूरा रिकॉर्ड पिछले इतिहास में कभी भी देखा जा सकता है।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset to Blank Slate Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF1F2),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDA4AF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "डेटा खाली करें / नया आरंभ (Reset to Blank)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBE123C)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "यदि आप सभी पुराने खाते, दूध प्रविष्टियां और हिसाब हटाकर नए सिरे से अपनी डायरी शुरू करना चाहते हैं, तो यहाँ से खाली कर सकते हैं।",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF881337)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { showResetConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    modifier = Modifier.fillMaxWidth().testTag("reset_all_data_blank_button")
                ) {
                    Text("सारा डेटा खाली करें (Blank App)")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Milk Diary • Founded by Ajay Pal",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("डेटा पूरी तरह खाली करें?") },
            text = {
                Text("क्या आप वाकई सभी खाते, दूध प्रविष्टियाँ, भाव व हिसाब मिटाकर ऐप को बिल्कुल नया और खाली (Blank) करना चाहते हैं?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetToBlank()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Text("हाँ, डेटा खाली करें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirm = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("बैकअप JSON पेस्ट करें (Paste JSON)") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "कॉपी किया गया बैकअप टेक्स्ट नीचे बॉक्स में पेस्ट करें:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreJsonInput,
                        onValueChange = { restoreJsonInput = it },
                        placeholder = { Text("{\"version\":1, \"accounts\":[...]}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("restore_json_input"),
                        maxLines = 8
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreJsonInput.isNotBlank()) {
                            onRestoreBackup(restoreJsonInput.trim())
                            showRestoreDialog = false
                            restoreJsonInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldFresh),
                    modifier = Modifier.testTag("confirm_restore_button")
                ) {
                    Text("रीस्टोर करें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRestoreDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
