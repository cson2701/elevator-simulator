package com.example.elevatorsimulator.elevator.config.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.elevatorsimulator.uicomponents.NumberInput

@OptIn(ExperimentalMaterial3Api::class)
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
                    label = "Set highest floor",
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
                    label = "Set lowest floor",
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
        Button(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = { /*TODO*/ }) {
            Text(text = "Save")
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