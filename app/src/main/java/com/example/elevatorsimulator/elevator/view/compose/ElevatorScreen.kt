package com.example.elevatorsimulator.elevator.view.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.elevatorsimulator.uicomponents.SevenSegmentPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevatorScreen(
    currentFloor: Int,
    elevatorStatus: ElevatorProps.Status,
    serviceDirection: ElevatorProps.ServiceDirection,
    highestFloor: Int,
    lowestFloor: Int,
    openDoor: Boolean,
    floorsInQueue: List<Int>,
    logs: List<String>,
    onDoorStateChange: (ElevatorDoorState) -> Unit,
    onFloorPressed: (targetFloor: Int) -> Unit,
    powerOn: () -> Unit,
    powerOff: () -> Unit,
    onOpenDoor: () -> Unit,
    onCloseDoor: () -> Unit,
    onConfigClick: () -> Unit,
    onAlarmClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Indicator Panel
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            SevenSegmentPanel(
                value = if (elevatorStatus == ElevatorProps.Status.POWER_OFF) null else currentFloor,
                serviceDirection = serviceDirection,
                modifier = Modifier.width(110.dp)
            )
        }

        // Door Area
        ElevatorDoorContent(
            openDoor = openDoor,
            elevatorStatus = elevatorStatus,
            onDoorStateChange = onDoorStateChange
        )

        // Operational Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OperationButton(
                onClick = onOpenDoor,
                enabled = elevatorStatus != ElevatorProps.Status.POWER_OFF
            ) {
                Text("<|>", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OperationButton(
                onClick = onCloseDoor,
                enabled = elevatorStatus != ElevatorProps.Status.POWER_OFF
            ) {
                Text(">|<", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OperationButton(
                onClick = onAlarmClick,
                enabled = elevatorStatus != ElevatorProps.Status.POWER_OFF
            ) {
                Text("🔔", fontSize = 20.sp)
            }
        }

        // Scrollable Floor Buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloorButtonsRow(
                    highestFloor = highestFloor,
                    lowestFloor = lowestFloor,
                    floorsInQueue = floorsInQueue,
                    onFloorClick = { onFloorPressed(it) },
                    enabled = elevatorStatus != ElevatorProps.Status.POWER_OFF
                )
            }
        }

        // Bottom Operations Bar
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Power Button (Square)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PowerIcon(
                        isPowerOn = elevatorStatus != ElevatorProps.Status.POWER_OFF,
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            if (elevatorStatus == ElevatorProps.Status.POWER_OFF) powerOn() else powerOff()
                        }
                    )
                }

                // Fixed Config Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onConfigClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Config",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Logs Section
            LogsSection(
                logs = logs,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun OperationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        content()
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
fun LogsSection(logs: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                logs.forEach { log ->
                    Text(
                        text = log,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ElevatorScreenPreview() {
    ElevatorScreen(
        currentFloor = 24,
        elevatorStatus = ElevatorProps.Status.IDLE,
        serviceDirection = ElevatorProps.ServiceDirection.UP,
        highestFloor = 10,
        lowestFloor = 1,
        openDoor = false,
        floorsInQueue = listOf(7, 8, 9),
        logs = listOf("[12:00:00] Status: IDLE", "[12:00:01] Door State: OPENING"),
        onDoorStateChange = {},
        onFloorPressed = {},
        powerOn = {},
        powerOff = {},
        onOpenDoor = {},
        onCloseDoor = {},
        onConfigClick = {},
    )
}
