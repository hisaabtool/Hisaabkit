package com.hisaabkit.app.tools.bike

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.pow

private val BikePurple = Color(0xFF7C3AED)
private val BikeBlue = Color(0xFF2563EB)
private val BikeGreen = Color(0xFF059669)
private val BikeOrange = Color(0xFFF59E0B)

private val BikeSoftPurple = Color(0xFFF3EDFF)
private val BikeSoftBlue = Color(0xFFECF3FF)
private val BikeSoftGreen = Color(0xFFE9F8F1)
private val BikeSoftOrange = Color(0xFFFFF5E3)

@Composable
fun BikeEmiScreen() {

    var bikePrice by remember { mutableStateOf("") }
    var downPayment by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var tenure by remember { mutableStateOf("") }

    var loanAmount by remember { mutableStateOf(0.0) }
    var emi by remember { mutableStateOf(0.0) }
    var totalInterest by remember { mutableStateOf(0.0) }
    var totalPayment by remember { mutableStateOf(0.0) }

    var calculated by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun calculateBikeEmi() {

        val price = bikePrice.toDoubleOrNull()
        val down = downPayment.toDoubleOrNull() ?: 0.0
        val rate = interestRate.toDoubleOrNull()
        val years = tenure.toDoubleOrNull()

        if (price == null || price <= 0) {
            errorMessage = "कृपया सही Bike Price दर्ज करें।"
            calculated = false
            return
        }

        if (down < 0 || down > price) {
            errorMessage = "Down Payment Bike Price से ज्यादा नहीं हो सकता।"
            calculated = false
            return
        }

        if (rate == null || rate < 0) {
            errorMessage = "कृपया सही Interest Rate दर्ज करें।"
            calculated = false
            return
        }

        if (years == null || years <= 0) {
            errorMessage = "कृपया सही Loan Tenure दर्ज करें।"
            calculated = false
            return
        }

        loanAmount = price - down

        val months = (years * 12).toInt()

        if (months <= 0) {
            errorMessage = "Tenure कम से कम 1 महीना होना चाहिए।"
            calculated = false
            return
        }

        if (loanAmount == 0.0) {
            emi = 0.0
            totalInterest = 0.0
            totalPayment = 0.0
            calculated = true
            errorMessage = ""
            return
        }

        val monthlyRate = rate / 12.0 / 100.0

        emi = if (monthlyRate == 0.0) {
            loanAmount / months
        } else {

            val factor =
                (1 + monthlyRate).pow(months.toDouble())

            loanAmount *
                    monthlyRate *
                    factor /
                    (factor - 1)
        }

        totalPayment = emi * months
        totalInterest = totalPayment - loanAmount

        calculated = true
        errorMessage = ""
    }

    fun resetCalculator() {

        bikePrice = ""
        downPayment = ""
        interestRate = ""
        tenure = ""

        loanAmount = 0.0
        emi = 0.0
        totalInterest = 0.0
        totalPayment = 0.0

        calculated = false
        errorMessage = ""
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

        // =========================================
        // HEADER
        // =========================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            BikePurple,
                            BikeBlue
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
                            Color.White.copy(
                                alpha = 0.18f
                            ),
                            RoundedCornerShape(17.dp)
                        )
                        .padding(14.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(
                    Modifier.width(14.dp)
                )

                Column {

                    Text(
                        text = "Bike Loan EMI",
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
                            "Bike loan की EMI और कुल खर्च जानें",
                        color =
                            Color.White.copy(
                                alpha = 0.88f
                            ),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // =========================================
        // INPUTS
        // =========================================

        Text(
            text = "Bike & Loan Details",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = bikePrice,
            onValueChange = {
                bikePrice = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Bike Price (₹)")
            },
            placeholder = {
                Text("जैसे 100000")
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = downPayment,
            onValueChange = {
                downPayment = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Down Payment (₹)")
            },
            placeholder = {
                Text("जैसे 20000")
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = interestRate,
            onValueChange = {
                interestRate = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Annual Interest Rate (%)")
            },
            placeholder = {
                Text("जैसे 10.5")
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = tenure,
            onValueChange = {
                tenure = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Loan Tenure (Years)")
            },
            placeholder = {
                Text("जैसे 3")
            }
        )

        if (errorMessage.isNotEmpty()) {

            Spacer(Modifier.height(10.dp))

            Text(
                text = errorMessage,
                color = Color(0xFFD32F2F),
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(16.dp))

        // =========================================
        // CALCULATE
        // =========================================

        Button(
            onClick = {
                calculateBikeEmi()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BikePurple
            )
        ) {

            Icon(
                imageVector =
                    Icons.Default.Calculate,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Calculate Bike EMI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                resetCalculator()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BikeSoftPurple,
                contentColor = BikePurple
            )
        ) {

            Icon(
                imageVector =
                    Icons.Default.Refresh,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Reset",
                fontWeight = FontWeight.Bold
            )
        }

        // =========================================
        // RESULT
        // =========================================

        if (calculated) {

            Spacer(Modifier.height(25.dp))

            Text(
                text = "Your Bike Loan Result",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BikeSoftPurple
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Monthly EMI",
                        color = BikePurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = formatMoney(emi),
                        color = BikePurple,
                        fontSize = 31.sp,
                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(18.dp))

                    ResultRow(
                        "Bike Price",
                        formatMoney(
                            bikePrice.toDoubleOrNull()
                                ?: 0.0
                        )
                    )

                    ResultRow(
                        "Down Payment",
                        formatMoney(
                            downPayment.toDoubleOrNull()
                                ?: 0.0
                        )
                    )

                    ResultRow(
                        "Loan Amount",
                        formatMoney(loanAmount)
                    )

                    ResultRow(
                        "Total Interest",
                        formatMoney(totalInterest)
                    )

                    ResultRow(
                        "Total Payment",
                        formatMoney(totalPayment)
                    )

                    ResultRow(
                        "Tenure",
                        "$tenure Years"
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                MiniCard(
                    modifier =
                        Modifier.weight(1f),
                    title = "Loan Amount",
                    value =
                        formatMoney(loanAmount),
                    background =
                        BikeSoftBlue,
                    icon =
                        Icons.Default.AccountBalance,
                    iconColor =
                        BikeBlue
                )

                MiniCard(
                    modifier =
                        Modifier.weight(1f),
                    title = "Interest",
                    value =
                        formatMoney(totalInterest),
                    background =
                        BikeSoftOrange,
                    icon =
                        Icons.Default.TrendingUp,
                    iconColor =
                        BikeOrange
                )
            }
        }

        // =========================================
        // HOW IT WORKS
        // =========================================

        Spacer(Modifier.height(28.dp))

        InfoSection(
            title =
                "Bike Loan EMI Calculator कैसे काम करता है?",
            icon =
                Icons.Default.Info,
            background =
                BikeSoftBlue
        ) {

            Text(
                text =
                    "यह calculator आपकी bike की कीमत और down payment से पहले loan amount निकालता है। इसके बाद interest rate और tenure के आधार पर monthly EMI calculate की जाती है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text =
                    "Loan Amount = Bike Price − Down Payment",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BikeBlue
            )
        }

        // =========================================
        // FORMULA
        // =========================================

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "Bike EMI का Formula",
            icon = Icons.Default.Calculate,
            background = BikeSoftPurple
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(
                            alpha = 0.75f
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text =
                        "EMI = P × R × (1 + R)ⁿ ÷ ((1 + R)ⁿ − 1)",
                    color = BikePurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text =
                    "P = Loan Amount\n" +
                            "R = Monthly Interest Rate\n" +
                            "n = Total Monthly Instalments",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text =
                    "Monthly Rate = Annual Rate ÷ 12 ÷ 100",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // =========================================
        // EXAMPLE
        // =========================================

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "Example Calculation",
            icon = Icons.Default.TrendingUp,
            background = BikeSoftGreen
        ) {

            Text(
                text =
                    "मान लीजिए आपकी bike की कीमत ₹1,00,000 है और आप ₹20,000 down payment करते हैं।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text =
                    "Bike Price = ₹1,00,000\n" +
                            "Down Payment = ₹20,000\n" +
                            "Loan Amount = ₹80,000\n" +
                            "Interest Rate = 10%\n" +
                            "Tenure = 3 Years",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text =
                    "इस जानकारी के आधार पर calculator monthly EMI, total interest और total payment दिखाएगा।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        // =========================================
        // IMPORTANT POINTS
        // =========================================

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "जरूरी बातें",
            icon = Icons.Default.Info,
            background = BikeSoftOrange
        ) {

            Bullet(
                "Down payment बढ़ाने से loan amount कम हो सकता है।"
            )

            Bullet(
                "ज्यादा tenure से EMI कम हो सकती है, लेकिन कुल interest बढ़ सकता है।"
            )

            Bullet(
                "Interest rate annual percentage में दर्ज करें।"
            )

            Bullet(
                "Calculator में दिखाया गया result अनुमानित है।"
            )

            Bullet(
                "Processing fee, insurance, registration और अन्य charges इस EMI calculation में शामिल नहीं हैं।"
            )
        }

        // =========================================
        // FAQ
        // =========================================

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Frequently Asked Questions",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(8.dp))

        FaqCard(
            "Bike EMI क्या होती है?",
            "Bike loan को interest सहित तय अवधि में चुकाने के लिए हर महीने दी जाने वाली राशि को EMI कहते हैं।"
        )

        FaqCard(
            "Down payment क्या है?",
            "Bike खरीदते समय जो राशि आप अपनी तरफ से पहले देते हैं, उसे down payment कहते हैं। बाकी amount loan हो सकता है।"
        )

        FaqCard(
            "Down payment ज्यादा करने का क्या फायदा है?",
            "ज्यादा down payment करने पर loan amount कम हो सकता है, जिससे monthly EMI और total interest कम हो सकते हैं।"
        )

        FaqCard(
            "क्या tenure बढ़ाने से EMI कम होती है?",
            "आमतौर पर हाँ, लेकिन लंबे tenure में कुल interest ज्यादा हो सकता है।"
        )

        FaqCard(
            "क्या calculator में insurance शामिल है?",
            "नहीं। यह calculator मुख्य रूप से bike loan principal और interest की EMI calculate करता है।"
        )

        FaqCard(
            "क्या bank की EMI अलग हो सकती है?",
            "हाँ। Bank या finance company की rounding, fees, insurance और अन्य loan terms के कारण final amount अलग हो सकता है।"
        )

        Spacer(Modifier.height(30.dp))
    }
}


// =====================================================
// RESULT ROW
// =====================================================

@Composable
private fun ResultRow(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
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
            fontWeight = FontWeight.Bold
        )
    }
}


// =====================================================
// MINI CARD
// =====================================================

@Composable
private fun MiniCard(
    modifier: Modifier,
    title: String,
    value: String,
    background: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = title,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}


// =====================================================
// INFO SECTION
// =====================================================

@Composable
private fun InfoSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BikePurple
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

            Spacer(Modifier.height(12.dp))

            content()
        }
    }
}


// =====================================================
// BULLET
// =====================================================

@Composable
private fun Bullet(text: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment =
            Alignment.Top
    ) {

        Text(
            text = "•",
            color = BikePurple,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    }
}


// =====================================================
// FAQ CARD
// =====================================================

@Composable
private fun FaqCard(
    question: String,
    answer: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = question,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(7.dp))

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


// =====================================================
// MONEY FORMAT
// =====================================================

private fun formatMoney(
    value: Double
): String {

    return if (
        value.isNaN() ||
        value.isInfinite()
    ) {
        "₹0"
    } else {

        String.format(
            Locale.US,
            "₹%,.0f",
            value
        )
    }
}
