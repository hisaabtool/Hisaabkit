package com.hisaabkit.app.tools.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min

@Composable
fun PhotoResizerScreen() {

    val context = androidx.compose.ui.platform.LocalContext.current

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var targetKb by remember { mutableStateOf("50") }
    var status by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
        status = if (uri != null) {
            "Photo selected"
        } else {
            ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Photo Resizer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Photo को लगभग target KB तक compress करें।",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                picker.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Photo")
        }

        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = targetKb,
            onValueChange = { targetKb = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Target Size (KB)") },
            singleLine = true
        )

        Spacer(Modifier.height(14.dp))

        Button(
            onClick = {

                val uri = selectedUri
                val kb = targetKb.toIntOrNull()

                if (uri == null) {
                    status = "पहले photo select करें"
                    return@Button
                }

                if (kb == null || kb < 5) {
                    status = "Valid KB डालें"
                    return@Button
                }

                try {

                    val output = compressImage(
                        context = context,
                        uri = uri,
                        targetKb = kb
                    )

                    if (output != null) {
                        status =
                            "Photo compressed: ${output.size / 1024} KB"
                    } else {
                        status = "Photo process नहीं हो सकी"
                    }

                } catch (e: Exception) {
                    status = "Photo process करने में error आया"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compress Photo")
        }

        Spacer(Modifier.height(20.dp))

        if (status.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(18.dp)
                )
            }
        }
    }
}

private fun compressImage(
    context: Context,
    uri: Uri,
    targetKb: Int
): ByteArray? {

    val input = context.contentResolver.openInputStream(uri)
        ?: return null

    val bitmap = input.use {
        BitmapFactory.decodeStream(it)
    } ?: return null

    var quality = 95
    var result: ByteArray

    do {

        val stream = ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            quality,
            stream
        )

        result = stream.toByteArray()

        quality -= 5

    } while (
        result.size > targetKb * 1024 &&
        quality >= 10
    )

    return result
}
