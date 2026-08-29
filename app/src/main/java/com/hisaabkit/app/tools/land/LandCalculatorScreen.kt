package com.hisaabkit.app.tools.land

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun LandCalculatorScreen() {

    var bigha by remember { mutableStateOf("") }
    var biswa by remember { mutableStateOf("") }
    var sqftPerBigha by remember { mutableStateOf("27225") }

    var result by remember { mutableStateOf<LandResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Bigha / Biswa Calculator",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "अपने क्षेत्र के अनुसार 1 Bigha का square feet value डालें।",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = bigha,
            onValueChange = { bigha = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Bigha") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = biswa,
            onValueChange = { biswa = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Biswa") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = sqftPerBigha,
            onValueChange = { sqftPerBigha = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("1 Bigha = कितने Sq Ft?") },
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {

                val b = bigha.toDoubleOrNull() ?: 0.0
                val w = biswa.toDoubleOrNull() ?: 0.0
                val sq = sqftPerBigha.toDoubleOrNull()

                if (sq != null && sq > 0 && b >= 0 && w >= 0) {

                    result = calculateLand(
                        bigha = b,
                        biswa = w,
                        sqftPerBigha = sq
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Land")
        }

        Spacer(Modifier.height(20.dp))

        result?.let {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text("Total Bigha")

                    Text(
                        "%.4f".format(Locale.US, it.totalBigha),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Total Area: ${money(it.squareFeet)} Sq Ft"
                    )
                }
            }
        }
    }
}

data class LandResult(
    val totalBigha: Double,
    val squareFeet: Double
)

private fun calculateLand(
    bigha: Double,
    biswa: Double,
    sqftPerBigha: Double
): LandResult {

    val totalBigha = bigha + (biswa / 20.0)

    return LandResult(
        totalBigha = totalBigha,
        squareFeet = totalBigha * sqftPerBigha
    )
}

private fun money(value: Double): String =
    String.format(Locale.US, "%,.2f", value)
