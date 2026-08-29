package com.hisaabkit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
            Icons.Default.Calculate
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
            .padding(horizontal = 16.dp)
    ) {

        Spacer(Modifier.height(24.dp))

        Text(
            text = "All Tools",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "अपने काम के हिसाब से tool चुनें",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(tools) { tool ->

                ToolCardForList(
                    tool = tool,
                    onClick = {
                        onToolClick(tool.title)
                    }
                )
            }
        }
    }
}

@Composable
private fun ToolCardForList(
    tool: Tool,
    onClick: () -> Unit
) {

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(1.dp)
            .then(
                Modifier
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        onClick = onClick
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            androidx.compose.material3.Icon(
                imageVector = tool.icon,
                contentDescription = null,
                modifier = Modifier.height(40.dp)
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = tool.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
