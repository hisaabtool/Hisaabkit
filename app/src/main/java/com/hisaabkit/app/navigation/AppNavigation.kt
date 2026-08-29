package com.hisaabkit.app.navigation

import androidx.compose.runtime.Composable
import com.hisaabkit.app.ui.screens.HomeScreen

@Composable
fun AppNavigation() {

    HomeScreen(
        onToolClick = { tool ->
            // अगले चरण में actual tool screens खुलेंगी
        }
    )
}
