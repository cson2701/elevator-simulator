package com.example.elevatorsimulator.uicomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SevenSegmentPanel(
    value: Int?,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.Red,
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF222222), // Dark metallic frame
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(2.dp, Color(0xFF444444))
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.Black)
                .drawWithContent {
                    drawContent()
                    // Glass reflection / Sheen
                    drawRect(
                        brush = Brush.linearGradient(
                            0.0f to Color.White.copy(alpha = 0.1f),
                            0.4f to Color.Transparent,
                            1.0f to Color.White.copy(alpha = 0.05f),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                    )
                    // Red-tinted plastic cover effect (if active color is red)
                    drawRect(
                        color = activeColor.copy(alpha = 0.05f)
                    )
                }
        ) {
            SevenSegmentDisplay(
                value = value,
                activeColor = activeColor,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

@Composable
fun SevenSegmentDisplay(
    value: Int?,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.Red,
    inactiveColor: Color = Color.DarkGray.copy(alpha = 0.5f)
) {
    val displayStr = value?.toString()?.padStart(2, ' ') ?: "  "

    Row(modifier = modifier.background(Color.Black).padding(4.dp)) {
        displayStr.takeLast(2).forEach { char ->
            SevenSegmentDigit(
                char = char,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.5f)
                    .padding(4.dp),
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }
    }
}

@Composable
fun SevenSegmentDigit(
    char: Char,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.Red,
    inactiveColor: Color = Color.DarkGray.copy(alpha = 0.1f)
) {
    // Segments: a, b, c, d, e, f, g
    val segments = when (char) {
        '0' -> listOf(true, true, true, true, true, true, false)
        '1' -> listOf(false, true, true, false, false, false, false)
        '2' -> listOf(true, true, false, true, true, false, true)
        '3' -> listOf(true, true, true, true, false, false, true)
        '4' -> listOf(false, true, true, false, false, true, true)
        '5' -> listOf(true, false, true, true, false, true, true)
        '6' -> listOf(true, false, true, true, true, true, true)
        '7' -> listOf(true, true, true, false, false, false, false)
        '8' -> listOf(true, true, true, true, true, true, true)
        '9' -> listOf(true, true, true, true, false, true, true)
        '-' -> listOf(false, false, false, false, false, false, true)
        else -> listOf(false, false, false, false, false, false, false)
    }

    Box(modifier = modifier.background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val thickness = size.width * 0.18f
            val spacing = size.width * 0.025f
            val hLength = size.width - (thickness * 2) - (spacing * 2)
            val vLength = (size.height - (thickness * 3) - (spacing * 4)) / 2

            // Segment A (Top)
            drawSegment(
                offset = Offset(thickness + spacing, spacing),
                size = Size(hLength, thickness),
                isActive = segments[0],
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                isHorizontal = true
            )

            // Segment B (Top Right)
            drawSegment(
                offset = Offset(size.width - thickness - spacing, thickness + (spacing * 2)),
                size = Size(thickness, vLength),
                isActive = segments[1],
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                isHorizontal = false
            )

            // Segment C (Bottom Right)
            drawSegment(
                offset = Offset(size.width - thickness - spacing, (thickness * 2) + vLength + (spacing * 4)),
                size = Size(thickness, vLength),
                isActive = segments[2],
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                isHorizontal = false
            )

            // Segment D (Bottom)
            drawSegment(
                offset = Offset(thickness + spacing, size.height - thickness - spacing),
                size = Size(hLength, thickness),
                isActive = segments[3],
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                isHorizontal = true
            )

            // Segment E (Bottom Left)
            drawSegment(
                offset = Offset(spacing, (thickness * 2) + vLength + (spacing * 4)),
                size = Size(thickness, vLength),
                isActive = segments[4],
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                isHorizontal = false
            )

            // Segment F (Top Left)
            drawSegment(
                offset = Offset(spacing, thickness + (spacing * 2)),
                size = Size(thickness, vLength),
                isActive = segments[5],
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                isHorizontal = false
            )

            // Segment G (Middle)
            drawSegment(
                offset = Offset(thickness + spacing, thickness + vLength + (spacing * 3)),
                size = Size(hLength, thickness),
                isActive = segments[6],
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                isHorizontal = true
            )
        }
    }
}

private fun DrawScope.drawSegment(
    offset: Offset,
    size: Size,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    isHorizontal: Boolean
) {
    val color = if (isActive) activeColor else inactiveColor
    val path = Path().apply {
        if (isHorizontal) {
            moveTo(offset.x, offset.y + size.height / 2)
            lineTo(offset.x + size.height / 2, offset.y)
            lineTo(offset.x + size.width - size.height / 2, offset.y)
            lineTo(offset.x + size.width, offset.y + size.height / 2)
            lineTo(offset.x + size.width - size.height / 2, offset.y + size.height)
            lineTo(offset.x + size.height / 2, offset.y + size.height)
            close()
        } else {
            moveTo(offset.x + size.width / 2, offset.y)
            lineTo(offset.x + size.width, offset.y + size.width / 2)
            lineTo(offset.x + size.width, offset.y + size.height - size.width / 2)
            lineTo(offset.x + size.width / 2, offset.y + size.height)
            lineTo(offset.x, offset.y + size.height - size.width / 2)
            lineTo(offset.x, offset.y + size.width / 2)
            close()
        }
    }
    drawPath(path, color)
}

@Preview
@Composable
fun SevenSegmentPanelPreview() {
    SevenSegmentPanel(value = 88)
}

@Preview
@Composable
fun SevenSegmentNegativePreview() {
    SevenSegmentPanel(value = -9)
}

@Preview
@Composable
fun SevenSegmentOffPreview() {
    SevenSegmentPanel(value = null)
}
