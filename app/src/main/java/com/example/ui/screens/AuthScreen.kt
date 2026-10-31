package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MilkDiaryViewModel
import com.example.ui.theme.DairyBluePrimary
import com.example.ui.theme.EmeraldFresh
import com.example.ui.theme.GoldenButter

@Composable
fun AuthMobileScreen(
    mobileInput: String,
    errorMessage: String?,
    onMobileChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onSkipForNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, GoldenButter, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_milk_logo),
                contentDescription = "Milk Diary Logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Milk Diary",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = DairyBluePrimary
            )
        )

        // FOUNDER NAME IN BOLD (MANDATORY REQUIREMENT)
        Text(
            text = "Founded by Ajay Pal",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            ),
            modifier = Modifier.testTag("founder_name_bold_login")
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "रोज़ का दूध, भाव व मासिक हिसाब किताब",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "मोबाइल नंबर से लॉगिन करें",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "आपके खाते का सुरक्षित बैकअप व हिसाब रखने के लिए",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = mobileInput,
                    onValueChange = onMobileChange,
                    label = { Text("मोबाइल नंबर (Mobile Number)") },
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = DairyBluePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+91", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mobile_number_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onSendOtp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_otp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary)
                ) {
                    Text(
                        text = "OTP प्राप्त करें (Send OTP)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onSkipForNow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("बिना लॉगिन के आगे बढ़ें (Continue as Guest)")
                }
            }
        }
    }
}

@Composable
fun AuthOtpScreen(
    mobileNumber: String,
    otpInput: String,
    generatedOtp: String,
    errorMessage: String?,
    onOtpChange: (String) -> Unit,
    onAutoFill: () -> Unit,
    onVerifyOtp: () -> Unit,
    onChangeNumber: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = DairyBluePrimary,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "OTP सत्यापन (Verify OTP)",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = "+91 $mobileNumber पर भेजा गया कोड दर्ज करें",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Demo simulated helper banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = EmeraldFresh.copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldFresh)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("परीक्षण हेतु OTP (Test Code):", style = MaterialTheme.typography.labelSmall)
                    Text(
                        generatedOtp,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldFresh,
                            letterSpacing = 2.sp
                        )
                    )
                }
                Button(
                    onClick = onAutoFill,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldFresh),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Auto Fill")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = otpInput,
                    onValueChange = onOtpChange,
                    label = { Text("6 अंकों का OTP दर्ज करें") },
                    leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, tint = DairyBluePrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_code_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onVerifyOtp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("verify_otp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyBluePrimary)
                ) {
                    Text(
                        text = "सत्यापित करें और शुरू करें (Verify)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onChangeNumber,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("नंबर बदलें (Change Mobile Number)")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Milk Diary • Founded by Ajay Pal",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
