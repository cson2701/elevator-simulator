package com.example.elevatorsimulator.uicomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.elevatorsimulator.R

@Suppress("PreviewAnnotationInFunctionWithParameters")
@Preview
@Composable
fun PowerIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isPowerOff: Boolean,
) {
    val teal = colorResource(id = R.color.teal_200)
    val red = colorResource(id = R.color.red)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = 24.dp)
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
                        color = if (isPowerOff) teal else red,
                        radius = circleRadius,
                        center = circleCenter
                    )
                },
            painter = painterResource(id = R.drawable.ic_power),
            contentDescription = "Power button",
            tint = colorResource(id = R.color.white) // Replace with your actual green color resource
        )
    }
}
