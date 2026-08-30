package com.hisaabkit.app.tools.emi

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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

private val EmiPurple = Color(0xFF6C3CEB)
private val EmiBlue = Color(0xFF2563EB)
private val EmiGreen = Color(0xFF0A9F6E)
private val EmiOrange = Color(0xFFF59E0B)
private val EmiSoftPurple = Color(0xFFF2EBFF)
private val EmiSoftBlue = Color(0xFFEAF1FF)
private val EmiSoftGreen = Color(0xFFE8F8F1)
private val EmiSoftOrange = Color(0xFFFFF4DF)

@Composable
fun EmiScreen() {

    var loanAmount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var tenure by remember { mutableStateOf("") }

    var emi by remember { mutableStateOf(0.0) }
    var totalInterest by remember { mutableStateOf(0.0) }
    var totalPayment by remember { mutableStateOf(0.0) }

    var calculated by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun calculateEmi() {

        val principal = loanAmount.toDoubleOrNull()
        val annualRate = interestRate.toDoubleOrNull()
        val years = tenure.toDoubleOrNull()

        if (principal == null || principal <= 0) {
            errorMessage = "कृपया सही Loan Amount दर्ज करें।"
            calculated = false
            return
        }

        if (annualRate == null || annualRate < 0) {
            errorMessage = "कृपया सही Interest Rate दर्ज करें।"
            calculated = false
            return
        }

        if (years == null || years <= 0) {
            errorMessage = "कृपया सही Loan Tenure दर्ज करें।"
            calculated = false
            return
        }

        val monthlyRate = annualRate / 12.0 / 100.0
        val months = (years * 12).toInt()

        if (months <= 0) {
            errorMessage = "Loan Tenure कम से कम 1 महीना होना चाहिए।"
            calculated = false
            return
        }

        emi = if (monthlyRate == 0.0) {
            principal / months
        } else {
            principal *
                    monthlyRate *
                    (1 + monthlyRate).pow(months.toDouble()) /
                    ((1 + monthlyRate).pow(months.toDouble()) - 1)
        }

        totalPayment = emi * months
        totalInterest = totalPayment - principal

        errorMessage = ""
        calculated = true
    }

    fun resetCalculator() {
        loanAmount = ""
        interestRate = ""
        tenure = ""
        emi = 0.0
        totalInterest = 0.0
        totalPayment = 0.0
        calculated = false
        errorMessage = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {

        /* ---------------- HEADER ---------------- */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            EmiPurple,
                            EmiBlue
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(22.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.20f),
                            RoundedCornerShape(17.dp)
                        )
                        .padding(14.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(15.dp))

                Column {

                    Text(
                        text = "EMI Calculator",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = "Loan की EMI और कुल ब्याज जानें",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        /* ---------------- INPUT SECTION ---------------- */

        Text(
            text = "Loan Details",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = loanAmount,
            onValueChange = {
                loanAmount = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Loan Amount")
            },
            placeholder = {
                Text("जैसे 500000")
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
                Text("जैसे 5")
            }
        )

        if (errorMessage.isNotEmpty()) {

            Spacer(Modifier.height(10.dp))

            Text(
                text = errorMessage,
                color = Color(0xFFD32F2F),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(16.dp))

        /* ---------------- BUTTONS ---------------- */

        Button(
            onClick = {
                calculateEmi()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmiPurple
            )
        ) {

            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Calculate EMI",
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
                containerColor = EmiSoftPurple,
                contentColor = EmiPurple
            )
        ) {

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Reset",
                fontWeight = FontWeight.Bold
            )
        }

        /* ---------------- RESULT ---------------- */

        if (calculated) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Your EMI Result",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmiSoftPurple
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Monthly EMI",
                        color = EmiPurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = formatMoney(emi),
                        fontSize = 31.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmiPurple
                    )

                    Spacer(Modifier.height(18.dp))

                    ResultRow(
                        title = "Loan Amount",
                        value = formatMoney(
                            loanAmount.toDoubleOrNull() ?: 0.0
                        )
                    )

                    ResultRow(
                        title = "Total Interest",
                        value = formatMoney(totalInterest)
                    )

                    ResultRow(
                        title = "Total Payment",
                        value = formatMoney(totalPayment)
                    )

                    ResultRow(
                        title = "Loan Tenure",
                        value = "${tenure} Years"
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                SmallResultCard(
                    modifier = Modifier.weight(1f),
                    title = "Principal",
                    value = formatMoney(
                        loanAmount.toDoubleOrNull() ?: 0.0
                    ),
                    background = EmiSoftBlue,
                    icon = Icons.Default.Info,
                    iconColor = EmiBlue
                )

                SmallResultCard(
                    modifier = Modifier.weight(1f),
                    title = "Interest",
                    value = formatMoney(totalInterest),
                    background = EmiSoftOrange,
                    icon = Icons.Default.TrendingUp,
                    iconColor = EmiOrange
                )
            }
        }

        /* ---------------- HOW IT WORKS ---------------- */

        Spacer(Modifier.height(28.dp))

        InfoSection(
            title = "EMI Calculator कैसे काम करता है?",
            icon = Icons.Default.Info,
            background = EmiSoftBlue
        ) {

            Text(
                text = "EMI यानी Equated Monthly Instalment वह निश्चित राशि है जो आपको loan की अवधि के दौरान हर महीने चुकानी होती है। EMI में principal amount और interest दोनों शामिल होते हैं।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "आप Loan Amount, Annual Interest Rate और Loan Tenure दर्ज करते हैं। Calculator इन तीनों values के आधार पर आपकी अनुमानित monthly EMI निकालता है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        /* ---------------- FORMULA ---------------- */

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "EMI का Formula",
            icon = Icons.Default.Calculate,
            background = EmiSoftPurple
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.75f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = "EMI = P × R × (1 + R)ⁿ ÷ ((1 + R)ⁿ − 1)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmiPurple
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "जहाँ:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "P = Loan का principal amount\n" +
                        "R = Monthly interest rate\n" +
                        "n = Total number of monthly instalments",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Monthly interest rate निकालने के लिए annual interest rate को 12 और 100 से divide किया जाता है।",
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }

        /* ---------------- EXAMPLE ---------------- */

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "Example: EMI Calculation",
            icon = Icons.Default.TrendingUp,
            background = EmiSoftGreen
        ) {

            Text(
                text = "मान लीजिए:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Loan Amount: ₹5,00,000\n" +
                        "Interest Rate: 10% प्रति वर्ष\n" +
                        "Tenure: 5 वर्ष\n" +
                        "Total Months: 60",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "इन values को formula में लगाने पर monthly EMI लगभग ₹10,624 आती है। Actual EMI lender की rounding और terms के अनुसार थोड़ी अलग हो सकती है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        /* ---------------- IMPORTANT POINTS ---------------- */

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "ध्यान रखने वाली बातें",
            icon = Icons.Default.Info,
            background = EmiSoftOrange
        ) {

            BulletText("Interest Rate annual percentage में दर्ज करें।")

            BulletText("Tenure को वर्षों में दर्ज करें।")

            BulletText("ज्यादा tenure से monthly EMI कम हो सकती है, लेकिन कुल interest बढ़ सकता है।")

            BulletText("Interest rate बढ़ने पर आमतौर पर EMI और total interest दोनों बढ़ते हैं।")

            BulletText("यह calculator अनुमानित calculation के लिए है। Bank/NBFC की final EMI उनके terms के अनुसार हो सकती है।")
        }

        /* ---------------- FAQ ---------------- */

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Frequently Asked Questions",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(10.dp))

        FaqCard(
            question = "EMI क्या होती है?",
            answer = "EMI वह fixed monthly payment है जिसमें loan का principal और interest दोनों शामिल होते हैं।"
        )

        FaqCard(
            question = "EMI किन चीजों पर निर्भर करती है?",
            answer = "EMI मुख्य रूप से loan amount, interest rate और loan tenure पर निर्भर करती है।"
        )

        FaqCard(
            question = "क्या ज्यादा tenure से EMI कम होती है?",
            answer = "आमतौर पर हाँ। लेकिन tenure बढ़ाने पर कुल interest ज्यादा हो सकता है।"
        )

        FaqCard(
            question = "Interest rate बढ़ने पर क्या होगा?",
            answer = "बाकी conditions समान रहने पर interest rate बढ़ने से EMI और कुल interest बढ़ सकता है।"
        )

        FaqCard(
            question = "क्या यह calculator zero interest पर भी काम करता है?",
            answer = "हाँ। अगर interest rate 0% है तो loan amount को total months से divide करके EMI निकाली जाती है।"
        )

        FaqCard(
            question = "क्या calculator की EMI bank से अलग हो सकती है?",
            answer = "हाँ। Bank की rounding, processing structure, insurance और अन्य charges के कारण final payable amount अलग हो सकता है।"
        )

        Spacer(Modifier.height(30.dp))
    }
}

/* ---------------- RESULT ROW ---------------- */

@Composable
private fun ResultRow(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/* ---------------- SMALL RESULT CARD ---------------- */

@Composable
private fun SmallResultCard(
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

            Spacer(Modifier.height(10.dp))

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

/* ---------------- INFO SECTION ---------------- */

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
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmiPurple
                )

                Spacer(Modifier.width(9.dp))

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(12.dp))

            content()
        }
    }
}

/* ---------------- BULLET ---------------- */

@Composable
private fun BulletText(text: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {

        Text(
            text = "•",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = EmiPurple
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    }
}

/* ---------------- FAQ ---------------- */

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
            containerColor = MaterialTheme.colorScheme.surface
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ---------------- MONEY FORMAT ---------------- */

private fun formatMoney(value: Double): String {

    return if (value.isNaN() || value.isInfinite()) {
        "₹0"
    } else {
        String.format(
            Locale.US,
            "₹%,.0f",
            value
        )
    }
}
