package com.hisaabkit.app.tools.photo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun PhotoResizerScreen() {

    val context = androidx.compose.ui.platform.LocalContext.current

    var selectedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var previewBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var compressedBytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    var selectedSize by remember {
        mutableIntStateOf(50)
    }

    var customSize by remember {
        mutableStateOf("")
    }

    var status by remember {
        mutableStateOf("Photo select करें")
    }

    var savedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedUri = uri
                compressedBytes = null
                savedUri = null

                previewBitmap = loadBitmap(
                    context = context,
                    uri = uri
                )

                status = if (previewBitmap != null) {
                    "Photo selected successfully"
                } else {
                    "Photo open नहीं हो सकी"
                }
            }
        }

    val targetKb =
        if (selectedSize == 0) {
            customSize.toIntOrNull() ?: 50
        } else {
            selectedSize
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "Photo Resizer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Photo को अपनी जरूरत के अनुसार छोटा करें।",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {
                imagePicker.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📷 Select Photo")
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Target Size",
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = selectedSize == 20,
                onClick = {
                    selectedSize = 20
                },
                label = {
                    Text("20 KB")
                }
            )

            FilterChip(
                selected = selectedSize == 50,
                onClick = {
                    selectedSize = 50
                },
                label = {
                    Text("50 KB")
                }
            )

            FilterChip(
                selected = selectedSize == 100,
                onClick = {
                    selectedSize = 100
                },
                label = {
                    Text("100 KB")
                }
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = customSize,
            onValueChange = {
                customSize = it
                selectedSize = 0
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Custom Size (KB)")
            },
            placeholder = {
                Text("जैसे 75")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = {

                val uri = selectedUri

                if (uri == null) {
                    status = "पहले photo select करें"
                    return@Button
                }

                if (targetKb < 5) {
                    status = "कम से कम 5 KB डालें"
                    return@Button
                }

                status = "Photo compress हो रही है..."

                compressedBytes = compressImageToTarget(
                    context = context,
                    uri = uri,
                    targetKb = targetKb
                )

                status =
                    if (compressedBytes != null) {
                        "Compression complete"
                    } else {
                        "Photo compress नहीं हो सकी"
                    }

                savedUri = null
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedUri != null
        ) {
            Text("🗜️ Compress Photo")
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        previewBitmap?.let { bitmap ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = "Preview",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        compressedBytes?.let { bytes ->

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Compression Result",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Target: $targetKb KB"
                    )

                    Text(
                        text = "Result: ${formatKb(bytes.size)} KB"
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedButton(
                            onClick = {

                                val uri = saveImageToGallery(
                                    context = context,
                                    imageBytes = bytes
                                )

                                savedUri = uri

                                status =
                                    if (uri != null) {
                                        "Photo Gallery में save हो गई"
                                    } else {
                                        "Photo save नहीं हो सकी"
                                    }
                            }
                        ) {
                            Text("💾 Save")
                        }

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        OutlinedButton(
                            onClick = {

                                shareImage(
                                    context = context,
                                    imageBytes = bytes
                                )
                            }
                        ) {
                            Text("📤 Share")
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (status.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = status,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

private fun loadBitmap(
    context: Context,
    uri: Uri
): Bitmap? {

    return try {

        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->
                BitmapFactory.decodeStream(input)
            }

    } catch (_: Exception) {
        null
    }
}

private fun compressImageToTarget(
    context: Context,
    uri: Uri,
    targetKb: Int
): ByteArray? {

    val bitmap = loadBitmap(
        context = context,
        uri = uri
    ) ?: return null

    var workingBitmap = bitmap

    var result = compressBitmap(
        bitmap = workingBitmap,
        quality = 90
    )

    var quality = 90

    repeat(12) {

        if (result.size <= targetKb * 1024) {
            return result
        }

        quality -= 5

        if (quality >= 10) {

            result = compressBitmap(
                bitmap = workingBitmap,
                quality = quality
            )
        }
    }

    if (result.size <= targetKb * 1024) {
        return result
    }

    var width = workingBitmap.width
    var height = workingBitmap.height

    repeat(8) {

        width = (width * 0.85).roundToInt()
        height = (height * 0.85).roundToInt()

        if (width < 320 || height < 320) {
            return@repeat
        }

        workingBitmap = Bitmap.createScaledBitmap(
            workingBitmap,
            width,
            height,
            true
        )

        quality = 85

        result = compressBitmap(
            bitmap = workingBitmap,
            quality = quality
        )

        repeat(10) {

            if (result.size <= targetKb * 1024) {
                return result
            }

            quality -= 7

            if (quality >= 10) {

                result = compressBitmap(
                    bitmap = workingBitmap,
                    quality = quality
                )
            }
        }

        if (result.size <= targetKb * 1024) {
            return result
        }
    }

    return result
}

private fun compressBitmap(
    bitmap: Bitmap,
    quality: Int
): ByteArray {

    val outputStream = ByteArrayOutputStream()

    bitmap.compress(
        Bitmap.CompressFormat.JPEG,
        quality.coerceIn(10, 100),
        outputStream
    )

    return outputStream.toByteArray()
}

private fun saveImageToGallery(
    context: Context,
    imageBytes: ByteArray
): Uri? {

    return try {

        val resolver = context.contentResolver

        val fileName =
            "HisaabKit_${System.currentTimeMillis()}.jpg"

        val values = ContentValues().apply {

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

        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: return null

        try {

            resolver.openOutputStream(uri)?.use { output ->
                output.write(imageBytes)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val completeValues = ContentValues().apply {
                    put(
                        MediaStore.Images.Media.IS_PENDING,
                        0
                    )
                }

                resolver.update(
                    uri,
                    completeValues,
                    null,
                    null
                )
            }

            uri

        } catch (e: Exception) {

            resolver.delete(
                uri,
                null,
                null
            )

            null
        }

    } catch (_: Exception) {
        null
    }
}

private fun shareImage(
    context: Context,
    imageBytes: ByteArray
) {

    try {

        val uri = saveImageToGallery(
            context = context,
            imageBytes = imageBytes
        ) ?: return

        val shareIntent = Intent(
            Intent.ACTION_SEND
        ).apply {

            type = "image/jpeg"

            putExtra(
                Intent.EXTRA_STREAM,
                uri
            )

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        context.startActivity(
            Intent.createChooser(
                shareIntent,
                "Share Photo"
            )
        )

    } catch (_: Exception) {
    }
}

private fun formatKb(
    bytes: Int
): String {

    return String.format(
        Locale.US,
        "%.1f",
        bytes / 1024.0
    )
}
