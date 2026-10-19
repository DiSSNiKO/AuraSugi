package com.example.aurasugi.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.NeonPink
import com.example.aurasugi.SoftText
import com.example.aurasugi.data.model.SavedItem
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.components.neonGlow

private const val MAX_IMPORT_EXPORT_ITEMS = 10000

private const val EXAMPLE_FORMAT_INSTRUCTIONS =
    "protein;carbs;fat;fiber;portion in grams;name"

private const val EXAMPLE_TEXT_CONTENT =
    "11;65;3;8;100;bread\n" +
    "25;0;10;0;150;chicken breast\n" +
    "3;27;1;3;100;cooked rice\n" +
    "1;14;0;2;120;apple"

@Composable
fun ImportExport(viewModel: MainViewModel) {
    val savedItems by viewModel.savedItems.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var manualInputText by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    fun parseAndImportText(content: String): Pair<Int, Int> {
        var importedCount = 0
        var skippedCount = 0

        val lines = content.split("\r\n", "\n", "\r")
        for (line in lines) {
            if (importedCount >= MAX_IMPORT_EXPORT_ITEMS) {
                break
            }

            val trimmed = line.trim()
            if (trimmed.isBlank() || trimmed.startsWith("#")) {
                continue
            }

            val parts = trimmed.split(";").map { it.trim() }
            if (parts.size >= 6) {
                val protein = parts[0].toIntOrNull()
                val carbs = parts[1].toIntOrNull()
                val fat = parts[2].toIntOrNull()
                val fiber = parts[3].toIntOrNull()
                val portion = parts[4].toIntOrNull()
                val name = parts.subList(5, parts.size).joinToString(";")

                if (protein != null && carbs != null && fat != null && fiber != null && portion != null && name.isNotEmpty()) {
                    val calories = (protein * 4) + (carbs * 4) + (fat * 9)
                    val savedItem = SavedItem(
                        name = name,
                        portion = portion,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        fiber = fiber
                    )
                    viewModel.insertSavedItem(savedItem)
                    importedCount++
                } else {
                    skippedCount++
                }
            } else {
                skippedCount++
            }
        }
        return Pair(importedCount, skippedCount)
    }

    // Export File Launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            try {
                val itemsToExport = savedItems.take(MAX_IMPORT_EXPORT_ITEMS)
                val exportText = itemsToExport.joinToString("\n") { item ->
                    "${item.protein};${item.carbs};${item.fat};${item.fiber};${item.portion};${item.name}"
                }
                context.contentResolver.openOutputStream(it)?.use { output ->
                    output.write(exportText.toByteArray())
                }
                Toast.makeText(context, "Successfully exported ${itemsToExport.size} saved items!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Import File Launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val text = context.contentResolver.openInputStream(it)?.use { input ->
                    input.bufferedReader().readText()
                }
                if (!text.isNullOrEmpty()) {
                    val (imported, skipped) = parseAndImportText(text)
                    importStatusMessage = "Imported $imported item(s) successfully ($skipped skipped/invalid). [Max $MAX_IMPORT_EXPORT_ITEMS per batch]"
                }
            } catch (e: Exception) {
                importStatusMessage = "Import error: ${e.localizedMessage}"
            }
        }
    }

    // Download Example File Launcher
    val downloadExampleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { output ->
                    output.write(EXAMPLE_TEXT_CONTENT.toByteArray())
                }
                Toast.makeText(context, "Downloaded example file successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to download example: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Formatting Instructions Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .neonGlow(color = NeoGreen, alpha = 0.15f),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = NeoGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Import & Export Instructions",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeoGreen
                    )
                }

                Text(
                    text = "Custom saved entries can be imported or exported as text (up to $MAX_IMPORT_EXPORT_ITEMS entries per batch). Each entry must be on its own line in the exact following format:",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText.copy(alpha = 0.8f)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = GreyishBlackSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = EXAMPLE_FORMAT_INSTRUCTIONS,
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonPink,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Text(
                    text = "Example entries:",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftText.copy(alpha = 0.6f)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = GreyishBlackSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = EXAMPLE_TEXT_CONTENT,
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftText,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            downloadExampleLauncher.launch("example_saved_entries.txt")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download Example")
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(EXAMPLE_TEXT_CONTENT))
                            Toast.makeText(context, "Example copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Example")
                    }
                }
            }
        }

        // Export Section Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Export Saved Entries",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoGreen
                )

                Text(
                    text = "You currently have ${savedItems.size} saved food items in your database.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            exportLauncher.launch("aurasugi_saved_entries.txt")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeoGreen, contentColor = Color.Black),
                        modifier = Modifier.weight(1f),
                        enabled = savedItems.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export to File")
                    }

                    OutlinedButton(
                        onClick = {
                            val itemsToExport = savedItems.take(MAX_IMPORT_EXPORT_ITEMS)
                            val exportText = itemsToExport.joinToString("\n") { item ->
                                "${item.protein};${item.carbs};${item.fat};${item.fiber};${item.portion};${item.name}"
                            }
                            clipboardManager.setText(AnnotatedString(exportText))
                            Toast.makeText(context, "Exported ${itemsToExport.size} items to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = savedItems.isNotEmpty()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy to Clipboard")
                    }
                }
            }
        }

        // Import Section Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Import Saved Entries",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoGreen
                )

                Button(
                    onClick = {
                        importLauncher.launch("text/plain")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeoGreen, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import from Text File")
                }

                HorizontalDivider(color = GreyishBlackSurface)

                Text(
                    text = "Or paste raw text entries below:",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText.copy(alpha = 0.8f)
                )

                OutlinedTextField(
                    value = manualInputText,
                    onValueChange = { manualInputText = it },
                    placeholder = { Text("protein;carbs;fat;fiber;portion;name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 6
                )

                Button(
                    onClick = {
                        if (manualInputText.isNotBlank()) {
                            val (imported, skipped) = parseAndImportText(manualInputText)
                            importStatusMessage = "Imported $imported item(s) from text ($skipped skipped)."
                            manualInputText = ""
                        }
                    },
                    enabled = manualInputText.isNotBlank(),
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = NeoGreen, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import from Text Box")
                }

                importStatusMessage?.let { msg ->
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeoGreen
                    )
                }
            }
        }
    }
}
