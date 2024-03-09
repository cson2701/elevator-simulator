package com.example.elevatorsimulator.elevator.config

import android.content.Context
import com.example.elevatorsimulator.utils.TinyDbSingleton

class ElevatorConfig(context: Context) {
    private val tinyDB = TinyDbSingleton().getInstance(context)

    private fun saveLowestFloor(lowestFloor: Int) {
        tinyDB?.putInt("lowestFloor", lowestFloor)
    }

    private fun saveHighestFloor(highestFloor: Int) {
        tinyDB?.putInt("highestFloor", highestFloor)
    }

    fun save(lowestFloor: Int, highestFloor: Int) {
        saveLowestFloor(lowestFloor)
        saveHighestFloor(highestFloor)
    }
}