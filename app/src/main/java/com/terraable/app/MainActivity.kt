package com.terraable.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.terraable.app.feature.main.MainScreen
import com.terraable.app.ui.theme.TerraAbleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TerraAbleTheme {
                MainScreen()
            }
        }
    }
}
