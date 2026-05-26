package com.example.jetlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jetlab.ui.JetLabApp
import com.example.jetlab.ui.theme.JetLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetLabTheme {
                JetLabApp()
            }
        }
    }
}
