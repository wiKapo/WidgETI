package com.wikapo.widgeti

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wikapo.widgeti.ui.theme.WidgETITheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val date: LocalDate? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable("date", LocalDate::class.java)
        } else {
            intent.extras?.getSerializable("date") as LocalDate?
        }
        setContent {
            WidgETITheme {
                WidgETIApp(startingDate = date)
            }
        }
    }
}