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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
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
private val PhotoRed = Color(0xFFDC2626)

private val SoftPurple = Color(0xFFF3EDFF)
private val SoftBlue = Color(0xFFECF3FF)
private val SoftGreen = Color(0xFFEAF8F1)
private val SoftOrange = Color(0xFFFFF5E3)
private val SoftRed = Color(0xFFFFEEEE)

@Composable
fun PhotoResizerScreen() {

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var targetWidth by remember {
        mutableIntStateOf(800)
    }

    var targetHeight by remember {
        mutableIntStateOf(800)
    }

    var targetSizeKb by remember {
        mutableStateOf("50")
    }

    var originalWidth by remember {
        mutableIntStateOf(0)
    }

    var originalHeight by remember {
        mutableIntStateOf(0)
    }

    var originalSizeKb by remember {
        mutableIntStateOf(0)
    }

    var outputWidth by remember {
        mutableIntStateOf(0)
    }

    var outputHeight by remember {
        mutableIntStateOf(0)
    }

    var outputSizeKb by remember {
        mutableIntStateOf(0)
    }

    var outputBytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    var quality by remember {
        mutableIntStateOf(85)
    }

    var isProcessing by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var success by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            imageUri = uri
            outputBytes = null
            outputSizeKb = 0
            outputWidth = 0
            outputHeight = 0
            message = ""
            success = false

            scope.launch {

                val info = withContext(Dispatchers.IO) {
                    readImageInfo(uri)
                }

                if (info != null) {

                    originalWidth = info.width
                    originalHeight = info.height
                    originalSizeKb = info.sizeKb

                    val ratio =
                        info.width.toFloat() /
                                info.height.toFloat()

                    if (info.width >= info.height) {

                        targetWidth =
                            minOf(info.width, 1200)

                        targetHeight =
                            (targetWidth / ratio)
                                .roundToInt()
                                .coerceAtLeast(1)

                    } else {

                        targetHeight =
                            minOf(info.height, 1200)

                        targetWidth =
                            (targetHeight * ratio)
                                .roundToInt()
                                .coerceAtLeast(1)
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

        val sizeKb =
            targetSizeKb.toIntOrNull()

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

            val result =
                withContext(Dispatchers.IO) {

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

                outputBytes = result.bytes
                outputSizeKb = result.sizeKb
                outputWidth = result.width
                outputHeight = result.height

                success = true

                message =
                    if (result.sizeKb <= sizeKb) {
                        "Photo successfully तैयार हो गई।"
                    } else {
                        "Photo तैयार है, लेकिन target size से थोड़ी बड़ी है।"
                    }

            } else {

                outputBytes = null
                success = false
                message =
                    "Photo process नहीं हो सकी। दूसरी image try करें।"
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

            val saved =
                withContext(Dispatchers.IO) {
                    saveImageToGallery(bytes)
                }

            if (saved) {
                message =
                    "Photo Gallery में successfully save हो गई।"
                success = true
            } else {
                message =
                    "Photo save नहीं हो सकी।"
                success = false
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
                    RoundedCornerShape(28.dp)
                )
                .padding(22.dp)
        ) {

            Column {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(
                                Color.White.copy(
                                    alpha = 0.18f
                                ),
                                RoundedCornerShape(18.dp)
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Image,
                            contentDescription =
                                null,
                            tint = Color.White,
                            modifier =
                                Modifier.size(32.dp)
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
                            Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                "Resize • Compress • Save",
                            color =
                                Color.White.copy(
                                    alpha = 0.88f
                                ),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(
                    Modifier.height(18.dp)
                )

                Text(
                    text =
                        "20KB, 50KB और custom size photo आसानी से बनाएं ✨",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            Modifier.height(22.dp)
        )

        // SELECT PHOTO

        SectionTitle(
            title = "1. Photo Select करें"
        )

        Spacer(
            Modifier.height(10.dp)
        )

        Button(
            onClick = {
                imagePicker.launch("image/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape =
                RoundedCornerShape(18.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        PhotoPurple
                )
        ) {

            Icon(
                imageVector =
                    Icons.Default.Image,
                contentDescription = null
            )

            Spacer(
                Modifier.width(9.dp)
            )

            Text(
                text =
                    if (imageUri == null)
                        "Choose Photo"
                    else
                        "Change Photo",
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }

        if (imageUri != null) {

            Spacer(
                Modifier.height(16.dp)
            )

            InfoCard(
                title = "Original Photo",
                icon = Icons.Default.Image,
                background = SoftBlue
            ) {

                InfoRow(
                    "Dimensions",
                    "$originalWidth × $originalHeight px"
                )

                InfoRow(
                    "File Size",
                    formatSize(originalSizeKb)
                )

                InfoRow(
                    "Format",
                    "Selected image"
                )
            }

            Spacer(
                Modifier.height(22.dp)
            )

            // SETTINGS

            SectionTitle(
                title = "2. Resize Settings"
            )

            Spacer(
                Modifier.height(10.dp)
            )

            OutlinedTextField(
                value =
                    targetWidth.toString(),
                onValueChange = { value ->

                    value.toIntOrNull()?.let {
                        targetWidth =
                            it.coerceIn(
                                50,
                                4000
                            )
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

            Spacer(
                Modifier.height(10.dp)
            )

            OutlinedTextField(
                value =
                    targetHeight.toString(),
                onValueChange = { value ->

                    value.toIntOrNull()?.let {
                        targetHeight =
                            it.coerceIn(
                                50,
                                4000
                            )
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

            Spacer(
                Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = targetSizeKb,
                onValueChange = {
                    targetSizeKb =
                        it.filter { char ->
                            char.isDigit()
                        }.take(5)
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
                    Text("जैसे 20, 50 या 100")
                }
            )

            Spacer(
                Modifier.height(18.dp)
            )

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(20.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            SoftPurple
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        text =
                            "JPEG Quality: $quality%",
                        fontSize = 15.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Slider(
                        value =
                            quality.toFloat(),
                        onValueChange = {
                            quality =
                                it.roundToInt()
                        },
                        valueRange =
                            30f..100f,
                        steps = 69
                    )

                    Text(
                        text =
                            "High quality = बेहतर clarity\nLow quality = छोटा file size",
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    processImage()
                },
                enabled =
                    !isProcessing,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(58.dp),
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

                    Text(
                        "Processing..."
                    )

                } else {

                    Icon(
                        imageVector =
                            Icons.Default.Compress,
                        contentDescription =
                            null
                    )

                    Spacer(
                        Modifier.width(9.dp)
                    )

                    Text(
                        "Resize & Compress",
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                Modifier.height(9.dp)
            )

            OutlinedButton(
                onClick = {
                    reset()
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                shape =
                    RoundedCornerShape(16.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Refresh,
                    contentDescription =
                        null
                )

                Spacer(
                    Modifier.width(7.dp)
                )

                Text(
                    "Reset",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        // MESSAGE

        if (message.isNotEmpty()) {

            Spacer(
                Modifier.height(14.dp)
            )

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
                                SoftRed
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
                                Icons.Default.Close,
                        contentDescription =
                            null,
                        tint =
                            if (success)
                                PhotoGreen
                            else
                                PhotoRed
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

            Spacer(
                Modifier.height(22.dp)
            )

            InfoCard(
                title = "3. Output Ready",
                icon =
                    Icons.Default.CheckCircle,
                background =
                    SoftGreen
            ) {

                InfoRow(
                    "Dimensions",
                    "$outputWidth × $outputHeight px"
                )

                InfoRow(
                    "Output Size",
                    formatSize(outputSizeKb)
                )

                val target =
                    targetSizeKb.toIntOrNull() ?: 0

                InfoRow(
                    "Target Size",
                    "$target KB"
                )

                if (
                    originalSizeKb > 0 &&
                    outputSizeKb > 0
                ) {

                    val reduction =
                        (
                            100f -
                                (
                                    outputSizeKb.toFloat() /
                                        originalSizeKb.toFloat()
                                ) * 100f
                            )
                            .coerceAtLeast(0f)

                    InfoRow(
                        "Size Reduced",
                        String.format(
                            Locale.US,
                            "%.1f%%",
                            reduction
                        )
                    )
                }

                Spacer(
                    Modifier.height(14.dp)
                )

                Button(
                    onClick = {
                        saveImage()
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                    shape =
                        RoundedCornerShape(17.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                PhotoBlue
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
                        "Save / Download",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            Modifier.height(24.dp)
        )

        // HOW IT WORKS

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
                    "यह tool सबसे पहले आपकी selected photo को read करता है। फिर आपकी दी गई Width और Height के अनुसार image को resize किया जाता है। इसके बाद JPEG compression के जरिए file size कम करने की कोशिश की जाती है।",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(
                text =
                    "20KB या 50KB जैसी limit वाले online forms के लिए यह उपयोगी है। अलग-अलग websites पर required dimensions और file size अलग हो सकते हैं।",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }

        Spacer(
            Modifier.height(14.dp)
        )

        // FORMULA

        InfoCard(
            title =
                "File Size किस formula से तय होता है?",
            icon =
                Icons.Default.Calculate,
            background =
                SoftPurple
        ) {

            Text(
                text =
                    "Photo file size किसी एक fixed formula से निर्धारित नहीं होता। यह मुख्य रूप से इन factors पर निर्भर करता है:",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Bullet(
                "Width × Height यानी total pixels"
            )

            Bullet(
                "JPEG compression quality"
            )

            Bullet(
                "Photo में मौजूद details और colors"
            )

            Bullet(
                "Image format जैसे JPEG या PNG"
            )
        }

        Spacer(
            Modifier.height(14.dp)
        )

        // TIPS

        InfoCard(
            title =
                "20KB / 50KB Photo Tips",
            icon =
                Icons.Default.PhotoSizeSelectLarge,
            background =
                SoftOrange
        ) {

            Bullet(
                "पहले required dimensions देखें।"
            )

            Bullet(
                "Target size में 20 या 50 KB डालें।"
            )

            Bullet(
                "जरूरत पड़ने पर JPEG quality कम करें।"
            )

            Bullet(
                "बहुत ज्यादा compression से image blurry हो सकती है।"
            )

            Bullet(
                "Form upload करने से पहले final photo जरूर check करें।"
            )
        }

        Spacer(
            Modifier.height(14.dp)
        )

        // PRIVACY

        InfoCard(
            title =
                "Privacy & Safety",
            icon =
                Icons.Default.Info,
            background =
                SoftGreen
        ) {

            Text(
                text =
                    "इस implementation में image processing device पर की जाती है। Photo को resize करने के लिए किसी online server पर भेजने की आवश्यकता नहीं है।",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }

        Spacer(
            Modifier.height(24.dp)
        )

        Text(
            text =
                "Frequently Asked Questions",
            fontSize = 21.sp,
            fontWeight =
                FontWeight.ExtraBold
        )

        Spacer(
            Modifier.height(8.dp)
        )

        FaqCard(
            "20KB photo कैसे बनाएं?",
            "Photo select करें, Target File Size में 20 डालें और Resize & Compress दबाएं। जरूरत के अनुसार dimensions और quality adjust करें।"
        )

        FaqCard(
            "50KB photo कैसे बनाएं?",
            "Photo select करके Target File Size में 50 डालें और Resize & Compress दबाएं।"
        )

        FaqCard(
            "क्या photo quality कम होगी?",
            "Compression बढ़ाने पर quality कम हो सकती है। इसलिए file size और image clarity के बीच balance रखें।"
        )

        FaqCard(
            "Photo dimensions क्या होते हैं?",
            "Dimensions image की width और height होती हैं, जिन्हें pixels में मापा जाता है। उदाहरण: 800 × 800 px।"
        )

        FaqCard(
            "क्या tool offline काम कर सकता है?",
            "Image processing इस implementation में device पर होती है। Internet की आवश्यकता image processing के लिए नहीं है।"
        )

        FaqCard(
            "क्या 50KB हर online form के लिए सही है?",
            "नहीं। हर form की अलग-अलग photo size, dimensions और format requirements हो सकती हैं। संबंधित official instructions जरूर देखें।"
        )

        Spacer(
            Modifier.height(30.dp)
        )
    }
}

// =====================================================
// DATA
// =====================================================

private data class ImageInfo(
    val width: Int,
    val height: Int,
    val sizeKb: Int
)

private data class CompressionResult(
    val bytes: ByteArray,
    val sizeKb: Int,
    val width: Int,
    val height: Int
)

// =====================================================
// IMAGE INFORMATION
// =====================================================

private fun readImageInfo(
    uri: Uri
): ImageInfo? {

    return try {

        val context =
            AppContextHolder.context
                ?: return null

        val inputStream =
            context.contentResolver
                .openInputStream(uri)
                ?: return null

        val bytes =
            inputStream.use {
                it.readBytes()
            }

        if (bytes.isEmpty()) {
            return null
        }

        val options =
            BitmapFactory.Options()

        options.inJustDecodeBounds = true

        BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            options
        )

        if (
            options.outWidth <= 0 ||
            options.outHeight <= 0
        ) {
            return null
        }

        ImageInfo(
            width = options.outWidth,
            height = options.outHeight,
            sizeKb =
                ((bytes.size + 1023) / 1024)
        )

    } catch (_: Exception) {
        null
    }
}

// =====================================================
// COMPRESS IMAGE
// =====================================================

private fun compressImageToTarget(
    uri: Uri,
    width: Int,
    height: Int,
    targetKb: Int,
    initialQuality: Int
): CompressionResult? {

    return try {

        val context =
            AppContextHolder.context
                ?: return null

        val inputStream =
            context.contentResolver
                .openInputStream(uri)
                ?: return null

        val original =
            inputStream.use {
                BitmapFactory.decodeStream(it)
            }

        if (original == null) {
            return null
        }

        val safeWidth =
            width.coerceIn(50, 4000)

        val safeHeight =
            height.coerceIn(50, 4000)

        val resized =
            original.scale(
                safeWidth,
                safeHeight
            )

        if (resized.width <= 0 || resized.height <= 0) {
            original.recycle()
            return null
        }

        val targetBytes =
            targetKb.coerceIn(
                1,
                10240
            ) * 1024

        var quality =
            initialQuality.coerceIn(
                30,
                100
            )

        var bestBytes: ByteArray? = null
        var bestDifference =
            Long.MAX_VALUE

        while (quality >= 30) {

            val stream =
                ByteArrayOutputStream()

            resized.compress(
                Bitmap.CompressFormat.JPEG,
                quality,
                stream
            )

            val bytes =
                stream.toByteArray()

            stream.close()

            val difference =
                kotlin.math.abs(
                    bytes.size.toLong() -
                        targetBytes.toLong()
                )

            if (difference < bestDifference) {
                bestDifference = difference
                bestBytes = bytes
            }

            if (bytes.size <= targetBytes) {
                bestBytes = bytes
                break
            }

            quality -= 5
        }

        val finalBytes =
            bestBytes

        resized.recycle()
        original.recycle()

        if (
            finalBytes == null ||
            finalBytes.isEmpty()
        ) {
            null
        } else {

            CompressionResult(
                bytes = finalBytes,
                sizeKb =
                    (
                        finalBytes.size + 1023
                        ) / 1024,
                width = safeWidth,
                height = safeHeight
            )
        }

    } catch (_: Exception) {
        null
    }
}

// =====================================================
// SAVE TO GALLERY
// =====================================================

private fun saveImageToGallery(
    bytes: ByteArray
): Boolean {

    return try {

        val context =
            AppContextHolder.context
                ?: return false

        val resolver =
            context.contentResolver

        val fileName =
            "HisaabKit_Photo_${System.currentTimeMillis()}.jpg"

        val values =
            ContentValues().apply {

                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    fileName
                )

                put(
                    MediaStore.Images.Media.MIME_TYPE,
                    "image/jpeg"
                )

                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.Q
                ) {

                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "Pictures/HisaabKit"
                    )

                    put(
                        MediaStore.Images.Media.IS_PENDING,
                        1
                    )
                }
            }

        val uri =
            resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
                ?: return false

        try {

            resolver.openOutputStream(uri)
                ?.use { output ->
                    output.write(bytes)
                    output.flush()
                }
                ?: throw Exception(
                    "Output stream unavailable"
                )

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q
            ) {

                val updateValues =
                    ContentValues().apply {
                        put(
                            MediaStore.Images.Media.IS_PENDING,
                            0
                        )
                    }

                resolver.update(
                    uri,
                    updateValues,
                    null,
                    null
                )
            }

            true

        } catch (e: Exception) {

            resolver.delete(
                uri,
                null,
                null
            )

            false
        }

    } catch (_: Exception) {
        false
    }
}

// =====================================================
// SECTION TITLE
// =====================================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 21.sp,
        fontWeight =
            FontWeight.ExtraBold
    )
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
                .padding(
                    vertical = 5.dp
                ),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            fontSize = 14.sp,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
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
// FORMAT SIZE
// =====================================================

private fun formatSize(
    kb: Int
): String {

    return if (kb < 1024) {
        "$kb KB"
    } else {

        String.format(
            Locale.US,
            "%.2f MB",
            kb / 1024f
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

                Box(
                    modifier =
                        Modifier
                            .size(38.dp)
                            .background(
                                Color.White.copy(
                                    alpha = 0.75f
                                ),
                                RoundedCornerShape(
                                    12.dp
                                )
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint =
                            PhotoPurple,
                        modifier =
                            Modifier.size(21.dp)
                    )
                }

                Spacer(
                    Modifier.width(10.dp)
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
                .padding(
                    vertical = 4.dp
                ),
        verticalAlignment =
            Alignment.Top
    ) {

        Text(
            text = "•",
            color =
                PhotoPurple,
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
                .padding(
                    vertical = 5.dp
                ),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme
                        .surface
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
