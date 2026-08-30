package com.hisaabkit.app.tools.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
fun PhotoResizerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var targetKb by remember { mutableStateOf("50") }
    var status by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
        status = if (uri != null) "Photo selected successfully." else ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Photo Resizer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Photo को बिना quality खोए target size तक compress करें।",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { picker.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (selectedUri == null) "Select Photo" else "Change Photo")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = targetKb,
            onValueChange = { input ->
                // Sirf numbers allow karein
                if (input.all { it.isDigit() }) targetKb = input
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Target Size (KB)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val uri = selectedUri
                val kb = targetKb.toIntOrNull()

                if (uri == null) {
                    status = "पहले photo select करें!"
                    return@Button
                }

                if (kb == null || kb < 5) {
                    status = "कृपया सही target size (KB) दर्ज करें।"
                    return@Button
                }

                isProcessing = true
                status = ""

                // Background thread par process karein
                scope.launch {
                    try {
                        val output = compressImage(context, uri, kb)
                        if (output != null) {
                            status = "Success! Photo size: ${output.size / 1024} KB"
                        } else {
                            status = "Photo process नहीं हो सकी।"
                        }
                    } catch (e: Exception) {
                        status = "Error: ${e.localizedMessage}"
                    } finally {
                        isProcessing = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isProcessing,
            shape = MaterialTheme.shapes.medium
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(12.dp))
                Text("Compressing...")
            } else {
                Text("Compress Photo", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))

        if (status.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (status.contains("Success")) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (status.contains("Success")) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

/**
 * Suspend function banaya gaya hai taaki ye main thread ko block na kare.
 */
suspend fun compressImage(
    context: Context,
    uri: Uri,
    targetKb: Int
): ByteArray? = withContext(Dispatchers.IO) {
    
    val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
    
    // OOM bachane ke liye pehle sirf bounds decode karein (Optional advanced step for huge images)
    // Abhi ke liye hum safe decoding ensure kar rahe hain using 'use' properly.
    val bitmap = input.use { BitmapFactory.decodeStream(it) } ?: return@withContext null

    var quality = 100
    var result: ByteArray
    var stream: ByteArrayOutputStream

    do {
        stream = ByteArrayOutputStream()
        // Compression process
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        result = stream.toByteArray()
        
        // Agar image bohot badi hai, to tezi se quality drop karein
        quality -= if (result.size > (targetKb * 1024 * 2)) 10 else 5

    } while (result.size > targetKb * 1024 && quality > 5)

    return@withContext result
}
