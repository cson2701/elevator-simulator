package com.example.elevatorsimulator.elevator.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.example.elevatorsimulator.elevator.ElevatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevatorView(elevatorViewModel: ElevatorViewModel) {
    val currentFloor by elevatorViewModel.currentFloor.observeAsState()
    val isTargetFloorReached by elevatorViewModel.isTargetFloorReached.observeAsState()

    var targetFloorInput by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (currentFloor == -99) "Elevator not started" else currentFloor.toString(),
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
                placeholder = { Text(text = "Target floor") }
            )
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Go!")
            }
        }
    }
}
