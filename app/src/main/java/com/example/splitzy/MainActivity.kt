package com.example.splitzy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.splitzy.presentation.navigation.SplitzyNavHost
import com.example.splitzy.ui.theme.SplitzyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplitzyTheme {
                SplitzyNavHost()
            }
        }
    }
}