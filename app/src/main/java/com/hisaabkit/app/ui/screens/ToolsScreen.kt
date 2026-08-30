package com.hisaabkit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hisaabkit.app.ui.theme.HisaabKitBlue
import com.hisaabkit.app.ui.theme.HisaabKitGreen
import com.hisaabkit.app.ui.theme.HisaabKitOrange
import com.hisaabkit.app.ui.theme.HisaabKitPink
import com.hisaabkit.app.ui.theme.HisaabKitPurple

@Composable
fun ToolsScreen(
    onToolClick: (String) -> Unit
) {

    val tools = listOf(
        Tool(
            "EMI Calculator",
            "Loan EMI और interest",
            Icons.Default.Calculate
        ),
        Tool(
            "Bike Loan EMI",
            "Bike finance का हिसाब",
            Icons.Default.AccountBalance
        ),
        Tool(
            "EMI Prepayment",
            "Loan जल्दी खत्म करें",
            Icons.Default.TrendingUp
        ),
        Tool(
            "Bigha / Biswa",
            "जमीन का area निकालें",
            Icons.Default.Home
        ),
        Tool(
            "Photo Resizer",
            "Photo size कम करें",
            Icons.Default.Image
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(Modifier.height(20.dp))

        Text(
            text = "All Tools 🧮",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "अपने काम के हिसाब से calculator चुनें",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(tools) { tool ->

                ToolListCard(
                    tool = tool,
                    color = getToolColorForTools(tool.title),
                    onClick = {
                        onToolClick(tool.title)
                    }
                )
            }
        }
    }
}

private fun getToolColorForTools(title: String): Color {
    return when (title) {
        "EMI Calculator" -> HisaabKitPurple
        "Bike Loan EMI" -> HisaabKitBlue
        "EMI Prepayment" -> HisaabKitGreen
        "Bigha / Biswa" -> HisaabKitOrange
        "Photo Resizer" -> HisaabKitPink
        else -> HisaabKitPurple
    }
}

@Composable
private fun ToolListCard(
    tool: Tool,
    color: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp),
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.10f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color,
                        RoundedCornerShape(17.dp)
                    ),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {

                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column {

                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = tool.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
