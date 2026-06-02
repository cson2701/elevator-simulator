package com.example.elevatorsimulator.elevator.view.compose

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elevatorsimulator.R
import com.example.elevatorsimulator.elevator.ElevatorProps
import com.example.elevatorsimulator.uicomponents.PowerIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevatorScreen(
    currentFloor: Int,
    elevatorStatus: ElevatorProps.Status,
    highestFloor: Int,
    lowestFloor: Int,
    openDoor: Boolean,
    onDoorStateChange: (ElevatorDoorState) -> Unit,
    move: (targetFloor: Int) -> Unit,
    powerOn: () -> Unit,
    powerOff: () -> Unit,
    onConfigClick: () -> Unit,
) {
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
            isPowerOn = elevatorStatus != ElevatorProps.Status.POWER_OFF,
            modifier = Modifier
                .size(128.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, radius = 64.dp),
                ) {
                    if (elevatorStatus == ElevatorProps.Status.POWER_OFF) {
                        powerOn()
                    } else {
                        powerOff()
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
                ElevatorProps.Status.MOVING_UP -> "⬆︎"

                ElevatorProps.Status.MOVING_DOWN -> "⬇︎"

                else -> ""
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
                text = "H:$highestFloor\nL:$lowestFloor",
            )
        }

        ElevatorDoorContent(
            openDoor = openDoor,
            onDoorStateChange = onDoorStateChange
        )

        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(
            text = when (elevatorStatus) {
                ElevatorProps.Status.DOOR_OPENING ->
                    "Door opening..."

                ElevatorProps.Status.DOOR_OPEN ->
                    "Door open"

                ElevatorProps.Status.DOOR_CLOSING ->
                    "Door closing..."

                else -> {
                    ""
                }
            },
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
                    move(targetFloorInput.toInt())
                    targetFloorInput = ""
                }
            ) {
                Text(text = "Go!")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onConfigClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Config"
                )
            }
        }
    }
}

@Composable
@Preview
fun ElevatorScreenPreview() {
    ElevatorScreen(
        currentFloor = 5,
        elevatorStatus = ElevatorProps.Status.IDLE,
        highestFloor = 10,
        lowestFloor = 1,
        openDoor = false,
        onDoorStateChange = {},
        move = {},
        powerOn = {},
        powerOff = {},
        onConfigClick = {},
    )
}