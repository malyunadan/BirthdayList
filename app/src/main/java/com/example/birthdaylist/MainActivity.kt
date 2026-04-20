package com.example.birthdaylist

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.birthdaylist.ui.theme.BirthdayListTheme
import com.example.birthdaylist.ui.view.BirthdayApp

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BirthdayListTheme {
                BirthdayApp()
            }
        }
    }
}




