package com.example.elevatorsimulator.elevator.views

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elevatorsimulator.R
import com.example.elevatorsimulator.elevator.ElevatorProps
import com.example.elevatorsimulator.elevator.ElevatorViewModel
import com.example.elevatorsimulator.elevator.config.ConfigActivity
import com.example.elevatorsimulator.uicomponents.PowerIcon
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevatorView(elevatorViewModel: ElevatorViewModel) {
    val currentFloor by elevatorViewModel.currentFloor.observeAsState()
    val isTargetFloorReached by elevatorViewModel.isTargetFloorReached.observeAsState()
    val elevatorStatus by elevatorViewModel.elevatorStatus.observeAsState()
    val context = LocalContext.current

    var targetFloorInput by remember {
        mutableStateOf("")
    }
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        PowerIcon(
            isPowerOff = elevatorStatus == ElevatorProps.Status.POWER_OFF,
            modifier = Modifier
                .size(128.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, radius = 64.dp),
                ) {
                    if (elevatorStatus == ElevatorProps.Status.POWER_OFF) {
                        Toast
                            .makeText(context, "Powering on...", Toast.LENGTH_SHORT)
                            .show()
                        elevatorViewModel.powerOn()
                    } else {
                        elevatorViewModel.powerOff()
                    }
                }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = when (elevatorStatus) {
                ElevatorProps.Status.MOVING_UP -> {
                    "⬆︎"
//                    "◭"
                }

                ElevatorProps.Status.MOVING_DOWN -> {
                    "⬇︎"
//                    "⧩"
                }

                else -> {
                    ""
                }
            },
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (elevatorStatus == ElevatorProps.Status.POWER_OFF) "--" else currentFloor.toString(),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "H:${elevatorViewModel.getHighestFloor()}\nL:${elevatorViewModel.getLowestFloor()}",
            )
        }

        var ding by remember {
            mutableStateOf("")
        }
        LaunchedEffect(isTargetFloorReached) {
            ding = if (isTargetFloorReached == true) {
                "Ding!"
            } else {
                ""
            }
            delay(2000)
            ding = ""
        }
        Text(
            text = ding,
        )

        var doorStatus by remember {
            mutableStateOf("")
        }
        LaunchedEffect(elevatorStatus) {
            doorStatus = when (elevatorStatus) {
                ElevatorProps.Status.DOOR_OPENING ->
                    "Door opening..."

                ElevatorProps.Status.DOOR_OPEN ->
                    "Door open"

                ElevatorProps.Status.DOOR_CLOSING ->
                    "Door closing..."

                else -> {
                    ""
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(
            text = doorStatus,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                singleLine = true,
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp),
                value = targetFloorInput,
                onValueChange = { targetFloorInput = it },
                placeholder = { Text(text = stringResource(R.string.target_floor)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                enabled = elevatorStatus == ElevatorProps.Status.IDLE && targetFloorInput.isNotBlank(),
                modifier = Modifier.padding(8.dp),
                onClick = {
                    elevatorViewModel.move(targetFloorInput.toInt())
                    targetFloorInput = ""
                }
            ) {
                Text(text = "Go!")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, ConfigActivity::class.java))
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Config"
                )
            }
        }
    }
}
