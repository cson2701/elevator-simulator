package com.example.elevatorsimulator.elevator.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.elevatorsimulator.elevator.ElevatorProps
import com.example.elevatorsimulator.elevator.ElevatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevatorView(elevatorViewModel: ElevatorViewModel) {
    val currentFloor by elevatorViewModel.currentFloor.observeAsState()
    val isTargetFloorReached by elevatorViewModel.isTargetFloorReached.observeAsState()
    val elevatorStatus by elevatorViewModel.elevatorStatus.observeAsState()

    var targetFloorInput by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = when (elevatorStatus) {
                ElevatorProps.Status.MOVING_UP -> {
                    "^"
                }
                ElevatorProps.Status.MOVING_DOWN -> {
                    "v"
                }
                else -> {
                    ""
                }
            },
        )
        Text(
            text = if (currentFloor == -99) "--" else currentFloor.toString(),
        )
        Text(
            text = if (isTargetFloorReached == true) "Ding!" else "",
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            TextField(
                modifier = Modifier.padding(8.dp),
                value = targetFloorInput,
                onValueChange = { targetFloorInput = it },
                placeholder = { Text(text = "Target floor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    elevatorViewModel.move(targetFloorInput.toInt())
                }
            ) {
                Text(text = "Go!")
            }
        }
    }
}
