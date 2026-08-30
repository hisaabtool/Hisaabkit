package com.hisaabkit.app.tools.photo

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
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
    
    var compressedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var originalSizeKb by remember { mutableIntStateOf(0) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
        compressedBytes = null
        status = ""
        
        if (uri != null) {
            try {
                // Original image ka size nikalna
                val fd = context.contentResolver.openFileDescriptor(uri, "r")
                val size = fd?.statSize ?: 0
                originalSizeKb = (size / 1024).toInt()
                fd?.close()
            } catch (e: Exception) {
                originalSizeKb = 0
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // --- HEADER ---
        Text(
            text = "Photo Resizer Pro",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Form या Upload के लिए photo का size कम करें।",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        // --- INSTRUCTIONS (निर्देश) ---
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("उपयोग कैसे करें (How to use)", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Text("1. 'Select Photo' बटन से अपनी इमेज चुनें।\n2. 'Target Size' में मनचाहा KB दर्ज करें (जैसे 50)।\n3. 'Compress' दबाएं और रिजल्ट चेक करें।\n4. अंत में 'Save to Gallery' से डाउनलोड करें।", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- SELECT PHOTO ---
        OutlinedButton(
            onClick = { picker.launch("image/*") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (selectedUri == null) "Select Photo" else "Change Photo", fontWeight = FontWeight.Bold)
        }

        if (selectedUri != null && originalSizeKb > 0) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Original Size: ~ $originalSizeKb KB",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )
        }

        Spacer(Modifier.height(16.dp))

        // --- TARGET SIZE INPUT ---
        OutlinedTextField(
            value = targetKb,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) targetKb = input
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Target Size (KB)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(Modifier.height(24.dp))

        // --- COMPRESS BUTTON ---
        Button(
            onClick = {
                val uri = selectedUri
                val kb = targetKb.toIntOrNull()

                if (uri == null) {
                    status = "⚠️ पहले photo select करें!"
                    return@Button
                }
                if (kb == null || kb < 5) {
                    status = "⚠️ कृपया सही target size (5 या अधिक) दर्ज करें।"
                    return@Button
                }

                isProcessing = true
                status = ""
                compressedBytes = null

                scope.launch {
                    try {
                        val output = compressImage(context, uri, kb)
                        if (output != null) {
                            compressedBytes = output
                            status = "✅ Success! Compressed Size: ${output.size / 1024} KB"
                        } else {
                            status = "❌ Photo process नहीं हो सकी।"
                        }
                    } catch (e: Exception) {
                        status = "❌ Error: ${e.localizedMessage}"
                    } finally {
                        isProcessing = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
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
                Text("Compress Photo", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- STATUS & DOWNLOAD SECTION ---
        if (status.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (status.contains("✅")) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (status.contains("✅")) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (compressedBytes != null) {
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        isProcessing = true
                        val saved = saveToGallery(context, compressedBytes!!)
                        isProcessing = false
                        status = if (saved) {
                            "✅ Photo Gallery में सफलतापूर्वक Save हो गई!"
                        } else {
                            "❌ Photo Save करने में दिक्कत आई।"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = !isProcessing,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save to Gallery", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
        
        Spacer(Modifier.height(30.dp))
    }
}

suspend fun compressImage(
    context: Context,
    uri: Uri,
    targetKb: Int
): ByteArray? = withContext(Dispatchers.IO) {
    val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
    val bitmap = input.use { BitmapFactory.decodeStream(it) } ?: return@withContext null

    var quality = 100
    var result: ByteArray
    var stream: ByteArrayOutputStream

    do {
        stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        result = stream.toByteArray()
        quality -= if (result.size > (targetKb * 1024 * 2)) 10 else 5
    } while (result.size > targetKb * 1024 && quality > 5)

    return@withContext result
}

suspend fun saveToGallery(context: Context, bytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
    try {
        val filename = "Hisaabkit_Resized_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Hisaabkit")
            }
        }
        
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(bytes)
            }
            true
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}
