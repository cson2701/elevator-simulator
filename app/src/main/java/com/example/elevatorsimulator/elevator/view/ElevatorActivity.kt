package com.example.elevatorsimulator.elevator.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.elevatorsimulator.elevator.config.ElevatorConfig
import com.example.elevatorsimulator.elevator.config.view.ConfigActivity
import com.example.elevatorsimulator.elevator.view.compose.ElevatorView
import com.example.elevatorsimulator.elevator.view.viewmodel.ElevatorViewModel
import com.example.elevatorsimulator.ui.theme.ElevatorSimulatorTheme

class ElevatorActivity : ComponentActivity() {
    private val elevatorViewModel by viewModels<ElevatorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentFloor by elevatorViewModel.currentFloor.collectAsState()
            val isTargetFloorReached by elevatorViewModel.isTargetFloorReached.collectAsState()
            val elevatorStatus by elevatorViewModel.elevatorStatus.collectAsState()

            ElevatorSimulatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ElevatorView(
                        currentFloor = currentFloor,
                        isTargetFloorReached = isTargetFloorReached,
                        elevatorStatus = elevatorStatus,
                        highestFloor = elevatorViewModel.getHighestFloor(),
                        lowestFloor = elevatorViewModel.getLowestFloor(),
                        powerOn = ::powerOn,
                        powerOff = ::powerOff,
                        move = { targetFloor -> elevatorViewModel.move(targetFloor) }
                    )
                }
            }
        }
    }

    private fun powerOn() {
        showToast("Powering on...")
        elevatorViewModel.powerOn()
    }

    private fun powerOff() {
        showToast("Powering off...")
        elevatorViewModel.powerOff()
    }

    private fun showToast(message: String) {
        Toast
            .makeText(this, message, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (ElevatorConfig.getLowestFloor() == 0 || ElevatorConfig.getHighestFloor() == 0) {
            startActivity(Intent(this, ConfigActivity::class.java))
            finish()
        }
    }
}