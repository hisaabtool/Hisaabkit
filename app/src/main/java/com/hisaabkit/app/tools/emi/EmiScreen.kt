package com.hisaabkit.app.tools.emi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun EmiScreen() {

    var loanAmount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var tenure by remember { mutableStateOf("") }

    var result by remember {
        mutableStateOf<EmiResult?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "EMI Calculator",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = loanAmount,
            onValueChange = { loanAmount = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Loan Amount") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = interestRate,
            onValueChange = { interestRate = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Interest Rate (%)") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = tenure,
            onValueChange = { tenure = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tenure (Years)") },
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {

                val amount = loanAmount.toDoubleOrNull()
                val rate = interestRate.toDoubleOrNull()
                val years = tenure.toIntOrNull()

                if (
                    amount != null &&
                    rate != null &&
                    years != null &&
                    amount > 0 &&
                    rate >= 0 &&
                    years > 0
                ) {
                    result = calculateEmi(
                        principal = amount,
                        annualRate = rate,
                        years = years
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate EMI")
        }

        Spacer(Modifier.height(20.dp))

        result?.let { data ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Monthly EMI",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = "₹${formatMoney(data.monthlyEmi)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Total Interest: ₹${formatMoney(data.totalInterest)}"
                    )

                    Text(
                        "Total Payment: ₹${formatMoney(data.totalPayment)}"
                    )
                }
            }
        }
    }
}

private fun formatMoney(value: Double): String {
    return String.format(
        Locale.US,
        "%,.2f",
        value
    )
}
