@file:OptIn(ExperimentalMaterial3Api::class)

package com.scrapps.elevatorsimulator.uicomponents

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scrapps.elevatorsimulator.R
import com.scrapps.elevatorsimulator.ui.theme.colors.Default
import com.scrapps.elevatorsimulator.ui.theme.colors.Icon
import com.scrapps.elevatorsimulator.ui.theme.colors.PowerButton
import com.scrapps.elevatorsimulator.ui.theme.colors.Pressed

@Composable
fun PowerIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isPowerOn: Boolean,
    isPoweringOn: Boolean = false,
) {
    val onColor = PowerButton.On.Default
    val onPressedColor = PowerButton.On.Pressed
    val offColor = PowerButton.Off.Default
    val offPressedColor = PowerButton.Off.Pressed
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "PowerIconTransition")
    val flashingColor by infiniteTransition.animateColor(
        initialValue = offColor,
        targetValue = onColor,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PowerIconColor"
    )

    val buttonColor = when {
        isPressed -> if (isPowerOn) onPressedColor else offPressedColor
        isPoweringOn -> flashingColor
        isPowerOn -> onColor
        else -> offColor
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
    ) {
        Icon(
            modifier = modifier
                .drawBehind {
                    val iconSize = size
                    val circleRadius = iconSize.minDimension / 2
                    val circleCenter = Offset(iconSize.width / 2, iconSize.height / 2)
                    drawCircle(
                        color = buttonColor,
                        radius = circleRadius,
                        center = circleCenter
                    )
                },
            painter = painterResource(id = R.drawable.ic_power),
            contentDescription = "Power button",
            tint = PowerButton.Icon
        )
    }
}

@Composable
@Preview
fun PowerIconPreviewOn() {
    PowerIcon(isPowerOn = true)
}

@Composable
@Preview
fun PowerIconPreviewPoweringOn() {
    PowerIcon(isPowerOn = false, isPoweringOn = true)
}

@Composable
@Preview
fun PowerIconPreviewOff() {
    PowerIcon(isPowerOn = false)
}

@Composable
fun NumberInput(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier
            .width(80.dp)
            .padding(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    )
}
