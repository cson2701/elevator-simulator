package com.scrapps.elevatorsimulator.ui.theme.colors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.scrapps.elevatorsimulator.R

object PowerButton {
    object On
    object Off
}

val PowerButton.On.Default: Color
    @Composable
    get() = colorResource(id = R.color.teal_200)

val PowerButton.On.Pressed: Color
    @Composable
    get() = colorResource(id = R.color.teal_700)

val PowerButton.Off.Default: Color
    @Composable
    get() = colorResource(id = R.color.grey)

val PowerButton.Off.Pressed: Color
    @Composable
    get() = colorResource(id = R.color.dark_grey)

val PowerButton.Icon: Color
    @Composable
    get() = Color.White
