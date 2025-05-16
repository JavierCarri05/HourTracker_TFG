package com.example.hourtracker_tfg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import com.example.hourtracker_tfg.Navegation.NavigationScreens
import com.example.hourtracker_tfg.ui.theme.HourTracker_TFGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContent {
            HourTracker_TFGTheme(
            ) {
                NavigationScreens()
            }
        }
    }
}

