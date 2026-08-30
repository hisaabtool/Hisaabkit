package com.hisaabkit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hisaabkit.app.ui.theme.HisaabKitBlue
import com.hisaabkit.app.ui.theme.HisaabKitGreen
import com.hisaabkit.app.ui.theme.HisaabKitOrange
import com.hisaabkit.app.ui.theme.HisaabKitPink
import com.hisaabkit.app.ui.theme.HisaabKitPurple

data class Tool(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(
    onToolClick: (String) -> Unit = {}
) {
    val tools = remember {
        listOf(
            Tool(
                "EMI Calculator",
                "Loan EMI और interest",
                Icons.Default.Calculate
            ),
            Tool(
                "Bike Loan EMI",
                "Bike loan का हिसाब",
                Icons.Default.AccountBalance
            ),
            Tool(
                "EMI Prepayment",
                "Loan जल्दी खत्म करें",
                Icons.Default.TrendingUp
            ),
            Tool(
                "Bigha / Biswa",
                "जमीन का area",
                Icons.Default.Home
            ),
            Tool(
                "Photo Resizer",
                "20KB / 50KB photo",
                Icons.Default.Image
            )
        )
    }

    var searchText by remember {
        mutableStateOf("")
    }

    val filteredTools = tools.filter {
        it.title.contains(searchText, ignoreCase = true) ||
        it.subtitle.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(Modifier.height(18.dp))

        // Premium Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(28.dp)
                )
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            HisaabKitPurple,
                            HisaabKitBlue
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(22.dp)
        ) {

            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                Color.White.copy(alpha = 0.20f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.size(14.dp))

                    Column {

                        Text(
                            text = "HisaabKit",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Smart हिसाब, आसान जिंदगी",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "हर calculation अब आसान है ✨",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Search
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    4.dp,
                    RoundedCornerShape(18.dp)
                ),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            placeholder = {
                Text("कोई calculator खोजें...")
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        )

        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Popular Tools",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "${filteredTools.size} Tools",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(filteredTools) { tool ->

                ToolCard(
                    tool = tool,
                    color = getToolColor(tool.title),
                    onClick = {
                        onToolClick(tool.title)
                    }
                )
            }
        }
    }
}

private fun getToolColor(title: String): Color {
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
private fun ToolCard(
    tool: Tool,
    color: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(158.dp)
            .clickable(onClick = onClick),
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

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color,
                        RoundedCornerShape(17.dp)
                    ),
                contentAlignment = Alignment.Center
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
