package com.scrapps.elevatorsimulator.elevator.config

import com.scrapps.elevatorsimulator.elevator.exceptions.IllegalFloorException
import com.scrapps.elevatorsimulator.utils.TinyDbSingleton

class ElevatorConfig private constructor() {

    private val tinyDB get() = TinyDbSingleton.getInstance()

    fun getLowestFloor(): Int {
        return tinyDB?.getInt("lowestFloor", 0) ?: 0
    }

    fun getHighestFloor(): Int {
        return tinyDB?.getInt("highestFloor", 0) ?: 0
    }

    @Throws(IllegalFloorException::class)
    fun save(lowestFloor: Int, highestFloor: Int): Boolean {
        if (lowestFloor < highestFloor) {
            tinyDB?.putInt("lowestFloor", lowestFloor)
            tinyDB?.putInt("highestFloor", highestFloor)
            return true
        } else {
            throw IllegalFloorException("Lowest floor must lower than highest floor.")
        }
    }

    companion object {
        const val CLOSE_DOOR_DELAY = 3000L
        private var instance: ElevatorConfig? = null

        fun getInstance(): ElevatorConfig {
            if (instance == null) {
                instance = ElevatorConfig()
            }
            return instance!!
        }
    }
}
