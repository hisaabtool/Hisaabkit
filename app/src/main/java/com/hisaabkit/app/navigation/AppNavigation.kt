package com.hisaabkit.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hisaabkit.app.tools.bike.BikeEmiScreen
import com.hisaabkit.app.tools.emi.EmiScreen
import com.hisaabkit.app.tools.land.LandCalculatorScreen
import com.hisaabkit.app.tools.photo.PhotoResizerScreen
import com.hisaabkit.app.tools.prepayment.PrepaymentScreen
import com.hisaabkit.app.ui.screens.HomeScreen
import com.hisaabkit.app.ui.screens.SettingsScreen
import com.hisaabkit.app.ui.screens.ToolsScreen
import com.hisaabkit.app.ui.theme.HisaabKitBlue
import com.hisaabkit.app.ui.theme.HisaabKitGreen
import com.hisaabkit.app.ui.theme.HisaabKitPurple


object Routes {

    const val HOME = "home"
    const val TOOLS = "tools"
    const val SETTINGS = "settings"

    const val EMI = "emi"
    const val BIKE = "bike"
    const val PREPAYMENT = "prepayment"
    const val LAND = "land"
    const val PHOTO = "photo"
}


data class BottomItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)


@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val bottomItems = listOf(

        BottomItem(
            route = Routes.HOME,
            title = "Home",
            icon = Icons.Default.Home
        ),

        BottomItem(
            route = Routes.TOOLS,
            title = "Tools",
            icon = Icons.Default.Calculate
        ),

        BottomItem(
            route = Routes.SETTINGS,
            title = "Settings",
            icon = Icons.Default.Settings
        )
    )


    val backStackEntry by navController
        .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry?.destination?.route


    val showBottomBar =
        currentRoute == Routes.HOME ||
        currentRoute == Routes.TOOLS ||
        currentRoute == Routes.SETTINGS


    Scaffold(

        bottomBar = {

            if (showBottomBar) {

                NavigationBar(

                    containerColor =
                        androidx.compose.material3.MaterialTheme
                            .colorScheme.surface,

                    tonalElevation = 8.dp

                ) {

                    bottomItems.forEach { item ->

                        val selected =
                            currentRoute == item.route


                        NavigationBarItem(

                            selected = selected,

                            onClick = {

                                if (currentRoute != item.route) {

                                    navController.navigate(
                                        item.route
                                    ) {

                                        popUpTo(
                                            Routes.HOME
                                        ) {
                                            saveState = true
                                        }

                                        launchSingleTop = true

                                        restoreState = true
                                    }
                                }
                            },


                            icon = {

                                Icon(

                                    imageVector = item.icon,

                                    contentDescription =
                                        item.title,

                                    tint = if (selected) {
                                        when (item.route) {

                                            Routes.HOME ->
                                                HisaabKitPurple

                                            Routes.TOOLS ->
                                                HisaabKitBlue

                                            Routes.SETTINGS ->
                                                HisaabKitGreen

                                            else ->
                                                androidx.compose.material3.MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant
                                        }
                                    } else {
                                        androidx.compose.material3.MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                    }
                                )
                            },


                            label = {

                                Text(

                                    text = item.title,

                                    fontWeight =
                                        if (selected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Medium
                                        }
                                )
                            },


                            colors =
                                NavigationBarItemDefaults.colors(

                                    selectedIconColor =
                                        when (item.route) {

                                            Routes.HOME ->
                                                HisaabKitPurple

                                            Routes.TOOLS ->
                                                HisaabKitBlue

                                            Routes.SETTINGS ->
                                                HisaabKitGreen

                                            else ->
                                                androidx.compose.material3.MaterialTheme
                                                    .colorScheme
                                                    .primary
                                        },

                                    selectedTextColor =
                                        when (item.route) {

                                            Routes.HOME ->
                                                HisaabKitPurple

                                            Routes.TOOLS ->
                                                HisaabKitBlue

                                            Routes.SETTINGS ->
                                                HisaabKitGreen

                                            else ->
                                                androidx.compose.material3.MaterialTheme
                                                    .colorScheme
                                                    .primary
                                        },

                                    indicatorColor =
                                        when (item.route) {

                                            Routes.HOME ->
                                                HisaabKitPurple
                                                    .copy(alpha = 0.14f)

                                            Routes.TOOLS ->
                                                HisaabKitBlue
                                                    .copy(alpha = 0.14f)

                                            Routes.SETTINGS ->
                                                HisaabKitGreen
                                                    .copy(alpha = 0.14f)

                                            else ->
                                                androidx.compose.material3.MaterialTheme
                                                    .colorScheme
                                                    .primaryContainer
                                        },

                                    unselectedIconColor =
                                        androidx.compose.material3.MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant,

                                    unselectedTextColor =
                                        androidx.compose.material3.MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                        )
                    }
                }
            }
        }

    ) { paddingValues ->


        NavHost(

            navController = navController,

            startDestination = Routes.HOME,

            modifier = Modifier.padding(
                paddingValues
            )

        ) {


            // =========================
            // HOME
            // =========================

            composable(Routes.HOME) {

                HomeScreen(

                    onToolClick = { tool ->

                        when (tool) {

                            "EMI Calculator" ->
                                navController.navigate(
                                    Routes.EMI
                                )

                            "Bike Loan EMI" ->
                                navController.navigate(
                                    Routes.BIKE
                                )

                            "EMI Prepayment" ->
                                navController.navigate(
                                    Routes.PREPAYMENT
                                )

                            "Bigha / Biswa" ->
                                navController.navigate(
                                    Routes.LAND
                                )

                            "Photo Resizer" ->
                                navController.navigate(
                                    Routes.PHOTO
                                )
                        }
                    }
                )
            }


            // =========================
            // TOOLS
            // =========================

            composable(Routes.TOOLS) {

                ToolsScreen(

                    onToolClick = { tool ->

                        when (tool) {

                            "EMI Calculator" ->
                                navController.navigate(
                                    Routes.EMI
                                )

                            "Bike Loan EMI" ->
                                navController.navigate(
                                    Routes.BIKE
                                )

                            "EMI Prepayment" ->
                                navController.navigate(
                                    Routes.PREPAYMENT
                                )

                            "Bigha / Biswa" ->
                                navController.navigate(
                                    Routes.LAND
                                )

                            "Photo Resizer" ->
                                navController.navigate(
                                    Routes.PHOTO
                                )
                        }
                    }
                )
            }


            // =========================
            // SETTINGS
            // =========================

            composable(Routes.SETTINGS) {

                SettingsScreen()
            }


            // =========================
            // EMI CALCULATOR
            // =========================

            composable(Routes.EMI) {

                BackHandler {

                    navController.popBackStack()
                }

                EmiScreen()
            }


            // =========================
            // BIKE EMI
            // =========================

            composable(Routes.BIKE) {

                BackHandler {

                    navController.popBackStack()
                }

                BikeEmiScreen()
            }


            // =========================
            // EMI PREPAYMENT
            // =========================

            composable(Routes.PREPAYMENT) {

                BackHandler {

                    navController.popBackStack()
                }

                PrepaymentScreen()
            }


            // =========================
            // LAND CALCULATOR
            // =========================

            composable(Routes.LAND) {

                BackHandler {

                    navController.popBackStack()
                }

                LandCalculatorScreen()
            }


            // =========================
            // PHOTO RESIZER
            // =========================

            composable(Routes.PHOTO) {

                BackHandler {

                    navController.popBackStack()
                }

                PhotoResizerScreen()
            }
        }
    }
}
