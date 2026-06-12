package com.scrapps.elevatorsimulator.elevator.config.view.compose

import android.content.Intent
import android.widget.NumberPicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.scrapps.elevatorsimulator.R
import com.scrapps.elevatorsimulator.elevator.config.ElevatorConfig
import com.scrapps.elevatorsimulator.elevator.view.ElevatorActivity


@Composable
fun ConfigView() {
    val context = LocalContext.current
    val elevatorConfig = ElevatorConfig.getInstance()
    val savedHighestFloor = elevatorConfig.getHighestFloor()
    val savedLowestFloor = elevatorConfig.getLowestFloor()
    val savedSpeed = elevatorConfig.getSpeed()
    val savedDoorOpenDuration = elevatorConfig.getDoorOpenDuration()
    val savedDoorCloseDuration = elevatorConfig.getDoorCloseDuration()
    val savedPlaySound = elevatorConfig.getPlaySound()
    val savedAnnounceFloor = elevatorConfig.getAnnounceFloor()

    var lowestFloorInput by remember { mutableIntStateOf(savedLowestFloor.coerceIn(-10, 99)) }
    var highestFloorInput by remember { mutableIntStateOf(savedHighestFloor.coerceIn(-10, 99)) }
    var speedInput by remember { mutableFloatStateOf(savedSpeed.toFloat().coerceIn(500f, 5000f)) }
    var doorOpenInput by remember { mutableFloatStateOf(savedDoorOpenDuration.toFloat().coerceIn(1000f, 5000f)) }
    var doorCloseInput by remember { mutableFloatStateOf(savedDoorCloseDuration.toFloat().coerceIn(1000f, 5000f)) }
    var playSound by remember { mutableStateOf(savedPlaySound) }
    var announceFloor by remember { mutableStateOf(savedAnnounceFloor) }

    var showFloorDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            SectionHeader(title = stringResource(R.string.section_floors))
            ListItem(
                modifier = Modifier.clickable { showFloorDialog = true },
                headlineContent = { Text(text = stringResource(R.string.floor_range)) },
                supportingContent = {
                    Text(
                        text = stringResource(
                            R.string.lowest
                        ) + ": $lowestFloorInput | " + stringResource(R.string.highest) + ": $highestFloorInput"
                    )
                }
            )

            HorizontalDivider()
            SectionHeader(title = stringResource(R.string.section_timings))
            SliderRow(
                label = stringResource(R.string.elevator_speed),
                value = speedInput,
                onValueChange = { speedInput = it },
                valueRange = 500f..5000f,
                steps = 17, // 250ms intervals
                unit = "ms/floor"
            )
            SliderRow(
                label = stringResource(R.string.door_opening_duration),
                value = doorOpenInput,
                onValueChange = { doorOpenInput = it },
                valueRange = 1000f..5000f,
                steps = 7, // 500ms intervals
                unit = "ms"
            )
            SliderRow(
                label = stringResource(R.string.door_closing_duration),
                value = doorCloseInput,
                onValueChange = { doorCloseInput = it },
                valueRange = 1000f..5000f,
                steps = 7,
                unit = "ms"
            )
            HorizontalDivider()
            SectionHeader(title = stringResource(R.string.section_audio))
            SwitchRow(
                label = stringResource(R.string.play_sound),
                checked = playSound,
                onCheckedChange = { playSound = it }
            )
            SwitchRow(
                label = stringResource(R.string.announce_floor),
                checked = announceFloor,
                onCheckedChange = { announceFloor = it }
            )
        }

        if (showFloorDialog) {
            FloorRangeDialog(
                currentLowest = lowestFloorInput,
                currentHighest = highestFloorInput,
                onDismiss = { showFloorDialog = false },
                onConfirm = { low, high ->
                    lowestFloorInput = low
                    highestFloorInput = high
                    showFloorDialog = false
                }
            )
        }

        val onSaveClick: () -> Unit = {
            val speed = speedInput.toLong()
            val doorOpen = doorOpenInput.toLong()
            val doorClose = doorCloseInput.toLong()

            if (lowestFloorInput < highestFloorInput) {
                if (elevatorConfig.save(
                        lowestFloorInput,
                        highestFloorInput,
                        speed,
                        doorOpen,
                        doorClose,
                        playSound,
                        announceFloor
                    )
                ) {
                    val intent = Intent(context, ElevatorActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
            } else {
                Toast.makeText(
                    context,
                    "Lowest floor must be lower than highest floor",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        Button(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = { onSaveClick() }
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
fun FloorRangeDialog(
    currentLowest: Int,
    currentHighest: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var tempLow by remember { mutableIntStateOf(currentLowest.coerceIn(-10, 98)) }
    var tempHigh by remember { mutableIntStateOf(currentHighest.coerceIn(tempLow + 1, 99)) }

    val fullRange = remember { (-10..99).map { it.toString() }.toTypedArray() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.floor_range)) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.set_lowest_floor))
                    AndroidView(
                        factory = { context ->
                            NumberPicker(context).apply {
                                wrapSelectorWheel = false
                                setOnValueChangedListener { _, _, newVal ->
                                    tempLow = newVal - 10
                                }
                            }
                        },
                        update = { view ->
                            val newMin = 0
                            val newMax = (tempHigh - 1) + 10
                            view.displayedValues = null
                            view.minValue = newMin
                            view.maxValue = newMax
                            view.displayedValues = fullRange.sliceArray(newMin..newMax)
                            view.value = tempLow + 10
                        }
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.set_highest_floor))
                    AndroidView(
                        factory = { context ->
                            NumberPicker(context).apply {
                                wrapSelectorWheel = false
                                setOnValueChangedListener { _, _, newVal ->
                                    tempHigh = newVal - 10
                                }
                            }
                        },
                        update = { view ->
                            val newMin = (tempLow + 1) + 10
                            val newMax = 109
                            view.displayedValues = null
                            view.minValue = newMin
                            view.maxValue = newMax
                            view.displayedValues = fullRange.sliceArray(newMin..newMax)
                            view.value = tempHigh + 10
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(tempLow, tempHigh) }) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}


@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp)
    )
}

@Composable
fun SliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    unit: String = ""
) {
    ListItem(
        headlineContent = { Text(text = label) },
        supportingContent = {
            Text(
                text = "${value.toInt()} $unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Slider(
                modifier = Modifier.fillMaxWidth(0.5f),
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps
            )
        }
    )
}

@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        modifier = Modifier.toggleable(
            value = checked,
            role = Role.Switch,
            onValueChange = onCheckedChange
        ),
        headlineContent = { Text(text = label) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null // Handled by toggleable row
            )
        }
    )
}
