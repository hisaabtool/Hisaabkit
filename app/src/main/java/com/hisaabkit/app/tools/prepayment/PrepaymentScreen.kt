package com.hisaabkit.app.tools.prepayment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.pow

@Composable
fun PrepaymentScreen() {

    var principal by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var years by remember { mutableStateOf("") }
    var prepayment by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<PrepaymentResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "EMI Prepayment",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = principal,
            onValueChange = { principal = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Outstanding Loan") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = rate,
            onValueChange = { rate = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Interest Rate (%)") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = years,
            onValueChange = { years = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Remaining Tenure (Years)") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = prepayment,
            onValueChange = { prepayment = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Prepayment Amount") },
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {

                val p = principal.toDoubleOrNull()
                val r = rate.toDoubleOrNull()
                val y = years.toIntOrNull()
                val pay = prepayment.toDoubleOrNull()

                if (
                    p != null &&
                    r != null &&
                    y != null &&
                    pay != null &&
                    p > 0 &&
                    r >= 0 &&
                    y > 0 &&
                    pay > 0 &&
                    pay < p
                ) {

                    result = calculatePrepayment(p, r, y, pay)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Savings")
        }

        Spacer(Modifier.height(20.dp))

        result?.let {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text("Estimated New EMI")

                    Text(
                        "₹${money(it.newEmi)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Interest Saving: ₹${money(it.interestSaving)}"
                    )

                    Text(
                        "New Loan Balance: ₹${money(it.newBalance)}"
                    )
                }
            }
        }
    }
}

data class PrepaymentResult(
    val newEmi: Double,
    val interestSaving: Double,
    val newBalance: Double
)

private fun calculatePrepayment(
    principal: Double,
    annualRate: Double,
    years: Int,
    prepayment: Double
): PrepaymentResult {

    val oldBalance = principal
    val newBalance = principal - prepayment
    val months = years * 12

    val monthlyRate = annualRate / 12.0 / 100.0

    val oldEmi =
        if (monthlyRate == 0.0) {
            oldBalance / months
        } else {
            val factor = (1 + monthlyRate).pow(months)
            oldBalance * monthlyRate * factor / (factor - 1)
        }

    val newEmi =
        if (monthlyRate == 0.0) {
            newBalance / months
        } else {
            val factor = (1 + monthlyRate).pow(months)
            newBalance * monthlyRate * factor / (factor - 1)
        }

    val oldInterest = oldEmi * months - oldBalance
    val newInterest = newEmi * months - newBalance

    return PrepaymentResult(
        newEmi = newEmi,
        interestSaving = oldInterest - newInterest,
        newBalance = newBalance
    )
}

private fun money(value: Double): String =
    String.format(Locale.US, "%,.2f", value)
