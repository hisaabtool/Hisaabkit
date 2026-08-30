package com.hisaabkit.app.tools.photo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Locale
import kotlin.math.roundToInt

private val PhotoPurple = Color(0xFF7C3AED)
private val PhotoBlue = Color(0xFF2563EB)
private val PhotoGreen = Color(0xFF059669)
private val PhotoOrange = Color(0xFFF59E0B)

private val SoftPurple = Color(0xFFF3EDFF)
private val SoftBlue = Color(0xFFECF3FF)
private val SoftGreen = Color(0xFFEAF8F1)
private val SoftOrange = Color(0xFFFFF5E3)

@Composable
fun PhotoResizerScreen() {

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var targetWidth by remember { mutableIntStateOf(800) }
    var targetHeight by remember { mutableIntStateOf(800) }

    var targetSizeKb by remember { mutableStateOf("50") }

    var originalWidth by remember { mutableIntStateOf(0) }
    var originalHeight by remember { mutableIntStateOf(0) }
    var originalSizeKb by remember { mutableIntStateOf(0) }

    var outputSizeKb by remember { mutableIntStateOf(0) }
    var outputBytes by remember { mutableStateOf<ByteArray?>(null) }

    var quality by remember { mutableIntStateOf(85) }

    var isProcessing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                imageUri = uri
                outputBytes = null
                success = false
                message = ""

                scope.launch {

                    val info = withContext(Dispatchers.IO) {
                        readImageInfo(uri)
                    }

                    if (info != null) {

                        originalWidth = info.first
                        originalHeight = info.second
                        originalSizeKb = info.third

                        targetWidth =
                            minOf(info.first, 1200)

                        targetHeight =
                            minOf(info.second, 1200)
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

        outputSizeKb = 0
        outputBytes = null

        quality = 85

        isProcessing = false
        message = ""
        success = false
    }

    fun resizeImage() {

        val uri = imageUri ?: return
        val sizeKb = targetSizeKb.toIntOrNull()

        if (sizeKb == null || sizeKb <= 0) {
            message = "कृपया सही target size दर्ज करें।"
            success = false
            return
        }

        isProcessing = true
        message = ""
        success = false

        scope.launch {

            val result = withContext(Dispatchers.IO) {

                compressImageToTarget(
                    uri = uri,
                    width = targetWidth,
                    height = targetHeight,
                    targetKb = sizeKb,
                    initialQuality = quality
                )
            }

            isProcessing = false

            if (result != null) {

                outputBytes = result
                outputSizeKb =
                    result.size / 1024

                success = true

                message =
                    "Photo successfully resize/compress हो गई।"
            } else {

                success = false

                message =
                    "Photo process नहीं हो सकी। दूसरी image try करें।"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            )
    ) {

        // HEADER

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            PhotoPurple,
                            PhotoBlue
                        )
                    ),
                    RoundedCornerShape(27.dp)
                )
                .padding(22.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.18f),
                            RoundedCornerShape(17.dp)
                        )
                        .padding(14.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Image,
                        contentDescription = null,
                        tint = Color.White,
                        modifier =
                            Modifier.size(30.dp)
                    )
                }

                Spacer(
                    Modifier.width(14.dp)
                )

                Column {

                    Text(
                        text = "Photo Resizer",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Photo का size और dimensions आसानी से बदलें",
                        color =
                            Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Select Photo",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                imagePicker.launch("image/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PhotoPurple
            )
        ) {

            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text =
                    if (imageUri == null)
                        "Choose Photo"
                    else
                        "Change Photo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ORIGINAL IMAGE INFO

        if (imageUri != null) {

            Spacer(Modifier.height(16.dp))

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(20.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            SoftBlue
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(17.dp)
                ) {

                    Text(
                        text =
                            "Original Photo",
                        fontSize = 17.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Spacer(
                        Modifier.height(9.dp)
                    )

                    InfoRow(
                        "Dimensions",
                        "${originalWidth} × ${originalHeight} px"
                    )

                    InfoRow(
                        "File Size",
                        "${originalSizeKb} KB"
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Resize Settings",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value =
                    targetWidth.toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { number ->
                        targetWidth = number
                    }
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true,
                shape =
                    RoundedCornerShape(16.dp),
                label = {
                    Text("Width (px)")
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value =
                    targetHeight.toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { number ->
                        targetHeight = number
                    }
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true,
                shape =
                    RoundedCornerShape(16.dp),
                label = {
                    Text("Height (px)")
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = targetSizeKb,
                onValueChange = {
                    targetSizeKb = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                singleLine = true,
                shape =
                    RoundedCornerShape(16.dp),
                label = {
                    Text("Target File Size (KB)")
                },
                placeholder = {
                    Text("जैसे 20 या 50")
                }
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text =
                    "JPEG Quality: $quality%",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = quality.toFloat(),
                onValueChange = {
                    quality =
                        it.roundToInt()
                },
                valueRange =
                    30f..100f,
                steps = 69
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text =
                    "Quality कम करने से file size कम हो सकता है, लेकिन image quality भी कम हो सकती है।",
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    resizeImage()
                },
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape =
                    RoundedCornerShape(18.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            PhotoGreen
                    )
            ) {

                if (isProcessing) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                    Spacer(
                        Modifier.width(9.dp)
                    )

                    Text("Processing...")
                } else {

                    Icon(
                        imageVector =
                            Icons.Default.Compress,
                        contentDescription = null
                    )

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "Resize & Compress",
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    reset()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape =
                    RoundedCornerShape(16.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(7.dp)
                )

                Text(
                    text = "Reset",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        if (message.isNotEmpty()) {

            Spacer(Modifier.height(14.dp))

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(18.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            if (success)
                                SoftGreen
                            else
                                SoftOrange
                    )
            ) {

                Row(
                    modifier =
                        Modifier.padding(16.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            if (success)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Info,
                        contentDescription =
                            null,
                        tint =
                            if (success)
                                PhotoGreen
                            else
                                PhotoOrange
                    )

                    Spacer(
                        Modifier.width(10.dp)
                    )

                    Text(
                        text = message,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }

        // RESULT

        if (outputBytes != null) {

            Spacer(Modifier.height(20.dp))

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(24.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            SoftGreen
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(20.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CheckCircle,
                            contentDescription =
                                null,
                            tint = PhotoGreen
                        )

                        Spacer(
                            Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Ready",
                            color =
                                PhotoGreen,
                            fontSize = 19.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }

                    Spacer(
                        Modifier.height(12.dp)
                    )

                    InfoRow(
                        "Output Dimensions",
                        "${targetWidth} × ${targetHeight} px"
                    )

                    InfoRow(
                        "Output Size",
                        "$outputSizeKb KB"
                    )

                    Spacer(
                        Modifier.height(14.dp)
                    )

                    Button(
                        onClick = {
                            message =
                                "Image तैयार है। इसे app के storage/download flow से save किया जा सकता है।"
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(16.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    PhotoGreen
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Download,
                            contentDescription =
                                null
                        )

                        Spacer(
                            Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Save / Download",
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ABOUT

        InfoCard(
            title =
                "Photo Resizer कैसे काम करता है?",
            icon =
                Icons.Default.Info,
            background =
                SoftBlue
        ) {

            Text(
                text =
                    "Photo Resizer image की width और height को बदलकर उसके dimensions कम करता है। इसके बाद JPEG compression की मदद से file size भी कम किया जाता है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(
                text =
                    "यह खास तौर पर उन online forms में उपयोगी है जहाँ photo को 20KB, 50KB या किसी निश्चित file size में upload करना होता है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        InfoCard(
            title = "File Size कम कैसे होता है?",
            icon =
                Icons.Default.Compress,
            background =
                SoftPurple
        ) {

            Text(
                text =
                    "File size मुख्य रूप से image के dimensions, JPEG quality और image के अंदर मौजूद details पर निर्भर करता है। Dimensions और quality कम करने से सामान्यतः file size भी कम हो जाता है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        InfoCard(
            title = "20KB / 50KB Photo के लिए Tips",
            icon =
                Icons.Default.PhotoSizeSelectLarge,
            background =
                SoftOrange
        ) {

            Bullet(
                "पहले image के dimensions कम करें।"
            )

            Bullet(
                "फिर JPEG quality adjust करें।"
            )

            Bullet(
                "Target size 20KB या 50KB रखें।"
            )

            Bullet(
                "बहुत ज्यादा compression से photo blurry हो सकती है।"
            )

            Bullet(
                "Form submit करने से पहले photo की readability जरूर check करें।"
            )
        }

        Spacer(Modifier.height(14.dp))

        InfoCard(
            title = "Privacy & Safety",
            icon =
                Icons.Default.Info,
            background =
                SoftGreen
        ) {

            Text(
                text =
                    "Photo processing device पर करना बेहतर है क्योंकि इससे personal images को unnecessary online server पर upload करने की जरूरत नहीं पड़ती। इस tool का उद्देश्य simple photo resizing और compression है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text =
                "Frequently Asked Questions",
            fontSize = 21.sp,
            fontWeight =
                FontWeight.ExtraBold
        )

        Spacer(Modifier.height(8.dp))

        FaqCard(
            "20KB photo कैसे बनाएं?",
            "Photo select करें, target file size में 20 KB डालें और Resize & Compress दबाएं। जरूरत पड़ने पर dimensions और quality adjust करें।"
        )

        FaqCard(
            "50KB photo कैसे बनाएं?",
            "Target File Size में 50 KB डालकर image resize और compress करें।"
        )

        FaqCard(
            "क्या photo quality खराब होगी?",
            "Compression बढ़ाने पर quality कम हो सकती है। इसलिए जरूरत के अनुसार dimensions और JPEG quality का balance रखें।"
        )

        FaqCard(
            "Photo का dimension क्या होता है?",
            "Dimension image की width और height को pixels में बताता है, जैसे 800 × 800 px।"
        )

        FaqCard(
            "क्या यह tool बिना internet काम कर सकता है?",
            "इस implementation में image processing device पर होती है और image को server पर भेजने की आवश्यकता नहीं है।"
        )

        FaqCard(
            "क्या सभी forms के लिए यही photo size सही है?",
            "नहीं। अलग-अलग forms में अलग dimensions, KB limit और format requirements हो सकती हैं। हमेशा संबंधित form की official requirements देखें।"
        )

        Spacer(Modifier.height(30.dp))
    }
}


// =====================================================
// IMAGE INFORMATION
// =====================================================

private fun readImageInfo(
    uri: Uri
): Triple<Int, Int, Int>? {

    return try {

        val context =
            AppContextHolder.context
                ?: return null

        val bytes =
            context.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }
                ?: return null

        val options =
            BitmapFactory.Options()

        options.inJustDecodeBounds = true

        BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            options
        )

        Triple(
            options.outWidth,
            options.outHeight,
            bytes.size / 1024
        )

    } catch (_: Exception) {
        null
    }
}


// =====================================================
// COMPRESS
// =====================================================

private fun compressImageToTarget(
    uri: Uri,
    width: Int,
    height: Int,
    targetKb: Int,
    initialQuality: Int
): ByteArray? {

    return try {

        val context =
            AppContextHolder.context
                ?: return null

        val input =
            context.contentResolver
                .openInputStream(uri)
                ?: return null

        val original =
            BitmapFactory.decodeStream(input)

        input.close()

        original ?: return null

        val safeWidth =
            width.coerceIn(50, 4000)

        val safeHeight =
            height.coerceIn(50, 4000)

        val resized =
            original.scale(
                safeWidth,
                safeHeight
            )

        var quality =
            initialQuality.coerceIn(30, 100)

        var result: ByteArray

        do {

            val stream =
                ByteArrayOutputStream()

            resized.compress(
                Bitmap.CompressFormat.JPEG,
                quality,
                stream
            )

            result = stream.toByteArray()

            stream.close()

            if (
                result.size / 1024 >
                targetKb &&
                quality > 30
            ) {
                quality -= 5
            } else {
                break
            }

        } while (quality >= 30)

        resized.recycle()

        result

    } catch (_: Exception) {
        null
    }
}


// =====================================================
// INFO ROW
// =====================================================

@Composable
private fun InfoRow(
    title: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            fontSize = 14.sp
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}


// =====================================================
// INFO CARD
// =====================================================

@Composable
private fun InfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    content: @Composable () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    background
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PhotoPurple
                )

                Spacer(
                    Modifier.width(9.dp)
                )

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }

            Spacer(
                Modifier.height(12.dp)
            )

            content()
        }
    }
}


// =====================================================
// BULLET
// =====================================================

@Composable
private fun Bullet(
    text: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        verticalAlignment =
            Alignment.Top
    ) {

        Text(
            text = "•",
            color = PhotoPurple,
            fontSize = 18.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.width(8.dp)
        )

        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    }
}


// =====================================================
// FAQ
// =====================================================

@Composable
private fun FaqCard(
    question: String,
    answer: String
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text = question,
                fontSize = 15.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )

            Spacer(
                Modifier.height(7.dp)
            )

            Text(
                text = answer,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}
