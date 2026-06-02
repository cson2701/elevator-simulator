package com.example.elevatorsimulator.elevator.view.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.elevatorsimulator.ui.theme.colors.Default
import com.example.elevatorsimulator.ui.theme.colors.ElevatorDoor
import kotlinx.coroutines.delay

@Composable
fun ElevatorDoorContent(
    openDoor: Boolean,
    modifier: Modifier = Modifier,
    onDoorStateChange: (ElevatorDoorState) -> Unit,
) {
    val progress by animateFloatAsState(
        targetValue = if (openDoor) 1f else 0f,
        animationSpec = tween(durationMillis = if (openDoor) DOOR_OPEN_DURATION.toInt() else DOOR_CLOSE_DURATION.toInt()),
        label = "door",
        finishedListener = { finalValue ->
            if (finalValue == 1f) {
                onDoorStateChange(ElevatorDoorState.OPEN)
            } else if (finalValue == 0f) {
                onDoorStateChange(ElevatorDoorState.CLOSED)
            }
        }
    )

    LaunchedEffect(openDoor) {
        if (openDoor) {
            onDoorStateChange(ElevatorDoorState.OPENING)
        } else {
            onDoorStateChange(ElevatorDoorState.CLOSING)
        }
    }

    val panelColor = ElevatorDoor.Panel.Default
    val borderColor = ElevatorDoor.Border.Default

    Canvas(
        modifier = modifier
            .size(width = 200.dp, height = 300.dp)
    ) {
        val doorWidth = size.width / 2f
        val doorHeight = size.height

        // How far each panel moves
        val offset = doorWidth * progress

        // Left door
        drawRect(
            color = panelColor,
            topLeft = Offset(x = -offset, y = 0f),
            size = Size(doorWidth, doorHeight)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(x = -offset, y = 0f),
            size = Size(doorWidth, doorHeight),
            style = Stroke(width = 4f)
        )

        // Right door
        drawRect(
            color = panelColor,
            topLeft = Offset(
                x = doorWidth + offset,
                y = 0f
            ),
            size = Size(doorWidth, doorHeight)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(
                x = doorWidth + offset,
                y = 0f
            ),
            size = Size(doorWidth, doorHeight),
            style = Stroke(width = 4f)
        )
    }
}

@Composable
@Preview
fun ElevatorDoorContentPreview(
    @PreviewParameter(ElevatorDoorContentPreviewProvider::class)
    isOpen: Boolean
) {
    var isOpenState by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            isOpenState = !isOpenState
        }
    }
    ElevatorDoorContent(openDoor = isOpenState) {}
}

private class ElevatorDoorContentPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}

const val DOOR_OPEN_DURATION = 1200L
const val DOOR_CLOSE_DURATION = 1500L

enum class ElevatorDoorState {
    OPENING,
    OPENING_STUCK,
    OPEN,
    CLOSING,
    CLOSING_STUCK,
    CLOSED
}