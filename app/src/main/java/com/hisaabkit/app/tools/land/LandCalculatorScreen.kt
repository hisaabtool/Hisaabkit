package com.hisaabkit.app.tools.land

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

private val LandPurple = Color(0xFF7C3AED)
private val LandBlue = Color(0xFF2563EB)
private val LandGreen = Color(0xFF059669)
private val LandOrange = Color(0xFFF59E0B)

private val SoftPurple = Color(0xFFF3EDFF)
private val SoftBlue = Color(0xFFECF3FF)
private val SoftGreen = Color(0xFFEAF8F1)
private val SoftOrange = Color(0xFFFFF5E3)

private val Units = listOf(
    "Square Feet",
    "Square Meter",
    "Biswa",
    "Bigha"
)

@Composable
fun LandCalculatorScreen() {

    var value by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf("Square Feet") }
    var toUnit by remember { mutableStateOf("Biswa") }

    var bighaSize by remember { mutableStateOf("27225") }

    var result by remember { mutableStateOf(0.0) }
    var calculated by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    fun calculate() {

        val input = value.toDoubleOrNull()
        val bigha = bighaSize.toDoubleOrNull()

        if (input == null || input < 0) {
            error = "कृपया सही area दर्ज करें।"
            calculated = false
            return
        }

        if (bigha == null || bigha <= 0) {
            error = "कृपया Bigha का सही square feet size दर्ज करें।"
            calculated = false
            return
        }

        val squareFeet = when (fromUnit) {

            "Square Feet" -> input

            "Square Meter" -> input * 10.7639104167

            "Biswa" -> input * (bigha / 20.0)

            "Bigha" -> input * bigha

            else -> input
        }

        result = when (toUnit) {

            "Square Feet" -> squareFeet

            "Square Meter" -> squareFeet / 10.7639104167

            "Biswa" -> squareFeet / (bigha / 20.0)

            "Bigha" -> squareFeet / bigha

            else -> squareFeet
        }

        calculated = true
        error = ""
    }

    fun swapUnits() {

        val oldFrom = fromUnit
        fromUnit = toUnit
        toUnit = oldFrom

        calculated = false
    }

    fun reset() {

        value = ""
        fromUnit = "Square Feet"
        toUnit = "Biswa"
        bighaSize = "27225"

        result = 0.0
        calculated = false
        error = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .verticalScroll(rememberScrollState())
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
                            LandGreen,
                            LandBlue
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
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {

                    Text(
                        text = "Land Calculator",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Bigha • Biswa • Sq Ft • Sq Meter",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Area Details",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = value,
            onValueChange = {
                value = it
                error = ""
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text("Area Value")
            },
            placeholder = {
                Text("जैसे 5000")
            }
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = "From Unit",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(6.dp))

        UnitDropdown(
            selected = fromUnit,
            onSelected = {
                fromUnit = it
                calculated = false
            }
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            OutlinedButton(
                onClick = {
                    swapUnits()
                },
                shape = RoundedCornerShape(16.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null
                )

                Spacer(Modifier.width(7.dp))

                Text("Swap")
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "To Unit",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(6.dp))

        UnitDropdown(
            selected = toUnit,
            onSelected = {
                toUnit = it
                calculated = false
            }
        )

        Spacer(Modifier.height(16.dp))

        // BIGHA SIZE

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = SoftOrange
            )
        ) {

            Column(
                modifier = Modifier.padding(17.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Info,
                        contentDescription = null,
                        tint = LandOrange
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Bigha Size",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text =
                        "Bigha का size अलग-अलग राज्यों/क्षेत्रों में अलग हो सकता है। अपने क्षेत्र के अनुसार 1 Bigha का square feet value दर्ज करें।",
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = bighaSize,
                    onValueChange = {
                        bighaSize = it
                        calculated = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    label = {
                        Text("1 Bigha = कितने Sq Ft?")
                    }
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text =
                        "Example: अगर आपके क्षेत्र में 1 Bigha = 27,225 Sq Ft है तो 27225 रखें।",
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (error.isNotEmpty()) {

            Text(
                text = error,
                color = Color(0xFFD32F2F),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = {
                calculate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LandGreen
            )
        ) {

            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Calculate Area",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                reset()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp)
        ) {

            Text(
                text = "Reset",
                fontWeight = FontWeight.Bold
            )
        }

        // RESULT

        if (calculated) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Conversion Result",
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
                        text =
                            "$value $fromUnit =",
                        fontSize = 15.sp,
                        color = LandGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text =
                            "${formatNumber(result)} $toUnit",
                        fontSize = 29.sp,
                        color = LandGreen,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(18.dp))

                    ResultRow(
                        "Input Area",
                        "$value $fromUnit"
                    )

                    ResultRow(
                        "Converted Area",
                        "${formatNumber(result)} $toUnit"
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ABOUT

        InfoCard(
            title = "Bigha / Biswa Calculator कैसे काम करता है?",
            icon = Icons.Default.Info,
            background = SoftBlue
        ) {

            Text(
                text =
                    "यह calculator अलग-अलग land area units के बीच conversion करता है। आप Square Feet, Square Meter, Biswa और Bigha के बीच area convert कर सकते हैं।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text =
                    "सबसे महत्वपूर्ण बात यह है कि Bigha और Biswa की standard measurement हर जगह एक जैसी नहीं होती। इसलिए calculator में 1 Bigha का local square-feet size manually सेट करने का विकल्प दिया गया है।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        // FORMULA

        InfoCard(
            title = "Conversion Formula",
            icon = Icons.Default.Calculate,
            background = SoftPurple
        ) {

            FormulaText(
                "1 Square Meter = 10.7639 Square Feet"
            )

            FormulaText(
                "1 Bigha = आपके क्षेत्र का निर्धारित Sq Ft"
            )

            FormulaText(
                "1 Biswa = 1 Bigha ÷ 20"
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text =
                    "उदाहरण: अगर 1 Bigha = 27,225 Sq Ft है, तो 1 Biswa = 27,225 ÷ 20 = 1,361.25 Sq Ft होगा।",
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        // IMPORTANT

        InfoCard(
            title = "Bigha की measurement क्यों बदलती है?",
            icon = Icons.Default.SquareFoot,
            background = SoftOrange
        ) {

            Text(
                text =
                    "भारत में Bigha एक universal fixed unit नहीं है। अलग-अलग राज्यों और स्थानीय क्षेत्रों में इसका area अलग हो सकता है। इसलिए जमीन की official measurement के लिए अपने राज्य के revenue records, registry documents या स्थानीय authority की measurement को प्राथमिकता दें।",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        // EXAMPLE

        InfoCard(
            title = "Example Calculation",
            icon = Icons.Default.Calculate,
            background = SoftGreen
        ) {

            Text(
                text =
                    "मान लीजिए आपके क्षेत्र में 1 Bigha = 27,225 Sq Ft है।",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text =
                    "1 Bigha = 27,225 Sq Ft\n" +
                            "1 Biswa = 1,361.25 Sq Ft\n\n" +
                            "अगर जमीन 5,000 Sq Ft है, तो:\n" +
                            "5,000 ÷ 1,361.25 ≈ 3.67 Biswa",
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        // FAQ

        Text(
            text = "Frequently Asked Questions",
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(8.dp))

        FaqCard(
            "Bigha क्या होता है?",
            "Bigha भारत में इस्तेमाल होने वाली traditional land measurement unit है। इसका actual area अलग-अलग क्षेत्रों में अलग हो सकता है।"
        )

        FaqCard(
            "1 Bigha में कितने Biswa होते हैं?",
            "इस calculator में सामान्य conversion के लिए 1 Bigha = 20 Biswa माना गया है। स्थानीय नियम अलग हो सकते हैं।"
        )

        FaqCard(
            "क्या 1 Bigha पूरे भारत में समान होता है?",
            "नहीं। Bigha का area राज्य और क्षेत्र के अनुसार बदल सकता है।"
        )

        FaqCard(
            "Square Feet से Biswa कैसे निकालें?",
            "Square Feet को एक Biswa के local Square Feet value से divide करें।"
        )

        FaqCard(
            "क्या यह calculator जमीन की सरकारी measurement है?",
            "नहीं। यह area conversion tool है। सरकारी या कानूनी measurement के लिए संबंधित revenue authority के official records को मानें।"
        )

        FaqCard(
            "1 Square Meter कितने Square Feet होता है?",
            "1 Square Meter लगभग 10.7639 Square Feet होता है।"
        )

        Spacer(Modifier.height(30.dp))
    }
}


// =====================================================
// UNIT DROPDOWN
// =====================================================

@Composable
private fun UnitDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = selected,
                    fontWeight = FontWeight.SemiBold
                )

                Text("▼")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            Units.forEach { unit ->

                DropdownMenuItem(
                    text = {
                        Text(unit)
                    },
                    onClick = {
                        onSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}


// =====================================================
// INFO CARD
// =====================================================

@Composable
private fun InfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    content: @Composable () -> Unit
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
                    tint = LandPurple
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


// =====================================================
// FORMULA
// =====================================================

@Composable
private fun FormulaText(
    text: String
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                Color.White.copy(alpha = 0.75f),
                RoundedCornerShape(13.dp)
            )
            .padding(13.dp)
    ) {

        Text(
            text = text,
            color = LandPurple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
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
            .padding(vertical = 6.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            fontSize = 14.sp
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
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
        elevation =
            CardDefaults.cardElevation(
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
// NUMBER FORMAT
// =====================================================

private fun formatNumber(
    value: Double
): String {

    return if (
        value.isNaN() ||
        value.isInfinite()
    ) {
        "0"
    } else {

        String.format(
            Locale.US,
            "%,.2f",
            value
        )
    }
}
