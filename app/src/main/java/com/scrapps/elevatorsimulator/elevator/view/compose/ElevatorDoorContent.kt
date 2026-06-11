package com.scrapps.elevatorsimulator.elevator.view.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.scrapps.elevatorsimulator.elevator.ElevatorProps
import com.scrapps.elevatorsimulator.ui.theme.colors.Default
import com.scrapps.elevatorsimulator.ui.theme.colors.ElevatorDoor

@Composable
fun ElevatorDoorContent(
    openDoor: Boolean,
    elevatorStatus: ElevatorProps.Status,
    modifier: Modifier = Modifier,
    onDoorStateChange: (ElevatorDoorState) -> Unit,
) {
    val currentOnDoorStateChange by rememberUpdatedState(onDoorStateChange)

    val progress by animateFloatAsState(
        targetValue = if (openDoor) 1f else 0f,
        animationSpec = tween(durationMillis = if (openDoor) DOOR_OPEN_DURATION.toInt() else DOOR_CLOSE_DURATION.toInt()),
        label = "door",
        finishedListener = { finalValue ->
            if (finalValue == 1f) {
                currentOnDoorStateChange(ElevatorDoorState.OPEN)
            } else if (finalValue == 0f) {
                currentOnDoorStateChange(ElevatorDoorState.CLOSED)
            }
        }
    )

    // Handle rotation or re-composition where the animation finished listener might be missed.
    // We only report if the elevator is currently in an intermediate state but the animation is at the target.
    LaunchedEffect(openDoor, elevatorStatus) {
        if (openDoor && elevatorStatus == ElevatorProps.Status.DOOR_OPENING && progress == 1f) {
            currentOnDoorStateChange(ElevatorDoorState.OPEN)
        } else if (!openDoor && elevatorStatus == ElevatorProps.Status.DOOR_CLOSING && progress == 0f) {
            currentOnDoorStateChange(ElevatorDoorState.CLOSED)
        }
    }

    val panelColor = ElevatorDoor.Panel.Default
    val borderColor = ElevatorDoor.Border.Default
    val coverColor = ElevatorDoor.Cover.Default
    val interiorColor = Color(0xFFEEEEEE) // Bright interior (Light Grey)

    Canvas(
        modifier = modifier
            .size(width = 320.dp, height = 300.dp)
    ) {
        val totalWidth = size.width
        val doorWidth = totalWidth / 4f
        val doorHeight = size.height
        val offset = doorWidth * progress

        // 1. Draw Elevator Interior (Visible when doors are open)
        // Using a vertical gradient to simulate overhead lighting
        val interiorGradient = Brush.verticalGradient(
            colors = listOf(Color.White, interiorColor),
            startY = 0f,
            endY = doorHeight
        )
        drawRect(
            brush = interiorGradient,
            topLeft = Offset(x = doorWidth, y = 0f),
            size = Size(doorWidth * 2, doorHeight)
        )

        // Metallic gradient for panels (Solid colors, no alpha)
        val panelGradient = Brush.linearGradient(
            colors = listOf(panelColor, panelColor, panelColor),
            start = Offset(0f, 0f),
            end = Offset(doorWidth, 0f)
        )

        // 2. Left door panel
        val leftDoorX = doorWidth - offset
        drawRect(
            brush = panelGradient,
            topLeft = Offset(x = leftDoorX, y = 0f),
            size = Size(doorWidth, doorHeight)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(x = leftDoorX, y = 0f),
            size = Size(doorWidth, doorHeight),
            style = Stroke(width = 2f)
        )

        // 3. Right door panel
        val rightDoorX = 2 * doorWidth + offset
        drawRect(
            brush = panelGradient,
            topLeft = Offset(x = rightDoorX, y = 0f),
            size = Size(doorWidth, doorHeight)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(x = rightDoorX, y = 0f),
            size = Size(doorWidth, doorHeight),
            style = Stroke(width = 2f)
        )

        // 4. Shadows under the covers (Cast onto the opening/interior)
        val shadowWidth = 15f
        // Shadow from left cover
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Black.copy(alpha = 0.4f),
                1f to Color.Transparent,
                startX = doorWidth,
                endX = doorWidth + shadowWidth
            ),
            topLeft = Offset(doorWidth, 0f),
            size = Size(shadowWidth, doorHeight)
        )
        // Shadow from right cover
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Transparent,
                1f to Color.Black.copy(alpha = 0.4f),
                startX = 3 * doorWidth - shadowWidth,
                endX = 3 * doorWidth
            ),
            topLeft = Offset(3 * doorWidth - shadowWidth, 0f),
            size = Size(shadowWidth, doorHeight)
        )

        // 5. Covers (The walls - Solid colors, no alpha)
        val coverGradient = Brush.linearGradient(
            colors = listOf(coverColor, coverColor),
            start = Offset(0f, 0f),
            end = Offset(doorWidth, 0f)
        )

        // Left cover
        drawRect(
            brush = coverGradient,
            topLeft = Offset(x = 0f, y = 0f),
            size = Size(doorWidth, doorHeight)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(x = 0f, y = 0f),
            size = Size(doorWidth, doorHeight),
            style = Stroke(width = 2f)
        )

        // Right cover
        drawRect(
            brush = coverGradient,
            topLeft = Offset(x = 3 * doorWidth, y = 0f),
            size = Size(doorWidth, doorHeight)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(x = 3 * doorWidth, y = 0f),
            size = Size(doorWidth, doorHeight),
            style = Stroke(width = 2f)
        )
    }
}

@Composable
@Preview
fun ElevatorDoorContentPreview(
    @PreviewParameter(ElevatorDoorContentPreviewProvider::class)
    isOpen: Boolean
) {
    ElevatorDoorContent(
        openDoor = isOpen,
        elevatorStatus = ElevatorProps.Status.IDLE
    ) {}
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