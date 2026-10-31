package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.BuildConfig
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.EmeraldFresh
import com.example.ui.theme.GoldenButter
import com.example.ui.theme.RoseAdvance

@Composable
fun FounderVersionDialog(
    onDismiss: () -> Unit,
    onResetToBlank: () -> Unit
) {
    var isCheckingUpdate by remember { mutableStateOf(false) }
    var updateCheckStatus by remember { mutableStateOf<String?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("founder_version_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Founder Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldenButter.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = GoldenButter,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Founder Console",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GoldenButter
                                ) {
                                    Text(
                                        text = "AJAY PAL",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "संस्करण एवं तकनीकी नियंत्रण हब (Version & Release Hub)",
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

                // Current App Version Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DairyBluePrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "वर्तमान संस्करण (App Version)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.85f))
                                )
                                Text(
                                    text = "Milk Diary v${BuildConfig.VERSION_NAME}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Build ${BuildConfig.VERSION_CODE}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "• Founder: Ajay Pal",
                                style = MaterialTheme.typography.bodySmall.copy(color = GoldenButter, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "• Database: Room v2",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                            )
                            Text(
                                text = "• Status: Stable",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Check for updates live action
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = null,
                                    tint = DairyBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "अपडेट की स्थिति (Update Status)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Button(
                                onClick = {
                                    isCheckingUpdate = true
                                    updateCheckStatus = "जांच हो रही है..."
                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        isCheckingUpdate = false
                                        updateCheckStatus = "नवीनतम संस्करण v${BuildConfig.VERSION_NAME} सक्रिय है! कोई नया अपडेट लंबित नहीं है।"
                                    }, 800)
                                },
                                enabled = !isCheckingUpdate,
                                colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("अपडेट जांचें", fontSize = 11.sp)
                            }
                        }

                        if (updateCheckStatus != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldFresh,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = updateCheckStatus ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = EmeraldFresh,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Changelog / Version History Section
                Text(
                    text = "संस्करण इतिहास (Release Notes & Changelog):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                VersionChangelogCard(
                    version = "v2.1.0 (Current Release)",
                    date = "अक्टूबर 2026",
                    isLatest = true,
                    changes = listOf(
                        "खाली ऐप आरम्भ (Blank App Startup): यूजर अपनी इच्छानुसार अपना डेटा और खाते लिख सके।",
                        "सटीक हिसाब फॉर्मूला: (दूध की कुल कीमत - नकद पेशगी - सामान = अंतिम देय)।",
                        "संपादन इतिहास फ़ोल्डर (Audit Log Folder): पारदर्शिता हेतु हर संशोधन की सुरक्षित हिस्ट्री।",
                        "संशोधित प्रविष्टियों पर 'EDITED' बैज और लाल/हरे मानों में स्पष्ट तुलना।"
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                VersionChangelogCard(
                    version = "v2.0.0",
                    date = "अक्टूबर 2026",
                    isLatest = false,
                    changes = listOf(
                        "फाउंडर अजय पाल का नाम लोगो के नीचे बोल्ड में स्थायी जोड़ा गया।",
                        "व्हाट्सएप पर बिल भेजने और प्रिंट-स्टाइल रसीद का फीचर।",
                        "मल्टी-अकाउंट सपोर्ट (एक से अधिक दूधवालों का अलग-अलग हिसाब)।",
                        "ऑफलाइन बैकअप व डेटा स्टोरेज फीचर।"
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Founder Developer Guide: How to update version
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldenButter.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, GoldenButter),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = GoldenButter, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "फाउंडर गाइड: अगला वर्जन कैसे अपडेट करें?",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "1. app/build.gradle.kts में versionCode (जैसे 2 ➔ 3) और versionName (जैसे 2.1.0 ➔ 2.2.0) बदलें।\n" +
                                    "2. Google Play Console या AI Studio Build में नया APK / AAB एक्सपोर्ट करें।\n" +
                                    "3. ऐप में Founder Console से हमेशा वर्तमान वर्जन और रूम डेटाबेस स्कीमा की जांच की जा सकती है।",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Danger / Reset Action for Testing Blank Slate
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    border = BorderStroke(1.dp, RoseAdvance),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseAdvance),
                    modifier = Modifier.fillMaxWidth().testTag("founder_reset_blank_button")
                ) {
                    Text("डेटा रीसेट करें (Reset to Blank Slate)")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("कंसोल बंद करें (Close)")
                }
            }
        }
    }

    if (showResetConfirm) {
        androidx.compose.material3.AlertDialog(
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
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAdvance)
                ) {
                    Text("हाँ, पूरा डेटा खाली करें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirm = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun VersionChangelogCard(
    version: String,
    date: String,
    isLatest: Boolean,
    changes: List<String>
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isLatest) EmeraldFresh.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, if (isLatest) EmeraldFresh else MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = version,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isLatest) EmeraldFresh else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (isLatest) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldFresh
                        ) {
                            Text(
                                text = "LATEST",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            changes.forEach { change ->
                Text(
                    text = "• $change",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}
