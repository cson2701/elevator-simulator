package com.example.elevatorsimulator.ui.theme.colors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object FloorButton {
    object Background
    object Border
    object Text
}

val FloorButton.Background.Default: Color
    @Composable
    get() = pickColor(
        light = Color(0xFFECECEC),
        dark = Color(0xFF424242)
    )


val FloorButton.Background.Disabled: Color
    @Composable
    get() = pickColor(
        light = Color(0xFFF5F5F5),
        dark = Color(0xFF303030)
    )

val FloorButton.Border.Default: Color
    @Composable
    get() = pickColor(
        light = Color(0xFFBDBDBD),
        dark = Color(0xFF616161)
    )

val FloorButton.Border.Pressed: Color
    @Composable
    get() = pickColor(
        light = Color(0xFF2196F3),
        dark = Color(0xFF64B5F6)
    )

val FloorButton.Border.Disabled: Color
    @Composable
    get() = pickColor(
        light = Color(0xFFEEEEEE),
        dark = Color(0xFF2B2B2B)
    )

val FloorButton.Text.Default: Color
    @Composable
    get() = pickColor(
        light = Color(0xFF212121),
        dark = Color(0xFFF5F5F5)
    )

val FloorButton.Text.Pressed: Color
    @Composable
    get() = pickColor(
        light = Color(0xFF1976D2),
        dark = Color(0xFF64B5F6)
    )

val FloorButton.Text.Disabled: Color
    @Composable
    get() = pickColor(
        light = Color(0xFF9E9E9E),
        dark = Color(0xFF757575)
    )
