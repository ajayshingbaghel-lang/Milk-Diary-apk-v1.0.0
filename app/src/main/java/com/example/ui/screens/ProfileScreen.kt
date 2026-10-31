package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.GoldenButter

@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    onSaveProfile: (name: String, dairyName: String, phone: String, address: String, photoUri: String?) -> Unit,
    onLogout: () -> Unit,
    onOpenFounderConsole: () -> Unit = {},
    onResetToBlank: () -> Unit = {}
) {
    var nameText by remember(userProfile) { mutableStateOf(userProfile?.fullName ?: "") }
    var dairyNameText by remember(userProfile) { mutableStateOf(userProfile?.dairyName ?: "") }
    var phoneText by remember(userProfile) { mutableStateOf(userProfile?.mobileNumber ?: "") }
    var addressText by remember(userProfile) { mutableStateOf(userProfile?.address ?: "") }
    var photoUriState by remember(userProfile) { mutableStateOf(userProfile?.photoUri) }
    var savedMessage by remember { mutableStateOf(false) }

    // Modern Zero-Permission Photo Picker for user profile photo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUriState = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen_container")
    ) {
        // Top Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DairyBluePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Photo Box with edit badge
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, GoldenButter, CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("profile_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUriState != null) {
                        AsyncImage(
                            model = photoUriState,
                            contentDescription = "User Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_milk_logo),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Camera Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(GoldenButter)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = nameText.ifEmpty { "डेयरी स्वामी" },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = dairyNameText.ifEmpty { "Milk Diary" },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f)
                    )
                )

                Text(
                    text = "फोटो बदलने के लिए ऊपर टैप करें (Tap photo to change)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Form
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "व्यक्तिगत व डेयरी विवरण (Profile Details)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        savedMessage = false
                    },
                    label = { Text("आपका नाम (Full Name)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = DairyBluePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dairyNameText,
                    onValueChange = {
                        dairyNameText = it
                        savedMessage = false
                    },
                    label = { Text("डेयरी / फार्म का नाम (Dairy Name)") },
                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = DairyBluePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_dairy_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phoneText,
                    onValueChange = {
                        phoneText = it
                        savedMessage = false
                    },
                    label = { Text("मोबाइल नंबर (Mobile Number)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = DairyBluePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = addressText,
                    onValueChange = {
                        addressText = it
                        savedMessage = false
                    },
                    label = { Text("पता / गांव / शहर (Address)") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = DairyBluePrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (savedMessage) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "✓ प्रोफ़ाइल सफलतापूर्वक सुरक्षित हो गई!",
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        onSaveProfile(nameText, dairyNameText, phoneText, addressText, photoUriState)
                        savedMessage = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("प्रोफ़ाइल सुरक्षित करें (Save Profile)")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MANDATORY FOUNDER BRANDING CARD: Founded by Ajay Pal
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GoldenButter.copy(alpha = 0.08f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenButter.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("founder_card")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, GoldenButter, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_milk_logo),
                            contentDescription = "Milk Diary Logo",
                            modifier = Modifier.size(42.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Milk Diary",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DairyBluePrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = DairyBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // MANDATORY: Founded by Ajay Pal in bold
                        Text(
                            text = "Founded by Ajay Pal",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.testTag("founder_name_bold_profile")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Milk Diary ऐप भारत के सभी डेयरी किसानों व दूध उपभोक्ताओं के लिए समर्पित है। रोज़ का दूध, महीने का भाव, नकद पेशगी व सामान का सटीक हिसाब रखने का विश्वसनीय साधन।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenFounderConsole,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenButter),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_founder_console_button")
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("👑 Founder Console & Version Hub (Ajay Pal)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout / Switch
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("लॉगआउट करें (Logout)")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
