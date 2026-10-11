package com.example.aurasugi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.SoftText
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.components.neonGlow

@Composable
fun USDAapi(viewModel: MainViewModel) {
    val isApiEnabled by viewModel.isUsdaApiEnabled.collectAsState()
    val savedApiKey by viewModel.usdaApiKey.collectAsState()

    var apiKeyInput by remember(savedApiKey) { mutableStateOf(savedApiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }

    var isTestingConnection by remember { mutableStateOf(false) }
    var testResultStatus by remember { mutableStateOf<Boolean?>(null) }
    var testResultMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .neonGlow(color = if (isApiEnabled) NeoGreen else Color.Gray, alpha = 0.2f),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Api,
                        contentDescription = "USDA API Icon",
                        tint = if (isApiEnabled) NeoGreen else SoftText.copy(alpha = 0.5f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "USDA API Search",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftText
                        )
                        Text(
                            text = if (isApiEnabled) "Enabled in Search Screen" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isApiEnabled) NeoGreen else SoftText.copy(alpha = 0.6f)
                        )
                    }
                }

                Switch(
                    checked = isApiEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.setUsdaApiEnabled(enabled)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeoGreen
                    )
                )
            }
        }

        // Settings Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .neonGlow(color = NeoGreen, alpha = 0.15f),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "API Key Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoGreen
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        testResultStatus = null
                    },
                    label = { Text("USDA API Key") },
                    placeholder = { Text("Enter your API Key (e.g., DEMO_KEY)") },
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null, tint = NeoGreen)
                    },
                    trailingIcon = {
                        IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                            Icon(
                                imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isKeyVisible) "Hide Key" else "Show Key",
                                tint = SoftText
                            )
                        }
                    },
                    visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = {
                            isTestingConnection = true
                            testResultStatus = null
                            viewModel.testUsdaApiConnection(apiKeyInput) { success, message ->
                                isTestingConnection = false
                                testResultStatus = success
                                testResultMessage = message
                            }
                        },
                        enabled = !isTestingConnection
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = NeoGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing...")
                        } else {
                            Text("Test Connection")
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.setUsdaApiKey(apiKeyInput.trim())
                            testResultStatus = true
                            testResultMessage = "API key saved successfully!"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeoGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Save Key")
                    }
                }

                testResultStatus?.let { success ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (success) NeoGreen else Color(0xFFCF6679),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = testResultMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (success) NeoGreen else Color(0xFFCF6679)
                        )
                    }
                }
            }
        }

        // Informational Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About USDA FoodData Central API",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeoGreen
                )
                Text(
                    text = "• The USDA API allows searching over 300,000 foods and nutrition records directly in aurasugi.\n" +
                            "• Only major macros (Calories, Protein, Carbs, Fat, Fiber) and serving sizes are imported.\n" +
                            "• If you don't have a key, 'DEMO_KEY' is used by default, but it has strict rate limits.\n" +
                            "• You can request a free API Key instantly at https://api.data.gov/signup/",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText.copy(alpha = 0.8f)
                )
            }
        }
    }
}
