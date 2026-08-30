package com.hisaabkit.app.tools.photo

import android.content.ContentValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

// ✅ FIX 1: AppContextHolder import
import com.hisaabkit.app.utils.AppContextHolder

private val PhotoPurple = Color(0xFF7C3AED)
private val PhotoBlue = Color(0xFF2563EB)
private val PhotoGreen = Color(0xFF059669)
private val PhotoRed = Color(0xFFDC2626)

private val SoftPurple = Color(0xFFF3EDFF)
private val SoftBlue = Color(0xFFECF3FF)
private val SoftGreen = Color(0xFFEAF8F1)

@Composable
fun PhotoResizerScreen() {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var targetWidth by remember { mutableIntStateOf(800) }
    var targetHeight by remember { mutableIntStateOf(800) }
    var targetSizeKb by remember { mutableStateOf("50") }

    var originalWidth by remember { mutableIntStateOf(0) }
    var originalHeight by remember { mutableIntStateOf(0) }
    var originalSizeKb by remember { mutableIntStateOf(0) }

    var outputWidth by remember { mutableIntStateOf(0) }
    var outputHeight by remember { mutableIntStateOf(0) }
    var outputSizeKb by remember { mutableIntStateOf(0) }
    var outputBytes by remember { mutableStateOf<ByteArray?>(null) }

    var quality by remember { mutableIntStateOf(85) }
    var isProcessing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        imageUri = uri
        outputBytes = null
        outputSizeKb = 0
        outputWidth = 0
        outputHeight = 0
        message = ""
        success = false

        scope.launch {
            val info = withContext(Dispatchers.IO) { readImageInfo(uri) }
            if (info != null) {
                originalWidth = info.width
                originalHeight = info.height
                originalSizeKb = info.sizeKb

                val ratio = info.width.toFloat() / info.height.toFloat()

                if (info.width >= info.height) {
                    targetWidth = minOf(info.width, 1200)
                    targetHeight = (targetWidth / ratio).roundToInt().coerceAtLeast(1)
                } else {
                    targetHeight = minOf(info.height, 1200)
                    targetWidth = (targetHeight * ratio).roundToInt().coerceAtLeast(1)
                }
            }
        }
    }

    fun reset() {
        imageUri = null
        targetWidth = 800
        targetHeight = 800
        targetSizeKb = "50"
        originalWidth = 0
        originalHeight = 0
        originalSizeKb = 0
        outputWidth = 0
        outputHeight = 0
        outputSizeKb = 0
        outputBytes = null
        quality = 85
        isProcessing = false
        message = ""
        success = false
    }

    fun processImage() {
        val uri = imageUri
        if (uri == null) {
            message = "पहले photo select करें।"
            success = false
            return
        }

        val sizeKb = targetSizeKb.toIntOrNull()
        if (sizeKb == null || sizeKb < 1) {
            message = "Target size सही दर्ज करें।"
            success = false
            return
        }

        if (targetWidth < 50 || targetHeight < 50) {
            message = "Width और Height कम से कम 50 px रखें।"
            success = false
            return
        }

        isProcessing = true
        message = ""
        success = false
        outputBytes = null

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                compressImageToTarget(uri, targetWidth, targetHeight, sizeKb, quality)
            }
            isProcessing = false

            if (result != null) {
                outputBytes = result.bytes
                outputSizeKb = result.sizeKb
                outputWidth = result.width
                outputHeight = result.height
                success = true
                message = if (result.sizeKb <= sizeKb) "Photo successfully तैयार हो गई।"
                          else "Photo तैयार है, लेकिन target size से थोड़ी बड़ी है।"
            } else {
                outputBytes = null
                success = false
                message = "Photo process नहीं हो सकी। दूसरी image try करें।"
            }
        }
    }

    fun saveImage() {
        val bytes = outputBytes
        if (bytes == null) {
            message = "पहले photo को Resize & Compress करें।"
            success = false
            return
        }

        scope.launch {
            val saved = withContext(Dispatchers.IO) { saveImageToGallery(bytes) }
            if (saved) {
                message = "Photo Gallery में successfully save हो गई।"
                success = true
            } else {
                message = "Photo save नहीं हो सकी।"
                success = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(PhotoPurple, PhotoBlue)), RoundedCornerShape(28.dp))
                .padding(22.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Photo Resizer", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(3.dp))
                        Text("Resize • Compress • Save", color = Color.White.copy(alpha = 0.88f), fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text("20KB, 50KB और custom size photo आसानी से बनाएं ✨", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(22.dp))

        // SELECT PHOTO
        SectionTitle("1. Photo Select करें")
        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PhotoPurple)
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(Modifier.width(9.dp))
            Text(if (imageUri == null) "Choose Photo" else "Change Photo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (imageUri != null) {
            Spacer(Modifier.height(16.dp))
            InfoCard("Original Photo", Icons.Default.Image, SoftBlue) {
                InfoRow("Dimensions", "$originalWidth × $originalHeight px")
                InfoRow("File Size", formatSize(originalSizeKb))
            }
            Spacer(Modifier.height(22.dp))

            // SETTINGS
            SectionTitle("2. Resize Settings")
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = targetWidth.toString(),
                onValueChange = { value -> value.toIntOrNull()?.let { targetWidth = it.coerceIn(50, 4000) } },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(16.dp), label = { Text("Width (px)") }
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = targetHeight.toString(),
                onValueChange = { value -> value.toIntOrNull()?.let { targetHeight = it.coerceIn(50, 4000) } },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(16.dp), label = { Text("Height (px)") }
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = targetSizeKb,
                onValueChange = { targetSizeKb = it.filter { char -> char.isDigit() }.take(5) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(16.dp), label = { Text("Target File Size (KB)") },
                placeholder = { Text("जैसे 20, 50 या 100") }
            )
            Spacer(Modifier.height(18.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SoftPurple)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("JPEG Quality: $quality%", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Slider(value = quality.toFloat(), onValueChange = { quality = it.roundToInt() }, valueRange = 30f..100f, steps = 69)
                    Text("High quality = बेहतर clarity\nLow quality = छोटा file size", fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { processImage() },
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PhotoGreen)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(9.dp))
                    Text("Processing...")
                } else {
                    Icon(Icons.Default.Compress, contentDescription = null)
                    Spacer(Modifier.width(9.dp))
                    Text("Resize & Compress", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(9.dp))

            OutlinedButton(
                onClick = { reset() },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(9.dp))
                Text("Reset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = message,
                    color = if (success) PhotoGreen else PhotoRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (outputBytes != null) {
                Spacer(Modifier.height(22.dp))
                SectionTitle("3. Result & Save")
                Spacer(Modifier.height(10.dp))

                InfoCard("Resized Photo", Icons.Default.CheckCircle, SoftGreen) {
                    InfoRow("Dimensions", "$outputWidth × $outputHeight px")
                    InfoRow("File Size", formatSize(outputSizeKb))
                }
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { saveImage() },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PhotoBlue)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(9.dp))
                    Text("Save to Gallery", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}

// ---------------- UI HELPER COMPONENTS ---------------- //

@Composable
fun SectionTitle(title: String) {
    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PhotoPurple)
}

@Composable
fun InfoCard(title: String, icon: ImageVector, background: Color, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = background), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = PhotoPurple)
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PhotoPurple)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

fun formatSize(kb: Int): String {
    return if (kb >= 1024) String.format("%.2f MB", kb / 1024f) else "$kb KB"
}

// ---------------- BACKEND LOGIC (ALL ERRORS FIXED HERE) ---------------- //

data class ImageInfo(val width: Int, val height: Int, val sizeKb: Int)
data class CompressResult(val bytes: ByteArray, val width: Int, val height: Int, val sizeKb: Int)

fun readImageInfo(uri: Uri): ImageInfo? {
    val context = AppContextHolder.currentContext
    return try {
        // ✅ FIX 2: Explicit variable name added (inputStream)
        val bytes = context.contentResolver.openInputStream(uri)?.use { inputStream -> inputStream.readBytes() }
        if (bytes != null) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bitmap != null) {
                val sizeKb = bytes.size / 1024
                ImageInfo(bitmap.width, bitmap.height, sizeKb)
            } else null
        } else null
    } catch (e: Exception) {
        null
    }
}

fun compressImageToTarget(uri: Uri, width: Int, height: Int, targetKb: Int, initialQuality: Int): CompressResult? {
    val context = AppContextHolder.currentContext
    return try {
        // ✅ FIX 2: Explicit variable name added (inputStream)
        val bytes = context.contentResolver.openInputStream(uri)?.use { inputStream -> inputStream.readBytes() } ?: return null
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        
        var currentQuality = initialQuality
        var outputBytes: ByteArray
        var stream: ByteArrayOutputStream
        
        do {
            stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, stream)
            outputBytes = stream.toByteArray()
            currentQuality -= 5
        } while (outputBytes.size / 1024 > targetKb && currentQuality > 10)
        
        CompressResult(outputBytes, width, height, outputBytes.size / 1024)
    } catch (e: Exception) {
        null
    }
}

fun saveImageToGallery(bytes: ByteArray): Boolean {
    val context = AppContextHolder.currentContext
    return try {
        val filename = "Resized_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Hisaabkit")
            }
        }
        
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            // ✅ FIX 3: Explicit 'outputStream' name added so compiler knows where 'write' comes from
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
