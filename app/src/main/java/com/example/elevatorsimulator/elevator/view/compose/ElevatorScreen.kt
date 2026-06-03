package com.example.elevatorsimulator.elevator.view.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elevatorsimulator.R
import com.example.elevatorsimulator.elevator.ElevatorProps
import com.example.elevatorsimulator.ui.theme.colors.Default
import com.example.elevatorsimulator.ui.theme.colors.Disabled
import com.example.elevatorsimulator.ui.theme.colors.FloorButton
import com.example.elevatorsimulator.ui.theme.colors.Pressed
import com.example.elevatorsimulator.uicomponents.PowerIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevatorScreen(
    currentFloor: Int,
    elevatorStatus: ElevatorProps.Status,
    highestFloor: Int,
    lowestFloor: Int,
    openDoor: Boolean,
    floorsInQueue: List<Int>,
    onDoorStateChange: (ElevatorDoorState) -> Unit,
    onFloorPressed: (targetFloor: Int) -> Unit,
    powerOn: () -> Unit,
    powerOff: () -> Unit,
    onConfigClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = when (elevatorStatus) {
                        ElevatorProps.Status.MOVING_UP -> "⬆︎"

                        ElevatorProps.Status.MOVING_DOWN -> "⬇︎"

                        else -> ""
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                PowerIcon(
                    isPowerOn = elevatorStatus != ElevatorProps.Status.POWER_OFF,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = false, radius = 32.dp),
                        ) {
                            if (elevatorStatus == ElevatorProps.Status.POWER_OFF) {
                                powerOn()
                            } else {
                                powerOff()
                            }
                        }
                )
            }
        }

        ElevatorDoorContent(
            openDoor = openDoor,
            elevatorStatus = elevatorStatus,
            onDoorStateChange = onDoorStateChange
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Floor Selection",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                FloorButtonsRow(
                    highestFloor = highestFloor,
                    lowestFloor = lowestFloor,
                    floorsInQueue = floorsInQueue,
                    onFloorClick = { onFloorPressed(it) },
                    enabled = elevatorStatus != ElevatorProps.Status.POWER_OFF
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FloorButtonsRow(
    highestFloor: Int,
    lowestFloor: Int,
    floorsInQueue: List<Int>,
    onFloorClick: (Int) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val floors = remember(highestFloor, lowestFloor) {
        (lowestFloor..highestFloor).toList()
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        floors.forEach { floor ->
            val isPressed = floorsInQueue.contains(floor)
            OutlinedButton(
                onClick = { onFloorClick(floor) },
                enabled = enabled,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .size(44.dp),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(
                    width = 1.5.dp,
                    color = when {
                        !enabled -> FloorButton.Border.Disabled
                        isPressed -> FloorButton.Border.Pressed
                        else -> FloorButton.Border.Default
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = FloorButton.Background.Default,
                    contentColor = if (isPressed) FloorButton.Text.Pressed else FloorButton.Text.Default,
                    disabledContainerColor = FloorButton.Background.Disabled,
                    disabledContentColor = FloorButton.Text.Disabled
                )
            ) {
                Text(
                    text = floor.toString(),
                    fontSize = 14.sp
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
        floorsInQueue = listOf(7, 8, 9),
        onDoorStateChange = {},
        onFloorPressed = {},
        powerOn = {},
        powerOff = {},
        onConfigClick = {},
    )
}