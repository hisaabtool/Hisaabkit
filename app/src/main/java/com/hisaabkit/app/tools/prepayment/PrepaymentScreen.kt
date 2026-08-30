package com.hisaabkit.app.tools.prepayment

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
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

private val PrepayPurple = Color(0xFF7C3AED)
private val PrepayBlue = Color(0xFF2563EB)
private val PrepayGreen = Color(0xFF059669)
private val PrepayOrange = Color(0xFFF59E0B)

private val SoftPurple = Color(0xFFF3EDFF)
private val SoftBlue = Color(0xFFECF3FF)
private val SoftGreen = Color(0xFFEAF8F1)
private val SoftOrange = Color(0xFFFFF5E3)

@Composable
fun PrepaymentScreen() {

    var outstandingLoan by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var remainingTenure by remember { mutableStateOf("") }
    var prepaymentAmount by remember { mutableStateOf("") }

    var oldEmi by remember { mutableStateOf(0.0) }
    var newBalance by remember { mutableStateOf(0.0) }
    var newEmi by remember { mutableStateOf(0.0) }
    var interestWithoutPrepayment by remember { mutableStateOf(0.0) }
    var interestAfterPrepayment by remember { mutableStateOf(0.0) }
    var interestSaved by remember { mutableStateOf(0.0) }
    var calculated by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun calculatePrepayment() {

        val loan = outstandingLoan.toDoubleOrNull()
        val rate = interestRate.toDoubleOrNull()
        val months = remainingTenure.toIntOrNull()
        val prepayment = prepaymentAmount.toDoubleOrNull()

        if (loan == null || loan <= 0) {
            errorMessage = "कृपया सही Outstanding Loan दर्ज करें।"
            calculated = false
            return
        }

        if (rate == null || rate < 0) {
            errorMessage = "कृपया सही Interest Rate दर्ज करें।"
            calculated = false
            return
        }

        if (months == null || months <= 0) {
            errorMessage = "कृपया Remaining Tenure महीनों में दर्ज करें।"
            calculated = false
            return
        }

        if (prepayment == null || prepayment < 0) {
            errorMessage = "कृपया सही Prepayment Amount दर्ज करें।"
            calculated = false
            return
        }

        if (prepayment >= loan) {
            errorMessage =
                "Prepayment Outstanding Loan से कम होना चाहिए।"
            calculated = false
            return
        }

        val monthlyRate = rate / 12.0 / 100.0

        oldEmi = calculateEmi(
            loan,
            monthlyRate,
            months
        )

        val oldTotalPayment = oldEmi * months

        interestWithoutPrepayment =
            oldTotalPayment - loan

        newBalance = loan - prepayment

        newEmi = calculateEmi(
            newBalance,
            monthlyRate,
            months
        )

        val newTotalPayment = newEmi * months

        interestAfterPrepayment =
            newTotalPayment - newBalance

        interestSaved =
            interestWithoutPrepayment -
                    interestAfterPrepayment

        calculated = true
        errorMessage = ""
    }

    fun resetCalculator() {

        outstandingLoan = ""
        interestRate = ""
        remainingTenure = ""
        prepaymentAmount = ""

        oldEmi = 0.0
        newBalance = 0.0
        newEmi = 0.0
        interestWithoutPrepayment = 0.0
        interestAfterPrepayment = 0.0
        interestSaved = 0.0

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

        // HEADER

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            PrepayPurple,
                            PrepayBlue
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
                            Color.White.copy(alpha = 0.18f),
                            RoundedCornerShape(17.dp)
                        )
                        .padding(14.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Payments,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(
                    Modifier.width(14.dp)
                )

                Column {

                    Text(
                        text = "EMI Prepayment",
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
                            "Loan जल्दी चुकाने पर कितनी बचत होगी?",
                        color =
                            Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Loan Details",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = outstandingLoan,
            onValueChange = {
                outstandingLoan = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Outstanding Loan (₹)")
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
                Text("जैसे 9.5")
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = remainingTenure,
            onValueChange = {
                remainingTenure = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Remaining Tenure (Months)")
            },
            placeholder = {
                Text("जैसे 48")
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = prepaymentAmount,
            onValueChange = {
                prepaymentAmount = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Prepayment Amount (₹)")
            },
            placeholder = {
                Text("जैसे 100000")
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

        Button(
            onClick = {
                calculatePrepayment()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrepayPurple
            )
        ) {

            Icon(
                imageVector =
                    Icons.Default.Calculate,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Calculate Savings",
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
                containerColor = SoftPurple,
                contentColor = PrepayPurple
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

        // RESULT

        if (calculated) {

            Spacer(Modifier.height(25.dp))

            Text(
                text = "Prepayment Result",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SoftGreen
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Estimated Interest Saved",
                        color = PrepayGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = formatMoney(interestSaved),
                        color = PrepayGreen,
                        fontSize = 31.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(18.dp))

                    ResultRow(
                        "Current EMI",
                        formatMoney(oldEmi)
                    )

                    ResultRow(
                        "New Loan Balance",
                        formatMoney(newBalance)
                    )

                    ResultRow(
                        "New EMI",
                        formatMoney(newEmi)
                    )

                    ResultRow(
                        "Interest Without Prepayment",
                        formatMoney(
                            interestWithoutPrepayment
                        )
                    )

                    ResultRow(
                        "Interest After Prepayment",
                        formatMoney(
                            interestAfterPrepayment
                        )
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
                    title = "Interest Saved",
                    value =
                        formatMoney(interestSaved),
                    background = SoftGreen,
                    icon =
                        Icons.Default.Savings,
                    iconColor = PrepayGreen
                )

                MiniCard(
                    modifier =
                        Modifier.weight(1f),
                    title = "New Balance",
                    value =
                        formatMoney(newBalance),
                    background = SoftBlue,
                    icon =
                        Icons.Default.TrendingDown,
                    iconColor = PrepayBlue
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        InfoSection(
            title =
                "EMI Prepayment Calculator कैसे काम करता है?",
            icon = Icons.Default.Info,
            background = SoftBlue
        ) {

            Text(
                text =
                    "जब आप अपने existing loan की कुछ राशि समय से पहले जमा करते हैं, तो इसे prepayment कहते हैं। इससे outstanding principal कम हो जाता है और भविष्य में लगने वाला interest भी कम हो सकता है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text =
                    "यह calculator prepayment से पहले और बाद के interest का अनुमान लगाकर संभावित savings दिखाता है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "EMI का Formula",
            icon = Icons.Default.Calculate,
            background = SoftPurple
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
                    text =
                        "EMI = P × R × (1 + R)ⁿ ÷ ((1 + R)ⁿ − 1)",
                    color = PrepayPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text =
                    "P = Outstanding Loan\n" +
                            "R = Monthly Interest Rate\n" +
                            "n = Remaining Monthly Instalments",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "Example",
            icon = Icons.Default.TrendingDown,
            background = SoftGreen
        ) {

            Text(
                text =
                    "मान लीजिए आपके loan का outstanding amount ₹5,00,000 है। Interest rate 10% है और 48 महीने बाकी हैं। अगर आप ₹1,00,000 का prepayment करते हैं, तो outstanding principal ₹4,00,000 रह जाएगा।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text =
                    "Calculator दोनों परिस्थितियों के estimated interest की तुलना करके संभावित interest saving दिखाता है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "Prepayment के फायदे",
            icon = Icons.Default.Savings,
            background = SoftOrange
        ) {

            Bullet(
                "Outstanding principal कम होता है।"
            )

            Bullet(
                "भविष्य का interest कम हो सकता है।"
            )

            Bullet(
                "Loan जल्दी खत्म करने में मदद मिल सकती है।"
            )

            Bullet(
                "कुल repayment cost कम हो सकती है।"
            )

            Bullet(
                "बड़ी lump-sum राशि मिलने पर prepayment उपयोगी हो सकता है।"
            )
        }

        Spacer(Modifier.height(14.dp))

        InfoSection(
            title = "जरूरी सावधानियां",
            icon = Icons.Default.Info,
            background = SoftBlue
        ) {

            Bullet(
                "कुछ banks या lenders prepayment/foreclosure charge ले सकते हैं।"
            )

            Bullet(
                "Actual savings आपके lender की repayment conditions पर निर्भर कर सकती है।"
            )

            Bullet(
                "यह calculator अनुमानित calculation देता है।"
            )

            Bullet(
                "Tax benefits और अन्य charges इसमें शामिल नहीं हैं।"
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Frequently Asked Questions",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(8.dp))

        FaqCard(
            "Loan prepayment क्या है?",
            "Loan की तय EMI के अलावा principal की कुछ राशि पहले जमा करना prepayment कहलाता है।"
        )

        FaqCard(
            "क्या prepayment से interest कम होता है?",
            "आमतौर पर principal कम होने के कारण भविष्य में लगने वाला interest कम हो सकता है।"
        )

        FaqCard(
            "क्या पूरा loan एक साथ चुकाया जा सकता है?",
            "इसे सामान्यतः foreclosure या full prepayment कहा जाता है। इसकी अनुमति और charges lender की terms पर निर्भर करते हैं।"
        )

        FaqCard(
            "Prepayment करने से EMI कम होगी या tenure?",
            "यह lender और आपकी चुनी हुई repayment option पर निर्भर करता है। कुछ मामलों में EMI कम होती है और कुछ में tenure कम किया जाता है।"
        )

        FaqCard(
            "क्या prepayment charge लगता है?",
            "कुछ loan products में charge हो सकता है। अपने bank या finance company की current terms जरूर देखें।"
        )

        FaqCard(
            "क्या calculator exact bank saving बताता है?",
            "नहीं। यह अनुमानित saving बताता है। Actual amount lender के interest calculation, dates, charges और repayment schedule के अनुसार अलग हो सकता है।"
        )

        Spacer(Modifier.height(30.dp))
    }
}


// =====================================================
// EMI CALCULATION
// =====================================================

private fun calculateEmi(
    principal: Double,
    monthlyRate: Double,
    months: Int
): Double {

    if (principal <= 0) return 0.0

    if (monthlyRate == 0.0) {
        return principal / months
    }

    val factor =
        (1 + monthlyRate).pow(months.toDouble())

    return principal *
            monthlyRate *
            factor /
            (factor - 1)
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
                    tint = PrepayPurple
                )

                Spacer(Modifier.width(9.dp))

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
        verticalAlignment = Alignment.Top
    ) {

        Text(
            text = "•",
            color = PrepayPurple,
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
// FAQ
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
