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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
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
                title = "EMI Calculator",
                subtitle = "Loan EMI और interest",
                icon = Icons.Default.Calculate
            ),

            Tool(
                title = "Bike Loan EMI",
                subtitle = "Bike loan का हिसाब",
                icon = Icons.Default.AccountBalance
            ),

            Tool(
                title = "EMI Prepayment",
                subtitle = "Loan जल्दी खत्म करें",
                icon = Icons.Default.TrendingUp
            ),

            Tool(
                title = "Bigha / Biswa",
                subtitle = "जमीन का area",
                icon = Icons.Default.Home
            ),

            Tool(
                title = "Photo Resizer",
                subtitle = "Photo size कम करें",
                icon = Icons.Default.Image
            )
        )
    }

    var searchText by remember {
        mutableStateOf("")
    }

    val filteredTools = tools.filter {

        it.title.contains(
            searchText,
            ignoreCase = true
        ) ||
        it.subtitle.contains(
            searchText,
            ignoreCase = true
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        LazyVerticalGrid(

            columns = GridCells.Fixed(2),

            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp
            ),

            horizontalArrangement =
                Arrangement.spacedBy(12.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // ==================================
            // HEADER
            // ==================================

            item(
                span = {
                    androidx.compose.foundation.lazy.grid.GridItemSpan(
                        2
                    )
                }
            ) {

                HeaderCard()
            }


            // ==================================
            // SEARCH
            // ==================================

            item(
                span = {
                    androidx.compose.foundation.lazy.grid.GridItemSpan(
                        2
                    )
                }
            ) {

                SearchBox(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    }
                )
            }


            // ==================================
            // SECTION TITLE
            // ==================================

            item(
                span = {
                    androidx.compose.foundation.lazy.grid.GridItemSpan(
                        2
                    )
                }
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Popular Tools",
                            style =
                                MaterialTheme.typography.titleLarge,
                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Text(
                            text =
                                "आपके रोज़ के काम के लिए",
                            style =
                                MaterialTheme.typography.bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }


                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(50.dp)
                            )
                            .background(
                                HisaabKitPurple.copy(
                                    alpha = 0.12f
                                )
                            )
                            .padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            )
                    ) {

                        Text(
                            text =
                                "${filteredTools.size} Tools",

                            color =
                                HisaabKitPurple,

                            fontWeight =
                                FontWeight.Bold,

                            style =
                                MaterialTheme.typography
                                    .labelMedium
                        )
                    }
                }
            }


            // ==================================
            // TOOLS
            // ==================================

            items(
                items = filteredTools,
                key = {
                    it.title
                }
            ) { tool ->

                ToolCard(

                    tool = tool,

                    color =
                        getToolColor(
                            tool.title
                        ),

                    onClick = {
                        onToolClick(
                            tool.title
                        )
                    }
                )
            }


            // ==================================
            // BOTTOM TIP
            // ==================================

            item(
                span = {
                    androidx.compose.foundation.lazy.grid.GridItemSpan(
                        2
                    )
                }
            ) {

                TipCard()
            }
        }
    }
}


// =====================================================
// PREMIUM HEADER
// =====================================================

@Composable
private fun HeaderCard() {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(30.dp)
            )
            .clip(
                RoundedCornerShape(30.dp)
            )
            .background(

                Brush.linearGradient(

                    listOf(

                        HisaabKitPurple,

                        HisaabKitBlue,

                        Color(0xFF06B6D4)
                    )
                )
            )
            .padding(22.dp)
    ) {

        // Decorative circles

        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Color.White.copy(
                        alpha = 0.08f
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(
                    Color.White.copy(
                        alpha = 0.07f
                    )
                )
        )


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(
                            RoundedCornerShape(17.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.18f
                            )
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Calculate,

                        contentDescription =
                            "HisaabKit",

                        tint = Color.White,

                        modifier =
                            Modifier.size(31.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(13.dp)
                )


                Column {

                    Text(
                        text = "HisaabKit",

                        color = Color.White,

                        style =
                            MaterialTheme.typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Text(
                        text =
                            "Smart हिसाब • आसान जिंदगी",

                        color =
                            Color.White.copy(
                                alpha = 0.88f
                            ),

                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(23.dp)
            )


            Text(
                text =
                    "हर calculation अब आसान है ✨",

                color = Color.White,

                style =
                    MaterialTheme.typography
                        .titleLarge,

                fontWeight =
                    FontWeight.ExtraBold
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            Text(
                text =
                    "EMI, Loan, Land और Photo tools एक ही जगह।",

                color =
                    Color.White.copy(
                        alpha = 0.85f
                    ),

                style =
                    MaterialTheme.typography.bodyMedium
            )
        }
    }
}


// =====================================================
// SEARCH BOX
// =====================================================

@Composable
private fun SearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(

        value = value,

        onValueChange = onValueChange,

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(20.dp)
            ),

        singleLine = true,

        shape =
            RoundedCornerShape(20.dp),

        placeholder = {

            Text(
                text =
                    "कोई calculator खोजें..."
            )
        },

        leadingIcon = {

            Icon(
                imageVector =
                    Icons.Default.Search,

                contentDescription =
                    "Search",

                tint =
                    MaterialTheme.colorScheme
                        .primary
            )
        }
    )
}


// =====================================================
// TOOL CARD
// =====================================================

@Composable
private fun ToolCard(

    tool: Tool,

    color: Color,

    onClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clickable(
                onClick = onClick
            ),

        shape =
            RoundedCornerShape(25.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    color.copy(
                        alpha = 0.10f
                    )
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            verticalArrangement =
                Arrangement.SpaceBetween
        ) {

            Box(

                modifier = Modifier
                    .size(54.dp)
                    .clip(
                        RoundedCornerShape(17.dp)
                    )
                    .background(color),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        tool.icon,

                    contentDescription =
                        tool.title,

                    tint = Color.White,

                    modifier =
                        Modifier.size(29.dp)
                )
            }


            Column {

                Text(

                    text =
                        tool.title,

                    style =
                        MaterialTheme.typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(

                    text =
                        tool.subtitle,

                    style =
                        MaterialTheme.typography
                            .bodySmall,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}


// =====================================================
// TIP CARD
// =====================================================

@Composable
private fun TipCard() {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    HisaabKitGreen.copy(
                        alpha = 0.10f
                    )
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(17.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier =
                    Modifier
                        .size(45.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            HisaabKitGreen
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "✓",
                    color = Color.White,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }


            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )


            Column {

                Text(
                    text =
                        "Quick & Easy",

                    fontWeight =
                        FontWeight.Bold,

                    style =
                        MaterialTheme.typography
                            .titleSmall
                )

                Text(
                    text =
                        "अपना हिसाब कुछ ही सेकंड में निकालें।",

                    style =
                        MaterialTheme.typography
                            .bodySmall,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}


// =====================================================
// TOOL COLORS
// =====================================================

private fun getToolColor(
    title: String
): Color {

    return when (title) {

        "EMI Calculator" ->
            HisaabKitPurple

        "Bike Loan EMI" ->
            HisaabKitBlue

        "EMI Prepayment" ->
            HisaabKitGreen

        "Bigha / Biswa" ->
            HisaabKitOrange

        "Photo Resizer" ->
            HisaabKitPink

        else ->
            HisaabKitPurple
    }
}
