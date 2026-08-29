package com.hisaabkit.app.tools.bike

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
import com.hisaabkit.app.tools.emi.calculateEmi
import java.util.Locale

@Composable
fun BikeEmiScreen() {

    var price by remember { mutableStateOf("") }
    var downPayment by remember { mutableStateOf("") }
    var interest by remember { mutableStateOf("") }
    var tenure by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<com.hisaabkit.app.tools.emi.EmiResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Bike Loan EMI",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Bike Price") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = downPayment,
            onValueChange = { downPayment = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Down Payment") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = interest,
            onValueChange = { interest = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Interest Rate (%)") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = tenure,
            onValueChange = { tenure = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Loan Tenure (Years)") },
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {

                val bikePrice = price.toDoubleOrNull()
                val down = downPayment.toDoubleOrNull() ?: 0.0
                val rate = interest.toDoubleOrNull()
                val years = tenure.toIntOrNull()

                if (
                    bikePrice != null &&
                    rate != null &&
                    years != null &&
                    bikePrice > down &&
                    rate >= 0 &&
                    years > 0
                ) {

                    result = calculateEmi(
                        principal = bikePrice - down,
                        annualRate = rate,
                        years = years
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Bike EMI")
        }

        Spacer(Modifier.height(20.dp))

        result?.let {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text("Monthly EMI")

                    Text(
                        "₹${money(it.monthlyEmi)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(10.dp))

                    Text("Total Interest: ₹${money(it.totalInterest)}")

                    Text("Total Payment: ₹${money(it.totalPayment)}")
                }
            }
        }
    }
}

private fun money(value: Double): String =
    String.format(Locale.US, "%,.2f", value)
