package com.example.elevatorsimulator.elevator.config.views

import android.content.Intent
import android.widget.Toast
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
import com.example.elevatorsimulator.MainActivity
import com.example.elevatorsimulator.R
import com.example.elevatorsimulator.elevator.config.ElevatorConfig
import com.example.elevatorsimulator.uicomponents.NumberInput


@Composable
fun ConfigView() {
    var lowestFloorInput by remember { mutableStateOf("") }
    var highestFloorInput by remember { mutableStateOf("") }
    var speedInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    val elevatorConfig = ElevatorConfig()
    val savedHighestFloor = elevatorConfig.getHighestFloor()
    val savedLowestFloor = elevatorConfig.getLowestFloor()

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
                    placeholder = savedHighestFloor.toString(),
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
                    placeholder = savedLowestFloor.toString(),
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
        val onSaveClick: () -> Unit = {
            val lowestFloor =
                lowestFloorInput.takeIf { it.isNotBlank() }?.toInt() ?: savedLowestFloor
            val highestFloor =
                highestFloorInput.takeIf { it.isNotBlank() }?.toInt() ?: savedHighestFloor
            if (lowestFloor == 0 || highestFloor == 0) {
                Toast.makeText(
                    context,
                    "Please set lowest and highest floor",
                    Toast.LENGTH_LONG
                ).show()
            } else if (lowestFloor < highestFloor) {
                if (elevatorConfig.save(lowestFloor, highestFloor)) {
                    val intent = Intent(context, MainActivity::class.java)
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
                .wrapContentSize()
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = { onSaveClick() }
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}


@Composable
fun NumberInputRow(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = label,
        )
        NumberInput(
            placeholder = placeholder,
            value = value,
            onValueChange = onValueChange,
        )
    }
}