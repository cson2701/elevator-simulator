package com.example.elevatorsimulator.elevator.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.elevatorsimulator.elevator.ElevatorViewModel

@Composable
fun ElevatorView(elevatorViewModel: ElevatorViewModel) {
    val currentFloor by elevatorViewModel.currentFloor.observeAsState(Int)

    Text(
        text = if (currentFloor == -99) "Elevator not started" else currentFloor.toString(),
    )
}
