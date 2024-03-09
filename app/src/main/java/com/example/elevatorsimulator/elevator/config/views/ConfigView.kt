package com.example.elevatorsimulator.elevator.config.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.elevatorsimulator.R
import com.example.elevatorsimulator.elevator.config.ElevatorConfig
import com.example.elevatorsimulator.uicomponents.NumberInput

@Composable
fun ConfigView() {
    var lowestFloorInput by remember { mutableStateOf("") }
    var highestFloorInput by remember { mutableStateOf("") }
    var speedInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                NumberInputRow(
                    label = stringResource(R.string.set_highest_floor),
                    value = highestFloorInput,
                    onValueChange = {
                        if (it.isBlank()) {
                            highestFloorInput = it
                        } else if (it.toInt() <= 100) {
                            highestFloorInput = it
                        }
                    },
                )
                NumberInputRow(
                    label = stringResource(R.string.set_lowest_floor),
                    value = lowestFloorInput,
                    onValueChange = {
                        if (it.isBlank()) {
                            lowestFloorInput = it
                        } else if (it.toInt() <= 100) {
                            lowestFloorInput = it
                        }
                    },
                )
            }
        }
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = { ElevatorConfig(context).save(lowestFloorInput.toInt(), highestFloorInput.toInt()) }) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
fun NumberInputRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = label,
        )
        NumberInput(
            value = value,
            onValueChange = onValueChange,
        )
    }
}