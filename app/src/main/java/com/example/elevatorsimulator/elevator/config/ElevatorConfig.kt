package com.example.elevatorsimulator.elevator.config

import com.example.elevatorsimulator.elevator.exceptions.IllegalFloorException
import com.example.elevatorsimulator.utils.TinyDbSingleton

class ElevatorConfig {
    private val tinyDB = TinyDbSingleton.getInstance()

    private fun saveLowestFloor(lowestFloor: Int) {
        tinyDB?.putInt("lowestFloor", lowestFloor)
    }

    fun getLowestFloor(): Int {
        return tinyDB?.getInt("lowestFloor") ?: 1
    }

    private fun saveHighestFloor(highestFloor: Int) {
        tinyDB?.putInt("highestFloor", highestFloor)
    }

    fun getHighestFloor(): Int {
        return tinyDB?.getInt("highestFloor") ?: 2
    }

    @Throws(IllegalFloorException::class)
    fun save(lowestFloor: Int, highestFloor: Int): Boolean {
        if (lowestFloor < highestFloor) {
            saveLowestFloor(lowestFloor)
            saveHighestFloor(highestFloor)
            return true
        } else {
            throw IllegalFloorException("Lowest floor must lower than highest floor.")
        }
    }
}