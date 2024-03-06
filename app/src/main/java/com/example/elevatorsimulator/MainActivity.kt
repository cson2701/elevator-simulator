package com.example.elevatorsimulator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.elevatorsimulator.elevator.ElevatorViewModel
import com.example.elevatorsimulator.elevator.config.ConfigActivity
import com.example.elevatorsimulator.elevator.views.ElevatorView
import com.example.elevatorsimulator.ui.theme.ElevatorSimulatorTheme

class MainActivity : ComponentActivity() {
    private val elevatorViewModel by viewModels<ElevatorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElevatorSimulatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ElevatorView(elevatorViewModel)
                }
            }
        }
        startActivity(Intent(this, ConfigActivity::class.java))
    }
}