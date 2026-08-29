package com.hisaabkit.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hisaabkit.app.ui.screens.HomeScreen
import com.hisaabkit.app.tools.bike.BikeEmiScreen
import com.hisaabkit.app.tools.emi.EmiScreen
import com.hisaabkit.app.tools.land.LandCalculatorScreen
import com.hisaabkit.app.tools.photo.PhotoResizerScreen
import com.hisaabkit.app.tools.prepayment.PrepaymentScreen

object Routes {
    const val HOME = "home"
    const val EMI = "emi"
    const val BIKE = "bike"
    const val PREPAYMENT = "prepayment"
    const val LAND = "land"
    const val PHOTO = "photo"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
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

        composable(Routes.EMI) {
            EmiScreen()
        }

        composable(Routes.BIKE) {
            BikeEmiScreen()
        }

        composable(Routes.PREPAYMENT) {
            PrepaymentScreen()
        }

        composable(Routes.LAND) {
            LandCalculatorScreen()
        }

        composable(Routes.PHOTO) {
            PhotoResizerScreen()
        }
    }
}
