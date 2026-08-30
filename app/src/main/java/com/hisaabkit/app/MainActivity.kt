package com.hisaabkit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hisaabkit.app.navigation.AppNavigation
import com.hisaabkit.app.ui.theme.HisaabKitTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            HisaabKitTheme {
                AppNavigation()
            }
        }
    }
}
