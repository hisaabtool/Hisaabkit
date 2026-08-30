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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            Routes.HOME,
            "Home",
            Icons.Default.Home
        ),
        BottomItem(
            Routes.TOOLS,
            "Tools",
            Icons.Default.Calculate
        ),
        BottomItem(
            Routes.SETTINGS,
            "Settings",
            Icons.Default.Settings
        )
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar =
        currentRoute == Routes.HOME ||
        currentRoute == Routes.TOOLS ||
        currentRoute == Routes.SETTINGS

    Scaffold(
        bottomBar = {

            if (showBottomBar) {

                NavigationBar {

                    bottomItems.forEach { item ->

                        NavigationBarItem(
                            selected = currentRoute == item.route,

                            onClick = {

                                navController.navigate(item.route) {

                                    popUpTo(Routes.HOME) {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },

                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },

                            label = {
                                Text(item.title)
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Routes.HOME) {

                HomeScreen(
                    onToolClick = { tool ->

                        when (tool) {

                            "EMI Calculator" ->
                                navController.navigate(Routes.EMI)

                            "Bike Loan EMI" ->
                                navController.navigate(Routes.BIKE)

                            "EMI Prepayment" ->
                                navController.navigate(Routes.PREPAYMENT)

                            "Bigha / Biswa" ->
                                navController.navigate(Routes.LAND)

                            "Photo Resizer" ->
                                navController.navigate(Routes.PHOTO)
                        }
                    }
                )
            }

            composable(Routes.TOOLS) {

                ToolsScreen(
                    onToolClick = { tool ->

                        when (tool) {

                            "EMI Calculator" ->
                                navController.navigate(Routes.EMI)

                            "Bike Loan EMI" ->
                                navController.navigate(Routes.BIKE)

                            "EMI Prepayment" ->
                                navController.navigate(Routes.PREPAYMENT)

                            "Bigha / Biswa" ->
                                navController.navigate(Routes.LAND)

                            "Photo Resizer" ->
                                navController.navigate(Routes.PHOTO)
                        }
                    }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen()
            }

            composable(Routes.EMI) {

                BackHandler {
                    navController.popBackStack()
                }

                EmiScreen()
            }

            composable(Routes.BIKE) {

                BackHandler {
                    navController.popBackStack()
                }

                BikeEmiScreen()
            }

            composable(Routes.PREPAYMENT) {

                BackHandler {
                    navController.popBackStack()
                }

                PrepaymentScreen()
            }

            composable(Routes.LAND) {

                BackHandler {
                    navController.popBackStack()
                }

                LandCalculatorScreen()
            }

            composable(Routes.PHOTO) {

                BackHandler {
                    navController.popBackStack()
                }

                PhotoResizerScreen()
            }
        }
    }
}
