package com.example.elevatorsimulator.elevator.config

import android.content.Context
import com.example.elevatorsimulator.utils.TinyDbSingleton

class ElevatorConfig(context: Context) {
    private val tinyDB = TinyDbSingleton().getInstance(context)

    private fun saveLowestFloor(lowestFloor: Int) {
        tinyDB?.putInt("lowestFloor", lowestFloor)
    }

    fun getLowestFloor(): Int? {
        return tinyDB?.getInt("lowestFloor")
    }

    private fun saveHighestFloor(highestFloor: Int) {
        tinyDB?.putInt("highestFloor", highestFloor)
    }

    fun getHighestFloor(): Int? {
        return tinyDB?.getInt("highestFloor")
    }

    fun save(lowestFloor: Int, highestFloor: Int) {
        // TODO: Check valid lowest and highest
        saveLowestFloor(lowestFloor)
        saveHighestFloor(highestFloor)
    }
}