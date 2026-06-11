package com.scrapps.elevatorsimulator.ui.theme.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun pickColor(light: Color, dark: Color) =
    if (isSystemInDarkTheme()) dark else light