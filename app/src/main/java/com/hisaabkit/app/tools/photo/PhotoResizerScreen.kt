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
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.material3.Switch
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

@Composable
fun PhotoResizerScreen() {

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var targetWidth by remember { mutableIntStateOf(800) }
    var targetHeight by remember { mutableIntStateOf(800) }

    var targetSizeText by remember { mutableStateOf("50") }

    var originalWidth by remember { mutableIntStateOf(0) }
    var originalHeight by remember { mutableIntStateOf(0) }
    var originalSizeKb by remember { mutableIntStateOf(0) }

    var outputWidth by remember { mutableIntStateOf(0) }
    var outputHeight by remember { mutableIntStateOf(0) }
    var outputSizeKb by remember { mutableIntStateOf(0) }

    var outputBytes by remember { mutableStateOf<ByteArray?>(null) }

    var quality by remember { mutableIntStateOf(85) }

    var keepRatio by remember { mutableStateOf(true) }

    var isProcessing by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
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

                val info = withContext(Dispatchers.IO) {
                    readImageInfo(context, uri)
                }

                if (info != null) {

                    originalWidth = info.width
                    originalHeight = info.height
                    originalSizeKb = info.sizeKb

                    val maxDimension = 1200

                    if (info.width >= info.height) {

                        targetWidth =
                            minOf(info.width, maxDimension)

                        targetHeight =
                            if (info.width > 0) {
                                (
                                    info.height.toFloat() *
                                        targetWidth.toFloat() /
                                        info.width.toFloat()
                                    ).roundToInt()
                            } else {
                                info.height
                            }

                    } else {

                        targetHeight =
                            minOf(info.height, maxDimension)

                        targetWidth =
                            if (info.height > 0) {
                                (
                                    info.width.toFloat() *
                                        targetHeight.toFloat() /
                                        info.height.toFloat()
                                    ).roundToInt()
                            } else {
                                info.width
                            }
                    }

                    targetWidth =
                        targetWidth.coerceAtLeast(50)

                    targetHeight =
                        targetHeight.coerceAtLeast(50)
                }
            }
        }

    fun reset() {

        imageUri = null

        targetWidth = 800
        targetHeight = 800

        targetSizeText = "50"

        originalWidth = 0
        originalHeight = 0
        originalSizeKb = 0

        outputWidth = 0
        outputHeight = 0
        outputSizeKb = 0

        outputBytes = null

        quality = 85
        keepRatio = true

        isProcessing = false
        message = ""
        success = false
    }

    fun processImage() {

        val uri = imageUri

        if (uri == null) {

            message = "पहले कोई photo select करें।"
            success = false

            return
        }

        val targetKb =
            targetSizeText
                .trim()
                .toIntOrNull()

        if (targetKb == null || targetKb < 5) {

            message =
                "Target size कम से कम 5 KB रखें।"

            success = false

            return
        }

        if (targetWidth < 50 || targetHeight < 50) {

            message =
                "Width और Height कम से कम 50 px रखें।"

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
                        context = context,
                        uri = uri,
                        requestedWidth = targetWidth,
                        requestedHeight = targetHeight,
                        targetKb = targetKb,
                        initialQuality = quality,
                        keepAspectRatio = keepRatio,
                        originalWidth = originalWidth,
                        originalHeight = originalHeight
                    )
                }

            isProcessing = false

            if (result != null) {

                outputBytes = result.bytes

                outputWidth = result.width
                outputHeight = result.height

                outputSizeKb =
                    bytesToKb(result.bytes.size)

                success = true

                message =
                    if (result.bytes.size <= targetKb * 1024) {
                        "Photo तैयार है और target size के अंदर है।"
                    } else {
                        "Photo तैयार है। बेहतर quality के लिए size target से थोड़ा ऊपर हो सकता है।"
                    }

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
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {

        // ------------------------------------------------
        // HEADER
        // ------------------------------------------------

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
                            .size(56.dp)
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
                                Icons.Default.PhotoSizeSelectLarge,
                            contentDescription = null,
                            tint = Color.White,
                            modifier =
                                Modifier.size(31.dp)
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
                    Modifier.height(17.dp)
                )

                Text(
                    text =
                        "Form upload के लिए photo को सही size में तैयार करें ✨",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            Modifier.height(20.dp)
        )

        // ------------------------------------------------
        // SELECT PHOTO
        // ------------------------------------------------

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
            shape = RoundedCornerShape(18.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = PhotoPurple
                )
        ) {

            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null
            )

            Spacer(
                Modifier.width(8.dp)
            )

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

        // ------------------------------------------------
        // ORIGINAL INFO
        // ------------------------------------------------

        if (imageUri != null) {

            Spacer(
                Modifier.height(15.dp)
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
                    formatKb(originalSizeKb)
                )

                InfoRow(
                    "Format",
                    "Image"
                )
            }

            Spacer(
                Modifier.height(20.dp)
            )

            // ------------------------------------------------
            // SETTINGS
            // ------------------------------------------------

            SectionTitle(
                title = "2. Resize & Compress"
            )

            Spacer(
                Modifier.height(10.dp)
            )

            // WIDTH

            OutlinedTextField(
                value =
                    targetWidth.toString(),

                onValueChange = { value ->

                    val number =
                        value.toIntOrNull()

                    if (number != null) {

                        targetWidth =
                            number.coerceIn(
                                50,
                                4000
                            )

                        if (keepRatio &&
                            originalWidth > 0 &&
                            originalHeight > 0
                        ) {

                            targetHeight =
                                (
                                    originalHeight.toFloat() *
                                        targetWidth.toFloat() /
                                        originalWidth.toFloat()
                                    ).roundToInt()
                                    .coerceIn(
                                        50,
                                        4000
                                    )
                        }
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

            // HEIGHT

            OutlinedTextField(
                value =
                    targetHeight.toString(),

                onValueChange = { value ->

                    val number =
                        value.toIntOrNull()

                    if (number != null) {

                        targetHeight =
                            number.coerceIn(
                                50,
                                4000
                            )

                        if (keepRatio &&
                            originalWidth > 0 &&
                            originalHeight > 0
                        ) {

                            targetWidth =
                                (
                                    originalWidth.toFloat() *
                                        targetHeight.toFloat() /
                                        originalHeight.toFloat()
                                    ).roundToInt()
                                    .coerceIn(
                                        50,
                                        4000
                                    )
                        }
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

            // ASPECT RATIO

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(18.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            SoftPurple
                    )
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 15.dp,
                                vertical = 11.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.SwapHoriz,
                        contentDescription =
                            null,
                        tint =
                            PhotoPurple
                    )

                    Spacer(
                        Modifier.width(10.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Maintain Aspect Ratio",
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Photo को stretch होने से बचाएं",
                            fontSize = 12.sp,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = keepRatio,
                        onCheckedChange = {
                            keepRatio = it
                        }
                    )
                }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            // TARGET SIZE

            OutlinedTextField(
                value =
                    targetSizeText,

                onValueChange = {
                    targetSizeText =
                        it.filter { char ->
                            char.isDigit()
                        }
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
                },

                supportingText = {
                    Text(
                        "Example: 50 KB"
                    )
                }
            )

            Spacer(
                Modifier.height(16.dp)
            )

            // QUALITY

            Text(
                text =
                    "Starting JPEG Quality: $quality%",
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
                    40f..100f,

                steps = 59
            )

            Text(
                text =
                    "Tool target size पाने के लिए quality और dimensions दोनों को automatically adjust करता है।",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                Modifier.height(17.dp)
            )

            // PROCESS

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
                        Modifier.width(10.dp)
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
                    text = "Reset",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        // ------------------------------------------------
        // MESSAGE
        // ------------------------------------------------

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

        // ------------------------------------------------
        // RESULT
        // ------------------------------------------------

        if (outputBytes != null) {

            Spacer(
                Modifier.height(20.dp)
            )

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
                            tint =
                                PhotoGreen,
                            modifier =
                                Modifier.size(27.dp)
                        )

                        Spacer(
                            Modifier.width(9.dp)
                        )

                        Text(
                            text =
                                "Photo Ready",
                            color =
                                PhotoGreen,
                            fontSize = 20.sp,
                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }

                    Spacer(
                        Modifier.height(13.dp)
                    )

                    InfoRow(
                        "Output Dimensions",
                        "$outputWidth × $outputHeight px"
                    )

                    InfoRow(
                        "Output Size",
                        formatKb(outputSizeKb)
                    )

                    val target =
                        targetSizeText
                            .toIntOrNull()
                            ?: 0

                    InfoRow(
                        "Target Size",
                        "$target KB"
                    )

                    Spacer(
                        Modifier.height(15.dp)
                    )

                    Button(
                        onClick = {

                            val bytes =
                                outputBytes

                            if (bytes == null) {
                                return@Button
                            }

                            scope.launch {

                                val saved =
                                    withContext(
                                        Dispatchers.IO
                                    ) {
                                        saveImageToGallery(
                                            context,
                                            bytes
                                        )
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
                            text =
                                "Save to Gallery",
                            fontSize = 16.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(
            Modifier.height(22.dp)
        )

        // ------------------------------------------------
        // HOW IT WORKS
        // ------------------------------------------------

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
                    "यह tool पहले आपकी photo के original dimensions और file size को पढ़ता है। इसके बाद चुनी गई width और height के अनुसार image को resize किया जाता है।",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(
                Modifier.height(9.dp)
            )

            Text(
                text =
                    "इसके बाद JPEG compression की मदद से file size कम किया जाता है। Target KB पाने के लिए tool quality और जरूरत पड़ने पर dimensions को adjust करता है।",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }

        Spacer(
            Modifier.height(13.dp)
        )

        InfoCard(
            title =
                "File Size किन चीजों पर निर्भर करता है?",
            icon =
                Icons.Default.PhotoSizeSelectLarge,
            background =
                SoftPurple
        ) {

            Bullet(
                "Image की width और height"
            )

            Bullet(
                "JPEG compression quality"
            )

            Bullet(
                "Photo में मौजूद details और colors"
            )

            Bullet(
                "Image का original format"
            )
        }

        Spacer(
            Modifier.height(13.dp)
        )

        InfoCard(
            title =
                "20KB / 50KB Photo बनाने का तरीका",
            icon =
                Icons.Default.Compress,
            background =
                SoftOrange
        ) {

            Bullet(
                "पहले Photo select करें।"
            )

            Bullet(
                "Target File Size में 20 या 50 डालें।"
            )

            Bullet(
                "जरूरत के अनुसार dimensions रखें।"
            )

            Bullet(
                "Maintain Aspect Ratio ON रखने की सलाह है।"
            )

            Bullet(
                "Resize & Compress दबाएं।"
            )

            Bullet(
                "Result मिलने के बाद Save to Gallery दबाएं।"
            )
        }

        Spacer(
            Modifier.height(13.dp)
        )

        InfoCard(
            title =
                "Privacy",
            icon =
                Icons.Default.CheckCircle,
            background =
                SoftGreen
        ) {

            Text(
                text =
                    "Image processing device पर होती है। Photo को किसी online server पर upload करने की आवश्यकता नहीं है।",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }

        Spacer(
            Modifier.height(20.dp)
        )

        SectionTitle(
            title =
                "Frequently Asked Questions"
        )

        Spacer(
            Modifier.height(7.dp)
        )

        FaqCard(
            "20KB photo कैसे बनाएं?",
            "Photo select करें, Target File Size में 20 KB डालें और Resize & Compress दबाएं।"
        )

        FaqCard(
            "50KB photo कैसे बनाएं?",
            "Target File Size में 50 KB डालकर Resize & Compress करें। Result के बाद Save to Gallery दबाएं।"
        )

        FaqCard(
            "क्या photo की quality खराब होगी?",
            "बहुत ज्यादा compression करने पर quality कम हो सकती है। Tool target size और image quality के बीच balance बनाने की कोशिश करता है।"
        )

        FaqCard(
            "Maintain Aspect Ratio क्या है?",
            "यह option width और height का original अनुपात बनाए रखता है, जिससे photo खिंची हुई या दबाई हुई नहीं दिखती।"
        )

        FaqCard(
            "क्या photo Gallery में save होगी?",
            "हाँ। Processing के बाद Save to Gallery button दबाने पर JPEG image device की Pictures/HisaabKit folder में save की जाती है।"
        )

        FaqCard(
            "क्या यह tool offline काम कर सकता है?",
            "हाँ। Image processing device पर होती है और इसके लिए image को online server पर भेजना जरूरी नहीं है।"
        )

        FaqCard(
            "क्या हर सरकारी form में 20KB photo चलेगी?",
            "नहीं। अलग-अलग forms में अलग file size, dimensions और format की requirements हो सकती हैं। हमेशा संबंधित form की official requirements देखें।"
        )

        Spacer(
            Modifier.height(30.dp)
        )
    }
}

// ======================================================
// DATA CLASS
// ======================================================

private data class ImageInfo(
    val width: Int,
    val height: Int,
    val sizeKb: Int
)

private data class CompressionResult(
    val bytes: ByteArray,
    val width: Int,
    val height: Int
)

// ======================================================
// IMAGE INFO
// ======================================================

private fun readImageInfo(
    context: Context,
    uri: Uri
): ImageInfo? {

    return try {

        val input =
            context.contentResolver
                .openInputStream(uri)
                ?: return null

        val options =
            BitmapFactory.Options()

        options.inJustDecodeBounds = true

        BitmapFactory.decodeStream(
            input,
            null,
            options
        )

        input.close()

        if (
            options.outWidth <= 0 ||
            options.outHeight <= 0
        ) {
            return null
        }

        val sizeBytes =
            context.contentResolver
                .openAssetFileDescriptor(
                    uri,
                    "r"
                )
                ?.use {
                    it.length
                }
                ?: 0L

        ImageInfo(
            width = options.outWidth,
            height = options.outHeight,
            sizeKb =
                bytesToKb(
                    sizeBytes.toInt()
                )
        )

    } catch (_: Exception) {
        null
    }
}

// ======================================================
// SMART COMPRESSION
// ======================================================

private fun compressImageToTarget(
    context: Context,
    uri: Uri,
    requestedWidth: Int,
    requestedHeight: Int,
    targetKb: Int,
    initialQuality: Int,
    keepAspectRatio: Boolean,
    originalWidth: Int,
    originalHeight: Int
): CompressionResult? {

    return try {

        val input =
            context.contentResolver
                .openInputStream(uri)
                ?: return null

        val original =
            BitmapFactory.decodeStream(input)

        input.close()

        original ?: return null

        val targetBytes =
            targetKb.toLong() * 1024L

        var width =
            requestedWidth.coerceIn(
                50,
                4000
            )

        var height =
            requestedHeight.coerceIn(
                50,
                4000
            )

        if (
            keepAspectRatio &&
            originalWidth > 0 &&
            originalHeight > 0
        ) {

            val ratio =
                originalWidth.toFloat() /
                    originalHeight.toFloat()

            height =
                (
                    width.toFloat() / ratio
                )
                    .roundToInt()
                    .coerceIn(
                        50,
                        4000
                    )
        }

        var bestBytes: ByteArray? = null
        var bestWidth = width
        var bestHeight = height

        var currentWidth = width
        var currentHeight = height

        var currentQuality =
            initialQuality.coerceIn(
                40,
                100
            )

        repeat(9) {

            val bitmap =
                if (
                    original.width == currentWidth &&
                    original.height == currentHeight
                ) {
                    original
                } else {
                    Bitmap.createScaledBitmap(
                        original,
                        currentWidth,
                        currentHeight,
                        true
                    )
                }

            val result =
                compressBitmap(
                    bitmap,
                    currentQuality
                )

            if (bitmap !== original) {
                bitmap.recycle()
            }

            if (bestBytes == null) {
                bestBytes = result
                bestWidth = currentWidth
                bestHeight = currentHeight
            }

            if (result.size <= targetBytes) {

                bestBytes = result
                bestWidth = currentWidth
                bestHeight = currentHeight

                if (
                    targetBytes -
                        result.size <
                        targetBytes / 10
                ) {
                    return@repeat
                }

                currentQuality =
                    minOf(
                        100,
                        currentQuality + 3
                    )

            } else {

                if (currentQuality > 45) {

                    currentQuality -= 8

                } else {

                    currentWidth =
                        (
                            currentWidth * 0.90f
                        )
                            .roundToInt()
                            .coerceAtLeast(50)

                    currentHeight =
                        (
                            currentHeight * 0.90f
                        )
                            .roundToInt()
                            .coerceAtLeast(50)
                }
            }
        }

        // Final smaller-size attempt if needed

        if (
            bestBytes == null ||
            bestBytes!!.size > targetBytes
        ) {

            var finalWidth =
                bestWidth

            var finalHeight =
                bestHeight

            repeat(8) {

                finalWidth =
                    (
                        finalWidth * 0.85f
                    )
                        .roundToInt()
                        .coerceAtLeast(50)

                finalHeight =
                    (
                        finalHeight * 0.85f
                    )
                        .roundToInt()
                        .coerceAtLeast(50)

                val bitmap =
                    Bitmap.createScaledBitmap(
                        original,
                        finalWidth,
                        finalHeight,
                        true
                    )

                val bytes =
                    compressBitmap(
                        bitmap,
                        45
                    )

                bitmap.recycle()

                if (
                    bytes.size <= targetBytes
                ) {

                    bestBytes = bytes
                    bestWidth = finalWidth
                    bestHeight = finalHeight

                    break
                }

                bestBytes = bytes
                bestWidth = finalWidth
                bestHeight = finalHeight
            }
        }

        original.recycle()

        bestBytes?.let {

            CompressionResult(
                bytes = it,
                width = bestWidth,
                height = bestHeight
            )
        }

    } catch (_: Exception) {
        null
    }
}

// ======================================================
// JPEG COMPRESSION
// ======================================================

private fun compressBitmap(
    bitmap: Bitmap,
    quality: Int
): ByteArray {

    val stream =
        java.io.ByteArrayOutputStream()

    bitmap.compress(
        Bitmap.CompressFormat.JPEG,
        quality.coerceIn(
            30,
            100
        ),
        stream
    )

    return stream.toByteArray()
}

// ======================================================
// SAVE TO GALLERY
// ======================================================

private fun saveImageToGallery(
    context: Context,
    bytes: ByteArray
): Boolean {

    return try {

        val resolver =
            context.contentResolver

        val fileName =
            "HisaabKit_${System.currentTimeMillis()}.jpg"

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

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

            resolver
                .openOutputStream(uri)
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

                val update =
                    ContentValues().apply {

                        put(
                            MediaStore.Images.Media.IS_PENDING,
                            0
                        )
                    }

                resolver.update(
                    uri,
                    update,
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

// ======================================================
// HELPERS
// ======================================================

private fun bytesToKb(
    bytes: Int
): Int {

    return if (bytes <= 0) {
        0
    } else {
        ((bytes + 1023) / 1024)
    }
}

private fun formatKb(
    kb: Int
): String {

    return if (kb >= 1024) {

        String.format(
            "%.2f MB",
            kb / 1024.0
        )

    } else {

        "$kb KB"
    }
}

// ======================================================
// SECTION TITLE
// ======================================================

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

// ======================================================
// INFO ROW
// ======================================================

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
                    vertical = 4.dp
                ),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = title,
            fontSize = 14.sp,
            color =
                MaterialTheme
                    .colorScheme
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

// ======================================================
// INFO CARD
// ======================================================

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
                                    alpha = 0.65f
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription =
                            null,
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
                    fontSize = 17.sp,
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

// ======================================================
// BULLET
// ======================================================

@Composable
private fun Bullet(
    text: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 3.dp
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

// ======================================================
// FAQ
// ======================================================

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
                    MaterialTheme
                        .colorScheme
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
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}
