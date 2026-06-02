package com.example.elevatorsimulator.ui.theme.colors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ElevatorDoor {
    object Panel
    object Border
}

val ElevatorDoor.Panel.Default: Color
    @Composable
    get() = pickColor(
        light = Color(0xFFB0BEC5), // Blue Grey 200
        dark = Color(0xFF455A64)  // Blue Grey 700
    )

val ElevatorDoor.Border.Default: Color
    @Composable
    get() = pickColor(
        light = Color(0xFF3A4D56),
        dark = Color(0xFF95B3C0),
    )
